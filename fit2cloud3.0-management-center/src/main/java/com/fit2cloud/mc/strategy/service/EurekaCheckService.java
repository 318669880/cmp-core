package com.fit2cloud.mc.strategy.service;

import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.strategy.entity.ModelStatusParam;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/13 5:49 下午
 * @Description: Please Write notes scientifically
 * 管理中心 通过注册中心eureka检查模块状态
 */

@Service
public class EurekaCheckService {

    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private ModelManagerService modelManagerService;

    public void checkModuleStatus(ModelStatusParam modelStatusParam) throws Exception{
        String module = modelStatusParam.getModule();
        ModuleStatusConstants status = modelStatusParam.getStatus();
        Map<String,List<ServiceInstance>> sis = new HashMap<>();
        List<String> services = discoveryClient.getServices();
        services.forEach(service -> {
            sis.put(service,discoveryClient.getInstances(service));
        });
        String client_host = modelManagerService.domain_host();
        List<ServiceInstance> moduleInstances = sis.get(module);
        String nextStatus = serviceAvailable(client_host,moduleInstances) ? status.nextSuccess() :status.nextFaild();
        modelManagerService.addOrUpdateModelNode(module, nextStatus);
        modelManagerService.modelStatu(module);
    }

    private boolean serviceAvailable(String host,List<ServiceInstance> moduleInstances){
        if (CollectionUtils.isEmpty(moduleInstances)) return false;
        return moduleInstances.stream().anyMatch(instalce -> instalce.getUri().toString().indexOf(host) != -1);
    }
}
