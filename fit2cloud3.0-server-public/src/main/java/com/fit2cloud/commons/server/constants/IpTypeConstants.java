package com.fit2cloud.commons.server.constants;

public class IpTypeConstants {

    public enum IpType {
        ipv4, ipv6, DualStack
    }

    public enum IpSettingMethod {
        SystemSetting, ManualSetting
    }

    public enum IpSettingPolicy {
        Random, BigToSmall, SmallToBig, ApplicantSetting, VmBased
    }
}
