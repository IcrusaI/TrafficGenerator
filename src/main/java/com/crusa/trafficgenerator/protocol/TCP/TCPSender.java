package com.crusa.trafficgenerator.protocol.TCP;

import com.crusa.trafficgenerator.entity.SenderReport;

import java.io.*;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.Socket;

public class TCPSender implements Runnable {
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
        Socket clientSocket = null;

        while(!Thread.interrupted()) {

            try {
                clientSocket = new Socket(packet.getAddress(), packet.getPort());

                clientSocket.setSendBufferSize(packet.getLength());

                OutputStream outputStream = clientSocket.getOutputStream();
                PrintWriter out = new PrintWriter(outputStream, true);
                out.write(new String(packet.getData()).toCharArray());
                out.flush();

                synchronized (report) {
                    report.addTotalSend();
                    report.notify();
                }

                InputStream inputStream = clientSocket.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                String answer = in.readLine();

                synchronized (report) {
                    report.addTotalReceive();
                    report.notify();
                }

                clientSocket.close();

                Thread.sleep(delay);
            } catch (ConnectException e) {
                System.out.println(e.getMessage()); //todo калбек в ui
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException exit) {
                break;
            }
        }

        Thread.currentThread().interrupt();
    }
}

