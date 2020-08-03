package com.fit2cloud.mc.controller;

import com.fit2cloud.mc.common.constants.PermissionConstants;
import com.fit2cloud.mc.service.DashboardService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Author: chunxing
 * Date: 2018/8/3  上午11:41
 * Description: dashboard 调用 API
 */
@RestController
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    @RequiresPermissions(PermissionConstants.DASHBOARD_ACCOUNT)
    @GetMapping("dashboard/tenant")
    public Object getTenant() {
        return dashboardService.getTenant();
    }

    @RequiresPermissions(PermissionConstants.DASHBOARD_CLOUD_ACCOUNT)
    @GetMapping("dashboard/cloud")
    public Object getCloud() {
        return dashboardService.getCloud();
    }
}
