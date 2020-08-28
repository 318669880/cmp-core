package com.fit2cloud.mc.job;

import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.dao.ModelNodeBatchMapper;
import com.fit2cloud.mc.model.ModelBasic;
import com.fit2cloud.mc.model.ModelNode;
import com.fit2cloud.mc.model.WsMessage;
import com.fit2cloud.mc.service.K8sOperatorModuleService;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.service.ModuleNodeService;
import com.fit2cloud.mc.service.WsService;
import com.fit2cloud.mc.strategy.util.ModelManagerUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Component
public class CheckModuleStatus {

    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private ModuleNodeService moduleNodeService;

    @Resource
    private ModelNodeBatchMapper modelNodeBatchMapper;

    @Resource
    private ModelManagerService modelManagerService;

    @Resource
    private WsService wsService;

    @Resource
    private K8sOperatorModuleService k8sOperatorModuleService;


    final public static String model_node_fresh_topic = "/topic/getResponse";

    //每30s执行一次检测
    @Scheduled(cron = "0/30 * * * * ? ")
    public void check(){
        if(SyncEurekaServer.IS_KUBERNETES){
            checkPodsNum();return;
        }
        checkNodes();
    }

    private void checkNodes(){
        Map<String, List<ModelNode>> listMap = dbNodes();
        Map<String, List<ModelNode>> result = new HashMap<>();
        AtomicBoolean atoChanged = new AtomicBoolean(false);
        listMap.entrySet().stream().forEach(entry -> {
            List<ModelNode> nodeList = entry.getValue();
            List<ModelNode> changeNodes = new ArrayList<>();
            Map<Boolean, List<ModelNode>> modelNodeMap = nodeList.stream().collect(Collectors.groupingBy(node -> is_node_available(node)));
            Optional.ofNullable(modelNodeMap.get(true)).ifPresent(nodes -> {
                changeNodes.addAll(on_line_nodes(nodes));
            });
            Optional.ofNullable(modelNodeMap.get(false)).ifPresent(nodes -> {
                changeNodes.addAll(off_line_node(nodes));
            });
            if(!CollectionUtils.isEmpty(changeNodes)){
                result.put(entry.getKey(),changeNodes);
                atoChanged.set(true);
            }
        });
        if(atoChanged.get()){
            WsMessage<Map<String, List<ModelNode>>> wsMessage = new WsMessage<Map<String, List<ModelNode>>>(null,model_node_fresh_topic,result);
            moduleNodeService.clearCache();
            wsService.releaseMessage(wsMessage);
        }
    }



    private Map<String, List<ModelNode>> dbNodes(){
        List<ModelNode> modelNodes = moduleNodeService.queryNodes(null);
        Map<String, List<ModelNode>> modelNodeMap = modelNodes.stream().collect(Collectors.groupingBy(ModelNode::getModelBasicUuid));
        return modelNodeMap;
    }

    private List<ModelNode> off_line_node(List<ModelNode> nodes){
        // 找到running状态的节点 且 状态改为stoppped
        nodes = nodes.stream().filter(node ->
                StringUtils.equals(node.getNodeStatus(), ModuleStatusConstants.running.value()))
                .map(item -> {
                    item.setNodeStatus(ModuleStatusConstants.stopped.value());
                    return item;
                }).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(nodes)){
            modelNodeBatchMapper.batchUpdate(nodes);
            //wsServer.sendMessage();
        }
        return nodes;
    }

    private List<ModelNode> on_line_nodes(List<ModelNode> nodes){
        // 找到非running状态的即诶单改为running状态
        nodes = nodes.stream().filter(node -> !StringUtils.equals(node.getNodeStatus(), ModuleStatusConstants.running.value()))
                .map(item -> {
                    item.setNodeStatus(ModuleStatusConstants.running.value());
                    return item;
                }).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(nodes)){
            modelNodeBatchMapper.batchUpdate(nodes);
        }
        return nodes;
    }


    private boolean is_node_available(ModelNode node){
        List<ServiceInstance> instances = discoveryClient.getInstances(node.getModelBasicUuid());
        if(CollectionUtils.isEmpty(instances)) return false;
        String nodeHost = node.getNodeHost();
        return instances.stream().anyMatch(instance -> instance.getUri().toString().indexOf(nodeHost) != -1);
    }

    /**
     * 从eureka中取出数据 与 redis数据对比 如果有区别 那么 触发websocket消息推送 前段页面刷新
     */
    private void checkPodsNum(){
        //这里从缓存中取出数据 否则 跟下面数据完全一样 则不会触发websocket消息推送
        Map<String, List<String>> pods = k8sOperatorModuleService.pods(true);
        List<ModelBasic> modelInstalls = modelManagerService.modelBasics();
        AtomicBoolean atomicChange = new AtomicBoolean(false);
        Map<String, List<String>> change_pods = new HashMap<>();
        modelInstalls.forEach(model -> {
            String module = model.getModule();
            List<String> cache_pods = pods.get(module);
            if (CollectionUtils.isEmpty(cache_pods))
                cache_pods = new ArrayList<>();
            List<String> instances = discoveryClient.getInstances(module).stream().map(ServiceInstance::getHost).collect(Collectors.toList());
            // 没有发生任何改变
            if(ModelManagerUtil.sameList(cache_pods,instances,item -> item.toString())){
                return;
            }
            atomicChange.set(true);
            change_pods.put(module,instances);
        });
        if(atomicChange.get()){
            //发生改变了 需要清楚缓存
            k8sOperatorModuleService.clearCache();
            //通知前台页面改变podNum
            WsMessage<Map<String, List<String>>> wsMessage = new WsMessage<Map<String, List<String>>>(null,model_node_fresh_topic,change_pods);
            wsService.releaseMessage(wsMessage);
        }
    }
}
