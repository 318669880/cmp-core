package com.fit2cloud.mc.strategy.service.imp;

import com.fit2cloud.commons.server.constants.RedisConstants;
import com.fit2cloud.commons.server.redis.subscribe.RedisMessagePublisher;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.dao.ModelBasicMapper;
import com.fit2cloud.mc.dao.ModelVersionMapper;
import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.model.ModelBasic;
import com.fit2cloud.mc.model.ModelBasicExample;
import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.model.ModelVersion;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.strategy.entity.ModuleMessageInfo;
import com.fit2cloud.mc.strategy.entity.ResultInfo;
import com.fit2cloud.mc.strategy.service.ModelOperateService;
import com.fit2cloud.mc.strategy.service.NetFileService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/10 9:57 上午
 * @Description: Please Write notes scientifically
 */

@Service
public class ModelOpServiceImp implements ModelOperateService{

    @Resource
    private NetFileService netFileService;

    @Resource
    private ModelBasicMapper modelBasicMapper;

    @Resource
    private ModelVersionMapper modelVersionMapper;


    @Resource
    private ModelManagerService modelManagerService;

    @Resource
    private RedisMessagePublisher redisMessagePublisher;

    @Transactional
    @Override
    public void installOrUpdate(ModelManager managerInfo, ModelInstalledDto modelInstalledDto) throws Exception{
        ModelBasic modelBasic = modelInstalledDto.getModelBasic();
        ModelVersion modelVersion = modelInstalledDto.getModelVersion();
        ModelBasicExample example = new ModelBasicExample();
        example.createCriteria().andModuleEqualTo(modelBasic.getModule());
        List<ModelBasic> modelBasics = modelBasicMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(modelBasics)){
            ModelBasic temp = modelBasics.get(0);
            modelBasic.setModelUuid(temp.getModelUuid());
            modelBasic.setCurrentStatus("updating");//设置状态为更新中。。。
            modelBasicMapper.updateByPrimaryKey(modelBasic);
        }else{
            modelBasic.setModelUuid(UUIDUtil.newUUID());
            modelBasic.setCurrentStatus("installing");//设置状态为安装中。。。
            modelBasicMapper.insert(modelBasic);
        }
        modelVersion.setModelBasicUuid(modelBasic.getModelUuid());
        modelVersion.setModelVersionUuid(UUIDUtil.newUUID());
        modelVersion.setInstallTime(new Date().getTime());
        modelVersionMapper.insert(modelVersion);
        String filePath = downLoad(modelInstalledDto);

        modelInstalledDto.getModelVersion().setDownloadUrl(filePath);
        ModuleMessageInfo moduleMessageInfo = new ModuleMessageInfo(managerInfo,"executeInstall",new Object[]{managerInfo,filePath});
        //通过广播的方式 让每一个集群节点都执行安装
        redisMessagePublisher.publish(RedisConstants.Topic.MODULE_OPERATE,moduleMessageInfo);
        //executeInstall(managerInfo, modelInstalledDto, filePath);
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
        modelManagerService.updateCurrentStatus(module,"starting");
        ModuleMessageInfo moduleMessageInfo = new ModuleMessageInfo(managerInfo,"executeStart",new Object[]{module});
        redisMessagePublisher.publish(RedisConstants.Topic.MODULE_OPERATE,moduleMessageInfo);
        //executeStart(module);
    }


    @Transactional
    @Override
    public void stop(ModelManager managerInfo, String module) throws Exception {
        modelManagerService.updateCurrentStatus(module,"stopping");
        ModuleMessageInfo moduleMessageInfo = new ModuleMessageInfo(managerInfo,"executeStop",new Object[]{module});
        redisMessagePublisher.publish(RedisConstants.Topic.MODULE_OPERATE,moduleMessageInfo);
        //executeStop(module);
    }


    @Override
    public void unInstall(ModelManager managerInfo, String module) throws Exception {
        modelManagerService.updateCurrentStatus(module,"unInstalling");
        ModuleMessageInfo moduleMessageInfo = new ModuleMessageInfo(managerInfo,"executeDelete",new Object[]{module});
        redisMessagePublisher.publish(RedisConstants.Topic.MODULE_OPERATE,moduleMessageInfo);
        //executeDelete(module);
    }





}
