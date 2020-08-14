package com.fit2cloud.mc.strategy.service;

import com.fit2cloud.mc.model.ModelManager;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/11 6:11 下午
 * @Description: Please Write notes scientifically
 */
public interface ModelOperateStrategy {

    public void executeInstall(ModelManager modelManager, String modeule, String filePath) throws Exception;

    public void executeDelete(String modeule);

    public void executeStart(String modeule);

    public void executeStop(String modeule);
}
