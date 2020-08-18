package com.fit2cloud.mc.strategy.strategy;

import com.fit2cloud.commons.server.dcslock.annotation.DcsLock;
import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.strategy.service.ModelOperateStrategy;
import com.fit2cloud.mc.utils.HelmUtil;
import com.fit2cloud.mc.utils.ModuleUtil;
import org.springframework.stereotype.Service;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/10 4:28 下午
 * @Description: Please Write notes scientifically
 * k8s环境基于API操作模块
 */
@Service("k8s")
public class K8SModelOpTemplateImp implements ModelOperateStrategy {

    @DcsLock
    @Override
    public void executeInstall(ModelManager modelManager, String module, String filePath)throws Exception {
        HelmUtil.installOrUpdateModule(filePath, modelManager.getOnLine());
    }

    @DcsLock
    @Override
    public void executeStart(String modeule) {

    }

    @DcsLock
    @Override
    public void executeStop(String modeule) {

    }

    @DcsLock
    @Override
    public void executeDelete(String modeule) {

    }
}
