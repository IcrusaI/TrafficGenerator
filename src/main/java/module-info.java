module com.crusa.trafficgenerator {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.crusa.trafficgenerator to javafx.fxml;
    exports com.crusa.trafficgenerator;
    exports com.crusa.trafficgenerator.view;
    opens com.crusa.trafficgenerator.view to javafx.fxml;
    exports com.crusa.trafficgenerator.protocol.UDP;
    opens com.crusa.trafficgenerator.protocol.UDP to javafx.fxml;
    exports com.crusa.trafficgenerator.protocol.TCP;
    opens com.crusa.trafficgenerator.protocol.TCP to javafx.fxml;
    exports com.crusa.trafficgenerator.protocol;
    opens com.crusa.trafficgenerator.protocol to javafx.fxml;
}