package com.fit2cloud.commons.server.model;

import java.util.List;

/**
 * 首页 每个Card的信息，包括标题、参数等信息
 * <p>
 * 如果自定义的时候
 */
public class Card {

    /**
     * card 唯一,可设置UUID
     */
    private String id;

    /**
     * card 的标题
     */
    private String title;

    /**
     * 显示内容的指令、也可以自定义，导入自己的JS 里面加入指令就好
     */
    private String directive;

    /**
     * 模板页面
     */
    private String templateUrl;

    /**
     * 需要引入的js
     */
    private String templateJs;

    /**
     * card的位置，left、center、right
     */
    private String position;

    /**
     * 排序 升序
     */
    private Integer order;

    /**
     * card 跳转地址
     */
    private String url;

    /**
     * card 跳转信息
     */
    private String desc;

    /**
     * 是否显示
     */
    private boolean display = true;

    /**
     * 当前card的权限，
     */
    private List<String> permissions;

    /**
     * 权限的关系，AND OR
     */
    private String logical = "AND";

    /**
     * 高度，为定义高度为100px
     */
    private String height;

    private String setting;

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    private String minHeight;

    public String getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(String minHeight) {
        this.minHeight = minHeight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirective() {
        return directive;
    }

    public void setDirective(String directive) {
        this.directive = directive;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

    public String getTemplateJs() {
        return templateJs;
    }

    public void setTemplateJs(String templateJs) {
        this.templateJs = templateJs;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public String getLogical() {
        return logical;
    }

    public void setLogical(String logical) {
        this.logical = logical;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
