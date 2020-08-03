package com.fit2cloud.sdk.model;
/**
 * 云磁盘监控实体
 */
public class F2CDiskMetric {
	private String diskId;
	private double IOPSRead;	// ops/s
	private double IOPSWrite;	// ops/s
	private double BPSRead;		// byte/s
	private double BPSWrite; 	// byte/s
	private long timepoint;
	public String getDiskId() {
		return diskId;
	}
	public void setDiskId(String diskId) {
		this.diskId = diskId;
	}
	public double getIOPSRead() {
		return IOPSRead;
	}
	public void setIOPSRead(double iOPSRead) {
		IOPSRead = iOPSRead;
	}
	public double getIOPSWrite() {
		return IOPSWrite;
	}
	public void setIOPSWrite(double iOPSWrite) {
		IOPSWrite = iOPSWrite;
	}
	public double getBPSRead() {
		return BPSRead;
	}
	public void setBPSRead(double bPSRead) {
		BPSRead = bPSRead;
	}
	public double getBPSWrite() {
		return BPSWrite;
	}
	public void setBPSWrite(double bPSWrite) {
		BPSWrite = bPSWrite;
	}
	public long getTimepoint() {
		return timepoint;
	}
	public void setTimepoint(long timepoint) {
		this.timepoint = timepoint;
	}
}
