package com.fit2cloud.mc.listener;

import com.fit2cloud.commons.server.service.StatsService;
import com.fit2cloud.commons.utils.CommonBeanFactory;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.mc.strategy.task.ModelNodeTask;
import com.netflix.appinfo.InstanceInfo;
import org.springframework.cloud.netflix.eureka.server.event.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class EurekaStateChangeListener {

    @Resource
    private ModelNodeTask modelNodeTask;

    @EventListener(condition = "#event.replication")
    public void listen(EurekaInstanceCanceledEvent event) {
        LogUtil.info("Service offline:" + event.getAppName() + "," + event.getServerId());
    }

    @EventListener(condition = "#event.replication")
    public void listen(EurekaInstanceRegisteredEvent event) {
        InstanceInfo info = event.getInstanceInfo();
        LogUtil.info("Service registration:" + info.getAppName() + "," + info.getIPAddr()+":" + info.getPort());
        //注册后验证连接
        CommonBeanFactory.getBean(StatsService.class).validateModuleConnection(info.getAppName(), info.getIPAddr(), info.getPort());
    }

    @EventListener(condition = "#event.replication")
    public void listen(EurekaInstanceRenewedEvent event) {
        InstanceInfo info = event.getInstanceInfo();
        //LogUtil.info("服务续约:" + info.getAppName());
    }

    @EventListener
    public void listen(EurekaRegistryAvailableEvent event) {
        LogUtil.info("Eureka Registration Center launched");
    }

    @EventListener
    public void listen(EurekaServerStartedEvent event) {
        LogUtil.info("Eureka Server Startup");
        try {
            modelNodeTask.registerCurrentMc();//注册自己
            modelNodeTask.clearRedisCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
