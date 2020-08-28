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
        operatorModuleRequest.getModules().forEach(module -> {
            try{
                LogUtil.info("Begin to start module: " + module);
                Integer podNumber = operatorModuleRequest.getParams() == null || operatorModuleRequest.getParams().get("pod_number") == null ? 1 : Integer.valueOf(operatorModuleRequest.getParams().get("pod_number").toString());
                modelManagerService.updateModelBasicPodNum(module, podNumber);
                ModelBasic modelBasic = modelManagerService.modelBasicInfo(module);
                K8sUtil.startService(module, new Gson().fromJson(modelBasic.getCustomData(), ModuleParamData.class) , operatorModuleRequest.getParams());
                LogUtil.info("End of start module: " + module);
            }catch (Exception e){
                LogUtil.error("Faild to start module: " + module, e);
            }
        });
    }

    public void stop(ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest){
        operatorModuleRequest.getModules().forEach(module -> {
            try{
                LogUtil.info("Begin to stop module: " + module);
                ModelBasic modelBasic = modelManagerService.modelBasicInfo(module);
                K8sUtil.stopService(module, new Gson().fromJson(modelBasic.getCustomData(), ModuleParamData.class));
                LogUtil.info("End of stop module: " + module);
            }catch (Exception e){
                LogUtil.error("Faild to stop module: " + module, e);
            }
        });
    }

    public void uninstall(ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest){
        operatorModuleRequest.getModules().forEach(module -> {
            try{
                LogUtil.info("Begin to uninstall module: " + module);
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
