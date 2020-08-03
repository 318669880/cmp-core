package com.fit2cloud.commons.server.model.billing;

import com.fit2cloud.commons.server.constants.BillingRollupType;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by liqiang on 2018/12/11.
 */
public class BillingItem implements Serializable {

    private String moduleId;

    private String workspaceId;

    private String organizationId;

    private long billingTime;

    private BillingRollupType billingRollupType = BillingRollupType.HOURLY;

    private List<BillingUsage> billingUsages = Collections.EMPTY_LIST;

    private List<BillingTag> billingTags = Collections.EMPTY_LIST;

    private BillingResource billingResource;

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public long getBillingTime() {
        return billingTime;
    }

    public void setBillingTime(long billingTime) {
        this.billingTime = billingTime;
    }

    public BillingRollupType getBillingRollupType() {
        return billingRollupType;
    }

    public void setBillingRollupType(BillingRollupType billingRollupType) {
        this.billingRollupType = billingRollupType;
    }

    public List<BillingUsage> getBillingUsages() {
        return billingUsages;
    }

    public void setBillingUsages(List<BillingUsage> billingUsages) {
        this.billingUsages = billingUsages;
    }

    public List<BillingTag> getBillingTags() {
        return billingTags;
    }

    public void setBillingTags(List<BillingTag> billingTags) {
        this.billingTags = billingTags;
    }

    public BillingResource getBillingResource() {
        return billingResource;
    }

    public void setBillingResource(BillingResource billingResource) {
        this.billingResource = billingResource;
    }
}
