package com.fit2cloud.mc.job;

import com.fit2cloud.commons.server.constants.ParamConstants;
import com.fit2cloud.commons.server.service.SystemLogService;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.mc.service.SystemParameterService;
import com.fit2cloud.quartz.anno.QuartzScheduled;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SystemLogCleanJob {
    @Resource
    private SystemParameterService systemParameterService;
    @Resource
    private SystemLogService systemLogService;

    @QuartzScheduled(cron = "0 30 1 * * ?")
    public void cleanSystemLog() {
        String value = systemParameterService.getValue(ParamConstants.Log.KEEP_MONTHS.getValue());
        int months = 3;
        if (StringUtil.isBlank(value)) {
            LogUtil.info("未设置系统日志保存月数，使用默认值: {}", months);
        } else {
            months = Integer.valueOf(value);
        }
        if (months < 1) {
            // if 0
            months = 3;
        }
        long now = System.currentTimeMillis();
        // 计算保留时长
        long logTime = now - (months * 30 * 24 * 3600 * 1000L);
        if (LogUtil.getLogger().isDebugEnabled()) {
            LogUtil.getLogger().debug("开始清理{}个月前的系统日志.", months);
        }
        systemLogService.cleanHistoryLog(logTime);
        if (LogUtil.getLogger().isDebugEnabled()) {
            LogUtil.getLogger().debug("系统日志清理完成.");
        }
    }
}
