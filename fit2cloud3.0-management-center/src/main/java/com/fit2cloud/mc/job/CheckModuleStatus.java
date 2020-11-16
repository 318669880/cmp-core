package com.fit2cloud.mc.job;


import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.common.constants.WsTopicConstants;
import com.fit2cloud.mc.model.ModelBasic;
import com.fit2cloud.mc.model.ModelNode;
import com.fit2cloud.mc.model.WsMessage;
import com.fit2cloud.mc.model.WsMessageBody;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.service.ModuleNodeService;
import com.fit2cloud.mc.service.WsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@Component
public class CheckModuleStatus {


    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private WsService wsService;


    @Resource
    private DynamicTaskJob dynamicTaskJob;


    @Resource
    private ModuleNodeService moduleNodeService;


    @Resource
    @Lazy
    private ModelManagerService modelManagerService;

    public void moduleStatusTrigger(String appName, String serviceId, Boolean onLine) {

        String model = appName.toLowerCase();
        Optional.ofNullable(modelManagerService.modelBasicInfo(model)).ifPresent(modelBaisc -> {
            LogUtil.info("eurekaEvent was triggered");
            LogUtil.info("Start operate node ["+appName +":"+ serviceId +"] for "+(onLine?"running":"stopped"));
            WsTopicConstants wsTopicConstants = WsTopicConstants.K8S_MODEL_START;
            if (!onLine){
                wsTopicConstants = WsTopicConstants.K8S_MODEL_STOP;
            }
            if (discoveryClient.getInstances(modelBaisc.getModule()).size() == modelBaisc.getPodNum() && StringUtils.equals("timeOut", modelBaisc.getCurrentStatus())){
                modelBaisc.setCurrentStatus(null);
                modelManagerService.updateModelBasic(modelBaisc);
            }
            sendMessage(modelBaisc, wsTopicConstants);
            LogUtil.info("End operate node ["+appName +":"+ serviceId +"] for "+(onLine?"running":"stopped"));
        });
    }

    private ServiceInstance instanceByNode(ModelNode node){
        String module = node.getModelBasicUuid();
        List<ServiceInstance> instances = discoveryClient.getInstances(module);
        for (ServiceInstance instance : instances){
            if (instance.getUri().toString().indexOf(node.getNodeIp()) !=-1 ){
                return instance;
            }
        }
        return null;
    }


    public void nodeStatuesTrigger(String appName, String serviceId, Boolean onLine){
        String model = appName.toLowerCase();
        if (SyncEurekaServer.IS_KUBERNETES){
            moduleStatusTrigger(appName, serviceId, onLine);
            return;
        }
        List<ModelNode> modelNodes = moduleNodeService.queryNodes(model);
        if (CollectionUtils.isEmpty(modelNodes)) return;
        LogUtil.info("eurekaEvent was triggered");
        LogUtil.info("Start operate node ["+appName +":"+ serviceId +"] for "+(onLine?"running":"stopped"));
        modelNodes.stream().filter(node -> {
            ServiceInstance serviceInstance = instanceByNode(node);
            LogUtil.info("The node mapping ServiceId is: "+serviceInstance.getInstanceId());
            LogUtil.info("The event target ServiceId is: "+serviceId);
            LogUtil.info("The node mapping serviceId equals event target ServiceId is : "+ StringUtils.equals(serviceInstance.getInstanceId(), serviceId));
            return null != serviceInstance && StringUtils.equals(serviceInstance.getInstanceId(), serviceId);
        }).findFirst().ifPresent(node -> {
            String status = ModuleStatusConstants.running.value();
            WsTopicConstants wsTopicConstants = WsTopicConstants.HOST_NODE_START;
            String currentStatus = node.getNodeStatus();
            if (StringUtils.equals(currentStatus, ModuleStatusConstants.startting.value()) && !onLine){
                return;
            }
            if (StringUtils.equals(currentStatus, ModuleStatusConstants.stopping.value()) && onLine){
                return;
            }
            if (!onLine){
                status = ModuleStatusConstants.stopped.value();
                wsTopicConstants = WsTopicConstants.HOST_NODE_STOP;
                if (StringUtils.equals(currentStatus, ModuleStatusConstants.stopped.value())){
                    return;
                }
            }
            if (StringUtils.equals(currentStatus, ModuleStatusConstants.running.value())){
                return;
            }
            node.setNodeStatus(status);
            try {
                moduleNodeService.addOrUpdateNodeClear(node);
                LogUtil.info("End operate node ["+appName +":"+ serviceId +"] for "+(onLine?"running":"stopped"));
            } catch (Exception e) {
                LogUtil.error(e.getMessage() , e);
            }
            sendMessage(node, wsTopicConstants);
        });
    }

    /**
     * @param modelNode     检测节点
     * @param stopCondition 跳出检测条件
     */
    @Async
    public void checkNode(ModelNode modelNode, Function<ModelNode, Boolean> stopCondition, Long delay) {
        if (null != delay && delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        AtomicReference<ScheduledFuture<?>> scheduledFutureReference = new AtomicReference<>();
        ScheduledFuture<?> future = dynamicTaskJob.add(() -> {
            Boolean canExit = stopCondition.apply(modelNode);
            if (canExit) {
                Optional.ofNullable(scheduledFutureReference.get()).ifPresent(dynamicTaskJob::delete);
            }
        }, "0/5 * * * * ? ");
        scheduledFutureReference.set(future);
    }

    @Async
    public void checkModule(ModelBasic modelBasic, Function<ModelBasic, Boolean> stopCondition, Long delay) {
        if (null != delay && delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        AtomicReference<ScheduledFuture<?>> futureReference = new AtomicReference();
        ScheduledFuture<?> future = dynamicTaskJob.add(() -> {
            Boolean canExit = stopCondition.apply(modelBasic);
            if (canExit) {
                Optional.ofNullable(futureReference.get()).ifPresent(dynamicTaskJob::delete);
            }
        }, "0/5 * * * * ? ");
        futureReference.set(future);
    }

    public void sendMessage(ModelBasic modelBasic, WsTopicConstants wsTopicConstants) {
        WsMessageBody wsMessageBody = WsMessageBody.builder().type(wsTopicConstants.name()).t(modelBasic).build();
        WsMessage<WsMessageBody> wsMessage = new WsMessage<WsMessageBody>(null, wsTopicConstants.value(), wsMessageBody);
        wsService.releaseMessage(wsMessage);
    }

    public void sendMessage(ModelNode modelNode, WsTopicConstants wsTopicConstants) {
        WsMessageBody wsMessageBody = WsMessageBody.builder().type(wsTopicConstants.name()).t(modelNode).build();
        WsMessage<WsMessageBody> wsMessage = new WsMessage<WsMessageBody>(null, wsTopicConstants.value(), wsMessageBody);
        wsService.releaseMessage(wsMessage);
    }


    /**
     * 是否超时
     *
     * @param actionTime 操作时间
     * @param outTime
     * @return
     */
    public Boolean isTimeOut(Long actionTime, Long outTime) {
        Long now = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        return now - actionTime > outTime;
    }

    public boolean is_node_available(ModelNode node) {
        List<ServiceInstance> instances = discoveryClient.getInstances(node.getModelBasicUuid());
        if (CollectionUtils.isEmpty(instances)) return false;
        String nodeIp = node.getNodeIp();
        return instances.stream().anyMatch(instance -> instance.getUri().toString().indexOf(nodeIp) != -1);
    }


}
