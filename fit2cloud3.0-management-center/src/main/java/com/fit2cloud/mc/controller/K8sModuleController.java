package com.fit2cloud.mc.controller;

import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.dto.request.OperatorModuleRequest;
import com.fit2cloud.mc.model.ModelNode;
import com.fit2cloud.mc.service.K8sOperatorModuleService;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.service.ModuleNodeService;
import com.fit2cloud.mc.strategy.service.NodeOperateService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/k8s-operator-module")
public class K8sModuleController {




    @Resource
    private K8sOperatorModuleService k8sOperatorModuleService;

    @Resource
    private ModelManagerService modelManagerService;

    @PostMapping("/start/")
    public void start(@RequestBody OperatorModuleRequest operatorModuleRequest) throws Exception{
        k8sOperatorModuleService.start(modelManagerService.select(), operatorModuleRequest);
    }

    @PostMapping("/stop/")
    public void stop(@RequestBody OperatorModuleRequest operatorModuleRequest) throws Exception{
        k8sOperatorModuleService.stop(modelManagerService.select(), operatorModuleRequest);
    }

    @GetMapping("/pods")
    public Map<String, List<String>> pods(){
        return k8sOperatorModuleService.pods();
    }

}
