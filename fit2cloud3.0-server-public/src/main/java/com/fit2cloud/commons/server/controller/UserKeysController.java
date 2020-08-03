package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.base.domain.UserKey;
import com.fit2cloud.commons.server.constants.PermissionConstants;
import com.fit2cloud.commons.server.service.UserKeysService;
import com.fit2cloud.commons.server.utils.SessionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("user/key")
public class UserKeysController {

    @Resource
    private UserKeysService userKeysService;

    @RequestMapping("info")
    public List<UserKey> getUserKeysInfo() {
        String userId = SessionUtils.getUser().getId();
        return userKeysService.getUserKeysInfo(userId);
    }

    @RequestMapping("generate")
    public void generateUserKey() {
        String userId = SessionUtils.getUser().getId();
        userKeysService.generateUserKey(userId);
    }

    @RequestMapping("delete/{id}")
    public void deleteUserKey(@PathVariable String id) {
        userKeysService.deleteUserKey(id);
    }

    @RequestMapping("active/{id}")
    public void activeUserKey(@PathVariable String id) {
        userKeysService.activeUserKey(id);
    }

    @RequestMapping("disabled/{id}")
    public void disabledUserKey(@PathVariable String id) {
        userKeysService.disabledUserKey(id);
    }
}
