package com.fit2cloud.sdk.model;


public class F2CInstanceSize {

    private int cpu = 0;

    private long mem = 0;//GB

    private long disk = 0;//GB

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public long getMem() {
        return mem;
    }

    public void setMem(long mem) {
        this.mem = mem;
    }

    public long getDisk() {
        return disk;
    }

    public void setDisk(long disk) {
        this.disk = disk;
    }
}
