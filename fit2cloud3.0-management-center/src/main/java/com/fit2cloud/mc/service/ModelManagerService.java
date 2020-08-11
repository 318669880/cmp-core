package com.fit2cloud.mc.service;

import com.fit2cloud.commons.server.dcslock.annotation.DcsLock;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.mc.dao.ModelBasicMapper;
import com.fit2cloud.mc.dao.ModelBasicPageMapper;
import com.fit2cloud.mc.dao.ModelManagerMapper;
import com.fit2cloud.mc.dao.ModelVersionMapper;
import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.model.*;
import com.fit2cloud.mc.strategy.factory.ModelOperateServiceFactory;
import com.fit2cloud.mc.utils.ModuleUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/5 10:51 下午
 * @Description: Please Write notes scientifically
 */

@Service
public class ModelManagerService {

    @Resource
    private ModelManagerMapper modelManagerMapper;


    @Resource
    private ModelBasicMapper modelBasicMapper;

    @Resource
    private ModelVersionMapper modelVersionMapper;

    @Resource
    private ModelBasicPageMapper modelBasicPageMapper;


    public void add(ModelManager modelManager) {
        ModelManagerExample modelManagerExample = new ModelManagerExample();
        modelManagerExample.createCriteria().andModelAddressIsNotNull();
        modelManagerMapper.deleteByExample(modelManagerExample);
        modelManagerMapper.insert(modelManager);
    }

    public ModelManager select() {
        ModelManagerExample modelManagerExample = new ModelManagerExample();
        modelManagerExample.createCriteria().andModelAddressIsNotNull();
        List<ModelManager> modelManagerList = modelManagerMapper.selectByExample(modelManagerExample);
        return CollectionUtils.isEmpty(modelManagerList) ? null : modelManagerList.get(0);
    }


    // 获取已安装模块
    /*public List<ModelBasic> modelBasicSelect() {
        ModelBasicExample example = new ModelBasicExample();
        example.createCriteria().andModelUuidIsNotNull();
        List<ModelBasic> modelBasics = modelBasicMapper.selectByExample(example);
        return modelBasics;
    }*/

    // 获取版本信息
    /*public ModelVersion modelVersionByBasic(String model_basic_uuid,String revision) {
        ModelVersionExample example = new ModelVersionExample();
        example.createCriteria().andModelBasicUuidEqualTo(model_basic_uuid).andRevisionEqualTo(revision);
        List<ModelVersion> modelVersions = modelVersionMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(modelVersions)){
            throw new RuntimeException("为查询到符合条件的版本信息");
        }
        return modelVersions.get(0);
    }*/






    public List<ModelInstall> paging(Map<String, Object> map) {
        return modelBasicPageMapper.select(map);
    }


    public List<ModelInstall> installInfoquery() {
        return modelBasicPageMapper.select(new HashMap<>());
    }


    public ModelBasic modelBasicInfo(String module){
        ModelBasicExample example = new ModelBasicExample();
        example.createCriteria().andModuleEqualTo(module);
        List<ModelBasic> modelBasics = modelBasicMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(modelBasics)){
            ModelBasic modelBasic = modelBasics.get(0);
            return modelBasic;
        }
        return null;
    }

    public void updateCurrentStatus(String module,String status) throws Exception{
        ModelBasic modelBasic = modelBasicInfo(module);
        if(ObjectUtils.isNotEmpty(modelBasic)){
            modelBasic.setCurrentStatus(status);
            modelBasicMapper.updateByPrimaryKey(modelBasic);
            return;
        }
        throw new RuntimeException("模块不存在");
    }



    public void actionModule(String action, String module) throws Exception{
        switch (action){
            case "start":
                ModuleUtil.startService(module);
                break;
            case "stop":
                ModuleUtil.stopService(module);
                break;
            default:
                break;
        }
    }



    @Transactional
    @DcsLock(key = "model_install")
    public void install(ModelManager modelManager, List<ModelInstalledDto> modelInstalledDtos){
        String addr = modelManager.getModelAddress();
        modelInstalledDtos.forEach(modelInstalledDto -> {
            try{
                String url = modelInstalledDto.getModelVersion().getDownloadUrl();
                if(url.indexOf(addr) == -1){
                    url = (addr.endsWith("/")? addr : (addr+"/")) + url;
                    modelInstalledDto.getModelVersion().setDownloadUrl(url);
                }
                ModelOperateServiceFactory.build(modelManager.getEnv()).installOrUpdate(modelManager,modelInstalledDto);
            }catch (Exception e){
                F2CException.throwException(e);
            }
        });
    }





}
