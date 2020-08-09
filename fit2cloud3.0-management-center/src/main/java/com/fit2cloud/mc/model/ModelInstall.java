package com.fit2cloud.mc.model;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/9 10:56 上午
 * @Description: Please Write notes scientifically
 */
public class ModelInstall extends ModelBasic{

    private Long installTime;

    private String revision;

    public Long getInstallTime() {
        return installTime;
    }

    public void setInstallTime(Long installTime) {
        this.installTime = installTime;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }
}
