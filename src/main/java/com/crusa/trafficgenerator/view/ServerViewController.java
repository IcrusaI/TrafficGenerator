package com.crusa.trafficgenerator.view;

import com.crusa.trafficgenerator.entity.ReceiverReport;
import com.crusa.trafficgenerator.controller.ServerTraffic;
import com.crusa.trafficgenerator.protocol.TypeProtocol;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ServerViewController extends ViewController {
    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    @FXML
    public TextField portText;

    @FXML
    private ComboBox<String> protocolCombobox;

    @FXML
    private TextField receivedPackagesText;

    @FXML
    private TextField sizePackagesText;

    @FXML
    private TextArea logTextArea;

    private ServerTraffic serverTraffic;

    @Override
    protected void start() {
        protocolCombobox.getItems().addAll(TypeProtocol.names());

        setStartReceiver(false);
    }

    private void validateForm() {
        int port;

        try {
            port = Integer.parseInt(portText.getText());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Порт должен быть числом");
        }

        if (port <= 0) {
            throw new RuntimeException("Время генерации должно быть больше 0");
        }

        if (!Arrays.stream(TypeProtocol.names()).toList().contains(protocolCombobox.getValue())) {
            throw new RuntimeException("Неверный тип трафика");
        }
    }

    public void setStartReceiver(Boolean startGenerator) {
        stopButton.setVisible(startGenerator);
        startButton.setVisible(!startGenerator);
        protocolCombobox.setDisable(startGenerator);
        portText.setDisable(startGenerator);
    }

    @FXML
    private void stopReceiver() {
        setStartReceiver(false);

        if (serverTraffic != null) {
            serverTraffic.destroy();
        }
        serverTraffic = null;
        System.gc();
    }

    @FXML
    private void startReceiver() {
        try {
            validateForm();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
            return;
        }

        setStartReceiver(true);

        ServerTraffic traffic = new ServerTraffic();

        traffic.setPort(Integer.parseInt(portText.getText()));
        traffic.setProtocol(TypeProtocol.valueOf(protocolCombobox.getValue()));

        serverTraffic = traffic;

        serverTraffic.run();

        taskUpdateStatistic();
    }

    private void taskUpdateStatistic() {
        Runnable task = () -> {

            synchronized(serverTraffic.getReport()) {
                try {
                    ReceiverReport report = serverTraffic.getReport();

                    while (true) {
                        report.wait();

                        Platform.runLater(() -> {
                            receivedPackagesText
                                    .setText(Integer.toString(report.getTotalReceive()));

                            sizePackagesText
                                    .setText(Integer.toString(report.getTotalSizeData()));

                            List<String> log = new ArrayList<>(report.getLog());

                            Collections.reverse(log);

                            logTextArea.setText(log
                                    .stream().map(Object::toString)
                                    .collect(Collectors.joining("\n")));
                        });
                    }

                } catch(InterruptedException e) {
                    System.out.println("interrupted");
                }
            }
            // После оповещения нас мы будем ждать, пока сможем взять лок
            System.out.println("thread");
        };

        Thread taskThread = new Thread(task);
        taskThread.start();
    }

    @FXML
    private void backToHome() throws Exception {
        stopReceiver();
        setView(ViewEnum.HOME);
    }
}
