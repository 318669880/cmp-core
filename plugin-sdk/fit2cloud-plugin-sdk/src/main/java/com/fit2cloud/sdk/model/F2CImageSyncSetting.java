package com.fit2cloud.sdk.model;

public class F2CImageSyncSetting extends F2CResource {
    private String regionId;
    private String osType;
    private String imageType;

    public F2CImageSyncSetting() {
    }

    public F2CImageSyncSetting(String regionId, String osType, String imageType) {
        this.regionId = regionId;
        this.osType = osType;
        this.imageType = imageType;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }
}
