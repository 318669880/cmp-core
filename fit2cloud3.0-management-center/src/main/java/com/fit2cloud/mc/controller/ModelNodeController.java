package com.fit2cloud.mc.controller;

import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.model.ModelNode;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.service.ModuleNodeService;
import com.fit2cloud.mc.strategy.service.NodeOperateService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/modelNode")
public class ModelNodeController {

    @Resource
    private ModuleNodeService moduleNodeService;

    @Resource
    private NodeOperateService nodeOperateService;

    @Resource
    private ModelManagerService modelManagerService;


    @PostMapping("/readyInstall")
    public void readyInstall(String module, String nodeId) throws Exception{
        //节点预安装状态 其ip和端口默认设置为对应的mc的ip和端口
        ModelNode modelNode = new ModelNode();
        modelNode.setNodeStatus(ModuleStatusConstants.readyInstall.value());
        modelNode.setModelNodeUuid(nodeId);
        modelNode.setModelBasicUuid(module);
        moduleNodeService.addOrUpdateModelNode(modelNode);
    }

    @PostMapping("/node/install")
    public void nodeInstall( String module, String nodeId) throws Exception{
        //moduleNodeService.installNode(module, nodeId);
        nodeOperateService.installOrUpdate(modelManagerService.select(),module);
    }

    @PostMapping("/node/start")
    public void nodeStart( String module, String nodeId) throws Exception{
        /*moduleNodeService.startNode(module, nodeId);*/
        nodeOperateService.start(modelManagerService.select(),module);
    }


    @PostMapping("/node/stop")
    public void nodeStop( String module, String nodeId) throws Exception{
        /*moduleNodeService.stopNode(module, nodeId);*/
        nodeOperateService.stop(modelManagerService.select(),module);
    }
}
