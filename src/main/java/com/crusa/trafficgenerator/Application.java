package com.crusa.trafficgenerator;

import com.crusa.trafficgenerator.view.ViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("home-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        ViewController controller = fxmlLoader.getController();
        controller.setStage(stage);

        stage.setTitle("Traffic Generator");
        stage.setScene(scene);
        stage.show();
    }
}