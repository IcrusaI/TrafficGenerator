package com.crusa.trafficgenerator.entity;

public class SenderReport {
    private int totalSend = 0;
    private int totalReceive = 0;

    public int getTotalSend() {
        return totalSend;
    }

    public void setTotalSend(int totalSend) {
        this.totalSend = totalSend;
    }

    public void addTotalSend() {
        setTotalSend(totalSend + 1);
    }

    public int getTotalReceive() {
        return totalReceive;
    }

    public void setTotalReceive(int totalReceive) {
        this.totalReceive = totalReceive;
    }

    public void addTotalReceive() {
        setTotalReceive(totalReceive + 1);
    }
}
