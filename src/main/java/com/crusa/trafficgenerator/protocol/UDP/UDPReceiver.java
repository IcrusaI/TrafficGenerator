package com.crusa.trafficgenerator.protocol.UDP;

import com.crusa.trafficgenerator.entity.ReceiverReport;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;

public class UDPReceiver implements Runnable {
    private int port;

    private ReceiverReport report;

    public void setPort(int port) {
        this.port = port;
    }

    public void setReport(ReceiverReport report) {
        this.report = report;
    }

    public void run() {
        DatagramSocket serverSocket;

        try {
            serverSocket = new DatagramSocket(port);

            synchronized (report) {
                report.addLog("Listening on udp:%s:%d%n".formatted(InetAddress.getLocalHost().getHostAddress(), port));
            }

            while (!Thread.currentThread().isInterrupted()) {
                byte[] receiveData = new byte[1500];

                DatagramPacket receivePacket = new DatagramPacket(receiveData,
                        receiveData.length);

                serverSocket.receive(receivePacket);
                String sentence = new String(receiveData);

                synchronized (report) {
                    String date = LocalDateTime.now().toString();

                    report.addLog("[UDP] " + date + ": " + receivePacket.getLength() + " байта");

                    report.addTotalReceive();

                    int size = report.getTotalSizeData();
                    report.setTotalSizeData(size + receivePacket.getLength());

                    report.notifyAll();
                }
            }

            serverSocket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
