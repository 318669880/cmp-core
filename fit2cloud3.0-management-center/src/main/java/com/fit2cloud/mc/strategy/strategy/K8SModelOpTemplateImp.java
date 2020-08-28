package com.fit2cloud.mc.strategy.strategy;

import com.fit2cloud.commons.server.dcslock.annotation.DcsLock;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.mc.dto.ModuleParamData;
import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.strategy.service.ModelOperateStrategy;
import com.fit2cloud.mc.utils.K8sUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


@Service("k8s")
public class K8SModelOpTemplateImp implements ModelOperateStrategy {

    @Resource
    private ModelManagerService modelManagerService;

    @DcsLock
    @Override
    public void executeInstall(ModelManager modelManager, String module, String filePath, Map<String, Object> params)throws Exception {
        if(ObjectUtils.isEmpty(params) || !params.containsKey("pod_number")){
            F2CException.throwException("Pod number can not be empty! ");
        }
        Integer podNum = Integer.valueOf(params.get("pod_number").toString());
        modelManagerService.updateModelBasicPodNum(module, podNum);
        ModuleParamData moduleParamData = K8sUtil.installOrUpdateModule(module, filePath, modelManager, params);
        modelManagerService.updateModelBasicCustomData(module, moduleParamData);
    }

    @DcsLock
    @Override
    public void executeStart(String modeule) {

    }

    @DcsLock
    @Override
    public void executeStop(String modeule)throws Exception {
    }

    @DcsLock
    @Override
    public void executeDelete(String modeule) {

    }



}
