package com.fit2cloud.mc.job;


import com.fit2cloud.mc.common.constants.WsTopicConstants;
import com.fit2cloud.mc.model.ModelBasic;
import com.fit2cloud.mc.model.ModelNode;
import com.fit2cloud.mc.model.WsMessage;
import com.fit2cloud.mc.model.WsMessageBody;
import com.fit2cloud.mc.service.WsService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
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

    /**
     * @param modelNode     检测节点
     * @param stopCondition 跳出检测条件
     */
    private void checkNode(ModelNode modelNode, Function<ModelNode, Boolean> stopCondition) {
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
    public void checkNode(ModelNode modelNode, Function<ModelNode, Boolean> stopCondition, Long delay) {
        if (null != delay && delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        checkNode(modelNode, stopCondition);
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
