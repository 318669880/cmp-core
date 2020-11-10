package com.fit2cloud.mc.service;

import com.fit2cloud.commons.server.dcslock.annotation.DcsLock;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.CommonBeanFactory;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
    /*@DcsLock*/
    @CacheEvict(value = "host-nodes-cache",allEntries = true)
    public void addOrUpdateModelNode (ModelNode node) throws Exception{
        String mc_hostName = domain_host();
        String mc_ip = environment.getProperty("eureka.instance.ip-address");
        String modelNodeUuid = node.getModelNodeUuid();
        Optional.ofNullable(modelNodeUuid).ifPresent(model_node_uuid -> {
            AtomicBoolean is_new = new AtomicBoolean(true);
            ModelNode template = modelNodeMapper.selectByPrimaryKey(modelNodeUuid);
            node.setUpdateTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
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
            node.setNodeCreateTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            node.setIsMc(false);
            node.setMcNodeUuid(mc_node.getModelNodeUuid());
            modelNodeMapper.insert(node);
        });
    }

    public void addOrUpdateNodeClear(ModelNode node) throws Exception{
        ModuleNodeService proxy = CommonBeanFactory.getBean(ModuleNodeService.class);
        proxy.addOrUpdateModelNode(node);
    }


    public void addOrUpdateMcNode(String node_status){
        ModelNode node = currentMcNode();
        AtomicBoolean is_new = new AtomicBoolean(true);
        Optional.ofNullable(node).ifPresent(mc_node -> {
            node.setUpdateTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            node.setNodeStatus(node_status);
            modelNodeMapper.updateByPrimaryKey(node);
            is_new.set(false);
        });
        if (!is_new.get()) return;

        String hostName = domain_host();
        String module = environment.getProperty("spring.application.name");
        ModelNode modelNode = new ModelNode();
        modelNode.setUpdateTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        modelNode.setNodeHost(hostName);
        modelNode.setModelNodeUuid(UUIDUtil.newUUID());
        modelNode.setNodeIp(environment.getProperty("eureka.instance.ip-address"));
        modelNode.setIsMc(true);
        modelNode.setModelBasicUuid(module);
        modelNode.setNodeStatus(node_status);
        modelNode.setNodeCreateTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        modelNodeMapper.insert(modelNode);
    }
    public String domain_host(){
        String port = environment.getProperty("local.server.port");
        String _ip = environment.getProperty("eureka.instance.ip-address");
        return "http://"+_ip+":"+port;
    }


    public void installNode(String module, String nodeId) throws Exception{
        ModelNode modelNode = nodeInfo(nodeId);
        modelNode.setNodeStatus(ModuleStatusConstants.installing.value());
        try{
            addOrUpdateNodeClear(modelNode);
            eurekaInstanceMonitor.execute(module, nodeId,"/modelNode/node/install",nodeInfo(nodeId));
        }catch (Exception e){
            modelNode.setNodeStatus(ModuleStatusConstants.installFaild.value());
            addOrUpdateNodeClear(modelNode);
            F2CException.throwException(e);
        }
    }

    public void startNode(String module, String nodeId) throws Exception {
        ModelNode modelNode = nodeInfo(nodeId);
        modelNode.setNodeStatus(ModuleStatusConstants.startting.value());
        try{
            addOrUpdateNodeClear(modelNode);
            eurekaInstanceMonitor.execute(module, nodeId,"/modelNode/node/start",nodeInfo(module));
        }catch (Exception e){
            modelNode.setNodeStatus(ModuleStatusConstants.startFaild.value());
            addOrUpdateNodeClear(modelNode);
            F2CException.throwException(e);
        }
    }

    public void stopNode(String module, String nodeId) throws Exception {
        ModelNode modelNode = nodeInfo(nodeId);
        modelNode.setNodeStatus(ModuleStatusConstants.stopping.value());
        try{
            addOrUpdateNodeClear(modelNode);
            eurekaInstanceMonitor.execute(module, nodeId,"/modelNode/node/stop",nodeInfo(module));
        }catch (Exception e){
            modelNode.setNodeStatus(ModuleStatusConstants.stopFaild.value());
            addOrUpdateNodeClear(modelNode);
            F2CException.throwException(e);
        }
    }




}
