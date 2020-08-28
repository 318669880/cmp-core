package com.fit2cloud.mc.service;

import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.mc.dto.ModuleParamData;
import com.fit2cloud.mc.dto.request.OperatorModuleRequest;
import com.fit2cloud.mc.model.ModelBasic;
import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.utils.K8sUtil;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class K8sOperatorModuleService {
    @Resource
    private ModelManagerService modelManagerService;
    @Resource
    private DiscoveryClient discoveryClient;

    public void start(ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest){
        Map<String, Object> params = operatorModuleRequest.getParams() == null ? new HashMap<String, Object>() : operatorModuleRequest.getParams();
        Integer podNumber = operatorModuleRequest.getParams() == null || operatorModuleRequest.getParams().get("pod_number") == null ? 1 : Integer.valueOf(operatorModuleRequest.getParams().get("pod_number").toString());
        params.put("pod_number", podNumber);
        operatorModuleRequest.setParams(params);
        actionModules("start", managerInfo, operatorModuleRequest);
    }

    public void stop(ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest){
        Map<String, Object> params = operatorModuleRequest.getParams() == null ? new HashMap<String, Object>() : operatorModuleRequest.getParams();
        params.put("pod_number", 0);
        operatorModuleRequest.setParams(params);
        actionModules("stop", managerInfo, operatorModuleRequest);
    }

    private void actionModules(String action, ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest){
        operatorModuleRequest.getModules().forEach(module -> {
            try{
                LogUtil.info("Begin to {} module: " + module, action);
                Integer podNumber = Integer.valueOf(operatorModuleRequest.getParams().get("pod_number").toString());
                modelManagerService.updateModelBasicPodNum(module, podNumber);
                ModelBasic modelBasic = modelManagerService.modelBasicInfo(module);
                K8sUtil.actionService(module, new Gson().fromJson(modelBasic.getCustomData(), ModuleParamData.class), operatorModuleRequest.getParams());
                LogUtil.info("End of {} module: " + module, action);
            }catch (Exception e){
                LogUtil.error("Faild to {} module: {}" + module, action,  e);
            }
        });
    }


    public void uninstall(ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest){
        operatorModuleRequest.getModules().forEach(module -> {
            try{
                LogUtil.info("Begin to uninstall module: " + module);
                modelManagerService.deleteModelBasic(module);
                K8sUtil.uninstallService(module);
                LogUtil.info("End of uninstall module: " + module);
            }catch (Exception e){
                LogUtil.error("Faild to uninstall module: " + module, e);
            }
        });
    }

    @Cacheable(value = "k8s-pod-cache" ,condition = "#user_cache==true")
    public Map<String, List<String>> pods(boolean user_cache){
        Map<String,List<String>> result = new HashMap<>();
        discoveryClient.getServices().forEach(module -> {
            List<ServiceInstance> instances = discoveryClient.getInstances(module);
            instances.forEach(instance ->{
            });
            if(!CollectionUtils.isEmpty(instances)){
                result.put(module,instances.stream().map(ServiceInstance::getHost).collect(Collectors.toList()));
            }
        });
        return result;
    }

    @CacheEvict(value = "k8s-pod-cache" ,allEntries = true)
    public void clearCache(){}

}
