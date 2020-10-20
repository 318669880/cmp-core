package com.fit2cloud.mc.controller;


import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.job.CheckModuleStatus;
import com.fit2cloud.mc.job.DynamicTaskJob;
import com.fit2cloud.mc.model.ModelNode;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.service.ModuleNodeService;
import com.fit2cloud.mc.strategy.service.NodeOperateService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/modelNode")
public class ModelNodeController {

    @Resource
    private ModuleNodeService moduleNodeService;

    @Resource
    private NodeOperateService nodeOperateService;

    @Resource
    private ModelManagerService modelManagerService;

    @Resource
    private DynamicTaskJob dynamicTaskJob;

    @Resource
    private CheckModuleStatus checkModuleStatus;


    @PostMapping("/readyInstall")
    public void readyInstall(String module, String nodeId) throws Exception{
        //节点预安装状态 其ip和端口默认设置为对应的mc的ip和端口
        ModelNode modelNode = new ModelNode();
        modelNode.setNodeStatus(ModuleStatusConstants.readyInstall.value());
        modelNode.setModelNodeUuid(nodeId);
        modelNode.setModelBasicUuid(module);
        moduleNodeService.addOrUpdateModelNode(modelNode);
        moduleNodeService.installNode(module,nodeId);
    }

    @PostMapping("/readyUpdate")
    public void readyUpdate(String module, String nodeId) throws Exception{
        //节点预安装状态 其ip和端口默认设置为对应的mc的ip和端口
        ModelNode mcNode = moduleNodeService.currentMcNode();
        List<ModelNode> modelNodes = moduleNodeService.queryNodes(module).stream().filter(node -> StringUtils.equals(node.getMcNodeUuid(), mcNode.getModelNodeUuid())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(modelNodes))  {
            F2CException.throwException("模块："+module +"对应的节点："+mcNode.getNodeHost()+"不存在");
        }
        ModelNode modelNode = modelNodes.get(0);
        modelNode.setNodeStatus(ModuleStatusConstants.readyInstall.value());
        moduleNodeService.addOrUpdateModelNode(modelNode);
        moduleNodeService.installNode(module,modelNode.getModelNodeUuid());
    }

    @PostMapping("/node/install")
    public void nodeInstall( String module, String nodeId) throws Exception{
        nodeOperateService.installOrUpdate(modelManagerService.select(), module, nodeId);
    }

    @PostMapping("/node/start")
    public void nodeStart( String module, String nodeId) throws Exception{
        nodeOperateService.start(modelManagerService.select(), module, nodeId);
    }


    @PostMapping("/node/stop")
    public void nodeStop( String module, String nodeId) throws Exception{
        nodeOperateService.stop(modelManagerService.select(), module, nodeId);
        //执行停止操作后 每5s执行一次检测 30s后销毁定时器
        dynamicTaskJob.addTaskWithTime(() -> checkModuleStatus.checkStatus(), "0/5 * * * * ? ", 30000L);
    }
}
