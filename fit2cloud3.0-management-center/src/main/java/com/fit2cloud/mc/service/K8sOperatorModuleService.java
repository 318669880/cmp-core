package com.fit2cloud.mc.service;

import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.mc.dto.ModuleParamData;
import com.fit2cloud.mc.dto.request.OperatorModuleRequest;
import com.fit2cloud.mc.model.ModelBasic;
import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.model.ModelVersion;
import com.fit2cloud.mc.strategy.entity.ResultInfo;
import com.fit2cloud.mc.strategy.service.NetFileService;
import com.fit2cloud.mc.utils.K8sUtil;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class K8sOperatorModuleService {
    @Resource
    private ModelManagerService modelManagerService;
    @Resource
    private NetFileService netFileService;


    public void start(ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest){
        operatorModuleRequest.getModules().forEach(module -> {
            try{
                ModelBasic modelBasic = modelManagerService.modelBasicInfo(module);
                K8sUtil.startService(module, new Gson().fromJson(modelBasic.getCustomData(), ModuleParamData.class) , operatorModuleRequest.getParams());

            }catch (Exception e){
                LogUtil.error("Faild to start module: " + module, e);
            }
        });
    }

    public void stop(ModelManager managerInfo, OperatorModuleRequest operatorModuleRequest){
        operatorModuleRequest.getModules().forEach(module -> {
            try{
                ModelBasic modelBasic = modelManagerService.modelBasicInfo(module);
                K8sUtil.stopService(module, new Gson().fromJson(modelBasic.getCustomData(), ModuleParamData.class));
            }catch (Exception e){
                LogUtil.error("Faild to stop module: " + module, e);
            }

        });

    }

    private String downLoad (ModelBasic modelBasic ) throws Exception{
        ModelVersion modelVersion = modelManagerService.modelVersionInfo(modelBasic.getModelUuid(), modelBasic.getLastRevision());
        String downloadUrl = modelVersion.getDownloadUrl();
        ResultInfo<String> resultInfo = netFileService.down(downloadUrl);
        return resultInfo.getData();
    }
}
