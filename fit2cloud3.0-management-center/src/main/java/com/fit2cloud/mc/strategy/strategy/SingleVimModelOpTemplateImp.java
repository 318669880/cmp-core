package com.fit2cloud.mc.strategy.strategy;

import com.fit2cloud.commons.server.dcslock.annotation.DcsLock;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.strategy.service.ModelOperateStrategy;
import com.fit2cloud.mc.utils.ModuleUtil;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/10 2:20 下午
 * @Description: Please Write notes scientifically
 * 单机docker-compose操作模块
 */
@Service("host")
public class SingleVimModelOpTemplateImp implements ModelOperateStrategy {

    // 集群环境避免各节点同时执行
    @DcsLock
    @Override
    public void executeInstall(ModelManager modelManager, String module, String filePath) throws Exception{
        try{
            ModuleUtil.installOrUpdateModule(filePath, modelManager.getOnLine());
        }catch (Exception e){
            F2CException.throwException(e);
        }

    }

    @DcsLock
    @Override
    public void executeStart(String modeule) {
        try {
            ModuleUtil.startService(modeule);
        } catch (Exception e) {
            F2CException.throwException(e);
        }
    }

    @DcsLock
    @Override
    public void executeStop(String modeule) {
        try {
            ModuleUtil.stopService(modeule);
        } catch (Exception e) {
            F2CException.throwException(e);
        }
    }

    @DcsLock
    @Override
    public void executeDelete(String modeule) {

    }
}
