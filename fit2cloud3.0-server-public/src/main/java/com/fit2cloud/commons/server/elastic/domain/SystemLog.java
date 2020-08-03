package com.fit2cloud.commons.server.elastic.domain;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "fit2cloud-cmp-logs", type = "log")
@Setting(settingPath = "/elastic-config/system-log-settings.json")
@Mapping(mappingPath = "/elastic-config/system-log-mapping.json")
public class SystemLog {
    @Id
    private String id;
    @ApiModelProperty("主机")
    private String host;
    @ApiModelProperty("级别")
    private String level;
    @ApiModelProperty("时间")
    private Long logTime;
    @ApiModelProperty("模块")
    private String module;
    @ApiModelProperty("详情")
    private String message;
    @ApiModelProperty("线程")
    private String thread;
    @ApiModelProperty("LOGGER")
    private String logger;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Long getLogTime() {
        return logTime;
    }

    public void setLogTime(Long logTime) {
        this.logTime = logTime;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public String getThread() {
        return thread;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public String getLogger() {
        return logger;
    }
}
