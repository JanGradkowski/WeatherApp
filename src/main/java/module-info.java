module com.example.weather_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;
    requires java.net.http;
    requires org.json;

    opens com.example.weather_app to javafx.fxml;
    exports com.example.weather_app;
}