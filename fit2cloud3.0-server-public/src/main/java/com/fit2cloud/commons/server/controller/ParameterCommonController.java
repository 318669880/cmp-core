package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.service.ParameterCommonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Author: chunxing
 * Date: 2018/7/18  下午2:49
 * Description:
 */
@RestController
public class ParameterCommonController {

    @Resource
    private ParameterCommonService parameterCommonService;

    @GetMapping("ui/info")
    public Object uiInfo() {
        return parameterCommonService.uiInfo();
    }
}
