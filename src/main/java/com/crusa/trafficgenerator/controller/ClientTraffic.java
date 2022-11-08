package com.crusa.trafficgenerator.controller;

import com.crusa.trafficgenerator.distribution.DistributionEnum;
import com.crusa.trafficgenerator.distribution.ErlangDistribution;
import com.crusa.trafficgenerator.entity.SenderReport;
import com.crusa.trafficgenerator.protocol.TCP.TCPSender;
import com.crusa.trafficgenerator.protocol.TypeProtocol;
import com.crusa.trafficgenerator.protocol.UDP.UDPSender;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;

public class ClientTraffic {
    private InetAddress address;
    private int port;
    private int size;

    private DistributionEnum distribution;
    // Задержка
    private int delay;
    // Эрланг
    private double shape;
    private double scale;
    // exponential
    private double mean;
    // uniform
    private double max;
    private double min;
    private TypeProtocol protocol;

    private Thread thread;

    private SenderReport report;

    public void setAddress(String address) throws UnknownHostException {
        this.address = InetAddress.getByName(address);
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSize(int size) {
        this.size = size;
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

    public void setMean(double mean) {
        this.mean = mean;
    }
    public void setMin(double min) {
        this.min = min;
    }
    public void setMax(double max) {
        this.max = max;
    }

    public void setProtocol(TypeProtocol protocol) {
        this.protocol = protocol;
    }

    public synchronized SenderReport getReport() {
        return report;
    }

    public void run() {
        report = new SenderReport();

        switch (protocol) {
            case UDP -> UDP();
            case TCP -> TCP();
        }


        thread.start();
    }

    public void UDP()  {
        byte[] buffer = generateBuffer(size);

        DatagramPacket packet = new DatagramPacket(
                buffer, size, address, port
        );

        UDPSender udpSender = new UDPSender();
        udpSender.setDistribution(distribution);
        switch (distribution) {
            case DELAY -> udpSender.setDelay(delay);
            case ERLANG -> {
                udpSender.setShape(shape);
                udpSender.setScale(scale);
            }
            case EXPONENTIAL -> udpSender.setMean(mean);
            case UNIFORM -> {
                udpSender.setMin(min);
                udpSender.setMax(max);
            }
        }
        udpSender.setPacket(packet);
        udpSender.setReport(report);

        thread = new Thread(udpSender);
    }


    private void TCP() {
        byte[] buffer = generateBuffer(size);

        DatagramPacket packet = new DatagramPacket(
                buffer, size, address, port
        );

        TCPSender tcpSender = new TCPSender();
        tcpSender.setDelay(delay);
        tcpSender.setPacket(packet);
        tcpSender.setReport(report);

        thread = new Thread(tcpSender);
    }

    public void destroy() {
        thread.interrupt();
    }

    private static byte[] generateBuffer(int size) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[size];
        random.nextBytes(bytes);

        return bytes;
    }
}

