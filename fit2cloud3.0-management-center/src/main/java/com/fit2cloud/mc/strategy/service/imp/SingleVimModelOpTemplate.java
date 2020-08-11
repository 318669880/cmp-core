package com.fit2cloud.mc.strategy.service.imp;

import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.strategy.template.AbstractModelOpTemplate;
import com.fit2cloud.mc.utils.ModuleUtil;
import org.springframework.stereotype.Service;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/10 2:20 下午
 * @Description: Please Write notes scientifically
 * 单机docker-compose操作模块
 */
@Service("single-vim")
public class SingleVimModelOpTemplate extends AbstractModelOpTemplate {

    @Override
    protected void executeInstall(ModelManager modelManager, ModelInstalledDto modelInstalledDto, String filePath) {
        try{
            ModuleUtil.installOrUpdateModule(filePath, modelManager.getOnLine());
        }catch (Exception e){
            F2CException.throwException(e);
        }
    }

    @Override
    protected void executeStart(String modeule) {
        try{
            ModuleUtil.startService(modeule);
        }catch (Exception e){
            F2CException.throwException(e);
        }
    }

    @Override
    protected void executeStop(String modeule) {
        try{
            ModuleUtil.stopService(modeule);
        }catch (Exception e){
            F2CException.throwException(e);
        }
    }

    @Override
    protected void executeDelete(String modeule) {

    }
}
