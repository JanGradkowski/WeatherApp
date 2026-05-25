package com.example.weather_app;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.util.List;

public class HelloController {
    @FXML
    private TextField searchBar;
    FetchWeather fetchWeather = new FetchWeather();
    private ContextMenu suggestionMenu;
    private PauseTransition pauseTransition;

    @FXML
    public void initialize(){
        suggestionMenu = new ContextMenu();
        pauseTransition = new PauseTransition(Duration.millis(500));
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            suggestionMenu.hide(); // Hide the menu while they are actively typing

            if (newValue.trim().length() >= 3) {
                // If they typed at least 3 letters, start/reset the timer
                pauseTransition.setOnFinished(event -> fetchAndDisplaySuggestions(newValue.trim()));
                pauseTransition.playFromStart();
            }
        });
        searchBar.setOnAction(event -> {
            String query = searchBar.getText().trim();

            if (!query.isEmpty()) {
                // 1. Hide the autocomplete dropdown if it's still floating there
                suggestionMenu.hide();

                // 2. Extract just the city name (in case they typed "Warsaw, PL")
                String cityToSearch = query.split(",")[0];

                // 3. Trigger your weather fetch!
                fetchWeather.getWeather(cityToSearch);

                System.out.println("User pressed Enter to search for: " + cityToSearch);
            }
        });
    }
    private void fetchAndDisplaySuggestions(String query) {
        // 3. Fetch data on a background thread so the UI doesn't freeze
        new Thread(() -> {
            List<String> cities = fetchWeather.getCitySuggestions(query);

            // 4. Update the UI back on the main thread
            Platform.runLater(() -> {
                suggestionMenu.getItems().clear(); // Clear old suggestions

                if (!cities.isEmpty()) {
                    for (String city : cities) {
                        MenuItem item = new MenuItem(city);

                        // What happens when the user CLICKS a suggestion?
                        item.setOnAction(e -> {
                            searchBar.setText(city);
                            suggestionMenu.hide();

                            // Call your main weather method!
                            // We split by "," to only pass the city name, not the country code
                            fetchWeather.getWeather(city.split(",")[0]);
                        });

                        suggestionMenu.getItems().add(item);
                    }
                    // Show the menu physically pinned to the bottom of the searchBar
                    suggestionMenu.show(searchBar, Side.BOTTOM, 0, 0);
                }
            });
        }).start();
    }
}