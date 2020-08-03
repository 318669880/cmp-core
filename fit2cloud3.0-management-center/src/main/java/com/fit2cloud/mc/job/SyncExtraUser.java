package com.fit2cloud.mc.job;


import com.fit2cloud.mc.service.ExtraUserService;
import com.fit2cloud.quartz.anno.QuartzScheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Author: chunxing
 * Date: 2018/6/26  上午10:45
 * Description:
 */
@Component
public class SyncExtraUser {

    @Resource
    private ExtraUserService extraUserService;

    @QuartzScheduled(cron = "${sync.extra.user}")
    public void syncExtraUser() {
        extraUserService.syncExtraUser(false);
    }

}
