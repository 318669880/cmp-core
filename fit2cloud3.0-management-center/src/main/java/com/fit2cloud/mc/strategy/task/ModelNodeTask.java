package com.fit2cloud.mc.strategy.task;

import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.CommonBeanFactory;
import com.fit2cloud.commons.utils.HttpClientUtil;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.common.constants.BusinessCacheConstants;
import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.dao.ConfigPropertiesMapper;
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
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
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

@Service
public class ModelNodeTask {

    private static final String DEFAULT_ZONE_PROPERTIES = "eureka.client.service-url.defaultZone";

    @Resource
    private Environment environment;


    @Value("${server.port}")
    private String port;

    @Value("${spring.cloud.config.profile}")
    private String currentProfile;
    @Value("${spring.cloud.config.name}")
    private String configName;
    @Value("${spring.cloud.config.label}")
    private String label;


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

    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private ConfigPropertiesMapper configPropertiesMapper;



    /**
     * ?????????????????????eureka??????
     * @throws Exception
     */
    public void registerCurrentMc() throws Exception{
        if (SyncEurekaServer.IS_KUBERNETES) {
            chainK8sStart();//?????????????????????????????????
            return;
        }
        moduleNodeService.addOrUpdateMcNode(ModuleStatusConstants.running.value());
        ModelNodeTask proxy = CommonBeanFactory.getBean(ModelNodeTask.class);
        proxy.syncNode();
    }

