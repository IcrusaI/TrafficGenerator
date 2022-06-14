package com.crusa.trafficgenerator;

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
    private int delay;
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

    public void setDelay(int delay) {
        this.delay = delay;
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
        udpSender.setDelay(delay);
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

