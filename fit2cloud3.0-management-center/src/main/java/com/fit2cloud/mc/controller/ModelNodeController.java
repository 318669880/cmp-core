package com.fit2cloud.mc.controller;

import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.service.ModuleNodeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/modelNode")
public class ModelNodeController {

    @Resource
    private ModuleNodeService moduleNodeService;


    @PostMapping("/readyInstall")
    public void readyInstall(String module) throws Exception{
        //节点预安装状态 其ip和端口默认设置为对应的mc的ip和端口
        moduleNodeService.addOrUpdateModelNode(module, ModuleStatusConstants.readyInstall.value(),null,null);
    }

    @PostMapping("/node/install")
    public void nodeInstall( String module) throws Exception{
        moduleNodeService.installNode(module);
    }

    @PostMapping("/node/start")
    public void nodeStart( String module) throws Exception{
        moduleNodeService.startNode(module);
    }


    @PostMapping("/node/stop")
    public void nodeStop( String module) throws Exception{
        moduleNodeService.stopNode(module);
    }
}
