package com.fit2cloud.mc.controller;

import com.fit2cloud.commons.server.base.domain.SystemParameter;
import com.fit2cloud.commons.server.constants.ParamConstants;
import com.fit2cloud.mc.common.constants.PermissionConstants;
import com.fit2cloud.mc.service.SystemParameterService;
import io.swagger.annotations.Api;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

/**
 * Author: chunxing
 * Date: 2018/6/21  下午6:30
 * Description:
 */
@RequestMapping("system/parameter")
@RestController
public class SystemParameterController {


    @Resource
    private SystemParameterService systemParameterService;

    @GetMapping("/keyCloak/info")
    @RequiresPermissions(PermissionConstants.KEYCLOAK_SETTING_READ)
    public Object keyCloakInfo() {
        return systemParameterService.keyCloakInfo(ParamConstants.Classify.KEYCLOAK.getValue());
    }

    @PostMapping("/keyCloak/info")
    @RequiresPermissions(value = PermissionConstants.KEYCLOAK_SETTING_READ)
    public void editKeyCloakInfo(@RequestBody List<SystemParameter> parameters) {
        systemParameterService.editKeyCloakInfo(parameters);
    }

    @GetMapping("/ui/info")
    @RequiresPermissions(PermissionConstants.UI_SETTING_READ)
    public Object uiInfo() throws InvocationTargetException, IllegalAccessException {
        return systemParameterService.uiInfo(ParamConstants.Classify.UI.getValue());
    }

    @PostMapping("/ui/info")
    @RequiresPermissions(PermissionConstants.UI_SETTING_EDIT)
    public void editUiInfo(@RequestParam(value = "file", required = false) MultipartFile[] files, @RequestParam(value = "parameter") String parameter) throws IOException {
        systemParameterService.editUiInfo(files, parameter);
    }

    @GetMapping("/message/info/{type}")
    @RequiresPermissions(PermissionConstants.MESSAGE_SETTING_READ)
    public Object mailInfo(@PathVariable String type) {
        return systemParameterService.messageInfo(type);
    }


    @PostMapping("/mail/info/enable")
    @RequiresPermissions(PermissionConstants.MESSAGE_SETTING_EDIT)
    public void editMailInfoEnable(@RequestBody SystemParameter parameter) {
        systemParameterService.editMailInfoAble(parameter);
    }

    @PostMapping("/message/info/{type}")
    @RequiresPermissions(PermissionConstants.MESSAGE_SETTING_EDIT)
    public void editMailInfo(@PathVariable String type, @RequestBody List<SystemParameter> parameters) {
        systemParameterService.editMessageInfo(parameters, type);
    }

    @PostMapping("/message/testConnection/{type}")
    public void testConnection(@PathVariable String type, @RequestBody HashMap<String, String> hashMap) throws Exception {
        switch (type) {
            case "mail":
                systemParameterService.testConnection(hashMap);
                break;
            case "wechat":
                systemParameterService.testWechat(hashMap);
                break;
            case "dingtalk":
                systemParameterService.testDingtalk(hashMap);
                break;
            default:
                break;
        }
    }
}
