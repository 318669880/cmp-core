package com.fit2cloud.mc.strategy.task;

import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.dao.ModelNodeMapper;
import com.fit2cloud.mc.model.ModelInstall;
import com.fit2cloud.mc.model.ModelNode;
import com.fit2cloud.mc.model.ModelNodeExample;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.service.ModuleNodeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ModelNodeTask {

    @Resource
    private Environment environment;


    @Value("${server.port}")
    private String port;


    @Resource
    private ModuleNodeService moduleNodeService;

    @Resource
    private EurekaClientConfigBean eurekaClientConfigBean;

    @Resource
    private ModelNodeMapper modelNodeMapper;

    @Resource
    private ModelManagerService modelManagerService;



    /**
     * 注册当前服务到eureka集群
     * @throws Exception
     */
    public void registerCurrentMc() throws Exception{
        moduleNodeService.addOrUpdateMcNode(ModuleStatusConstants.running.value());
        syncFromDb();
    }

    /**
     * 从数据库中同步其他节点已经预安装的模块
     * @throws Exception
     */
    private void syncFromDb() throws Exception {
        List<ModelInstall> modelInstalls = modelManagerService.installInfoquery();
        ModelNodeExample modelNodeExample = new ModelNodeExample();
        String host = "http://"+environment.getProperty("eureka.instance.ip-address")+":"+port;
        modelNodeExample.createCriteria().andIsMcEqualTo(false).andNodeHostEqualTo(host);
        List<ModelNode> modelNodes = modelNodeMapper.selectByExample(modelNodeExample);
        //  当前节点如果 没有包含预安装模块 那么这里自动执行预安装
        modelInstalls.stream().filter(model -> !modelNodes.stream().anyMatch(node -> StringUtils.equals(node.getModelBasicUuid(),model.getModule()))).forEach(modelInstall -> {
            ModelNode modelNode = new ModelNode();
            modelNode.setNodeStatus(ModuleStatusConstants.readyInstall.value());
            modelNode.setModelBasicUuid(modelInstall.getModule());
            modelNode.setModelNodeUuid(UUIDUtil.newUUID());
            try {
                moduleNodeService.addOrUpdateModelNode(modelNode);
            } catch (Exception e) {
                F2CException.throwException(e);
            }
        });
    }


    /**
     * 加入eureka集群
     */
    @Scheduled(fixedDelay = 10000,initialDelay = 5000)
    public void joinEurekaCluster(){
        final String eureka_instance_ip_address = environment.getProperty("eureka.instance.ip-address");
        String host = "http://"+eureka_instance_ip_address+":"+port;
        ModelNodeExample modelNodeExample = new ModelNodeExample();
        modelNodeExample.createCriteria().andIsMcEqualTo(true);
        List<ModelNode> modelNodes = modelNodeMapper.selectByExample(modelNodeExample);
        modelNodes = modelNodes.stream().filter(node -> {
            try {
                String nodeIp = node.getNodeIp();
                boolean isReachable = InetAddress.getByName(nodeIp).isReachable(3000) && isHostConnectable(node.getNodeHost());
                if(!isReachable){
                    String mc_modelNode_uuid = node.getModelNodeUuid();
                    //不可用 删除mc节点记录
                    modelNodeMapper.deleteByPrimaryKey(mc_modelNode_uuid);
                    //再删除 mc节点挂接的所有业务模块节点记录
                    modelNodeExample.clear();
                    modelNodeExample.createCriteria().andIsMcEqualTo(false).andMcNodeUuidEqualTo(mc_modelNode_uuid);
                    modelNodeMapper.deleteByExample(modelNodeExample);
                    modelNodeExample.clear();
                }
                if(node.getNodeHost().indexOf(host) != -1)return false; //去除 本机地址 相互注册 无需注册自己
                return isReachable;
            } catch (Exception e) {
                LogUtil.error(e.getMessage(),e);
                F2CException.throwException(e);
            }
            return false;
        }).collect(Collectors.toList());
        List<String> dbEurekaServers = modelNodes.stream().map(node -> node.getNodeHost()+"/eureka/").collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(dbEurekaServers)){
            String[] servers = eurekaClientConfigBean.getServiceUrl().get(EurekaClientConfigBean.DEFAULT_ZONE).split(",");
            List<String> currentServersLists = Arrays.asList(servers);
            List<String> newServers = dbEurekaServers.stream().filter(server -> !currentServersLists.contains(server)).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(newServers)){
                List<String> realServers = Stream.of(newServers, currentServersLists).flatMap(Collection::stream).distinct().collect(Collectors.toList());
                eurekaClientConfigBean.getServiceUrl().put(EurekaClientConfigBean.DEFAULT_ZONE, StringUtils.join(realServers, ","));
            }
        }
    }


    /**
     * 测试端口是否可达
     * @param nodeIp
     * @return
     */
    public boolean isHostConnectable(String nodeIp) {
        Socket socket = new Socket();
        try {
            int beginIndex = nodeIp.startsWith("http://") ? (nodeIp.indexOf("//")+2) : 0;
            int end = nodeIp.lastIndexOf(":");
            String host = nodeIp.substring(beginIndex,end);
            String portstr = nodeIp.substring(end+1);
            int port = Integer.valueOf(portstr);
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            LogUtil.error(e.getMessage(),e);
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
