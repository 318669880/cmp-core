package com.fit2cloud.commons.server.model;

import com.fit2cloud.commons.server.base.domain.User;

/**
 * @Author gin
 * @Date 2020/12/2 10:55 上午
 */
public class UserNotificationSettingDTO extends User {
    private String wechatAccount;

    public String getWechatAccount() {
        return wechatAccount;
    }

    public void setWechatAccount(String wechatAccount) {
        this.wechatAccount = wechatAccount;
    }
}
