package com.fit2cloud.commons.server.model;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @Author gin
 * @Date 2020/12/9 12:06 下午
 */
public class Authentication extends Authenticator {
    String username = null;
    String password = null;

    public Authentication() {
    }

    public Authentication(String username, String password) {
        this.username = username;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        PasswordAuthentication pa = new PasswordAuthentication(username, password);
        return pa;
    }
}
