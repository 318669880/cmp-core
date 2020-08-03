package com.fit2cloud.sdk.model;

import java.math.BigDecimal;

/**
 * Created by dongbin on 2017/7/27.
 */
public class F2CSlbMetric {
    private int packetTX;
    private int packetRX;
    private BigDecimal trafficRXNew;
    private BigDecimal trafficTXNew;
    private int activeConnection;
    private int inactiveConnection;
    private int newConnection;
    private int maxConnection;

    public int getPacketTX() {
        return packetTX;
    }

    public void setPacketTX(int packetTX) {
        this.packetTX = packetTX;
    }

    public int getPacketRX() {
        return packetRX;
    }

    public void setPacketRX(int packetRX) {
        this.packetRX = packetRX;
    }

    public BigDecimal getTrafficRXNew() {
        return trafficRXNew;
    }

    public void setTrafficRXNew(BigDecimal trafficRXNew) {
        this.trafficRXNew = trafficRXNew;
    }

    public BigDecimal getTrafficTXNew() {
        return trafficTXNew;
    }

    public void setTrafficTXNew(BigDecimal trafficTXNew) {
        this.trafficTXNew = trafficTXNew;
    }

    public int getActiveConnection() {
        return activeConnection;
    }

    public void setActiveConnection(int activeConnection) {
        this.activeConnection = activeConnection;
    }

    public int getInactiveConnection() {
        return inactiveConnection;
    }

    public void setInactiveConnection(int inactiveConnection) {
        this.inactiveConnection = inactiveConnection;
    }

    public int getNewConnection() {
        return newConnection;
    }

    public void setNewConnection(int newConnection) {
        this.newConnection = newConnection;
    }

    public int getMaxConnection() {
        return maxConnection;
    }

    public void setMaxConnection(int maxConnection) {
        this.maxConnection = maxConnection;
    }
}
