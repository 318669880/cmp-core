package com.fit2cloud.mc.strategy.service;

import com.fit2cloud.mc.model.ModelManager;

import java.util.Map;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/11 6:11 下午
 * @Description: Please Write notes scientifically
 */
public interface ModelOperateStrategy {

    public String executeInstall(ModelManager modelManager, String modeule, String filePath, Map<String, Object> params) throws Exception;

    public void executeDelete(String modeule)throws Exception;

    public void executeStart(String modeule)throws Exception;

    public void executeStop(String modeule)throws Exception;

}
