package com.fit2cloud.commons.server.model.billing;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by liqiang on 2018/12/11.
 */
public class BillingUsage implements Serializable {

    private BigDecimal usageCost = BigDecimal.ZERO;

    private String usageType; // Running Instance, Storage, Requests

    private BigDecimal usageQuality = BigDecimal.ONE;

    //usageQuality是否可以累加
    private boolean cumulative = true;

    private String usageDescription;

    public BigDecimal getUsageCost() {
        return usageCost;
    }

    public void setUsageCost(BigDecimal usageCost) {
        this.usageCost = usageCost;
    }

    public String getUsageType() {
        return usageType;
    }

    public void setUsageType(String usageType) {
        this.usageType = usageType;
    }

    public BigDecimal getUsageQuality() {
        return usageQuality;
    }

    public void setUsageQuality(BigDecimal usageQuality) {
        this.usageQuality = usageQuality;
    }

    public boolean isCumulative() {
        return cumulative;
    }

    public void setCumulative(boolean cumulative) {
        this.cumulative = cumulative;
    }

    public String getUsageDescription() {
        return usageDescription;
    }

    public void setUsageDescription(String usageDescription) {
        this.usageDescription = usageDescription;
    }

}

