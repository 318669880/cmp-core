package com.fit2cloud.mc.service;

import com.fit2cloud.commons.utils.CommonBeanFactory;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.common.constants.RuntimeEnvironment;
import com.fit2cloud.mc.config.DockerRegistry;
import com.fit2cloud.mc.dao.*;
import com.fit2cloud.mc.dto.ModuleParamData;
import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.job.SyncEurekaServer;
import com.fit2cloud.mc.model.*;
import com.fit2cloud.mc.strategy.service.NodeOperateService;
import com.fit2cloud.mc.strategy.task.EurekaInstanceMonitor;
import com.fit2cloud.mc.utils.K8sUtil;
import com.google.gson.Gson;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
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

    @Value("${modelAddress:https://f2c-index.oss-cn-beijing.aliyuncs.com}")
    private String modelAddress;

    @Resource
    private ModelManagerMapper modelManagerMapper;

    @Resource
    private ModelBasicMapper modelBasicMapper;

    @Resource
    private ModelBasicPageMapper modelBasicPageMapper;

    @Resource
    private ModelVersionMapper modelVersionMapper;

    @Lazy
    @Resource
    private EurekaInstanceMonitor eurekaInstanceMonitor;

    @Resource
    private NodeOperateService nodeOperateService;

    @CacheEvict(value = "model-manager-info",allEntries = true)
    public ModelManager add(ModelManager modelManager) {
        if(SyncEurekaServer.IS_KUBERNETES && !modelManager.getOnLine()){
            K8sUtil.createOrReplaceSeccet(new Gson().fromJson(modelManager.getDockerRegistry(), DockerRegistry.class));
        }
        ModelManagerExample modelManagerExample = new ModelManagerExample();
        modelManagerMapper.deleteByExample(modelManagerExample);
        modelManager.setEnv(SyncEurekaServer.IS_KUBERNETES? RuntimeEnvironment.K8S : RuntimeEnvironment.HOST);
        modelManager.setUuid(UUIDUtil.newUUID());
        modelManagerMapper.insert(modelManager);
        return modelManager;
    }

    //  运行环境 一旦部署 一般不会发生改变 但查询的频率还是蛮高的 加入redis
    @Cacheable(value = "model-manager-info")
    public ModelManager select() {
        ModelManagerExample modelManagerExample = new ModelManagerExample();
        modelManagerExample.createCriteria().andModelAddressIsNotNull();
        List<ModelManager> modelManagerList = modelManagerMapper.selectByExampleWithBLOBs(modelManagerExample);
        return CollectionUtils.isEmpty(modelManagerList) ? null : modelManagerList.get(0);
    }

    public ModelManager queryModelManager(){
        ModelManagerService proxy = CommonBeanFactory.getBean(ModelManagerService.class);
        ModelManager modelManager = proxy.select();
        if(modelManager == null){
            modelManager = new ModelManager();
            if (SyncEurekaServer.IS_KUBERNETES) {
                modelManager.setEnv(RuntimeEnvironment.K8S);
            } else {
                modelManager.setEnv(RuntimeEnvironment.HOST);
            }
            //modelManager.setModelAddress(modelAddress);
            modelManager.setOnLine(true);
        }
        return modelManager;
    }

    public String address(){
        return modelAddress;
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
        List<ModelBasic> modelBasics = modelBasicMapper.selectByExampleWithBLOBs(example);
        if(CollectionUtils.isNotEmpty(modelBasics)){
            ModelBasic modelBasic = modelBasics.get(0);
            return modelBasic;
        }
        return null;
    }

    public void deleteModelBasic(String module){
        ModelBasic modelBasic = modelBasicInfo(module);
        if(modelBasic == null){
            return;
        }
        ModelVersionExample modelVersionExample = new ModelVersionExample();
        modelVersionExample.createCriteria().andModelBasicUuidEqualTo(modelBasic.getModelUuid());
        modelVersionMapper.deleteByExample(modelVersionExample);
        ModelBasicExample example = new ModelBasicExample();
        example.createCriteria().andModuleEqualTo(module);
        modelBasicMapper.deleteByExample(example);

    }

    public void updateModelBasicPodNum(String module, Integer podNum){
        ModelBasicExample example = new ModelBasicExample();
        example.createCriteria().andModuleEqualTo(module);
        ModelBasic modelBasic = new ModelBasic();
        modelBasic.setPodNum(podNum);
        modelBasicMapper.updateByExampleSelective(modelBasic, example);
    }

    public void updateModelBasicCustomData(String module, ModuleParamData moduleParamData){
        ModelBasicExample example = new ModelBasicExample();
        example.createCriteria().andModuleEqualTo(module);
        ModelBasic modelBasic = new ModelBasic();
        modelBasic.setCustomData(new Gson().toJson(moduleParamData));
        modelBasicMapper.updateByExampleSelective(modelBasic, example);
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


    public String prefix(String pre,String value ){
        if(value.indexOf(pre) == -1){
            value = (pre.endsWith("/")? pre : (pre+"/")) + value;
        }
        return value;
    }

    /**
     * 模块安装
     * @param modelInstalledDto
     * 删除所有model_baisc_lists中缓存
     */
    @CacheEvict(value = "model_baisc_lists",allEntries = true)
    public void readyInstallModule(ModelInstalledDto modelInstalledDto, ModelNode node, boolean isUpdate) throws Exception{
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
            modelBasicMapper.updateByPrimaryKeySelective(modelBasic);
        }else{
            modelBasic.setModelUuid(UUIDUtil.newUUID());
            //modelBasic.setCurrentStatus("installing");//设置状态为安装中。。。
            modelBasicMapper.insert(modelBasic);
        }
        modelVersion.setModelBasicUuid(modelBasic.getModelUuid());
        modelVersion.setModelVersionUuid(UUIDUtil.newUUID());
        modelVersion.setInstallTime(new Date().getTime());
        modelVersionMapper.insert(modelVersion);
        ModelManagerService proxy = CommonBeanFactory.getBean(ModelManagerService.class);
        ModelManager managerInfo = proxy.select();
        if(managerInfo.getEnv().equals("k8s")){
            nodeOperateService.installOrUpdate(managerInfo, module, null);
            return;
        }
        if (!isUpdate){
            eurekaInstanceMonitor.execute(module, null, "/modelNode/readyInstall", node);
            return;
        }
        eurekaInstanceMonitor.execute(module, null, "/modelNode/readyUpdate", node);
    }




    @Cacheable(value = "model_baisc_lists")
    public List<ModelBasic> modelBasics(){
        ModelBasicExample example = new ModelBasicExample();
        example.createCriteria().andModuleIsNotNull();
        return modelBasicMapper.selectByExample(example);
    }


}
