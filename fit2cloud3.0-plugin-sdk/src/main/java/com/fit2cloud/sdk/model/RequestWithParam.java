package com.fit2cloud.sdk.model;

import com.fit2cloud.sdk.constants.Language;

/**
 * Created by zhangbohan on 15/8/3.
 */
public class RequestWithParam extends Request {
    private String param;

    public RequestWithParam() {
    }

    public RequestWithParam(String credential, String regionId, Language language, String param) {
        super(credential, regionId, language);
        this.param = param;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}