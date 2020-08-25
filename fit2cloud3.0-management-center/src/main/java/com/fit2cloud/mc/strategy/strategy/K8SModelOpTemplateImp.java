package com.fit2cloud.mc.strategy.strategy;

import com.fit2cloud.commons.server.dcslock.annotation.DcsLock;
import com.fit2cloud.mc.model.ModelBasic;
import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.strategy.service.ModelOperateStrategy;
import com.fit2cloud.mc.utils.HelmUtil;
import com.fit2cloud.mc.utils.ModuleUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@Service("k8s")
public class K8SModelOpTemplateImp implements ModelOperateStrategy {

    @Resource
    private ModelManagerService modelManagerService;

    @DcsLock
    @Override
    public void executeInstall(ModelManager modelManager, String module, String filePath, Map<String, Object> params)throws Exception {
        if(ObjectUtils.isEmpty(params) || !params.containsKey("pod_number")){
            ModelBasic modelBasic = modelManagerService.modelBasicInfo(module);
            Integer podNum = modelBasic.getPodNum();
            params = new HashMap<>();
            params.put("pod_number",podNum);
        }
        HelmUtil.installOrUpdateModule(module, filePath, modelManager.getOnLine(), params);
    }

    @DcsLock
    @Override
    public void executeStart(String modeule) {

    }

    @DcsLock
    @Override
    public void executeStop(String modeule)throws Exception {
        HelmUtil.stopService(modeule);
    }

    @DcsLock
    @Override
    public void executeDelete(String modeule) {

    }



}
