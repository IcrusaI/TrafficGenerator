package com.crusa.trafficgenerator.view;

import com.crusa.trafficgenerator.ClientTraffic;
import com.crusa.trafficgenerator.ServerTraffic;
import com.crusa.trafficgenerator.TypeProtocol;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class ServerViewController extends ViewController {
    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    @FXML
    public TextField portText;

    @FXML
    private ComboBox<String> protocolCombobox;


    private ServerTraffic serverTraffic;

    public ServerViewController() {
        serverTraffic = new ServerTraffic();

        serverTraffic.setPort(55);
        serverTraffic.setProtocol(TypeProtocol.UDP);
        serverTraffic.run();
    }

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

        serverTraffic.destroy();
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
    }

    @FXML
    private void backToHome() throws Exception {
        stopReceiver();
        setView(ViewEnum.HOME);
    }
}
