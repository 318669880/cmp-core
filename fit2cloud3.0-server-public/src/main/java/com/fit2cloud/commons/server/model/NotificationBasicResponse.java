package com.fit2cloud.commons.server.model;

/**
 * @Author gin
 * @Date 2020/10/29 2:10 下午
 */
public class NotificationBasicResponse {
    private Long errcode;
    private String errmsg;

    public Long getErrcode() {
        return errcode;
    }

    public void setErrcode(Long errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}
