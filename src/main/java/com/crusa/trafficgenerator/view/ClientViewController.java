package com.crusa.trafficgenerator.view;

import com.crusa.trafficgenerator.controller.ClientTraffic;
import com.crusa.trafficgenerator.distribution.DistributionEnum;
import com.crusa.trafficgenerator.entity.SenderReport;
import com.crusa.trafficgenerator.protocol.TypeProtocol;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

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
    private TextField workingSizeText;

    @FXML
    private TextField delayText;

    @FXML
    private TextField sendVolumeText;

    @FXML
    private ComboBox<String> protocolCombobox;

    @FXML
    private ComboBox<String> distributionCombobox;

    @FXML
    private TextField sentPackagesText;

    private ClientTraffic clientTraffic;

    @Override
    protected void start() {
        protocolCombobox.getItems().addAll(TypeProtocol.names());
        distributionCombobox.getItems().addAll(DistributionEnum.methods());

        setDistribution();
        setStartGenerator(false);
    }


    @FXML
    public void onUpdateDistribution(ActionEvent a) throws Exception {
        setDistribution(getDistribution());
    }

    @FXML
    private VBox delayVBox;
    @FXML
    private VBox erlangVBox;
    @FXML
    private VBox exponentialVBox;
    @FXML
    private VBox uniformVBox;
    @FXML
    private TextField erlangShapeText;
    @FXML
    private TextField erlangScaleText;
    @FXML
    private TextField exponentialMeanText;
    @FXML
    public TextField uniformMaxText;
    @FXML
    public TextField uniformMinText;

    private void setDistribution() {
        delayVBox.setDisable(true);
        delayVBox.setOpacity(0);
        erlangVBox.setDisable(true);
        erlangVBox.setOpacity(0);
        exponentialVBox.setDisable(true);
        exponentialVBox.setOpacity(0);
        uniformVBox.setDisable(true);
        uniformVBox.setOpacity(0);
    }
    private void setDistribution(DistributionEnum distribution) {
        setDistribution();

        switch (distribution) {
            case DELAY -> {
                delayVBox.setDisable(false);
                delayVBox.setOpacity(1);
            }
            case ERLANG -> {
                erlangVBox.setDisable(false);
                erlangVBox.setOpacity(1);
            }
            case EXPONENTIAL -> {
                exponentialVBox.setDisable(false);
                exponentialVBox.setOpacity(1);
            }
            case UNIFORM -> {
                uniformVBox.setDisable(false);
                uniformVBox.setOpacity(1);
            }
        }
    }

    private DistributionEnum getDistribution() throws Exception {
        return switch (distributionCombobox.getValue()) {
            case "DELAY" -> DistributionEnum.DELAY;
            case "ERLANG" -> DistributionEnum.ERLANG;
            case "EXPONENTIAL" -> DistributionEnum.EXPONENTIAL;
            case "UNIFORM" -> DistributionEnum.UNIFORM;
            default -> null;
        };
    }

    private void validateForm() throws Exception {
        if (addressText.getText().split(":").length != 2) {
            throw new RuntimeException("Неверно задан адрес сервера");
        }

        int size;

        try {
            size = Integer.parseInt(sizeText.getText());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Значения поля \"Размер\" должно быть числом");
        }

        if (size < 32 || size > 1500) {
            throw new RuntimeException("Размер задается в диапазоне 32-1500 байт");
        }

        switch (getDistribution()) {
            case DELAY:
                int delay;

                try {
                    delay = Integer.parseInt(delayText.getText());
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Значения поля и \"Время генерации\" должно быть числом");
                }

                if (delay <= 0) {
                    throw new RuntimeException("Время генерации должно быть больше 0");
                }
                break;
            case ERLANG:
                double scale;

                try {
                    scale = Double.parseDouble(erlangScaleText.getText());
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Значения поля \"масштаб\" должно быть числом");
                }
                double shape;

                try {
                    shape = Double.parseDouble(erlangShapeText.getText());
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Значения поля \"форма\" должно быть числом");
                }
                break;
            case EXPONENTIAL:
                double mean;

                try {
                    mean = Double.parseDouble(exponentialMeanText.getText());
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Значения поля \"значение\" должно быть числом");
                }
                break;
            case UNIFORM:
                double max;

                try {
                    max = Double.parseDouble(uniformMaxText.getText());
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Значения поля \"макс\" должно быть числом");
                }

                double min;

                try {
                    min = Double.parseDouble(uniformMinText.getText());
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Значения поля \"мин\" должно быть числом");
                }
                break;
        }


        if (!Arrays.stream(TypeProtocol.names()).toList().contains(protocolCombobox.getValue())) {
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
        protocolCombobox.setDisable(startGenerator);
        addressText.setDisable(startGenerator);
        if (clientTraffic != null) {
            sentPackagesText.setText(Integer.toString(clientTraffic.getReport().getTotalSend()));
        } else {
            sentPackagesText.setText("0");
        }
    }

    @FXML
    private void stopGenerator() {
        setStartGenerator(false);

        if (clientTraffic != null) {
            clientTraffic.destroy();
        }
        System.gc();
    }

    @FXML
    private void startGenerator() throws Exception {
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

        traffic.setDistribution(getDistribution());

        switch (getDistribution()) {
            case DELAY ->         traffic.setDelay(Integer.parseInt(delayText.getText()));
            case ERLANG -> {
                traffic.setShape(Double.parseDouble(erlangShapeText.getText()));
                traffic.setScale(Double.parseDouble(erlangScaleText.getText()));
            }
            case EXPONENTIAL -> traffic.setMean(Double.parseDouble(exponentialMeanText.getText()));
            case UNIFORM -> {
                traffic.setMax(Double.parseDouble(uniformMaxText.getText()));
                traffic.setMin(Double.parseDouble(uniformMinText.getText()));
            }
        }
        traffic.setProtocol(TypeProtocol.valueOf(protocolCombobox.getValue()));
        traffic.setSize(Integer.parseInt(sizeText.getText()));

        if (clientTraffic != null) {
            traffic.setReport(clientTraffic.getReport());
        }

        clientTraffic = traffic;

        if (!workingTimeText.getText().equals("") && Integer.parseInt(workingTimeText.getText()) > 0) {
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
                }
            }
        }, 0, 1);
    }

    private void taskUpdateStatistic() {
        Runnable task = () -> {

            synchronized(clientTraffic.getReport()) {
                try {
                    SenderReport report = clientTraffic.getReport();


                    final int[] allSize = {Integer.parseInt(workingSizeText.getText())};

                    while (true) {
                        report.wait();

                        Platform.runLater(() -> {
                            sentPackagesText
                                    .setText(Integer.toString(report.getTotalSend()));
                            sendVolumeText.setText(Integer.toString(report.getTotalSend() * clientTraffic.getSize()));

                            if (!workingSizeText.getText().equals("")) {
                                int size = Integer.parseInt(sizeText.getText());
                                if (size > 0) {
                                    allSize[0] = allSize[0]-size;
                                    workingSizeText.setText(String.valueOf(allSize[0]));
                                    if (allSize[0] - size < 0) {
                                        stopGenerator();
                                    }
                                }
                            }
                        });
                    }
                } catch(InterruptedException e) {
                    System.err.println("interrupted");
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
    @FXML
    private void openNewClient() throws Exception {
        newWindowView(ViewEnum.CLIENT);
    }

    @FXML
    private void clear() throws Exception {
        sizeText.clear();
        delayText.clear();
        workingTimeText.clear();
        addressText.clear();
        protocolCombobox.setValue(null);
        setDistribution();
        distributionCombobox.setValue(null);
        sendVolumeText.clear();
        sentPackagesText.clear();
    }
}
