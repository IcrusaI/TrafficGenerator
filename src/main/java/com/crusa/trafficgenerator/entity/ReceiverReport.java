package com.crusa.trafficgenerator.entity;

import java.util.ArrayList;
import java.util.List;

public class ReceiverReport {
    private int totalReceive = 0;
    private int totalSizeData = 0;
    private List<String> log = new ArrayList<>();

    public int getTotalReceive() {
        return totalReceive;
    }

    public void setTotalReceive(int totalReceive) {
        this.totalReceive = totalReceive;
    }

    public void addTotalReceive() {
        setTotalReceive(totalReceive + 1);
    }

    public int getTotalSizeData() {
        return totalSizeData;
    }

    public void setTotalSizeData(int totalSizeData) {
        this.totalSizeData = totalSizeData;
    }

    public List<String> getLog() {
        return log;
    }

    public void addLog(String data) {
        log.add(data);
    }
}
