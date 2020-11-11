package com.fit2cloud.mc.controller;


import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.common.constants.WsTopicConstants;
import com.fit2cloud.mc.job.CheckModuleStatus;
import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.model.ModelNode;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.service.ModuleNodeService;
import com.fit2cloud.mc.strategy.service.NodeOperateService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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
    private CheckModuleStatus checkModuleStatus;

    //节点启动超时时间 默认是90s
    @Value("${fit2cloud.node_start_time_out:90000}")
    private Long node_start_time_out;

    //节点安装超时时间
    @Value("${fit2cloud.node_install_time_out:60000}")
    private Long node_install_time_out;

    @Value("${fit2cloud.node_stop_time_out:30000}")
    private Long node_stop_time_out;


    @PostMapping("/readyInstall")
    public void readyInstall(String module, String nodeId) throws Exception {
        //节点预安装状态 其ip和端口默认设置为对应的mc的ip和端口
        ModelNode modelNode = new ModelNode();
        modelNode.setNodeStatus(ModuleStatusConstants.readyInstall.value());
        modelNode.setModelNodeUuid(nodeId);
        modelNode.setModelBasicUuid(module);
        moduleNodeService.addOrUpdateModelNode(modelNode);
        moduleNodeService.installNode(module, nodeId);
    }

    @PostMapping("/readyUpdate")
    public void readyUpdate(String module, String nodeId) throws Exception {
        //节点预安装状态 其ip和端口默认设置为对应的mc的ip和端口
        ModelNode mcNode = moduleNodeService.currentMcNode();
        List<ModelNode> modelNodes = moduleNodeService.queryNodes(module).stream().filter(node -> StringUtils.equals(node.getMcNodeUuid(), mcNode.getModelNodeUuid())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(modelNodes)) {
            F2CException.throwException("模块：" + module + "对应的节点：" + mcNode.getNodeHost() + "不存在");
        }
        ModelNode modelNode = modelNodes.get(0);
        modelNode.setNodeStatus(ModuleStatusConstants.readyInstall.value());
        moduleNodeService.addOrUpdateModelNode(modelNode);
        moduleNodeService.installNode(module, modelNode.getModelNodeUuid());
    }

    @PostMapping("/node/install")
    public void nodeInstall(String module, String nodeId) throws Exception {
        nodeOperateService.installOrUpdate(modelManagerService.select(), module, nodeId);
        ModelNode modelNode = moduleNodeService.nodeInfo(nodeId);
        Long updateTime = modelNode.getUpdateTime();
        checkModuleStatus.checkNode(modelNode, node -> {
            ModelNode currentNode = moduleNodeService.nodeInfo(nodeId);
            if (StringUtils.equals(currentNode.getNodeStatus(), ModuleStatusConstants.installFaild.value())) {
                //执行ModelOperateStrategy方法异常 状态已经改变了 那么结束检测任务
                moduleNodeService.clearCache();
                checkModuleStatus.sendMessage(currentNode, WsTopicConstants.HOST_NODE_INSTALL);
                return true;
            }
            if (checkModuleStatus.isTimeOut(updateTime, node_install_time_out)) {
                moduleNodeService.clearCache();
                currentNode.setNodeStatus(ModuleStatusConstants.stopped.value());
                try {
                    moduleNodeService.addOrUpdateModelNode(currentNode);
                } catch (Exception e) {
                    LogUtil.error(e.getMessage(), e);
                }
                checkModuleStatus.sendMessage(currentNode, WsTopicConstants.HOST_NODE_INSTALL);
                return true;
            }
            return false;
        }, 10000L);
    }


    @PostMapping("/node/start")
    public void nodeStart(String module, String nodeId) throws Exception {
        ModelManager select = modelManagerService.select();
        nodeOperateService.start(select, module, nodeId);
        ModelNode modelNode = moduleNodeService.nodeInfo(nodeId);

        checkModuleStatus.checkNode(modelNode, node -> {
            Long updateTime = modelNode.getUpdateTime();
            ModelNode currentNode = moduleNodeService.nodeInfo(nodeId);
            if (StringUtils.equals(currentNode.getNodeStatus(), ModuleStatusConstants.startFaild.value())) {
                //执行ModelOperateStrategy方法异常 状态已经改变了 那么结束检测任务
                moduleNodeService.clearCache();
                checkModuleStatus.sendMessage(currentNode, WsTopicConstants.HOST_NODE_START);
                return true;
            }
            if (checkModuleStatus.isTimeOut(updateTime, node_start_time_out)) {
                moduleNodeService.clearCache();
                currentNode.setNodeStatus(ModuleStatusConstants.startTimeOut.value());
                try {
                    moduleNodeService.addOrUpdateModelNode(currentNode);
                    //nodeOperateService.stop(select, module, nodeId);//启动超时 立刻停止
                    nodeStop(module, nodeId);
                } catch (Exception e) {
                    LogUtil.error(e.getMessage(), e);
                }
                checkModuleStatus.sendMessage(currentNode, WsTopicConstants.HOST_NODE_START);
                return true;
            }
            if (checkModuleStatus.is_node_available(currentNode)) {
                moduleNodeService.clearCache();
                currentNode.setNodeStatus(ModuleStatusConstants.running.value());
                try {
                    moduleNodeService.addOrUpdateModelNode(currentNode);
                } catch (Exception e) {
                    LogUtil.error(e.getMessage(), e);
                }
                checkModuleStatus.sendMessage(currentNode, WsTopicConstants.HOST_NODE_START);
                return true;
            }
            return false;
        }, 15000L);
        //执行停止操作后 每5s执行一次检测 30s后销毁定时器
        //dynamicTaskJob.addTaskWithTime(() -> checkModuleStatus.checkSingleNode(moduleNodeService.nodeInfo(nodeId)), "0/5 * * * * ? ", 30000L, 15000L);
    }


    @PostMapping("/node/stop")
    public void nodeStop(String module, String nodeId) throws Exception {
        ModelManager select = modelManagerService.select();
        nodeOperateService.stop(select, module, nodeId);
        ModelNode modelNode = moduleNodeService.nodeInfo(nodeId);

        checkModuleStatus.checkNode(modelNode, node -> {
            Long updateTime = modelNode.getUpdateTime();
            ModelNode currentNode = moduleNodeService.nodeInfo(nodeId);
            if (StringUtils.equals(currentNode.getNodeStatus(), ModuleStatusConstants.stopFaild.value())) {
                //执行ModelOperateStrategy方法异常 状态已经改变了 那么结束检测任务
                moduleNodeService.clearCache();
                checkModuleStatus.sendMessage(currentNode, WsTopicConstants.HOST_NODE_STOP);
                return true;
            }
            if (checkModuleStatus.isTimeOut(updateTime, node_stop_time_out)) {
                moduleNodeService.clearCache();
                currentNode.setNodeStatus(ModuleStatusConstants.stopTimeOut.value());
                try {
                    moduleNodeService.addOrUpdateModelNode(currentNode);
                    //nodeOperateService.start(select, module, nodeId);//停止超时 重新启动
                    nodeStop(module, nodeId);
                } catch (Exception e) {
                    LogUtil.error(e.getMessage(), e);
                }
                checkModuleStatus.sendMessage(currentNode, WsTopicConstants.HOST_NODE_STOP);
                return true;
            }
            if (!checkModuleStatus.is_node_available(currentNode)) {
                moduleNodeService.clearCache();
                currentNode.setNodeStatus(ModuleStatusConstants.stopped.value());
                try {
                    moduleNodeService.addOrUpdateModelNode(currentNode);
                } catch (Exception e) {
                    LogUtil.error(e.getMessage(), e);
                }
                checkModuleStatus.sendMessage(currentNode, WsTopicConstants.HOST_NODE_STOP);
                return true;
            }
            return false;
        }, 10000L);
        //执行停止操作后 每5s执行一次检测 30s后销毁定时器
        //dynamicTaskJob.addTaskWithTime(() -> checkModuleStatus.checkSingleNode(modelNode), "0/5 * * * * ? ", 30000L, 5000L);
    }

}
