package com.crusa.trafficgenerator.protocol.UDP;

import com.crusa.trafficgenerator.distribution.DistributionEnum;
import com.crusa.trafficgenerator.distribution.ErlangDistribution;
import com.crusa.trafficgenerator.entity.SenderReport;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPSender implements Runnable {
    private DistributionEnum distribution;
    // Задержка
    private int delay;
    // Эрланг
    private double shape;
    private double scale;
    private DatagramPacket packet;

    private SenderReport report;

    public void setPacket(DatagramPacket packet) {
        this.packet = packet;
    }

    public void setDistribution(DistributionEnum distribution) {
        this.distribution = distribution;
    }
    public void setDelay(int delay) {
        this.delay = delay;
    }
    public void setShape(double shape) {
        this.shape = shape;
    }
    public void setScale(double scale) {
        this.scale = scale;
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

                double delay;
                switch (distribution) {
                    case ERLANG -> delay = ErlangDistribution.erlang(shape, scale) * 1000;
                    case DELAY -> delay = this.delay;
                    default -> delay = 0;
                }

                System.out.println((int) delay);

                Thread.sleep((int) delay);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException exit) {
                break;
            }
        }
    }
}

