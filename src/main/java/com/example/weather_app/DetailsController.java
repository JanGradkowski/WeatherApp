package com.example.weather_app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class DetailsController {

    @FXML private Label cityNameLabel;
    @FXML private Label currentTempLabel;
    @FXML private Label currentDescLabel;
    @FXML private VBox forecastContainer;
    @FXML private VBox dailyForecastContainer; 
    @FXML private Button saveBtn;

    private String currentCity;

    public void setWeatherData(JSONObject jsonResponse, String city) {
        this.currentCity = city;
        cityNameLabel.setText(city.substring(0, 1).toUpperCase() + city.substring(1));

        if (UserSession.savedCities.contains(city)) {
            saveBtn.setText("★ Unsave");
        } else {
            saveBtn.setText("♡ Save");
        }

        JSONArray forecastList = jsonResponse.getJSONArray("list");

        // Set Current Weather
        JSONObject currentBlock = forecastList.getJSONObject(0);
        double currentTemp = currentBlock.getJSONObject("main").getDouble("temp");
        String currentDesc = currentBlock.getJSONArray("weather").getJSONObject(0).getString("description");

        currentTempLabel.setText(Math.round(currentTemp) + "°C");
        currentDescLabel.setText(currentDesc.substring(0, 1).toUpperCase() + currentDesc.substring(1));

        // 1. Populate the Hourly forecast (Next 12 hours)
        for (int i = 1; i <= 4; i++) {
            JSONObject block = forecastList.getJSONObject(i);
            String timeString = block.getString("dt_txt").substring(11, 16);
            double temp = block.getJSONObject("main").getDouble("temp");
            String desc = block.getJSONArray("weather").getJSONObject(0).getString("main");

            HBox row = createForecastRow(timeString, desc, temp);
            forecastContainer.getChildren().add(row);
        }

        // 2. Populate the Daily forecast (Next 5 Days)
        for (int i = 0; i < forecastList.length(); i++) {
            JSONObject block = forecastList.getJSONObject(i);
            String dtTxt = block.getString("dt_txt");

            if (dtTxt.contains("12:00:00")) {
                // Convert "2023-10-25" into "Wed"
                LocalDate date = LocalDate.parse(dtTxt.substring(0, 10));
                String dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

                double temp = block.getJSONObject("main").getDouble("temp");
                String desc = block.getJSONArray("weather").getJSONObject(0).getString("main");

                HBox row = createForecastRow(dayName, desc, temp);
                dailyForecastContainer.getChildren().add(row);
            }
        }
    }

    // Helper method to keep our code clean since hourly and daily rows look identical
    private HBox createForecastRow(String timeOrDay, String desc, double temp) {
        HBox row = new HBox(15);
        row.setStyle("-fx-background-color: rgba(0,0,0,0.2); -fx-padding: 15; -fx-background-radius: 10;");

        Label timeLbl = new Label(timeOrDay);
        timeLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 50;");

        Label descLbl = new Label(desc);
        descLbl.setStyle("-fx-text-fill: white;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label tempLbl = new Label(Math.round(temp) + "°");
        tempLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        row.getChildren().addAll(timeLbl, descLbl, spacer, tempLbl);
        return row;
    }

    @FXML
    public void saveCity() {
        if (UserSession.savedCities.contains(currentCity)) {
            UserSession.removeCity(currentCity);
            saveBtn.setText("♡ Save");
        } else {
            UserSession.addCity(currentCity);
            saveBtn.setText("★ Unsave");
        }
    }

    @FXML
    public void goBack(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 450, 700));
    }
}
