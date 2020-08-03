package com.fit2cloud.mc.dto;

import com.fit2cloud.commons.server.base.domain.PluginWithBLOBs;

public class PluginInfoDTO extends PluginWithBLOBs {
    private String platformVersion;
    private String buildTime;
    private String pluginVersion;
    private String documentUrl;

    public String getPlatformVersion() {
        return platformVersion;
    }

    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(String buildTime) {
        this.buildTime = buildTime;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }
}
