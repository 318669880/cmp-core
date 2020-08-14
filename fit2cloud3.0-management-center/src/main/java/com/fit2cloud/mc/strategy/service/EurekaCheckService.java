package com.fit2cloud.mc.strategy.service;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/13 5:49 下午
 * @Description: Please Write notes scientifically
 */

@Service
public class EurekaCheckService {

    @Resource
    private DiscoveryClient discoveryClient;

    public void checkModuleStatus(String module) throws Exception{
        List<String> services = discoveryClient.getServices();

    }
}
