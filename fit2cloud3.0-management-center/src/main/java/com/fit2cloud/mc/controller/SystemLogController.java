package com.fit2cloud.mc.controller;

import com.fit2cloud.commons.server.base.domain.SystemParameter;
import com.fit2cloud.commons.server.constants.ParamConstants;
import com.fit2cloud.commons.server.elastic.domain.SystemLog;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.service.SystemLogService;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.mc.common.constants.PermissionConstants;
import com.fit2cloud.mc.dto.request.SystemLogRequest;
import com.fit2cloud.mc.service.SystemParameterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("log/system")
@Api(tags = {Translator.PREFIX + "i18n_mc_system_log_tag" + Translator.SUFFIX})
public class SystemLogController {
    @Resource
    private SystemLogService systemLogService;
    @Resource
    private SystemParameterService systemParameterService;

    @PostMapping("{goPage}/{pageSize}")
    @RequiresPermissions(PermissionConstants.SYSTEM_LOG_READ)
    @ApiOperation(Translator.PREFIX + "i18n_mc_system_log_list" + Translator.SUFFIX)
    public Pager<List<SystemLog>> querySystemLog(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody SystemLogRequest request) {
        return systemLogService.querySystemLog(goPage, pageSize, BeanUtils.objectToMap(request));
    }

    @GetMapping("keep/months")
    @RequiresPermissions(PermissionConstants.SYSTEM_LOG_READ)
    public Integer getKeepMonths() {
        String value = systemParameterService.getValue(ParamConstants.Log.KEEP_MONTHS.getValue());
        if (StringUtils.isNotBlank(value)) {
            return Integer.valueOf(value);
        }
        // 如果数据库里没有这个值，默认3
        return 3;
    }

    @PostMapping("keep/months")
    @RequiresPermissions(PermissionConstants.SYSTEM_LOG_KEEP_MONTH_UPDATE)
    public void saveKeepMonths(@RequestBody SystemParameter systemParameter) {
        systemParameter.setParamKey(ParamConstants.Log.KEEP_MONTHS.getValue());
        systemParameterService.saveValue(systemParameter);
    }
}
