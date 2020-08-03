package com.fit2cloud.sdk.model;

/**
 * 分配 IP 请求实体
 */
public class AllocateIpRequest extends ExecuteScriptRequest {

    public static final String NEW_IP_PLACEHOLDER = "@\\[NEW_IP\\]";
    public static final String NEW_IPV6_PLACEHOLDER = "@\\[NEW_IPV6\\]";
    public static final String DEVICE_PLACEHOLDER = "@\\[NET_DEVICE\\]";
    public static final String NET_TYPE_PLACEHOLDER = "@\\[NET_TYPE\\]";
    public static final String NET_MASK_PLACEHOLDER = "@\\[NET_MASK\\]";
    public static final String GATEWAY_PLACEHOLDER = "@\\[NET_GATEWAY\\]";
    public static final String DNS1 = "@\\[DNS1\\]";
    public static final String DNS2 = "@\\[DNS2\\]";

    private String networkDevice;
    private String networkType;
    private String mask;
    private String gateway;
    private String dns1;
    private String dns2;

    private String ipv6Mask;
    private String ipv6Gateway;
    private String ipv6Dns1;
    private String ipv6Dns2;
    private String ipType;

    public String getNetworkDevice() {
        return networkDevice;
    }

    public void setNetworkDevice(String networkDevice) {
        this.networkDevice = networkDevice;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getDns1() {
        return dns1;
    }

    public void setDns1(String dns1) {
        this.dns1 = dns1;
    }

    public String getDns2() {
        return dns2;
    }

    public void setDns2(String dns2) {
        this.dns2 = dns2;
    }

    public String getIpv6Mask() {
        return ipv6Mask;
    }

    public void setIpv6Mask(String ipv6Mask) {
        this.ipv6Mask = ipv6Mask;
    }

    public String getIpv6Gateway() {
        return ipv6Gateway;
    }

    public void setIpv6Gateway(String ipv6Gateway) {
        this.ipv6Gateway = ipv6Gateway;
    }

    public String getIpv6Dns1() {
        return ipv6Dns1;
    }

    public void setIpv6Dns1(String ipv6Dns1) {
        this.ipv6Dns1 = ipv6Dns1;
    }

    public String getIpv6Dns2() {
        return ipv6Dns2;
    }

    public void setIpv6Dns2(String ipv6Dns2) {
        this.ipv6Dns2 = ipv6Dns2;
    }

    public String getIpType() {
        return ipType;
    }

    public void setIpType(String ipType) {
        this.ipType = ipType;
    }
}
