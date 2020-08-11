package com.fit2cloud.mc.service;

import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.mc.dao.ModelBasicMapper;
import com.fit2cloud.mc.dao.ModelBasicPageMapper;
import com.fit2cloud.mc.dao.ModelManagerMapper;
import com.fit2cloud.mc.dao.ModelVersionMapper;
import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.model.*;
import com.fit2cloud.mc.strategy.factory.ModelOperateStrategyFactory;
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

}
