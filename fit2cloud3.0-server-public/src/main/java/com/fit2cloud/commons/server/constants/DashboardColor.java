package com.fit2cloud.commons.server.constants;


public enum DashboardColor {
    GREEN("green"),
    BLACK("black"),
    RED("red");


    private String color;

    DashboardColor(String rgb) {
        this.color = rgb;
    }

    public String getColor() {
        return color;
    }
}
