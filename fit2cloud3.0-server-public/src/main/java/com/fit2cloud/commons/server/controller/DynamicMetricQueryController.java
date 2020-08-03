package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.constants.PermissionConstants;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.DynamicMetricData;
import com.fit2cloud.commons.server.model.DynamicMetricRequest;
import com.fit2cloud.commons.server.service.DynamicMetricQueryService;
import com.fit2cloud.commons.server.swagger.annotation.ApiHasModules;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("dynamic")
@Api(tags = Translator.PREFIX + "i18n_swagger_metric_tag" + Translator.SUFFIX)
public class DynamicMetricQueryController {
    @Resource
    private DynamicMetricQueryService dynamicMetricQueryService;

    @PostMapping("metric/query")
    @ApiOperation(Translator.PREFIX + "i18n_swagger_metric_query" + Translator.SUFFIX)
    @ApiHasModules({"dbaas-configuration"})
    @RequiresPermissions(value = {PermissionConstants.METRIC_READ})
    public List<DynamicMetricData> queryMetric(@RequestBody DynamicMetricRequest request) {
        return dynamicMetricQueryService.queryMetricData(request);
    }
}
