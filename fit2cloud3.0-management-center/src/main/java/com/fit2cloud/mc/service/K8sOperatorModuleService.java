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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
public class K8sOperatorModuleService {
    @Resource
    private ModelManagerService modelManagerService;
    @Resource
    private NetFileService netFileService;


    public void start(ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest){
        operatorModuleRequest.getModules().forEach(module -> {
            try{
                String filePath = downLoad(module);
                ModelOperateStrategy operateStrategy = NodeOperateStrategyFactory.build(managerInfo.getEnv());
                operateStrategy.executeInstall(managerInfo, module, filePath, new HashMap<>());
            }catch (Exception e){
                LogUtil.error("Faild to start module: " + module);
            }

        });

    }

    public void stop(ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest){
        operatorModuleRequest.getModules().forEach(module -> {
            try{
                ModelOperateStrategy operateStrategy = NodeOperateStrategyFactory.build(managerInfo.getEnv());
                operateStrategy.executeStop(module);
            }catch (Exception e){
                LogUtil.error("Faild to stop module: " + module);
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
}
