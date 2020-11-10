package com.fit2cloud.mc.service;

import com.fit2cloud.commons.server.constants.ResourceOperation;
import com.fit2cloud.commons.server.constants.ResourceTypeConstants;
import com.fit2cloud.commons.server.service.OperationLogService;
import com.fit2cloud.commons.utils.CommonBeanFactory;
import com.fit2cloud.commons.utils.CommonThreadPool;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.mc.dto.ModuleParamData;
import com.fit2cloud.mc.dto.request.OperatorModuleRequest;
import com.fit2cloud.mc.job.CheckModuleStatus;
import com.fit2cloud.mc.job.DynamicTaskJob;
import com.fit2cloud.mc.model.ModelBasic;
import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.model.ModelNode;
import com.fit2cloud.mc.model.WsMessage;
import com.fit2cloud.mc.utils.K8sUtil;
import com.google.gson.Gson;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class K8sOperatorModuleService {

    @Resource
    @Lazy
    private ModelManagerService modelManagerService;
    @Resource
    private DiscoveryClient discoveryClient;
    @Resource
    private CommonThreadPool commonThreadPool;

    @Resource
    private WsService wsService;

    @Resource
    private DynamicTaskJob dynamicTaskJob;

    public void start(ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest){
        Map<String, Object> params = operatorModuleRequest.getParams() == null ? new HashMap<String, Object>() : operatorModuleRequest.getParams();
        Integer podNumber = operatorModuleRequest.getParams() == null || operatorModuleRequest.getParams().get("pod_number") == null ? 1 : Integer.valueOf(operatorModuleRequest.getParams().get("pod_number").toString());
        params.put("pod_number", podNumber);
        operatorModuleRequest.setParams(params);
        actionModules(managerInfo, operatorModuleRequest);
    }

    public void stop(ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest){
        Map<String, Object> params = operatorModuleRequest.getParams() == null ? new HashMap<String, Object>() : operatorModuleRequest.getParams();
        params.put("pod_number", 0);
        operatorModuleRequest.setParams(params);
        actionModules( managerInfo, operatorModuleRequest);
    }

    private void actionModules(ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest){
        Integer podNumber = Integer.valueOf(operatorModuleRequest.getParams().get("pod_number").toString());
        operatorModuleRequest.getModules().forEach(module -> {
            commonThreadPool.addTask(() ->{
                try{
                    ModelBasic modelBasic = modelManagerService.modelBasicInfo(module);
                    String action = podNumber > modelBasic.getPodNum() ? ResourceOperation.EXPANSION : ResourceOperation.SHRINK;
                    String msg = "scale pod from " + modelBasic.getPodNum() + " to " + podNumber;
                    LogUtil.info("Begin to operation {} ,: " + msg, module);
                    modelManagerService.updateModelBasicPodNum(module, podNumber);
                    modelBasic = modelManagerService.modelBasicInfo(module);
                    K8sUtil.actionService(module, new Gson().fromJson(modelBasic.getCustomData(), ModuleParamData.class), operatorModuleRequest.getParams());
                    LogUtil.info("Success to operation {} ,: " + msg, module);
                    OperationLogService.log(null, module, modelBasic.getName(), ResourceTypeConstants.MODULE.name(), action, null);
                }catch (Exception e){
                    LogUtil.error("Faild to operation module: " + module, e.getMessage());
                }
            });
        });
    }

    @Transactional
    public void uninstall(ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest){
        operatorModuleRequest.getModules().forEach(module -> {
            try{
                LogUtil.info("Begin to uninstall module: " + module);
                K8sUtil.uninstallService(module);
                modelManagerService.updateModelBasicPodNum(module, 0);
                ModelBasic modelBasic = modelManagerService.modelBasicInfo(module);
                //modelManagerService.deleteModelBasic(module);
                K8sOperatorModuleService proxy = CommonBeanFactory.getBean(K8sOperatorModuleService.class);
                proxy.removeK8sModel(modelBasic);
                LogUtil.info("End of uninstall module: " + module);
                OperationLogService.log(null, module, modelBasic.getName(), ResourceTypeConstants.MODULE.name(), ResourceOperation.UNINSTALL, null);
            }catch (Exception e){
                LogUtil.error("Faild to uninstall module: " + module, e);
            }
        });
    }



    @Async
    public void removeK8sModel(ModelBasic modelBasic){
        AtomicReference<ScheduledFuture<?>> futureReference = new AtomicReference();
        ScheduledFuture<?> add = dynamicTaskJob.add(() -> {
            List<ServiceInstance> instances = discoveryClient.getInstances(modelBasic.getModule());
            if (CollectionUtils.isEmpty(instances)){
                modelManagerService.deleteModelBasic(modelBasic.getModule());
                dynamicTaskJob.delete(futureReference.get());
                K8sOperatorModuleService proxy = CommonBeanFactory.getBean(K8sOperatorModuleService.class);
                proxy.clearCache();
                WsMessage<Map<String, List<ModelNode>>> wsMessage = new WsMessage<Map<String, List<ModelNode>>>(null, CheckModuleStatus.model_node_fresh_topic,new HashMap<>());
                wsService.releaseMessage(wsMessage);
            }
        }, "0/5 * * * * ? ");
        futureReference.set(add);
    }

    @Cacheable(value = "k8s-pod-cache" ,condition = "#user_cache==true")
    public Map<String, List<String>> pods(boolean user_cache){
        Map<String,List<String>> result = new HashMap<>();
        discoveryClient.getServices().forEach(module -> {
            List<ServiceInstance> instances = discoveryClient.getInstances(module);
            if(!CollectionUtils.isEmpty(instances)){
                result.put(module,instances.stream().map(ServiceInstance::getHost).collect(Collectors.toList()));
            }
        });
        return result;
    }

    @CacheEvict(value = "k8s-pod-cache" ,allEntries = true)
    public void clearCache(){}

}
