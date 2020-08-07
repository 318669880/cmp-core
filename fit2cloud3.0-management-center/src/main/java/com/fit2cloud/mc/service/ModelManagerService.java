package com.fit2cloud.mc.service;

import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.dao.ModelBasicMapper;
import com.fit2cloud.mc.dao.ModelManagerMapper;
import com.fit2cloud.mc.dao.ModelVersionMapper;
import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
    public List<ModelBasic> modelBasicSelect() {
        ModelBasicExample example = new ModelBasicExample();
        example.createCriteria().andModelUuidIsNotNull();
        List<ModelBasic> modelBasics = modelBasicMapper.selectByExample(example);
        return modelBasics;
    }

    // 获取版本信息
    public ModelVersion modelVersionByBasic(String model_basic_uuid,String revision) {
        ModelVersionExample example = new ModelVersionExample();
        example.createCriteria().andModelBasicUuidEqualTo(model_basic_uuid).andRevisionEqualTo(revision);
        List<ModelVersion> modelVersions = modelVersionMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(modelVersions)){
            throw new RuntimeException("为查询到符合条件的版本信息");
        }
        return modelVersions.get(0);
    }


    public void addInstaller(ModelInstalledDto modelInstalledDto) {
        ModelBasic modelBasic = modelInstalledDto.getModelBasic();
        ModelVersion modelVersion = modelInstalledDto.getModelVersion();
        ModelBasicExample example = new ModelBasicExample();
        example.createCriteria().andModuleEqualTo(modelBasic.getModule());
        List<ModelBasic> modelBasics = modelBasicMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(modelBasics)){
            ModelBasic temp = modelBasics.get(0);
            modelBasic.setModelUuid(temp.getModelUuid());
            modelBasicMapper.updateByPrimaryKey(modelBasic);
        }else{
            modelBasic.setModelUuid(UUIDUtil.newUUID());
            modelBasicMapper.insert(modelBasic);
        }
        modelVersion.setModelBasicUuid(modelBasic.getModelUuid());
        modelVersion.setModelVersionUuid(UUIDUtil.newUUID());
        modelVersionMapper.insert(modelVersion);
    }

    public void deleteInstaller(String model_basic_uuid) {
        ModelVersionExample example = new ModelVersionExample();
        example.createCriteria().andModelBasicUuidEqualTo(model_basic_uuid);
        modelVersionMapper.deleteByExample(example);
        modelBasicMapper.deleteByPrimaryKey(model_basic_uuid);
    }











}
