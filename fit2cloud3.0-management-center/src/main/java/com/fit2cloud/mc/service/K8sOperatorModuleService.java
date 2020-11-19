package com.fit2cloud.mc.service;

import com.fit2cloud.commons.server.constants.ResourceOperation;
import com.fit2cloud.commons.server.constants.ResourceTypeConstants;
import com.fit2cloud.commons.server.service.OperationLogService;
import com.fit2cloud.commons.utils.CommonThreadPool;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.mc.common.constants.WsTopicConstants;
import com.fit2cloud.mc.dto.ModuleParamData;
import com.fit2cloud.mc.dto.request.OperatorModuleRequest;
import com.fit2cloud.mc.job.CheckModuleStatus;
import com.fit2cloud.mc.model.ModelBasic;
import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.utils.K8sUtil;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Lazy
    private CheckModuleStatus checkModuleStatus;

    @Value("${fit2cloud.k8s_operate_time_out:300000}")
    private Long k8s_operate_time_out;

    public void start(ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest) {
        Map<String, Object> params = operatorModuleRequest.getParams() == null ? new HashMap<String, Object>() : operatorModuleRequest.getParams();
        Integer podNumber = operatorModuleRequest.getParams() == null || operatorModuleRequest.getParams().get("pod_number") == null ? 1 : Integer.valueOf(operatorModuleRequest.getParams().get("pod_number").toString());
        params.put("pod_number", podNumber);
        operatorModuleRequest.setParams(params);
        actionModules(managerInfo, operatorModuleRequest);
    }

    public void stop(ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest) {
        Map<String, Object> params = operatorModuleRequest.getParams() == null ? new HashMap<String, Object>() : operatorModuleRequest.getParams();
        params.put("pod_number", 0);
        operatorModuleRequest.setParams(params);
        actionModules(managerInfo, operatorModuleRequest);
    }

    private void actionModules(ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest) {
        Integer podNumber = Integer.valueOf(operatorModuleRequest.getParams().get("pod_number").toString());
        operatorModuleRequest.getModules().forEach(module -> {
            commonThreadPool.addTask(() -> {
                try {
                    Long updateTime = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
                    ModelBasic modelBasic = modelManagerService.modelBasicInfo(module);
                    String action = podNumber > modelBasic.getPodNum() ? ResourceOperation.EXPANSION : ResourceOperation.SHRINK;
                    String msg = "scale pod from " + modelBasic.getPodNum() + " to " + podNumber;
                    LogUtil.info("Begin to operation {} ,: " + msg, module);
                    modelBasic.setPodNum(podNumber);
                    modelBasic.setCurrentStatus(action);
                    modelManagerService.updateModelBasic(modelBasic);
                    K8sUtil.actionService(module, new Gson().fromJson(modelBasic.getCustomData(), ModuleParamData.class), operatorModuleRequest.getParams());
                    LogUtil.info("Success to operation {} ,: " + msg, module);

                    int dbPodNum = modelBasic.getPodNum();
                    int eurekaPodNum = discoveryClient.getInstances(module).size();
                    int abs = Math.abs(dbPodNum - eurekaPodNum);

                    checkModuleStatus.checkModule(modelBasic, model -> {
                        ModelBasic currentModel = modelManagerService.modelBasicInfo(module);
                        String currentStatus = currentModel.getCurrentStatus();
                        if (StringUtils.isEmpty(currentStatus)){
                            LogUtil.info("Timer detected ["+module+"] "+action+" removed");
                            return true;
                        }

                        if (checkModuleStatus.isTimeOut(updateTime, k8s_operate_time_out * abs)) {
                            LogUtil.info("Timer detected ["+module+"] "+action+" timeout");
                            LogUtil.info("dbPodNum = "+dbPodNum+" ,eurekaPodNum = "+eurekaPodNum);
                            modelBasic.setCurrentStatus("timeOut");
                            modelManagerService.updateModelBasic(modelBasic);
                            checkModuleStatus.sendMessage(model, WsTopicConstants.K8S_MODEL_START);
                            LogUtil.info("The status of module["+module+"] has been set to timeout by Timer");
                            LogUtil.info("Timer detected ["+module+"] "+action+" removed");
                            return true;
                        }
                        return false;
                    }, 0L);
                    OperationLogService.log(null, module, modelBasic.getName(), ResourceTypeConstants.MODULE.name(), action, null);
                } catch (Exception e) {
                    LogUtil.error("Faild to operation module: " + module, e.getMessage());
                }
            });
        });
    }

    @Transactional
    public void uninstall(ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest) {
        operatorModuleRequest.getModules().forEach(module -> {
            try {
                LogUtil.info("Begin to uninstall module: " + module);
                K8sUtil.uninstallService(module);
                ModelBasic modelBasic = modelManagerService.modelBasicInfo(module);
                modelBasic.setCurrentStatus("uninstalling");
                modelBasic.setPodNum(0);
                modelManagerService.updateModelBasic(modelBasic);
                checkModuleStatus.checkModule(modelBasic, model -> {
                    List<ServiceInstance> instances = discoveryClient.getInstances(modelBasic.getModule());
                    if (CollectionUtils.isEmpty(instances)) {
                        modelManagerService.deleteModelBasic(modelBasic.getModule());
                        checkModuleStatus.sendMessage(modelBasic, WsTopicConstants.K8S_MODEL_UNINSTALL);
                        return true;
                    }
                    return false;
                }, 5000L);
                LogUtil.info("End of uninstall module: " + module);
                OperationLogService.log(null, module, modelBasic.getName(), ResourceTypeConstants.MODULE.name(), ResourceOperation.UNINSTALL, null);
            } catch (Exception e) {
                LogUtil.error("Faild to uninstall module: " + module, e);
            }
        });
    }


    @Cacheable(value = "k8s-pod-cache")
    public Map<String, List<String>> pods() {
        Map<String, List<String>> result = new HashMap<>();
        discoveryClient.getServices().forEach(module -> {
            List<ServiceInstance> instances = discoveryClient.getInstances(module);
            if (!CollectionUtils.isEmpty(instances)) {
                result.put(module, instances.stream().map(ServiceInstance::getHost).collect(Collectors.toList()));
            }
        });
        return result;
    }


}
