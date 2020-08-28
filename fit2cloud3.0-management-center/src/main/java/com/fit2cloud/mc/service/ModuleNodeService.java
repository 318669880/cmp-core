package com.fit2cloud.mc.service;

import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.dao.ModelBasicMapper;
import com.fit2cloud.mc.dao.ModelBasicPageMapper;
import com.fit2cloud.mc.dao.ModelNodeMapper;
import com.fit2cloud.mc.model.*;
import com.fit2cloud.mc.strategy.task.EurekaInstanceMonitor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private Environment environment;

    @Resource
    private EurekaInstanceMonitor eurekaInstanceMonitor;



    /**
     * 根据model_uuid查询节点
     * 查询全部节点使用缓存
     * @param module
     * @return
     */
    @Cacheable(value = "host-nodes-cache",condition = "#module==null")
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

    @CacheEvict(value = "host-nodes-cache",allEntries = true)
    public void clearCache(){}


    public ModelNode currentMcNode(){
        String host = domain_host();
        String module = environment.getProperty("spring.application.name");
        ModelNodeExample modelNodeExample = new ModelNodeExample();
        ModelNodeExample.Criteria criteria = modelNodeExample.createCriteria();
        criteria.andNodeHostEqualTo(host).andIsMcEqualTo(true).andModelBasicUuidEqualTo(module);
        List<ModelNode> nodes = modelNodeMapper.selectByExample(modelNodeExample);
        return CollectionUtils.isEmpty(nodes) ? null : nodes.get(0);
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
    @CacheEvict(value = "host-nodes-cache",allEntries = true)
    public void addOrUpdateModelNode (ModelNode node) throws Exception{
        String mc_hostName = domain_host();
        String mc_ip = environment.getProperty("eureka.instance.ip-address");
        String modelNodeUuid = node.getModelNodeUuid();
        Optional.ofNullable(modelNodeUuid).ifPresent(model_node_uuid -> {
            AtomicBoolean is_new = new AtomicBoolean(true);
            ModelNode template = modelNodeMapper.selectByPrimaryKey(modelNodeUuid);
            Optional.ofNullable(template).ifPresent(temp -> {
                node.setModelNodeUuid(template.getModelNodeUuid());
                modelNodeMapper.updateByPrimaryKeySelective(node);
                is_new.set(false);
            });
            if (!is_new.get())return;
            ModelNode mc_node = currentMcNode();
            if (ObjectUtils.isEmpty(mc_node)) {
                F2CException.throwException("找不到当前管理中心节点信息");
            }
            node.setNodeHost(Optional.ofNullable(node.getNodeHost()).orElse(mc_hostName));
            node.setNodeIp(Optional.ofNullable(node.getNodeIp()).orElse(mc_ip));
            node.setNodeCreateTime(new Date().getTime());
            node.setIsMc(false);
            node.setMcNodeUuid(mc_node.getModelNodeUuid());
            modelNodeMapper.insert(node);
        });
    }


    public void addOrUpdateMcNode(String node_status){
        ModelNode node = currentMcNode();
        AtomicBoolean is_new = new AtomicBoolean(true);
        Optional.ofNullable(node).ifPresent(mc_node -> {
            node.setNodeStatus(node_status);
            modelNodeMapper.updateByPrimaryKey(node);
            is_new.set(false);
        });
        if (!is_new.get()) return;
        String hostName = domain_host();
        String module = environment.getProperty("spring.application.name");
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
    /*
    public void modelStatu(String module){
        ModelBasic model = modelManagerService.modelBasicInfo(module);
        Optional.ofNullable(model).ifPresent(modelBasic -> {
            List<ModelNode> modelNodes = queryNodes(module);

            AtomicReference<String> atomicReference = new AtomicReference<>(null);
            modelNodes.stream().anyMatch(node -> {
                atomicReference.set(node.getNodeStatus());
                return !node.getNodeStatus().endsWith("Faild");
            });
            if(modelNodes.stream().anyMatch(node -> node.getNodeStatus().equals(ModuleStatusConstants.running.value()))){
                atomicReference.set(ModuleStatusConstants.running.value());
            }
            if(ObjectUtils.isNotEmpty(modelBasic)){
                modelBasic.setCurrentStatus(atomicReference.get());
                modelBasicMapper.updateByPrimaryKey(modelBasic);
            }
        });
    }
    */


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
        /*if(ModuleStatusConstants.installing.value().equals(modelNode.getNodeStatus()))
        moduleDelayTaskManager.addTask(90000,param -> {
            try {
                eurekaCheckService.checkModuleStatus((ModelStatusParam)param);
            } catch (Exception e) {
                F2CException.throwException(e);
            }
        },new ModelStatusParam(modelNode,module,ModuleStatusConstants.installing));*/
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

        /*if (ModuleStatusConstants.startting.value().equals(modelNode.getNodeStatus()))
        moduleDelayTaskManager.addTask(90000,param -> {
            try {
                eurekaCheckService.checkModuleStatus((ModelStatusParam)param);
            } catch (Exception e) {
                e.printStackTrace();
            }
        },new ModelStatusParam(modelNode,module,ModuleStatusConstants.startting));*/
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

        /*if (ModuleStatusConstants.stopping.value().equals(modelNode.getNodeStatus()))
        moduleDelayTaskManager.addTask(90000,param -> {
            try {
                eurekaCheckService.checkModuleStatus((ModelStatusParam)param);
            } catch (Exception e) {
                e.printStackTrace();
            }
        },new ModelStatusParam(modelNode,module,ModuleStatusConstants.stopping));*/
    }




}
