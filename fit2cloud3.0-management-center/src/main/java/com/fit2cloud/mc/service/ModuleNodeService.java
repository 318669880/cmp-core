package com.fit2cloud.mc.service;

import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.dao.ModelBasicMapper;
import com.fit2cloud.mc.dao.ModelBasicPageMapper;
import com.fit2cloud.mc.dao.ModelNodeMapper;
import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.model.*;
import com.fit2cloud.mc.strategy.entity.ModelStatusParam;
import com.fit2cloud.mc.strategy.queue.ModuleDelayTaskManager;
import com.fit2cloud.mc.strategy.service.EurekaCheckService;
import com.fit2cloud.mc.strategy.service.NodeOperateService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Resource
    private ModuleDelayTaskManager moduleDelayTaskManager;

    @Resource
    private EurekaCheckService eurekaCheckService;

    @Resource
    private Environment environment;



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

    public ModelNode queryNode(String module){
        ModelBasic modelBasic = modelManagerService.modelBasicInfo(module);
        ModelNodeExample modelNodeExample = new ModelNodeExample();
        ModelNodeExample.Criteria criteria = modelNodeExample.createCriteria();
        criteria.andModelBasicUuidEqualTo(modelBasic.getModelUuid());
        criteria.andIsMcEqualTo(false);
        modelNodeExample.setOrderByClause("node_create_time desc");
        List<ModelNode> modelNodes = modelNodeMapper.selectByExample(modelNodeExample);
        if(CollectionUtils.isEmpty(modelNodes))return null;
        return modelNodes.get(0);
    }

    public ModelNode nodeInfo(String nodeId){
        return modelNodeMapper.selectByPrimaryKey(nodeId);
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
    public void addOrUpdateModelNode (String module,String node_status,String ip,String port) throws Exception{
        Map<String,String> ipMap = new HashMap<>();
        ipMap.put("ip",ip);
        Optional.ofNullable(modelManagerService.modelBasicInfo(module)).ifPresent(model -> {
            String hostName = "http://"+ip+":"+port;
            if(StringUtils.isEmpty(ip)){
                hostName = domain_host();
                ipMap.put("ip",environment.getProperty("eureka.instance.ip-address"));
            }
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
                modelNode.setNodeIp(ipMap.get("ip"));
                modelNode.setModelBasicUuid(model.getModelUuid());
                modelNode.setNodeStatus(node_status);
                modelNode.setNodeCreateTime(new Date().getTime());
                modelNodeMapper.insert(modelNode);
            }
        });
    }

    public void addOrUpdateMcNode(String node_status){
        String hostName = domain_host();
        String module = environment.getProperty("spring.application.name");
        ModelNodeExample modelNodeExample = new ModelNodeExample();
        modelNodeExample.createCriteria().andNodeHostEqualTo(hostName).andIsMcEqualTo(true);
        List<ModelNode> modelNodes = modelNodeMapper.selectByExample(modelNodeExample);
        if(CollectionUtils.isNotEmpty(modelNodes)){
            ModelNode temp =modelNodes.get(0);
            temp.setNodeStatus(node_status);
            modelNodeMapper.updateByPrimaryKey(temp);
            return ;
        }
        ModelNode modelNode = new ModelNode();
        modelNode.setNodeHost(hostName);
        modelNode.setModelNodeUuid(UUIDUtil.newUUID());
        modelNode.setNodeIp(environment.getProperty("eureka.instance.ip-address"));
        modelNode.setIsMc(true);
        modelNode.setModelBasicUuid(module);
        modelNode.setNodeStatus(node_status);
        modelNode.setNodeCreateTime(new Date().getTime());
        modelNodeMapper.insert(modelNode);
    }
    public String domain_host(){
        String port = environment.getProperty("local.server.port");
        String _ip = environment.getProperty("eureka.instance.ip-address");
        return "http://"+_ip+":"+port;
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
        addOrUpdateModelNode(module,ModuleStatusConstants.installing.value(),null,null);
        nodeOperateService.installOrUpdate(managerInfo,modelInstalledDto);
        moduleDelayTaskManager.addTask(90000,param -> {
            try {
                eurekaCheckService.checkModuleStatus((ModelStatusParam)param);
            } catch (Exception e) {
                e.printStackTrace();
            }
        },new ModelStatusParam(queryNode(module),module,ModuleStatusConstants.installing));
    }

    public void startNode(String module) throws Exception {
        addOrUpdateModelNode(module,ModuleStatusConstants.startting.value(),null,null);
        ModelManager managerInfo = modelManagerService.select();
        nodeOperateService.start(managerInfo,module);
        moduleDelayTaskManager.addTask(90000,param -> {
            try {
                eurekaCheckService.checkModuleStatus((ModelStatusParam)param);
            } catch (Exception e) {
                e.printStackTrace();
            }
        },new ModelStatusParam(queryNode(module),module,ModuleStatusConstants.startting));
    }

    public void stopNode(String module) throws Exception {
        addOrUpdateModelNode(module,ModuleStatusConstants.stopping.value(),null,null);
        ModelManager managerInfo = modelManagerService.select();
        nodeOperateService.stop(managerInfo,module);
        moduleDelayTaskManager.addTask(90000,param -> {
            try {
                eurekaCheckService.checkModuleStatus((ModelStatusParam)param);
            } catch (Exception e) {
                e.printStackTrace();
            }
        },new ModelStatusParam(queryNode(module),module,ModuleStatusConstants.stopping));
    }




}
