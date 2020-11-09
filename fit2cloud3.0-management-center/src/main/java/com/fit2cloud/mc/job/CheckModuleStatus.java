package com.fit2cloud.mc.job;

import com.fit2cloud.commons.utils.LogUtil;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Component
public class CheckModuleStatus {

    //节点启动超时时间 默认是90s
    @Value("${fit2cloud.node_start_time_out:90000}")
    private Long node_start_time_out;

    //节点安装超时时间
    @Value("${fit2cloud.node_install_time_out:90000}")
    private Long node_install_time_out;

    @Value("${fit2cloud.node_stop_time_out:25000}")
    private Long node_stop_time_out;

    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private ModuleNodeService moduleNodeService;


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
        checkStatus();
    }

    public void checkStatus(){
        if(SyncEurekaServer.IS_KUBERNETES){
            checkPodsNum();return;
        }
        checkNodes();
        /*DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String format = LocalDateTime.now().format(ofPattern);
        System.out.println(format);
        LogUtil.info(format);*/
    }

    public void checkSingleNode(ModelNode node) {
        String modelNodeUuid = node.getModelNodeUuid();
        String modelBasicUuid = node.getModelBasicUuid();
        List<ModelNode> modelNodes = dbNodes().get(modelBasicUuid);
        AtomicBoolean atoChanged = new AtomicBoolean(false);
        modelNodes.stream().filter(cacheNode -> StringUtils.equals(cacheNode.getModelNodeUuid(), modelNodeUuid)).findFirst().ifPresent(cacheNode -> {
            Boolean runningInEureka = is_node_available(cacheNode);
            String nodeStatus = cacheNode.getNodeStatus();
            Long updateTime = cacheNode.getUpdateTime();
            if (StringUtils.equals(ModuleStatusConstants.stopping.value(), nodeStatus)) {
                if (runningInEureka) {
                    if (isTimeOut(updateTime, node_stop_time_out)) {
                        cacheNode.setNodeStatus(ModuleStatusConstants.running.value());
                        atoChanged.set(true);
                    }
                }else {
                    cacheNode.setNodeStatus(ModuleStatusConstants.stopped.value());
                    atoChanged.set(true);
                }
            } else if (StringUtils.equals(ModuleStatusConstants.startting.value(), nodeStatus)){
                if (runningInEureka) {
                    cacheNode.setNodeStatus(ModuleStatusConstants.running.value());
                    atoChanged.set(true);
                }else {
                    if (isTimeOut(updateTime, node_start_time_out)) {
                        cacheNode.setNodeStatus(ModuleStatusConstants.startFaild.value());
                        atoChanged.set(true);
                    }
                }
            }
            if (atoChanged.get()){
                try {
                    moduleNodeService.addOrUpdateModelNode(cacheNode);
                    WsMessage<Map<String, List<ModelNode>>> wsMessage = new WsMessage<Map<String, List<ModelNode>>>(null,model_node_fresh_topic,new HashMap<>());
                    moduleNodeService.clearCache();
                    wsService.releaseMessage(wsMessage);
                } catch (Exception e) {
                    LogUtil.error(e);
                }
            }
        });

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

    /**
     * 是否超时
     * @param actionTime 操作时间
     * @param outTime
     * @return
     */
    private Boolean isTimeOut(Long actionTime, Long outTime){
        Long now = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        return now - actionTime > outTime;
    }

    private List<ModelNode> off_line_node(List<ModelNode> nodes){
        // 找到running或者installing或者startting状态的节点 且 状态改为stoppped
        List<String> status = new ArrayList<>();
        status.add(ModuleStatusConstants.running.value());
        status.add(ModuleStatusConstants.installing.value());
        status.add(ModuleStatusConstants.startting.value());
        nodes = nodes.stream().filter(node -> {
            Long updateTime = node.getUpdateTime();
            String nodeStatus = node.getNodeStatus();
            if (StringUtils.equals(ModuleStatusConstants.installing.value(), nodeStatus)){
                return isTimeOut(updateTime, node_install_time_out);
            }
            if (StringUtils.equals(ModuleStatusConstants.startting.value(), nodeStatus)){
                return isTimeOut(updateTime, node_start_time_out);
            }
            if (StringUtils.equals(ModuleStatusConstants.stopping.value(), nodeStatus)){
                return true;
            }
            if (StringUtils.equals(ModuleStatusConstants.running.value(), nodeStatus)){
                return true;
            }
            return false;
        }).map(item -> {
            String nodeStatus = item.getNodeStatus();
            item.setNodeStatus(ModuleStatusConstants.stopped.value());
            if (StringUtils.equals(ModuleStatusConstants.installing.value(), nodeStatus) ) {
                item.setNodeStatus(ModuleStatusConstants.installing.nextFaild());
            }

            if (StringUtils.equals(ModuleStatusConstants.startting.value(), nodeStatus) ) {
                item.setNodeStatus(ModuleStatusConstants.startting.nextFaild());
            }
            return item;
        }).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(nodes)){
            //modelNodeBatchMapper.batchUpdate(nodes);
            nodes.forEach(node -> {
                try{
                    moduleNodeService.addOrUpdateModelNode(node);
                }catch (Exception e){
                    LogUtil.error(e);
                }
            });
        }
        return nodes;
    }

    private List<ModelNode> on_line_nodes(List<ModelNode> nodes){
        // 找到非running状态的即诶单改为running状态
        nodes = nodes.stream().filter(node -> {
            String nodeStatus = node.getNodeStatus();
            Long updateTime = node.getUpdateTime();
            if (StringUtils.equals(nodeStatus, ModuleStatusConstants.stopping.value())){
                return isTimeOut(updateTime, node_stop_time_out);
            } else if (!StringUtils.equals(nodeStatus, ModuleStatusConstants.running.value())){
                return true;
            }
            return false;
        }).map(item -> {
            item.setNodeStatus(ModuleStatusConstants.running.value());
            return item;
        }).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(nodes)){
            nodes.forEach(node -> {
                try{
                    moduleNodeService.addOrUpdateModelNode(node);
                }catch (Exception e){
                    LogUtil.error(e);
                }
            });
        }
        return nodes;
    }


    private boolean is_node_available(ModelNode node){
        List<ServiceInstance> instances = discoveryClient.getInstances(node.getModelBasicUuid());
        if(CollectionUtils.isEmpty(instances)) return false;
        String nodeIp = node.getNodeIp();
        return instances.stream().anyMatch(instance -> instance.getUri().toString().indexOf(nodeIp) != -1);
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
