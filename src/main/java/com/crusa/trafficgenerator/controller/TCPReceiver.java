package com.crusa.trafficgenerator.controller;

import com.crusa.trafficgenerator.protocol.TypeProtocol;
import com.crusa.trafficgenerator.protocol.UDP.UDPReceiver;

import java.io.*;
import java.net.*;

public class TCPReceiver implements Runnable {
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

