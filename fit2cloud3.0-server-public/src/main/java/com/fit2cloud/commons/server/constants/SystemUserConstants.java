package com.fit2cloud.commons.server.constants;

import com.fit2cloud.commons.server.base.domain.User;

/**
 * Created by liqiang on 2018/10/10.
 */
public class SystemUserConstants extends User {

    private static User user = new User();

    static {
        user.setId("system");
        user.setName("SYSTEM");
        user.setLastSourceId("ADMIN");
    }

    public static User getUser() {
        return user;
    }

    public static String getUserId() {
        return user.getId();
    }

}
