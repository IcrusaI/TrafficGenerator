package com.crusa.trafficgenerator.view;

import com.crusa.trafficgenerator.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public abstract class ViewController {
    private Stage stage;

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    protected void setView(ViewEnum view) throws Exception {
        FXMLLoader loader = new FXMLLoader(getFXMLView(view));
        Parent root = (Parent) loader.load();

        ViewController controller = loader.getController();
        controller.setStage(stage);
        controller.start();

        Scene oldScene = stage.getScene();

        Scene scene = new Scene(root, oldScene.getWidth(), oldScene.getHeight());
        stage.setScene(scene);
        stage.show();
    }

    abstract void start();

    private URL getFXMLView(ViewEnum view) throws Exception {
        String name;

        switch (view) {
            case HOME -> name = "home-view.fxml";
            case CLIENT -> name = "client-view.fxml";
            case SERVER -> name = "server-view.fxml";
            default -> throw new Exception("Not found view fxml");
        }

        return Application.class.getResource(name);
    }
}