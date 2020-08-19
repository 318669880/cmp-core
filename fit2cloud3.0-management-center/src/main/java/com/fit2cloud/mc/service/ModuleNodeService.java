package com.fit2cloud.mc.service;

import com.fit2cloud.commons.server.exception.F2CException;
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
import com.fit2cloud.mc.strategy.task.EurekaInstanceMonitor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
    private ModuleDelayTaskManager moduleDelayTaskManager;

    @Resource
    private EurekaCheckService eurekaCheckService;

    @Resource
    private Environment environment;

    @Resource
    private EurekaInstanceMonitor eurekaInstanceMonitor;



    /**
     * 根据model_uuid查询节点
     * @param module
     * @return
     */
    public List<ModelNode> queryNodes(String module){
        ModelNodeExample modelNodeExample = new ModelNodeExample();
        ModelNodeExample.Criteria criteria = modelNodeExample.createCriteria();
        Optional.ofNullable(module).ifPresent(cmodule -> {
            criteria.andModelBasicUuidEqualTo(cmodule);
        });
        criteria.andIsMcEqualTo(false);
        modelNodeExample.setOrderByClause("node_create_time desc");
        return modelNodeMapper.selectByExample(modelNodeExample);
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
     *
     */
    public void addOrUpdateModelNode (ModelNode node) throws Exception{
        String mc_hostName = domain_host();
        String mc_ip = environment.getProperty("eureka.instance.ip-address");
        ModelNode template = null;
        String modelNodeUuid = node.getModelNodeUuid();
        if(!StringUtils.isEmpty(modelNodeUuid)){
            template = modelNodeMapper.selectByPrimaryKey(modelNodeUuid);
            if(ObjectUtils.isNotEmpty(template)){
                node.setModelNodeUuid(template.getModelNodeUuid());
                modelNodeMapper.updateByPrimaryKeySelective(node);
                return;
            }
            node.setNodeHost(Optional.ofNullable(node.getNodeHost()).orElse(mc_hostName));
            node.setNodeIp(Optional.ofNullable(node.getNodeIp()).orElse(mc_ip));
            node.setNodeCreateTime(new Date().getTime());
            node.setIsMc(false);
            modelNodeMapper.insert(node);
            return;
        }
        ModelNodeExample modelNodeExample = new ModelNodeExample();
        ModelNodeExample.Criteria criteria = modelNodeExample.createCriteria();
        criteria.andIsMcEqualTo(node.getIsMc());
        if(!StringUtils.isEmpty(node.getNodeIp())){
            criteria.andNodeIpEqualTo(node.getNodeIp());
        }
        if(!StringUtils.isEmpty(node.getNodeHost())){
            criteria.andNodeHostEqualTo(node.getNodeHost());
        }
        if(!StringUtils.isEmpty(node.getModelBasicUuid())){
            criteria.andModelBasicUuidEqualTo(node.getModelBasicUuid());
        }
        List<ModelNode> modelNodes = modelNodeMapper.selectByExample(modelNodeExample);
        if(CollectionUtils.isNotEmpty(modelNodes)){
            template = modelNodes.get(0);
            template.setNodeStatus(node.getNodeStatus());
            modelNodeMapper.updateByPrimaryKey(template);
            return ;
        }
        node.setNodeHost(Optional.of(node.getNodeHost()).orElse(mc_hostName));
        node.setNodeIp(Optional.of(node.getNodeIp()).orElse(mc_ip));
        node.setNodeCreateTime(new Date().getTime());
        node.setIsMc(false);
        modelNodeMapper.insert(node);
    }

    public void addOrUpdateMcNode(String node_status){
        String hostName = domain_host();
        String module = environment.getProperty("spring.application.name");
        ModelNodeExample modelNodeExample = new ModelNodeExample();
        modelNodeExample.createCriteria().andNodeHostEqualTo(hostName).andModelBasicUuidEqualTo(module);
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
            List<ModelNode> modelNodes = queryNodes(module);
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



    public void installNode(String module, String nodeId) throws Exception{
        ModelNode modelNode = nodeInfo(nodeId);
        modelNode.setNodeStatus(ModuleStatusConstants.installing.value());
        try{
            addOrUpdateModelNode(modelNode);
            eurekaInstanceMonitor.execute(module, nodeId,"/modelNode/node/install",nodeInfo(nodeId));
        }catch (Exception e){
            modelNode.setNodeStatus(ModuleStatusConstants.installFaild.value());
            addOrUpdateModelNode(modelNode);
            F2CException.throwException(e);
        }
        if(ModuleStatusConstants.installing.value().equals(modelNode.getNodeStatus()))
        moduleDelayTaskManager.addTask(90000,param -> {
            try {
                eurekaCheckService.checkModuleStatus((ModelStatusParam)param);
            } catch (Exception e) {
                F2CException.throwException(e);
            }
        },new ModelStatusParam(modelNode,module,ModuleStatusConstants.installing));
    }

    public void startNode(String module, String nodeId) throws Exception {
        ModelNode modelNode = nodeInfo(nodeId);
        modelNode.setNodeStatus(ModuleStatusConstants.startting.value());
        try{
            addOrUpdateModelNode(modelNode);
            eurekaInstanceMonitor.execute(module, nodeId,"/modelNode/node/start",nodeInfo(module));
        }catch (Exception e){
            modelNode.setNodeStatus(ModuleStatusConstants.startFaild.value());
            addOrUpdateModelNode(modelNode);
            F2CException.throwException(e);
        }

        if (ModuleStatusConstants.startting.value().equals(modelNode.getNodeStatus()))
        moduleDelayTaskManager.addTask(90000,param -> {
            try {
                eurekaCheckService.checkModuleStatus((ModelStatusParam)param);
            } catch (Exception e) {
                e.printStackTrace();
            }
        },new ModelStatusParam(nodeInfo(nodeId),module,ModuleStatusConstants.startting));
    }

    public void stopNode(String module, String nodeId) throws Exception {
        ModelNode modelNode = nodeInfo(nodeId);
        modelNode.setNodeStatus(ModuleStatusConstants.stopping.value());
        try{
            addOrUpdateModelNode(modelNode);
            eurekaInstanceMonitor.execute(module, nodeId,"/modelNode/node/stop",nodeInfo(module));
        }catch (Exception e){
            modelNode.setNodeStatus(ModuleStatusConstants.stopFaild.value());
            addOrUpdateModelNode(modelNode);
            F2CException.throwException(e);
        }

        if (ModuleStatusConstants.stopping.value().equals(modelNode.getNodeStatus()))
        moduleDelayTaskManager.addTask(90000,param -> {
            try {
                eurekaCheckService.checkModuleStatus((ModelStatusParam)param);
            } catch (Exception e) {
                e.printStackTrace();
            }
        },new ModelStatusParam(nodeInfo(nodeId),module,ModuleStatusConstants.stopping));
    }




}
