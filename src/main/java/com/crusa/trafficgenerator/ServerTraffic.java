package com.crusa.trafficgenerator;

import java.io.*;
import java.net.*;

public class ServerTraffic {
    private int port;
    private Thread thread;
    private TypeProtocol protocol;

    public void setPort(int port) {
        this.port = port;
    }

    public void setProtocol(TypeProtocol protocol) {
        this.protocol = protocol;
    }

    public void run() {
        switch (protocol) {
            case UDP -> UDP();
            case TCP -> TCP();
        }
    }

    public void TCP() {
        TCPSocket tcpSocket = new TCPSocket();
        tcpSocket.setPort(port);

        Thread myThready = new Thread(tcpSocket);
        myThready.start();
    }

    public void UDP() {

        UDPReceiver udpReceiver = new UDPReceiver();
        udpReceiver.setPort(port);

        thread = new Thread(udpReceiver);

        thread.start();
    }

    public void destroy() {
        thread.interrupt();
    }
}

class TCPSocket implements Runnable {
    int port;
    ServerSocket serverSocket;

    public TCPSocket() {}

    public TCPSocket(int port) {
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

class UDPReceiver implements Runnable {
    private int port;

    public void setPort(int port) {
        this.port = port;
    }

    public void run() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[8];
            String sendString = "polo";
            byte[] sendData = sendString.getBytes("UTF-8");

            System.out.printf("Listening on udp:%s:%d%n",
                    InetAddress.getLocalHost().getHostAddress(), port);
            DatagramPacket receivePacket = new DatagramPacket(receiveData,
                    receiveData.length);

            while(true)
            {
                serverSocket.receive(receivePacket);
                String sentence = new String(receiveData);
                System.out.println("RECEIVED: " + sentence);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        // should close serverSocket in finally block
    }
}
