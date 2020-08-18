package com.fit2cloud.mc.common.constants;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/13 4:06 下午
 * @Description: Please Write notes scientifically
 */
public enum ModuleStatusConstants {
    readyInstall("readyInstall"),
    installing("installing","running","installFaild"),
    installFaild("installFaild"),
    running("running"),

    startting("startting","running","startFaild"),
    startFaild("startFaild"),

    stopping("stopping","stopped","running"),
    stopFaild("stopFaild"),
    stopped("stopped");

    private String value;

    private String nextSuccess;

    private String nextFaild;

    ModuleStatusConstants(String value, String nextSuccess, String nextFaild) {
        this.value = value;
        this.nextSuccess = nextSuccess;
        this.nextFaild = nextFaild;
    }

    ModuleStatusConstants(String value) {
        this.value = value;
    }

    public String value(){
        return this.value;
    }
    public String nextSuccess(){
        return  this.nextSuccess;
    }
    public String nextFaild(){
        return this.nextFaild;
    }
}
