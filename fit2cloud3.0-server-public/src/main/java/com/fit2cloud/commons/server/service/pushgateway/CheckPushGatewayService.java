package com.fit2cloud.commons.server.service.pushgateway;

import com.fit2cloud.commons.utils.CommonThreadPool;
import com.fit2cloud.commons.utils.LogUtil;
import io.prometheus.client.exporter.PushGateway;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

public class CheckPushGatewayService {
    @Resource
    private PushGateway pushGateway;
    @Value("${prometheus.push-gateway.clear-period:25}")
    private long period;
    @Resource
    private CommonThreadPool pushGatewayThreadPool;

    void removePushGatewayJob(String job) {
        pushGatewayThreadPool.scheduleTask(() -> {
            try {
                if (LogUtil.getLogger().isDebugEnabled()) {
                    LogUtil.getLogger().debug("删除 push gateway job:{}", job);
                }
                pushGateway.delete(job);
            } catch (Exception e) {
                LogUtil.error("删除 job:{} 出错:{}", job, ExceptionUtils.getStackTrace(e));
            }
        }, period, TimeUnit.SECONDS);
    }
}
