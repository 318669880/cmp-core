package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.constants.PermissionConstants;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.MetricData;
import com.fit2cloud.commons.server.model.MetricRequest;
import com.fit2cloud.commons.server.service.MetricQueryService;
import com.fit2cloud.commons.server.swagger.annotation.ApiHasModules;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("server")
@Api(tags = Translator.PREFIX + "i18n_swagger_metric_tag" + Translator.SUFFIX)
public class MetricQueryController {
    @Resource
    private MetricQueryService metricQueryService;

    @PostMapping("metric/query")
    @ApiOperation(Translator.PREFIX + "i18n_swagger_metric_query" + Translator.SUFFIX)
    @ApiHasModules({"vm-service", "operation-analytics"})
    @RequiresPermissions(value = {PermissionConstants.CLOUD_SERVER_READ, PermissionConstants.METRIC_READ}, logical = Logical.OR)
    public List<MetricData> queryMetric(@RequestBody MetricRequest request) {
        return metricQueryService.queryMetricData(request);
    }
}
