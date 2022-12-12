package com.crusa.trafficgenerator;

import com.crusa.trafficgenerator.view.ViewController;
import com.crusa.trafficgenerator.view.ViewEnum;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    private static ViewEnum view = ViewEnum.HOME;

    public static int defaultHeight = 320;
    public static int defaultWidth = 240;

    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("-view")) {
                view = ViewEnum.valueOf(args[1]);
            }
        }

        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        String fxmlName;

        switch (view) {
            case CLIENT:
                fxmlName = "client-view.fxml";
            break;
            case SERVER:
                fxmlName = "server-view.fxml";
                break;
            default:
            case HOME:
                fxmlName = "home-view.fxml";
        }

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource(fxmlName));

        Scene scene = new Scene(fxmlLoader.load(), defaultHeight, defaultWidth);

        ViewController controller = fxmlLoader.getController();
        controller.setStage(stage);

        stage.setTitle("Traffic Generator");
        stage.setScene(scene);
        stage.show();
    }
}