package com.crusa.trafficgenerator;

import com.crusa.trafficgenerator.protocol.TypeProtocol;
import com.crusa.trafficgenerator.protocol.UDP.UDPReceiver;

import java.io.*;
import java.net.*;

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

class TCPReceiver implements Runnable {
    int port;
    ServerSocket serverSocket;

    public TCPReceiver() {}

    public TCPReceiver(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void run() {
        //создаем объект сервер-сокет
        try {
            createSocket();

            // в цикле обрабатываем входящие соединения.
            while (true)
            {
                handler();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handler() throws IOException {
        Socket socket = serverSocket.accept();

        InputStream inputStream = socket.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String message = in.readLine();

        OutputStream outputStream = socket.getOutputStream();
        PrintWriter out = new PrintWriter(outputStream, true);
        out.println(message);
        out.flush();

               /* //читаем сообщение
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                String message = in.readLine();

                //придумываем ответ – просто разворачиваем строку задом наперед
                String reverseMessage = new StringBuilder(message).reverse().toString();

                //отправляем ответ
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter out = new PrintWriter(outputStream, true);
                out.println(reverseMessage);
                out.flush();*/
    }

    private void createSocket() throws IOException {
        serverSocket = new ServerSocket(port);
    }
}

