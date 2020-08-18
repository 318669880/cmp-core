package com.fit2cloud.mc.service;

import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.dao.*;
import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.job.SyncEurekaServer;
import com.fit2cloud.mc.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

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
    private ModelBasicPageMapper modelBasicPageMapper;



    @Resource
    private ModelVersionMapper modelVersionMapper;


    @Resource
    private Environment environment;

    @Value("${spring.cloud.client.ipaddress}")
    private String client_ip;


    public void add(ModelManager modelManager) {
        ModelManagerExample modelManagerExample = new ModelManagerExample();
        modelManagerExample.createCriteria().andModelAddressIsNotNull();
        modelManagerMapper.deleteByExample(modelManagerExample);

        modelManager.setEnv(SyncEurekaServer.IS_KUBERNETES? "k8s" : "host");
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

    public ModelVersion modelVersionInfo(String model_uuid,String lastVersion){
        ModelVersionExample example = new ModelVersionExample();
        example.createCriteria().andModelBasicUuidEqualTo(model_uuid).andRevisionEqualTo(lastVersion);
        List<ModelVersion> modelVersions = modelVersionMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(modelVersions)) {
            return  modelVersions.get(0);
        }
        return null;
    }


    public String domain_host(){
        String port = environment.getProperty("local.server.port");
        return "http://"+client_ip+":"+port;
    }

    public String prefix(String pre,String value ){
        if(value.indexOf(pre) == -1){
            value = (pre.endsWith("/")? pre : (pre+"/")) + value;
        }
        return value;
    }

    /**
     * 模块安装
     * @param modelInstalledDto
     */
    public void installModule(ModelInstalledDto modelInstalledDto){
        ModelBasic modelBasic = modelInstalledDto.getModelBasic();
        String module = modelBasic.getModule();
        ModelVersion modelVersion = modelInstalledDto.getModelVersion();
        ModelBasicExample example = new ModelBasicExample();
        example.createCriteria().andModuleEqualTo(modelBasic.getModule());
        List<ModelBasic> modelBasics = modelBasicMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(modelBasics)){
            ModelBasic temp = modelBasics.get(0);
            modelBasic.setModelUuid(temp.getModelUuid());
            //modelBasic.setCurrentStatus("updating");//设置状态为更新中。。。
            modelBasicMapper.updateByPrimaryKey(modelBasic);
        }else{
            modelBasic.setModelUuid(UUIDUtil.newUUID());
            //modelBasic.setCurrentStatus("installing");//设置状态为安装中。。。
            modelBasicMapper.insert(modelBasic);
        }
        modelVersion.setModelBasicUuid(modelBasic.getModelUuid());
        modelVersion.setModelVersionUuid(UUIDUtil.newUUID());
        modelVersion.setInstallTime(new Date().getTime());
        modelVersionMapper.insert(modelVersion);
    }



}
