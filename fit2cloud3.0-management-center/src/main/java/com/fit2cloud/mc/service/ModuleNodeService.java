package com.fit2cloud.mc.service;

import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.dao.ModelBasicMapper;
import com.fit2cloud.mc.dao.ModelBasicPageMapper;
import com.fit2cloud.mc.dao.ModelNodeMapper;
import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.model.*;
import com.fit2cloud.mc.strategy.queue.ModuleDelayTaskManager;
import com.fit2cloud.mc.strategy.service.NodeOperateService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/14 5:46 下午
 * @Description: Please Write notes scientifically
 */
@Service
public class ModuleNodeService {

    @Resource
    private ModelBasicPageMapper modelBasicPageMapper;

    @Resource
    private ModelNodeMapper modelNodeMapper;



    @Resource
    private ModelBasicMapper modelBasicMapper;

    @Resource
    private ModelManagerService modelManagerService;

    @Resource
    private NodeOperateService nodeOperateService;

    /**
     * 根据model_uuid查询节点
     * @param model_uuid
     * @return
     */
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

    /**
     * 某模块节点分页查询
     * @param map
     * @return
     */
    public List<ModelNode> nodePage(Map<String, Object> map) {
        return modelBasicPageMapper.nodePage(map);
    }

    /**
     * 新增或修改模块节点状态
     * @param module 模块
     * @param node_status 状态
     */
    public void addOrUpdateModelNode (String module,String node_status) throws Exception{
        Optional.ofNullable(modelManagerService.modelBasicInfo(module)).ifPresent(model -> {
            String hostName = modelManagerService.domain_host();
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

    /**
     * 根据各个模块节点的状态设置模块状态
     * 只要有一个节点操作是成功的 则认为 模块是成功的
     * @param module
     */
    public void modelStatu(String module){
        ModelBasic model = modelManagerService.modelBasicInfo(module);
        Optional.ofNullable(model).ifPresent(modelBasic -> {
            List<ModelNode> modelNodes = queryNodes(modelBasic.getModelUuid());
            final Map<String,String> statusMap = new HashMap<>();
            modelNodes.stream().anyMatch(node -> {
                statusMap.put("key",node.getNodeStatus());
                return !node.getNodeStatus().endsWith("Faild");
            });
            if(modelNodes.stream().anyMatch(node -> node.getNodeStatus().equals(ModuleStatusConstants.running.value()))){
                statusMap.put("key",ModuleStatusConstants.running.value());
            }
            String status = statusMap.get("key");
            if(ObjectUtils.isNotEmpty(modelBasic)){
                modelBasic.setCurrentStatus(status);
                modelBasicMapper.updateByPrimaryKey(modelBasic);
            }
        });
    }

    public void installNode(String module) throws Exception{
        ModelManager managerInfo = modelManagerService.select();
        ModelBasic modelBasic = modelManagerService.modelBasicInfo(module);
        ModelVersion modelVersion = modelManagerService.modelVersionInfo(modelBasic.getModelUuid(),modelBasic.getLastRevision());
        ModelInstalledDto modelInstalledDto = new ModelInstalledDto();
        modelInstalledDto.setModelBasic(modelBasic);
        modelInstalledDto.setModelVersion(modelVersion);
        addOrUpdateModelNode(module,ModuleStatusConstants.installing.value());
        nodeOperateService.installOrUpdate(managerInfo,modelInstalledDto);

        //moduleDelayTaskManager.addTask(60000);

    }

    @Resource
    private ModuleDelayTaskManager moduleDelayTaskManager;


}
