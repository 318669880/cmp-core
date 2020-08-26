package com.fit2cloud.mc.service;

import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.mc.dto.request.OperatorModuleRequest;
import com.fit2cloud.mc.model.ModelBasic;
import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.model.ModelVersion;
import com.fit2cloud.mc.strategy.entity.ResultInfo;
import com.fit2cloud.mc.strategy.factory.NodeOperateStrategyFactory;
import com.fit2cloud.mc.strategy.service.ModelOperateStrategy;
import com.fit2cloud.mc.strategy.service.NetFileService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class K8sOperatorModuleService {
    @Resource
    private ModelManagerService modelManagerService;
    @Resource
    private NetFileService netFileService;
    @Resource
    private DiscoveryClient discoveryClient;


    public void start(ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest){
        operatorModuleRequest.getModules().forEach(module -> {
            try{
                String filePath = downLoad(module);
                ModelOperateStrategy operateStrategy = NodeOperateStrategyFactory.build(managerInfo.getEnv());
                operateStrategy.executeInstall(managerInfo, module, filePath, operatorModuleRequest.getParams());
            }catch (Exception e){
                LogUtil.error("Faild to start module: " + module, e);
            }
        });

    }

    public void stop(ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest){
        operatorModuleRequest.getModules().forEach(module -> {
            try{
                ModelOperateStrategy operateStrategy = NodeOperateStrategyFactory.build(managerInfo.getEnv());
                operateStrategy.executeStop(module);
            }catch (Exception e){
                LogUtil.error("Faild to stop module: " + module, e);
            }
        });
    }

    private String downLoad (String module) throws Exception{
        ModelBasic modelBasic = modelManagerService.modelBasicInfo(module);
        ModelVersion modelVersion = modelManagerService.modelVersionInfo(modelBasic.getModelUuid(), modelBasic.getLastRevision());
        String downloadUrl = modelVersion.getDownloadUrl();
        ResultInfo<String> resultInfo = netFileService.down(downloadUrl);
        return resultInfo.getData();
    }

    @Cacheable(value = "k8s-pod-cache" )
    public Map<String, List<String>> pods(){
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
