package com.crusa.trafficgenerator.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeViewController extends ViewController {

    @FXML
    public void clientButtonClick(ActionEvent actionEvent) throws Exception {
        setView(ViewEnum.CLIENT);
    }

    @FXML
    public void serverButtonClick(ActionEvent actionEvent) throws Exception {
        setView(ViewEnum.SERVER);
    }

    @Override
    void start() {

    }
}