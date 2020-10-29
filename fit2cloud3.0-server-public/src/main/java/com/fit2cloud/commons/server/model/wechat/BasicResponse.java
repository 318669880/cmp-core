package com.fit2cloud.commons.server.model.wechat;

/**
 * @Author gin
 * @Date 2020/10/29 2:10 下午
 */
public class BasicResponse {
    private Integer errcode;
    private String errmsg;

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}
