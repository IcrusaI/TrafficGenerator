package com.crusa.trafficgenerator.protocol.TCP;

import com.crusa.trafficgenerator.SenderReport;

import java.io.*;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Arrays;

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

        while (true) {

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
    }
}

