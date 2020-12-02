package com.fit2cloud.mc.controller;

import com.fit2cloud.commons.server.model.UserNotificationSettingDTO;
import com.fit2cloud.commons.server.service.UserNotificationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author gin
 * @Date 2020/12/2 11:10 上午
 */
@RequestMapping("userNotification")
@RestController
public class UserNotificationController {
    @Resource
    private UserNotificationService userNotificationService;

    @PostMapping("getNotification/{id}")
    public UserNotificationSettingDTO getNotification(@PathVariable String id) {
        return userNotificationService.getUserNotification(id);
    }

    @PostMapping("save")
    public void save(@RequestBody UserNotificationSettingDTO userNotificationSettingDTO) {
        userNotificationService.updateUserNotification(userNotificationSettingDTO);
    }
}
