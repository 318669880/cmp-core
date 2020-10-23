package com.fit2cloud.mc.job;


import com.fit2cloud.commons.server.license.DefaultLicenseService;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.mc.service.ExtraUserService;
import com.fit2cloud.quartz.anno.QuartzScheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SyncF2cLicense {

    @Resource
    private DefaultLicenseService defaultLicenseService;

    @QuartzScheduled(cron = "0 0 1 * * ?")
    public void syncF2cLicense() {
        try {
            LogUtil.info("Begin to validate f2c license.");
            defaultLicenseService.validateF2cLicense();
            LogUtil.info("End of validate f2c license.");
        }catch (Exception e){
            LogUtil.info("Failed of validate f2c license: " + e.getMessage());
        }

    }

}
