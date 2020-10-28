package com.fit2cloud.mc.strategy.strategy;

import com.fit2cloud.commons.server.dcslock.annotation.DcsLock;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.strategy.service.ModelOperateStrategy;
import com.fit2cloud.mc.utils.ModuleUtil;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/10 2:20 下午
 * @Description: Please Write notes scientifically
 * 单机docker-compose操作模块
 */
@Service("host")
public class HostModelOpTemplateImp implements ModelOperateStrategy {

    @Override
    public String executeInstall(ModelManager modelManager, String module, String filePath, Map<String, Object> params) throws Exception{
        try{
            return ModuleUtil.installOrUpdateModule(module, filePath, modelManager.getOnLine());
        }catch (Exception e){
            LogUtil.error("Failed to install module: " + module, e);
            throw e;
        }
    }

    // 集群环境避免各节点同时执行
    // 锁住该方法1分钟 避免同时启动导致报错
    @DcsLock(overtime = 60000,waitime = 5000)
    @Override
    public void executeStart(String modeule) {
        try {
            ModuleUtil.startService(modeule);
            // 执行命令后等待50s 因为模块启动是一个过程
            Thread.sleep(50000);
        } catch (Exception e) {
            LogUtil.error("Failed to start module: " + modeule, e);
            F2CException.throwException(e);
        }
    }

    @Override
    public void executeStop(String modeule) {
        try {
            ModuleUtil.stopService(modeule);
        } catch (Exception e) {
            LogUtil.error("Failed to stop module: " + modeule, e);
            F2CException.throwException(e);
        }
    }

    @Override
    public void executeDelete(String modeule) {
        try {
            ModuleUtil.deleteService(modeule);
        } catch (Exception e) {
            LogUtil.error("Failed to delete module: " + modeule, e);
            F2CException.throwException(e);
        }
    }

}
