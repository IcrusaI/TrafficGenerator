package com.crusa.trafficgenerator.controller;

import com.crusa.trafficgenerator.entity.ReceiverReport;
import com.crusa.trafficgenerator.protocol.TypeProtocol;
import com.crusa.trafficgenerator.protocol.UDP.UDPReceiver;

public class ServerTraffic {
    private int port;
    private Thread thread;
    private TypeProtocol protocol;

    private ReceiverReport report;

    public void setPort(int port) {
        this.port = port;
    }

    public void setProtocol(TypeProtocol protocol) {
        this.protocol = protocol;
    }

    public synchronized ReceiverReport getReport() {
        return report;
    }

    public void run() {
        report = new ReceiverReport();

        switch (protocol) {
            case UDP -> UDP();
            case TCP -> TCP();
        }

        thread.start();
    }

    public void UDP() {
        UDPReceiver udpReceiver = new UDPReceiver();
        udpReceiver.setPort(port);
        udpReceiver.setReport(report);

        thread = new Thread(udpReceiver);
    }

    public void TCP() {
        TCPReceiver tcpReceiver = new TCPReceiver();
        tcpReceiver.setPort(port);

        thread = new Thread(tcpReceiver);
    }

    public void destroy() {
        thread.interrupt();
    }
}
