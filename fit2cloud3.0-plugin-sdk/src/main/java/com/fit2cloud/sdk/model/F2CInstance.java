package com.fit2cloud.sdk.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * FIT2CLOUD定义的虚机模型
 */
public class F2CInstance extends F2CResource {
	private String name;
	private String imageId;
	private String os;
	private String instanceId;
	private String instanceStatus;
	private String instanceType;
	private String instanceTypeDescription;
	private String region;
	private String zone;
	private String remoteIP;
	private String localIP;
	private String remoteIPV6;
	private String localIPV6;
	private String description;
	private Date created;
	private Date expired;
	private String hostname;
	private String customData;
	private String dataCenter;
	private String cluster;
	private String host;
	private int cpu;		// core num
	private int memory;		// GB
	private int disk;		// GB
	private int cpuUsed;		// MHZ
	private int memoryUsed;		// MB
	private long diskUsed;		// GB
	private String instanceUUID;
	private Long createTime;	// ms
	private Long expiredTime;	// ms
	private String macAddress;
	private List<String> ipArray;
	private String datastoreName;
	private String datastoreType;
	private String vmtoolsVersion;

	private Long keypasswordId;
	private int sshPort;
	private String sshUser;
	//用于海通项目，保存虚拟机的projectId，海通中的OpenStack和工作空间一一对应，所以这里相当于同步虚拟机的工作空间
	private String projectId;
	private List<String> volumes;
	private String vpcId;
	private String subnetId;
	private String networkInterfaceId;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public String getInstanceStatus() {
		return instanceStatus;
	}
	public void setInstanceStatus(String instanceStatus) {
		this.instanceStatus = instanceStatus;
	}
	public String getInstanceType() {
		return instanceType;
	}
	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
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
	public String getRemoteIP() {
		return remoteIP;
	}
	public void setRemoteIP(String remoteIP) {
		this.remoteIP = remoteIP;
	}
	public String getLocalIP() {
		return localIP;
	}
	public void setLocalIP(String localIP) {
		this.localIP = localIP;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getExpired() {
		return expired;
	}
	public void setExpired(Date expired) {
		this.expired = expired;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getCustomData() {
		return customData;
	}
	public void setCustomData(String customData) {
		this.customData = customData;
	}
	public Long getKeypasswordId() {
		return keypasswordId;
	}
	public void setKeypasswordId(Long keypasswordId) {
		this.keypasswordId = keypasswordId;
	}
	public int getSshPort() {
		return sshPort;
	}
	public void setSshPort(int sshPort) {
		this.sshPort = sshPort;
	}
	public String getSshUser() {
		return sshUser;
	}
	public void setSshUser(String sshUser) {
		this.sshUser = sshUser;
	}
	public String getInstanceTypeDescription() {
		return instanceTypeDescription;
	}
	public void setInstanceTypeDescription(String instanceTypeDescription) {
		this.instanceTypeDescription = instanceTypeDescription;
	}
	public String getDataCenter() {
		return dataCenter;
	}
	public void setDataCenter(String dataCenter) {
		this.dataCenter = dataCenter;
	}
	public String getCluster() {
		return cluster;
	}
	public void setCluster(String cluster) {
		this.cluster = cluster;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getCpu() {
		return cpu;
	}
	public void setCpu(int cpu) {
		this.cpu = cpu;
	}
	public int getMemory() {
		return memory;
	}
	public void setMemory(int memory) {
		this.memory = memory;
	}
	public int getDisk() {
		return disk;
	}
	public void setDisk(int disk) {
		this.disk = disk;
	}
	public String getInstanceUUID() {
		return instanceUUID;
	}
	public void setInstanceUUID(String instanceUUID) {
		this.instanceUUID = instanceUUID;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public Long getExpiredTime() {
		return expiredTime;
	}
	public void setExpiredTime(Long expiredTime) {
		this.expiredTime = expiredTime;
	}
	public List<String> getIpArray() {
		return null == ipArray ? ipArray = new ArrayList<String>() : ipArray;
	}
	public void setIpArray(List<String> ipArray) {
		this.ipArray = ipArray;
	}
	public String getDatastoreName() {
		return datastoreName;
	}
	public void setDatastoreName(String datastoreName) {
		this.datastoreName = datastoreName;
	}
	public int getCpuUsed() {
		return cpuUsed;
	}
	public void setCpuUsed(int cpuUsed) {
		this.cpuUsed = cpuUsed;
	}
	public int getMemoryUsed() {
		return memoryUsed;
	}
	public void setMemoryUsed(int memoryUsed) {
		this.memoryUsed = memoryUsed;
	}
	public long getDiskUsed() {
		return diskUsed;
	}
	public void setDiskUsed(long diskUsed) {
		this.diskUsed = diskUsed;
	}
	public String getDatastoreType() {
		return datastoreType;
	}
	public void setDatastoreType(String datastoreType) {
		this.datastoreType = datastoreType;
	}
	public String getVmtoolsVersion() {
		return vmtoolsVersion;
	}
	public void setVmtoolsVersion(String vmtoolsVersion) {
		this.vmtoolsVersion = vmtoolsVersion;
	}

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

	public List<String> getVolumes() {
		return volumes;
	}

	public void setVolumes(List<String> vloumes) {
		this.volumes = vloumes;
	}

	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getRemoteIPV6() {
		return remoteIPV6;
	}

	public void setRemoteIPV6(String remoteIPV6) {
		this.remoteIPV6 = remoteIPV6;
	}

	public String getLocalIPV6() {
		return localIPV6;
	}

	public void setLocalIPV6(String localIPV6) {
		this.localIPV6 = localIPV6;
	}

	public String getVpcId() {
		return vpcId;
	}

	public void setVpcId(String vpcId) {
		this.vpcId = vpcId;
	}

	public String getSubnetId() {
		return subnetId;
	}

	public void setSubnetId(String subnetId) {
		this.subnetId = subnetId;
	}

	public String getNetworkInterfaceId() {
		return networkInterfaceId;
	}

	public void setNetworkInterfaceId(String networkInterfaceId) {
		this.networkInterfaceId = networkInterfaceId;
	}
}
