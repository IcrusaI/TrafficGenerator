package com.crusa.trafficgenerator.view;

import com.crusa.trafficgenerator.controller.ClientTraffic;
import com.crusa.trafficgenerator.entity.SenderReport;
import com.crusa.trafficgenerator.protocol.TypeProtocol;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class ClientViewController extends ViewController {
    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    @FXML
    public TextField addressText;

    @FXML
    private TextField sizeText;

    @FXML
    private TextField workingTimeText;

    @FXML
    private TextField delayText;

    @FXML
    private ComboBox<String> typeCombobox; //todo переименовать в protocol, я ж долбаеб, который сразу не понял этого

    @FXML
    private TextField sentPackagesText;

    private ClientTraffic clientTraffic;

    @Override
    protected void start() {
        typeCombobox.getItems().addAll(TypeProtocol.names());

        setStartGenerator(false);
    }

    private void validateForm() {
        if (addressText.getText().split(":").length != 2) {
            throw new RuntimeException("Неверно задан адрес сервера");
        }

        int size, delay;

            try {
                size = Integer.parseInt(sizeText.getText());
                delay = Integer.parseInt(delayText.getText());
            } catch (NumberFormatException e) {
                throw new RuntimeException("Значения полей \"Размер\" и \"Время генерации\" должны быть числом");
            }

            if (size < 32 || size > 1500) {
                throw new RuntimeException("Размер задается в диапазоне 32-1500 байт");
            }
            if (delay <= 0) {
                throw new RuntimeException("Время генерации должно быть больше 0");
            }

            if (!Arrays.stream(TypeProtocol.names()).toList().contains(typeCombobox.getValue())) {
                throw new RuntimeException("Неверный тип трафика");
            }
    }

    public Boolean isStartGenerator() {
        return startButton.isVisible();
    }

    public void setStartGenerator(Boolean startGenerator) {
        stopButton.setVisible(startGenerator);
        startButton.setVisible(!startGenerator);
        sizeText.setDisable(startGenerator);
        delayText.setDisable(startGenerator);
        workingTimeText.setDisable(startGenerator);
        typeCombobox.setDisable(startGenerator);
        addressText.setDisable(startGenerator);
        sentPackagesText.setText("0");
    }

    @FXML
    private void stopGenerator() {
        setStartGenerator(false);

        if (clientTraffic != null) {
            clientTraffic.destroy();
            clientTraffic = null;
        }
        System.gc();
    }

    @FXML
    private void startGenerator() {
        try {
            validateForm();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
            return;
        }

        setStartGenerator(true);

        ClientTraffic traffic = new ClientTraffic();

        String[] address = addressText.getText().split(":");

        try {
            traffic.setAddress(address[0]);
            traffic.setPort(Integer.parseInt(address[1]));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        traffic.setDelay(Integer.parseInt(delayText.getText()));
        traffic.setProtocol(TypeProtocol.valueOf(typeCombobox.getValue()));
        traffic.setSize(Integer.parseInt(sizeText.getText()));
        clientTraffic = traffic;

        if (Integer.parseInt(workingTimeText.getText()) > 0) {
            timerToStop();
        }

        clientTraffic.run();

        taskUpdateStatistic();
    }

    private void timerToStop() {
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            int time = Integer.parseInt(workingTimeText.getText());

            public void run() {
                time--;

                Platform.runLater(() -> workingTimeText.setText(Integer.toString(time)));

                if (time <= 0 || isStartGenerator()) {
                    stopGenerator();
                    timer.cancel();
                    timer.purge();
                    return;
                }
            }
        }, 0, 1);
    }

    private void taskUpdateStatistic() {
        Runnable task = () -> {

            synchronized(clientTraffic.getReport()) {
                try {
                    SenderReport report = clientTraffic.getReport();

                    while (true) {
                        report.wait();

                        Platform.runLater(() -> sentPackagesText
                                .setText(Integer.toString(report.getTotalSend())));
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
        stopGenerator();
        setView(ViewEnum.HOME);
    }
}