    @Async
    public void syncNode() throws Exception{

        syncFromDb();
        chainStart();//?????????????????????????????????
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

    private boolean is_node_available(ModelNode node){
        List<ServiceInstance> instances = discoveryClient.getInstances(node.getModelBasicUuid());
        if(CollectionUtils.isEmpty(instances)) return false;
        String nodeIp = node.getNodeIp();
        return instances.stream().anyMatch(instance -> instance.getUri().toString().indexOf(nodeIp) != -1);
    }



    /**
     * ?????????????????????????????????????????????????????????
     * @throws Exception
     */
    private void syncFromDb() throws Exception {
        List<ModelInstall> modelInstalls = modelManagerService.installInfoquery();
        ModelNodeExample modelNodeExample = new ModelNodeExample();
        String host = "http://"+environment.getProperty("eureka.instance.ip-address")+":"+port;
        modelNodeExample.createCriteria().andIsMcEqualTo(false).andNodeHostEqualTo(host);
        List<ModelNode> modelNodes = modelNodeMapper.selectByExample(modelNodeExample);
        //  ?????????????????? ??????????????????????????? ?????????????????????????????????
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
     * ??????eureka??????
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
                boolean is_current_mc_branch = (node.getNodeHost().indexOf(host) != -1);
                String nodeIp = node.getNodeIp();
                boolean isReachable = InetAddress.getByName(nodeIp).isReachable(3000) && isHostConnectable(node.getNodeHost());
                //??????????????????????????????127.0.0.1 ?????? ?????? 127.0.0.1?????????
                //????????????????????????127.0.0.1 ???????????????
                //????????????????????????????????????????????????????????? ?????????????????? ??????????????????
                if (localIps.contains(nodeIp) && !StringUtils.equals(configMcIp, nodeIp)){
                    isReachable = false;
                }
                if(!isReachable && !is_current_mc_branch){
                    String mc_modelNode_uuid = node.getModelNodeUuid();
                    //????????? ??????mc????????????
                    modelNodeMapper.deleteByPrimaryKey(mc_modelNode_uuid);
                    //????????? mc?????????????????????????????????????????????
                    modelNodeExample.clear();
                    modelNodeExample.createCriteria().andIsMcEqualTo(false).andMcNodeUuidEqualTo(mc_modelNode_uuid);
                    modelNodeMapper.deleteByExample(modelNodeExample);
                    removeConfigByHost(node.getNodeHost());
                    modelNodeExample.clear();
                    moduleNodeService.clearNodesCache();
                }
                if(is_current_mc_branch)return false; //?????? ???????????? ???????????? ??????????????????
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
            if (CollectionUtils.isEmpty(currentServersLists) ) return;
            List<String> newServers = dbEurekaServers.stream().filter(server -> {
                String cHost = server.substring(0, server.indexOf("/eureka"));
                return !currentServersLists.contains(server) && isHostConnectable(cHost);
            }).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(newServers)){
                LogUtil.info("ready to regist current eureka [" +host+ " to others eureka cluster ["+StringUtils.join(newServers, ",")+"]");
                //List<String> realServers = Stream.of(newServers, currentServersLists).flatMap(Collection::stream).distinct().collect(Collectors.toList());
                /*List<String> realServers = newServers;
                String fixedEureka = environment.getProperty("fixed-eureka");
                if (StringUtils.isNotEmpty(fixedEureka) && StringUtils.equals(fixedEureka, "true")) return;*/
                //eurekaClientConfigBean.getServiceUrl().put(EurekaClientConfigBean.DEFAULT_ZONE, StringUtils.join(realServers, ","));
                joinCurrentEureka2Cluster(newServers);
            }
        }
    }




    private String currentConfigPk(){
        String eureka_key = DEFAULT_ZONE_PROPERTIES;
        return currentProfile+eureka_key;
    }
    public void joinCurrentEureka2Cluster(List<String> newEurekaServers){
        String pk = currentConfigPk();
        Optional.ofNullable(configPropertiesMapper.selectByPrimaryKey(pk)).ifPresent(config -> {
            config.setConfv(StringUtils.join(newEurekaServers, ","));
            configPropertiesMapper.updateByPrimaryKey(config);
            ModelNodeTask proxy = CommonBeanFactory.getBean(ModelNodeTask.class);
            proxy.refreshConfig();
        });
    }

    /**
     * ????????????
     * ????????????????????? ??????????????????
     */
    @Async
    public void refreshConfig(){
        String host = "http://127.0.0.1:"+port;
        String url = host+"/actuator/refresh";
        String result = HttpClientUtil.post(url, "{}");
        LogUtil.info("The config of "+host + "has changed with ["+result+"]");
    }



    public void initCurrentConfig(){
        String pk = currentConfigPk();
        Optional.ofNullable(configPropertiesMapper.selectByPrimaryKey(pk)).ifPresent(config -> {
            configPropertiesMapper.deleteByPrimaryKey(pk);
        });
        ConfigProperties config = new ConfigProperties();
        config.setId(pk);
        config.setConfk(DEFAULT_ZONE_PROPERTIES);
        config.setConfv(environment.getProperty(DEFAULT_ZONE_PROPERTIES));
        config.setApplication(configName);
        config.setProfile(currentProfile);
        config.setLabel(label);
        configPropertiesMapper.insert(config);
    }

    private void removeConfigByHost(String host){
        String splitStr = "://";
        int start = host.indexOf(splitStr);
        start = (start == -1) ? 0 : (start + splitStr.length());
        String pk = host.substring(start);
        configPropertiesMapper.deleteByPrimaryKey(pk);
    }


    /**
     * ????????????????????????
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
     * ????????????
     * ???mc??????????????? mc?????????????????????????????????
     */
    private void chainStart() {
        ModelNode mcNode = moduleNodeService.currentMcNode();
        Optional.ofNullable(mcNode.getModelNodeUuid()).ifPresent(nodeId -> {
            ModelNodeExample example = new ModelNodeExample();
            example.createCriteria().andMcNodeUuidEqualTo(nodeId).andNodeStatusNotEqualTo(ModuleStatusConstants.installFaild.value());
            List<ModelNode> modelNodes = modelNodeMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(modelNodes)) return;
            modelNodes.forEach(node -> {
                try {
                    if (!is_node_available(node)){
                        moduleNodeService.startNode(node.getModelBasicUuid(), node.getModelNodeUuid());
                    }
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
