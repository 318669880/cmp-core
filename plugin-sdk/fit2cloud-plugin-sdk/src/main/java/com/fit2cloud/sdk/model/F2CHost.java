package com.fit2cloud.sdk.model;
/**
 * 宿主机实体
 */
public class F2CHost {
	private String dataCenterId;
	private String dataCenterName;
	private String clusterId;
	private String clusterName;
	private String hostId;
	private String hostName;
	private String hostModel;
	private String hostVendor;
	private String cpuModel;
	private int cpuMHzPerOneCore;
	private int numCpuCores;
	private long cpuMHzTotal;
	private long cpuMHzAllocated;
	private long memoryTotal;
	private long memoryAllocated;
	private long vmTotal;
	private long vmRunning;
	private long vmStopped;
	private String hostIp;
	private String status;
	private String hypervisorType;
	private String hypervisorVersion;
	private int vmCpuCores;

	
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
	public String getHostId() {
		return hostId;
	}
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public long getCpuMHzTotal() {
		return cpuMHzTotal;
	}
	public void setCpuMHzTotal(long cpuMHzTotal) {
		this.cpuMHzTotal = cpuMHzTotal;
	}
	public long getCpuMHzAllocated() {
		return cpuMHzAllocated;
	}
	public void setCpuMHzAllocated(long cpuMHzAllocated) {
		this.cpuMHzAllocated = cpuMHzAllocated;
	}
	public long getMemoryTotal() {
		return memoryTotal;
	}
	public void setMemoryTotal(long memoryTotal) {
		this.memoryTotal = memoryTotal;
	}
	public long getMemoryAllocated() {
		return memoryAllocated;
	}
	public void setMemoryAllocated(long memoryAllocated) {
		this.memoryAllocated = memoryAllocated;
	}
	public String getHostIp() {
		return hostIp;
	}
	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getVmTotal() {
		return vmTotal;
	}
	public void setVmTotal(long vmTotal) {
		this.vmTotal = vmTotal;
	}
	public long getVmRunning() {
		return vmRunning;
	}
	public void setVmRunning(long vmRunning) {
		this.vmRunning = vmRunning;
	}
	public long getVmStopped() {
		return vmStopped;
	}
	public void setVmStopped(long vmStopped) {
		this.vmStopped = vmStopped;
	}
	public String getHypervisorType() {
		return hypervisorType;
	}
	public void setHypervisorType(String hypervisorType) {
		this.hypervisorType = hypervisorType;
	}
	public String getHypervisorVersion() {
		return hypervisorVersion;
	}
	public void setHypervisorVersion(String hypervisorVersion) {
		this.hypervisorVersion = hypervisorVersion;
	}

	public String getHostModel() {
		return hostModel;
	}

	public void setHostModel(String hostModel) {
		this.hostModel = hostModel;
	}

	public String getHostVendor() {
		return hostVendor;
	}

	public void setHostVendor(String hostVendor) {
		this.hostVendor = hostVendor;
	}

	public String getCpuModel() {
		return cpuModel;
	}

	public void setCpuModel(String cpuModel) {
		this.cpuModel = cpuModel;
	}

	public int getCpuMHzPerOneCore() {
		return cpuMHzPerOneCore;
	}

	public void setCpuMHzPerOneCore(int cpuMHzPerOneCore) {
		this.cpuMHzPerOneCore = cpuMHzPerOneCore;
	}

	public int getNumCpuCores() {
		return numCpuCores;
	}

	public void setNumCpuCores(int numCpuCores) {
		this.numCpuCores = numCpuCores;
	}
	public int getVmCpuCores() {
		return vmCpuCores;
	}
	public void setVmCpuCores(int vmCpuCores) {
		this.vmCpuCores = vmCpuCores;
	}
}
