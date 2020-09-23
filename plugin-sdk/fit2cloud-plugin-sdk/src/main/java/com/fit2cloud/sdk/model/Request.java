package com.fit2cloud.sdk.model;

import com.fit2cloud.sdk.constants.Language;

/**
 * Created by zhangbohan on 15/8/3.
 */
public class Request {
    private String credential;
    private String regionId;
    private Language language = Language.zh_CN;

    public Request() {
    }

    public Request(String credential, String regionId, Language language) {
        this.credential = credential;
        this.regionId = regionId;
        this.language = language;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}