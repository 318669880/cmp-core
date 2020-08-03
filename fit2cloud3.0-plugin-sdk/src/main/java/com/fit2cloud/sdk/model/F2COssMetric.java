package com.fit2cloud.sdk.model;

import java.math.BigDecimal;

/**
 * Created by dongbin on 2017/7/27.
 */
public class F2COssMetric {
    private String bucketName;
    private String region;
    private BigDecimal meteringStorageUtilization;
    private long meteringGetRequest;
    private long meteringPutRequest;
    private BigDecimal meteringInternetTX;
    private BigDecimal meteringCdnTX;
    private BigDecimal meteringSyncRX;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public BigDecimal getMeteringStorageUtilization() {
        return meteringStorageUtilization;
    }

    public void setMeteringStorageUtilization(BigDecimal meteringStorageUtilization) {
        this.meteringStorageUtilization = meteringStorageUtilization;
    }

    public long getMeteringGetRequest() {
        return meteringGetRequest;
    }

    public void setMeteringGetRequest(long meteringGetRequest) {
        this.meteringGetRequest = meteringGetRequest;
    }

    public long getMeteringPutRequest() {
        return meteringPutRequest;
    }

    public void setMeteringPutRequest(long meteringPutRequest) {
        this.meteringPutRequest = meteringPutRequest;
    }

    public BigDecimal getMeteringInternetTX() {
        return meteringInternetTX;
    }

    public void setMeteringInternetTX(BigDecimal meteringInternetTX) {
        this.meteringInternetTX = meteringInternetTX;
    }

    public BigDecimal getMeteringCdnTX() {
        return meteringCdnTX;
    }

    public void setMeteringCdnTX(BigDecimal meteringCdnTX) {
        this.meteringCdnTX = meteringCdnTX;
    }

    public BigDecimal getMeteringSyncRX() {
        return meteringSyncRX;
    }

    public void setMeteringSyncRX(BigDecimal meteringSyncRX) {
        this.meteringSyncRX = meteringSyncRX;
    }
}
