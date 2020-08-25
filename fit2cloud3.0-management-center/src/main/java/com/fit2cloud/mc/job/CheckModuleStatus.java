package com.fit2cloud.mc.job;

import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.dao.ModelNodeBatchMapper;
import com.fit2cloud.mc.model.ModelNode;
import com.fit2cloud.mc.model.WsMessage;
import com.fit2cloud.mc.service.ModuleNodeService;
import com.fit2cloud.mc.service.WsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private WsService wsService;


    final public static String model_node_fresh_topic = "/topic/getResponse";

    //每30s执行一次检测
    @Scheduled(cron = "0/30 * * * * ? ")
    public void check(){
        Map<String, List<ModelNode>> listMap = dbNodes();
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
                WsMessage<Boolean> booleanWsMessage = new WsMessage<Boolean>(null,model_node_fresh_topic,true);
                wsService.releaseMessage(booleanWsMessage);
            }
        });
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


}
