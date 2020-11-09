package com.fit2cloud.mc.strategy.task;

import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.CommonBeanFactory;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.common.constants.BusinessCacheConstants;
import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.dao.ModelNodeMapper;
import com.fit2cloud.mc.dto.request.OperatorModuleRequest;
import com.fit2cloud.mc.job.SyncEurekaServer;
import com.fit2cloud.mc.model.*;
import com.fit2cloud.mc.service.K8sOperatorModuleService;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.service.ModuleNodeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
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
    @Lazy
    private ModuleNodeService moduleNodeService;

    @Resource
    private EurekaClientConfigBean eurekaClientConfigBean;

    @Resource
    private ModelNodeMapper modelNodeMapper;

    @Resource
    @Lazy
    private ModelManagerService modelManagerService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private K8sOperatorModuleService k8sOperatorModuleService;

    /**
     * 注册当前服务到eureka集群
     * @throws Exception
     */
    public void registerCurrentMc() throws Exception{
        if (SyncEurekaServer.IS_KUBERNETES) {
            chainK8sStart();//关联启动下属所有子模块
            return;
        }
        moduleNodeService.addOrUpdateMcNode(ModuleStatusConstants.running.value());
        ModelNodeTask proxy = CommonBeanFactory.getBean(ModelNodeTask.class);
        proxy.syncNode();
    }

    @Async
    public void syncNode() throws Exception{
        syncFromDb();
        chainStart();//关联启动下属所有子模块
    }


    public void clearRedisCache(String ... cacheNames){
        List<BusinessCacheConstants> values = Arrays.asList(BusinessCacheConstants.values());
        if(!ObjectUtils.isEmpty(cacheNames) &&  cacheNames.length > 0){
            List<String> cacheNameList = Arrays.asList(cacheNames);
            values = values.stream().filter(value -> cacheNameList.contains(value)).collect(Collectors.toList());
        }
        values.forEach(value -> {
            String prefix = value.getCacheName()+"::*";
            Set keys = redisTemplate.keys(prefix);
            redisTemplate.delete(keys);
        });
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
            String module = modelInstall.getModule();
            String modelNodeId = UUIDUtil.newUUID();
            ModelNode modelNode = new ModelNode();
            modelNode.setNodeStatus(ModuleStatusConstants.readyInstall.value());
            modelNode.setModelBasicUuid(module);
            modelNode.setModelNodeUuid(modelNodeId);
            try {
                moduleNodeService.addOrUpdateModelNode(modelNode);
                moduleNodeService.installNode(module,modelNodeId);
            } catch (Exception e) {
                F2CException.throwException(e);
            }
        });
    }


    /**
     * 加入eureka集群
     */
    @Scheduled(fixedDelay = 10000,initialDelay = 10000)
    public void joinEurekaCluster(){
        if (SyncEurekaServer.IS_KUBERNETES) return;
        final String eureka_instance_ip_address = environment.getProperty("eureka.instance.ip-address");
        String host = "http://"+eureka_instance_ip_address+":"+port;
        ModelNodeExample modelNodeExample = new ModelNodeExample();
        modelNodeExample.createCriteria().andIsMcEqualTo(true);
        List<ModelNode> modelNodes = modelNodeMapper.selectByExample(modelNodeExample);
        String configMcIp = environment.getProperty("eureka.instance.ip-address");
        List<String> localIps = new ArrayList<String>(){{add("127.0.0.1");add("localhost");}};
        modelNodes = modelNodes.stream().filter(node -> {
            try {
                String nodeIp = node.getNodeIp();
                boolean isReachable = InetAddress.getByName(nodeIp).isReachable(3000) && isHostConnectable(node.getNodeHost());
                //如果服务器配置的不是127.0.0.1 那么 剔除 127.0.0.1的节点
                //如果本地配置的是127.0.0.1 则不会剔除
                //注意：本地如果和服务器使用同一个数据库 并且同时运行 本地会受影响
                if (localIps.contains(nodeIp) && !StringUtils.equals(configMcIp, nodeIp)){
                    isReachable = false;
                }
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
        } catch (Exception e) {
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

    /**
     * 连锁启动
     * 当mc模块启动时 mc下属所有子模块跟着启动
     */
    private void chainStart() {
        ModelNode mcNode = moduleNodeService.currentMcNode();
        Optional.ofNullable(mcNode.getModelNodeUuid()).ifPresent(nodeId -> {
            ModelNodeExample example = new ModelNodeExample();
            example.createCriteria().andMcNodeUuidEqualTo(nodeId);
            List<ModelNode> modelNodes = modelNodeMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(modelNodes)) return;
            modelNodes.forEach(node -> {
                try {
                    moduleNodeService.startNode(node.getModelBasicUuid(), node.getModelNodeUuid());
                } catch (Exception e) {
                    LogUtil.error(e.getMessage() ,e);
                }
            });
        });
    }
    private void chainK8sStart() {
        List<ModelBasic> modelInstalls = modelManagerService.modelBasics();
        modelInstalls.forEach(modelBasic -> {
            String module = modelBasic.getModule();
            if (!StringUtils.equals("management-center" ,module)){
                ModelManager manager = modelManagerService.select();
                List<String> modules = new ArrayList<String>(){{add(module);}};
                Map<String,Object> params = new HashMap<>();
                Integer podNum = modelBasic.getPodNum();
                if (ObjectUtils.isEmpty(podNum) || podNum==0){
                    podNum = 1;
                }
                params.put("pod_number",podNum);
                OperatorModuleRequest request = new OperatorModuleRequest(){{setModules(modules);setParams(params);}};
                k8sOperatorModuleService.start(manager, request);
            }
        });
    }
}
