package com.fit2cloud.mc.strategy.service.imp;

import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.strategy.template.AbstractModelOpTemplate;
import org.springframework.stereotype.Service;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/10 4:28 下午
 * @Description: Please Write notes scientifically
 * k8s环境基于API操作模块
 */
@Service("k8s-model")
public class K8sModelOpTemplate extends AbstractModelOpTemplate {

    @Override
    protected void executeInstall(ModelInstalledDto modelInstalledDto, String filePath) {

    }

    @Override
    protected void executeStart(String modeule) {

    }

    @Override
    protected void executeStop(String modeule) {

    }

    @Override
    protected void executeDelete(String modeule) {

    }
}
