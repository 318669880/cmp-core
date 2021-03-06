package com.fit2cloud.mc.controller;

import com.fit2cloud.mc.dto.request.OperatorModuleRequest;
import com.fit2cloud.mc.service.K8sOperatorModuleService;
import com.fit2cloud.mc.service.ModelManagerService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


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

    @PostMapping("/uninstall/")
    public void uninstall(@RequestBody OperatorModuleRequest operatorModuleRequest) throws Exception{
        k8sOperatorModuleService.uninstall(modelManagerService.select(), operatorModuleRequest);
    }


    @GetMapping("/pods")
    public Map<String, List<String>> pods(){
        return k8sOperatorModuleService.pods();
    }

}
