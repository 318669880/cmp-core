package com.fit2cloud.sdk.model;

import com.fit2cloud.sdk.constants.DeleteWithInstance;
import org.apache.commons.lang.StringUtils;

/**
 * 云磁盘实体
 */
public class F2CDisk extends F2CResource {
    private String region;
    private String zone;
    private String diskId;
    private String diskName;
    private String instanceUuid;
    private String diskType;
    private String diskMode;
    private String category;
    private String status;
    private String diskChargeType;
    private String description;
    private long size;    // GB
    private String device;
    private String datastoreUniqueId;
    private long createTime;
    private String instanceName;
    private String datastoreName;
    private long newSize;
    private String projectId;
    private boolean bootable;
    private String deleteWithInstance;

    public boolean isDeleteDiskWithInstance(){
        if(StringUtils.isNotEmpty(getDeleteWithInstance()) && getDeleteWithInstance().equalsIgnoreCase(DeleteWithInstance.YES.name())){
            return true;
        }else {
            return false;
        }
    }

    public String getInstanceUuid() {
        return instanceUuid;
    }

    public void setInstanceUuid(String instanceUuid) {
        this.instanceUuid = instanceUuid;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getDiskId() {
        return diskId;
    }

    public void setDiskId(String diskId) {
        this.diskId = diskId;
    }

    public String getDiskName() {
        return diskName;
    }

    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    public String getDiskType() {
        return diskType;
    }

    public void setDiskType(String diskType) {
        this.diskType = diskType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDiskChargeType() {
        return diskChargeType;
    }

    public void setDiskChargeType(String diskChargeType) {
        this.diskChargeType = diskChargeType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getDatastoreUniqueId() {
        return datastoreUniqueId;
    }

    public void setDatastoreUniqueId(String datastoreUniqueId) {
        this.datastoreUniqueId = datastoreUniqueId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getDatastoreName() {
        return datastoreName;
    }

    public void setDatastoreName(String datastoreName) {
        this.datastoreName = datastoreName;
    }

    public String getDiskMode() {
        return diskMode;
    }

    public void setDiskMode(String diskMode) {
        this.diskMode = diskMode;
    }

    public long getNewSize() {
        return newSize;
    }

    public void setNewSize(long newSize) {
        this.newSize = newSize;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public boolean isBootable() {
        return bootable;
    }

    public void setBootable(boolean bootable) {
        this.bootable = bootable;
    }

    public String getDeleteWithInstance() {
        return deleteWithInstance;
    }

    public void setDeleteWithInstance(String deleteWithInstance) {
        this.deleteWithInstance = deleteWithInstance;
    }

}