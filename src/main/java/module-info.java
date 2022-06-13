module com.crusa.trafficgenerator {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.crusa.trafficgenerator to javafx.fxml;
    exports com.crusa.trafficgenerator;
}