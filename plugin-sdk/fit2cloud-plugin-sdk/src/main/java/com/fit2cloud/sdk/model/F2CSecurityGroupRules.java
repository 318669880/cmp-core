package com.fit2cloud.sdk.model;

public class F2CSecurityGroupRules extends F2CResource {
    private int fromPort;
    private int toPort;
    private String ipRanges;
    private String prefixListId;
    private String userId;
    private String groupName;
    private String source;
    private String destination;

    private String requestId;

    private String regionId;

    private String securityGroupId;

    private String description;

    private String securityGroupName;

    private String vpcId;

    private String ipProtocol;

    private String portRange;

    private String sourceGroupId;

    private String sourceGroupName;

    private String sourceCidrIp;

    private String policy;

    private String nicType;

    private String sourceGroupOwnerAccount;

    private String destGroupId;

    private String destGroupName;

    private String destCidrIp;

    private String destGroupOwnerAccount;

    private String priority;

    private String direction;
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getIpProtocol() {
        return this.ipProtocol;
    }

    public void setIpProtocol(String ipProtocol) {
        this.ipProtocol = ipProtocol;
    }

    public String getPortRange() {
        return this.portRange;
    }

    public void setPortRange(String portRange) {
        this.portRange = portRange;
    }

    public String getSourceGroupId() {
        return this.sourceGroupId;
    }

    public void setSourceGroupId(String sourceGroupId) {
        this.sourceGroupId = sourceGroupId;
    }

    public String getSourceGroupName() {
        return this.sourceGroupName;
    }

    public void setSourceGroupName(String sourceGroupName) {
        this.sourceGroupName = sourceGroupName;
    }

    public String getSourceCidrIp() {
        return this.sourceCidrIp;
    }

    public void setSourceCidrIp(String sourceCidrIp) {
        this.sourceCidrIp = sourceCidrIp;
    }

    public String getPolicy() {
        return this.policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getNicType() {
        return this.nicType;
    }

    public void setNicType(String nicType) {
        this.nicType = nicType;
    }

    public String getSourceGroupOwnerAccount() {
        return this.sourceGroupOwnerAccount;
    }

    public void setSourceGroupOwnerAccount(String sourceGroupOwnerAccount) {
        this.sourceGroupOwnerAccount = sourceGroupOwnerAccount;
    }

    public String getDestGroupId() {
        return this.destGroupId;
    }

    public void setDestGroupId(String destGroupId) {
        this.destGroupId = destGroupId;
    }

    public String getDestGroupName() {
        return this.destGroupName;
    }

    public void setDestGroupName(String destGroupName) {
        this.destGroupName = destGroupName;
    }

    public String getDestCidrIp() {
        return this.destCidrIp;
    }

    public void setDestCidrIp(String destCidrIp) {
        this.destCidrIp = destCidrIp;
    }

    public String getDestGroupOwnerAccount() {
        return this.destGroupOwnerAccount;
    }

    public void setDestGroupOwnerAccount(String destGroupOwnerAccount) {
        this.destGroupOwnerAccount = destGroupOwnerAccount;
    }

    public String getPriority() {
        return this.priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDirection() {
        return this.direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRegionId() {
        return this.regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getSecurityGroupId() {
        return this.securityGroupId;
    }

    public void setSecurityGroupId(String securityGroupId) {
        this.securityGroupId = securityGroupId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSecurityGroupName() {
        return this.securityGroupName;
    }

    public void setSecurityGroupName(String securityGroupName) {
        this.securityGroupName = securityGroupName;
    }

    public String getVpcId() {
        return this.vpcId;
    }

    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }
}