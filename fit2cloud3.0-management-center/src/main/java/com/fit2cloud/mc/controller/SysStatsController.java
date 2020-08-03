package com.fit2cloud.mc.controller;

import com.fit2cloud.commons.server.constants.I18nConstants;
import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.model.MetricData;
import com.fit2cloud.commons.server.model.MetricRequest;
import com.fit2cloud.commons.server.service.MetricQueryService;
import com.fit2cloud.mc.common.constants.PermissionConstants;
import com.fit2cloud.mc.dto.McSysStatsDTO;
import com.fit2cloud.mc.service.SysStatsService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("sys")
public class SysStatsController {

    @Resource
    private SysStatsService sysStatsService;
    @Resource
    private MetricQueryService metricQueryService;

    @GetMapping("stats")
    @RequiresPermissions(PermissionConstants.SYS_STATS)
    @I18n(I18nConstants.CLUSTER)
    public List<McSysStatsDTO> getAllMcSysStats() {
        return sysStatsService.getAllMcSysStats();
    }


    @GetMapping("metric/{resourceId}")
    @RequiresPermissions(PermissionConstants.SYS_STATS)
    public List<MetricData> getSysMetric(@PathVariable String resourceId) {
        return sysStatsService.getSysMetric(resourceId);
    }


    @PostMapping("metric/query")
    @RequiresPermissions(PermissionConstants.SYS_STATS)
    public List<MetricData> queryMetric(@RequestBody MetricRequest request) {
        return metricQueryService.queryMetricData(request);
    }

}
