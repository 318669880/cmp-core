package com.fit2cloud.sdk.model;
/**
 * 数据存储实体
 */
public class F2CDataStore {
	private String dataCenterId;
	private String dataCenterName;
	private String clusterId;
	private String clusterName;
	private String dataStoreId;
	private String dataStoreName;
	private long capacity;
	private long freeSpace;
	private String status;
	private String type;
	private long lastUpdate;
	public String getDataCenterId() {
		return dataCenterId;
	}
	public void setDataCenterId(String dataCenterId) {
		this.dataCenterId = dataCenterId;
	}
	public String getDataCenterName() {
		return dataCenterName;
	}
	public void setDataCenterName(String dataCenterName) {
		this.dataCenterName = dataCenterName;
	}

	public String getClusterId() {
		return clusterId;
	}

	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getDataStoreId() {
		return dataStoreId;
	}
	public void setDataStoreId(String dataStoreId) {
		this.dataStoreId = dataStoreId;
	}
	public String getDataStoreName() {
		return dataStoreName;
	}
	public void setDataStoreName(String dataStoreName) {
		this.dataStoreName = dataStoreName;
	}
	public long getCapacity() {
		return capacity;
	}
	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}
	public long getFreeSpace() {
		return freeSpace;
	}
	public void setFreeSpace(long freeSpace) {
		this.freeSpace = freeSpace;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
