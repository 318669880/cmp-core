package com.fit2cloud.mc.service;

import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.dao.*;
import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.model.*;
import com.fit2cloud.mc.strategy.factory.ModelOperateStrategyFactory;
import com.fit2cloud.mc.utils.ModuleUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.env.Environment;
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
    private ModelBasicPageMapper modelBasicPageMapper;


    @Resource
    private Environment environment;


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


    /**
     * 新增或修改模块节点状态
     * @param module 模块
     * @param node_status 状态
     */
    public void addOrUpdateModelNode (String module,String node_status) throws Exception{
        Optional.ofNullable(modelBasicInfo(module)).ifPresent(model -> {
            String hostName = environment.getProperty("HOST_HOSTNAME");
            ModelNodeExample modelNodeExample = new ModelNodeExample();
            modelNodeExample.createCriteria().andModelBasicUuidEqualTo(model.getModelUuid()).andNodeHostEqualTo(hostName);
            List<ModelNode> modelNodes = modelNodeMapper.selectByExample(modelNodeExample);
            if(CollectionUtils.isNotEmpty(modelNodes)){
                ModelNode temp =modelNodes.get(0);
                temp.setNodeStatus(node_status);
                modelNodeMapper.updateByPrimaryKey(temp);
            }else{
                ModelNode modelNode = new ModelNode();
                modelNode.setNodeHost(hostName);
                modelNode.setModelNodeUuid(UUIDUtil.newUUID());
                modelNode.setIsMc(false);
                modelNode.setModelBasicUuid(model.getModelUuid());
                modelNode.setNodeStatus(node_status);
                modelNode.setNodeCreateTime(new Date().getTime());
                modelNodeMapper.insert(modelNode);
            }
        });
    }
    public List<ModelNode> queryNodes(String model_uuid){
        ModelNodeExample modelNodeExample = new ModelNodeExample();
        ModelNodeExample.Criteria criteria = modelNodeExample.createCriteria();
        Optional.ofNullable(model_uuid).ifPresent(uuid -> {
            criteria.andModelBasicUuidEqualTo(model_uuid);
        });
        criteria.andIsMcEqualTo(false);
        modelNodeExample.setOrderByClause("node_create_time desc");
        return modelNodeMapper.selectByExample(modelNodeExample);
    }



    @Resource
    private ModelNodeMapper modelNodeMapper;

    /**
     * 根据各个模块节点的状态设置模块状态
     * 只要有一个节点操作是成功的 则认为 模块是成功的
     * @param module
     */
    public void modelStatu(String module){
        ModelBasic model = modelBasicInfo(module);
        Optional.ofNullable(model).ifPresent(modelBasic -> {
            List<ModelNode> modelNodes = queryNodes(modelBasic.getModelUuid());
            final Map<String,String> statusMap = new HashMap<>();
            modelNodes.stream().anyMatch(node -> {
                statusMap.put("key",node.getNodeStatus());
                return !node.getNodeStatus().endsWith("Faild");
            });
            String status = statusMap.get("key");
            if(ObjectUtils.isNotEmpty(modelBasic)){
                modelBasic.setCurrentStatus(status);
                modelBasicMapper.updateByPrimaryKey(modelBasic);
            }
        });
    }

}
