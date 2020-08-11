package com.fit2cloud.mc.strategy.template;

import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.dao.ModelBasicMapper;
import com.fit2cloud.mc.dao.ModelVersionMapper;
import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.model.ModelBasic;
import com.fit2cloud.mc.model.ModelBasicExample;
import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.model.ModelVersion;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.strategy.entity.ResultInfo;
import com.fit2cloud.mc.strategy.service.ModelOperateService;
import com.fit2cloud.mc.strategy.service.NetFileService;
import org.apache.commons.collections.CollectionUtils;
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

public abstract class AbstractModelOpTemplate implements ModelOperateService{

    @Resource
    private NetFileService netFileService;

    @Resource
    private ModelBasicMapper modelBasicMapper;

    @Resource
    private ModelVersionMapper modelVersionMapper;


    @Resource
    private ModelManagerService modelManagerService;

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
        executeInstall(managerInfo, modelInstalledDto, filePath);
    }
    private String downLoad (ModelInstalledDto modelInstalledDto) throws Exception{
        ModelBasic modelBasic = modelInstalledDto.getModelBasic();
        ModelVersion modelVersion = modelInstalledDto.getModelVersion();
        String downloadUrl = modelVersion.getDownloadUrl();
        ResultInfo<String> resultInfo = netFileService.down(downloadUrl);
        return resultInfo.getData();
    }

    protected abstract void executeInstall(ModelManager managerInfo, ModelInstalledDto modelInstalledDto, String filePath) throws Exception;

    @Transactional
    @Override
    public void start(ModelManager managerInfo, String module) throws Exception {
        modelManagerService.updateCurrentStatus(module,"starting");
        executeStart(module);
    }
    protected abstract void executeStart(String modeule);

    @Transactional
    @Override
    public void stop(ModelManager managerInfo, String module) throws Exception {
        modelManagerService.updateCurrentStatus(module,"stopping");
        executeStop(module);
    }
    protected abstract void executeStop(String modeule);

    @Override
    public void unInstall(ModelManager managerInfo, String modeule) throws Exception {
        modelManagerService.updateCurrentStatus(modeule,"unInstalling");
        executeDelete(modeule);
    }
    protected abstract void executeDelete(String modeule);
}
