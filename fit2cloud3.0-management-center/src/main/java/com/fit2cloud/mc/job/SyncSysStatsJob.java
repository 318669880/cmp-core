package com.fit2cloud.mc.job;

import com.fit2cloud.commons.utils.CommonBeanFactory;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.mc.service.SysStatsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SyncSysStatsJob {

    @Resource
    private Environment environment;
    @Resource
    private SysStatsService sysStatsService;

    @Scheduled(cron = "0 * * * * ? ")
    public void SyncStatsHostInfoJob() {

        String hostname = environment.getProperty("HOST_HOSTNAME");
        if (!StringUtils.isBlank(hostname) && !SyncEurekaServer.IS_KUBERNETES) {
            sysStatsService.SyncStatsHostInfoJob(hostname);
        }
    }

    @Scheduled(cron = "0 0/5 * * * ? ")
    public void SyncStatsHostMetricJob() {

        String hostname = environment.getProperty("HOST_HOSTNAME");
        if (!StringUtils.isBlank(hostname) && !SyncEurekaServer.IS_KUBERNETES) {
            sysStatsService.SyncStatsHostMetricJob(hostname);
        }
    }
}
