package com.fit2cloud.commons.server.model;


import com.fit2cloud.commons.server.constants.DashboardColor;

public class DashBoardTextDTO {

    private String name;
    private Object value;
    private String valueColor;

    public DashBoardTextDTO() {
    }

    public DashBoardTextDTO(String name, Object value) {
        this.name = name;
        this.value = value;
        this.valueColor = DashboardColor.BLACK.getColor();
    }

    public DashBoardTextDTO(String name, Object value, String valueColor) {
        this.name = name;
        this.value = value;
        this.valueColor = valueColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getValueColor() {
        return valueColor;
    }

    public void setValueColor(String valueColor) {
        this.valueColor = valueColor;
    }
}
