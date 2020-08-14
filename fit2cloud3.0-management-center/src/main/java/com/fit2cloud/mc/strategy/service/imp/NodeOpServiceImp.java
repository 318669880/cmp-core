package com.fit2cloud.mc.strategy.service.imp;

import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.model.ModelBasic;
import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.model.ModelVersion;
import com.fit2cloud.mc.service.ModuleNodeService;
import com.fit2cloud.mc.strategy.entity.ResultInfo;
import com.fit2cloud.mc.strategy.factory.NodeOperateStrategyFactory;
import com.fit2cloud.mc.strategy.service.ModelOperateStrategy;
import com.fit2cloud.mc.strategy.service.NodeOperateService;
import com.fit2cloud.mc.strategy.service.NetFileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/10 9:57 上午
 * @Description: Please Write notes scientifically
 */

@Service
public class NodeOpServiceImp implements NodeOperateService {

    @Resource
    private NetFileService netFileService;


    @Transactional
    @Override
    public void installOrUpdate(ModelManager managerInfo, ModelInstalledDto modelInstalledDto) throws Exception{
        ModelBasic modelBasic = modelInstalledDto.getModelBasic();
        String filePath = downLoad(modelInstalledDto);
        ModelOperateStrategy operateStrategy = NodeOperateStrategyFactory.build(managerInfo.getEnv());
        operateStrategy.executeInstall(managerInfo,modelBasic.getModule(),filePath);

    }
    private String downLoad (ModelInstalledDto modelInstalledDto) throws Exception{
        ModelVersion modelVersion = modelInstalledDto.getModelVersion();
        String downloadUrl = modelVersion.getDownloadUrl();
        ResultInfo<String> resultInfo = netFileService.down(downloadUrl);
        return resultInfo.getData();
    }



    @Transactional
    @Override
    public void start(ModelManager managerInfo, String module) throws Exception {
        ModelOperateStrategy operateStrategy = NodeOperateStrategyFactory.build(managerInfo.getEnv());
        operateStrategy.executeStart(module);
    }


    @Transactional
    @Override
    public void stop(ModelManager managerInfo, String module) throws Exception {

        ModelOperateStrategy operateStrategy = NodeOperateStrategyFactory.build(managerInfo.getEnv());
        operateStrategy.executeStop(module);

    }


    @Override
    public void unInstall(ModelManager managerInfo, String module) throws Exception {

        ModelOperateStrategy operateStrategy = NodeOperateStrategyFactory.build(managerInfo.getEnv());
        operateStrategy.executeDelete(module);

    }





}
