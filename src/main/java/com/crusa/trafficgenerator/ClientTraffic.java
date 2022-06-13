package com.crusa.trafficgenerator;

import java.io.*;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.net.*;
import java.security.SecureRandom;
import java.util.EventListener;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

public class ClientTraffic {
    private InetAddress address;
    private int port;
    private int size;
    private int delay;
    private TypeProtocol protocol;

    private Thread thread;

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

    public void run() {
        switch (protocol) {
            case UDP -> UDP();
            case TCP -> TCP();
        }
    }

    public void UDP()  {
        byte[] buffer = generateBuffer(size);

        DatagramPacket packet = new DatagramPacket(
                buffer, size, address, port
        );

        UDPSender udpSender = new UDPSender();
        udpSender.setDelay(delay);
        udpSender.setPacket(packet);

        thread = new Thread(udpSender);

        thread.start();
    }


    private void TCP() {
       /* //создаем сокет
        Socket clientSocket = new Socket("localhost", 4444);

        clientSocket.setSendBufferSize(32);

        OutputStream outputStream = clientSocket.getOutputStream();
        PrintWriter out = new PrintWriter(outputStream, true);
        out.write(32);
        out.flush();

        InputStream inputStream = clientSocket.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String answer = in.readLine();

        clientSocket.close();*/
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

class UDPSender implements Runnable {
    private int delay;
    private DatagramPacket packet;

    private Method listener;

    public void setPacket(DatagramPacket packet) {
        this.packet = packet;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setListener(Method listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        while (true) {
            DatagramSocket datagramSocket = null;

            try {
                datagramSocket = new DatagramSocket();
                datagramSocket.send(packet);
/*
                if (listener != null) {
                    listener(packet);
                }*/

                Thread.sleep(delay);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException exit) {
                break;
            }
        }
    }
}