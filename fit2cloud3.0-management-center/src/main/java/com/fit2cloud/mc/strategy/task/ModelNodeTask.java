package com.fit2cloud.mc.strategy.task;

import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.dao.ModelNodeMapper;
import com.fit2cloud.mc.model.ModelNode;
import com.fit2cloud.mc.model.ModelNodeExample;
import com.fit2cloud.mc.service.ModuleNodeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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

@Service
public class ModelNodeTask {

    @Resource
    private Environment environment;

    @Value("${spring.application.name}")
    private String serverName;

    @Value("${server.port}")
    private String port;


    @Resource
    private ModuleNodeService moduleNodeService;

    @Resource
    private EurekaClientConfigBean eurekaClientConfigBean;

    @Resource
    private ModelNodeMapper modelNodeMapper;


    public void registerCurrentMc() throws Exception{
        moduleNodeService.addOrUpdateMcNode(ModuleStatusConstants.running.value());
    }


    /**
     * 加入eureka集群
     */
    @Scheduled(fixedDelay = 10000)
    public void joinEurekaCluster(){
        final String eureka_instance_ip_address = environment.getProperty("eureka.instance.ip-address");
        String host = "http://"+eureka_instance_ip_address+":"+port;
        ModelNodeExample modelNodeExample = new ModelNodeExample();
        modelNodeExample.createCriteria().andIsMcEqualTo(true);
        List<ModelNode> modelNodes = modelNodeMapper.selectByExample(modelNodeExample);
        modelNodes = modelNodes.stream().filter(node -> {
            try {
                String nodeIp = node.getNodeIp();
                //if(node.getNodeHost().indexOf(host) != -1)return false; //去除 本机地址 相互注册 无需注册自己
                boolean isReachable = InetAddress.getByName(nodeIp).isReachable(3000) && isHostConnectable(node.getNodeHost());
                if(!isReachable){
                    modelNodeMapper.deleteByPrimaryKey(node.getModelNodeUuid());
                }
                return isReachable;
            } catch (Exception e) {
                LogUtil.error(e.getMessage(),e);
                F2CException.throwException(e);
            }
            return false;
        }).collect(Collectors.toList());
        List<String> dbEurekaServers = modelNodes.stream().map(node -> node.getNodeHost()+"/eureka/").collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(dbEurekaServers))
        eurekaClientConfigBean.getServiceUrl().put(EurekaClientConfigBean.DEFAULT_ZONE, StringUtils.join(dbEurekaServers, ","));

       /* eurekaClientConfigBean.getServiceUrl().put("zone-0","");
        eurekaClientConfigBean.getServiceUrl().put("zone-1","");
        ArrayList<String> arrayList = new ArrayList<>();
        eurekaClientConfigBean.getAvailabilityZones().put("us-east-1","zone-0,zone-1");
        eurekaClientConfigBean.setRegion("us-east-1");*/
    }



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
