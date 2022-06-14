package com.crusa.trafficgenerator.protocol.UDP;

import com.crusa.trafficgenerator.SenderReport;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPSender implements Runnable {
    private int delay;
    private DatagramPacket packet;

    private SenderReport report;

    public void setPacket(DatagramPacket packet) {
        this.packet = packet;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setReport(SenderReport report) {
        this.report = report;
    }

    @Override
    public void run() {
        while (true) {
            DatagramSocket datagramSocket = null;

            try {
                datagramSocket = new DatagramSocket();
                datagramSocket.send(packet);

                synchronized (report) {
                    report.addTotalSend();
                    report.notify();
                }

                Thread.sleep(delay);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException exit) {
                break;
            }
        }
    }
}

