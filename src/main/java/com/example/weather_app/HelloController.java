package com.example.weather_app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    public void initialize(){
        FetchWeather fetchWeather = new FetchWeather();
        fetchWeather.getWeather("Brussels");
    }
}