package com.fit2cloud.mc.strategy.service.imp;

import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.model.ModelBasic;
import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.model.ModelNode;
import com.fit2cloud.mc.model.ModelVersion;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.service.ModuleNodeService;
import com.fit2cloud.mc.strategy.entity.ResultInfo;
import com.fit2cloud.mc.strategy.factory.NodeOperateStrategyFactory;
import com.fit2cloud.mc.strategy.service.ModelOperateStrategy;
import com.fit2cloud.mc.strategy.service.NodeOperateService;
import com.fit2cloud.mc.strategy.service.NetFileService;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/10 9:57 上午
 * @Description: Please Write notes scientifically
 */

@Service
public class NodeOpServiceImp implements NodeOperateService {

    @Resource
    private NetFileService netFileService;

    @Resource
    @Lazy
    private ModelManagerService modelManagerService;

    @Resource
    private ModuleNodeService moduleNodeService;

    @Async
    @Transactional
    @Override
    public void installOrUpdate(ModelManager managerInfo, String module, String nodeId) throws Exception {
        try{
            ModelBasic modelBasic = modelManagerService.modelBasicInfo(module);
            String filePath = downLoad(modelBasic);
            ModelOperateStrategy operateStrategy = NodeOperateStrategyFactory.build(managerInfo.getEnv());
            Map<String, Object> params = new HashMap<>();
            if(managerInfo.getEnv().equalsIgnoreCase("k8s")){
                params.put("pod_number", modelBasic.getPodNum());
            }
            operateStrategy.executeInstall(managerInfo, module, filePath, params);
        }catch (Exception e){
            changestatus(nodeId,ModuleStatusConstants.installFaild.value());
        }
    }

    @Async
    @Transactional
    @Override
    public void start(ModelManager managerInfo, String module, String nodeId) throws Exception {
        ModelOperateStrategy operateStrategy = NodeOperateStrategyFactory.build(managerInfo.getEnv());
        try{
            operateStrategy.executeStart(module);
        }catch (Exception e){
            changestatus(nodeId,ModuleStatusConstants.startFaild.value());
        }
    }

    @Async
    @Transactional
    @Override
    public void stop(ModelManager managerInfo, String module ,String nodeId) throws Exception {
        ModelOperateStrategy operateStrategy = NodeOperateStrategyFactory.build(managerInfo.getEnv());
        try{
            operateStrategy.executeStop(module);
        }catch (Exception e){
            changestatus(nodeId,ModuleStatusConstants.stopFaild.value());
        }
    }


    @Override
    public void unInstall(ModelManager managerInfo, String module) throws Exception {
        ModelOperateStrategy operateStrategy = NodeOperateStrategyFactory.build(managerInfo.getEnv());
        operateStrategy.executeDelete(module);
    }

    private void changestatus(String nodeId,String status) throws Exception{
        if (ObjectUtils.isEmpty(nodeId)) return ;
        ModelNode modelNode = new ModelNode();
        modelNode.setNodeStatus(status);
        modelNode.setModelNodeUuid(nodeId);
        moduleNodeService.addOrUpdateModelNode(modelNode);
    }

    private String downLoad (ModelBasic modelBasic) throws Exception{
        ModelVersion modelVersion = modelManagerService.modelVersionInfo(modelBasic.getModelUuid(), modelBasic.getLastRevision());
        String downloadUrl = modelVersion.getDownloadUrl();
        ResultInfo<String> resultInfo = netFileService.down(downloadUrl);
        return resultInfo.getData();
    }

}
