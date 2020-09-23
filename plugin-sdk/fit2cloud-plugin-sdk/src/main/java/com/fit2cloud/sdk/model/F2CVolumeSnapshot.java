package com.fit2cloud.sdk.model;

/**
 * FIT2CLOUD定义的磁盘快照
 */
public class F2CVolumeSnapshot extends F2CResource {
    private String snapshotId;
    private String name;
    private String state;
    private int size;
    private Integer volumeNum;
    private String volumeId;
    private String volumeName;
    private String regionId;

    public String getRegionId() {
        return this.regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(String volumeId) {
        this.volumeId = volumeId;
    }

    public String getVolumeName() {
        return volumeName;
    }

    public void setVolumeName(String volumeName) {
        this.volumeName = volumeName;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public Integer getVolumeNum() {
        return volumeNum;
    }

    public void setVolumeNum(Integer volumeNum) {
        this.volumeNum = volumeNum;
    }

}
