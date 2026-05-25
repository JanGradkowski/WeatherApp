package com.example.weather_app;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class HelloController {
    @FXML private TextField searchBar;
    @FXML private FlowPane savedCitiesContainer;
    @FXML private FlowPane popularSearchesContainer;
    @FXML private Label errorLabel;

    private FetchWeather fetchWeather = new FetchWeather();
    private ContextMenu suggestionMenu;
    private PauseTransition pauseTransition;

    @FXML
    public void initialize() {
        suggestionMenu = new ContextMenu();
        pauseTransition = new PauseTransition(Duration.millis(500));

        setupSearchLogic();
        loadSavedCities();
        loadPopularSearches();
    }

    private void setupSearchLogic() {
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            suggestionMenu.hide();
            errorLabel.setVisible(false);
            if (newValue.trim().length() >= 3) {
                pauseTransition.setOnFinished(event -> fetchAndDisplaySuggestions(newValue.trim()));
                pauseTransition.playFromStart();
            }
        });

        searchBar.setOnAction(event -> {
            String query = searchBar.getText().trim();
            if (!query.isEmpty()) {
                suggestionMenu.hide();
                executeWeatherSearch(query.split(",")[0]);
            }
        });
    }

    private void executeWeatherSearch(String city) {
        searchBar.setDisable(true); // Prevent spam clicking
        fetchWeather.getWeather(city,
                jsonResponse -> {
                    searchBar.setDisable(false);
                    switchToDetailsScene(jsonResponse, city);
                },
                errorMessage -> {
                    searchBar.setDisable(false);
                    errorLabel.setText(errorMessage);
                    errorLabel.setVisible(true);
                }
        );
    }

    private void switchToDetailsScene(JSONObject weatherData, String cityName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("details.fxml"));
            Parent root = loader.load();

            // Pass data to the next controller
            DetailsController controller = loader.getController();
            controller.setWeatherData(weatherData, cityName);

            Stage stage = (Stage) searchBar.getScene().getWindow();
            stage.setScene(new Scene(root, 450, 700));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSavedCities() {
        savedCitiesContainer.getChildren().clear();

        for (String city : UserSession.savedCities) {
            VBox tile = new VBox();
            tile.getStyleClass().add("weather-tile");

            Label cityLabel = new Label(city);
            cityLabel.getStyleClass().add("tile-city");

            Label descLabel = new Label("Loading...");
            descLabel.getStyleClass().add("tile-desc");

            Label tempLabel = new Label("--°");
            tempLabel.getStyleClass().add("tile-temp");

            tile.getChildren().addAll(cityLabel, descLabel, tempLabel);

            // Make the tile clickable (Left click)
            tile.setOnMouseClicked(e -> {
                // We only want left-clicks to open the details page!
                if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                    executeWeatherSearch(city);
                }
            });
            tile.setStyle("-fx-cursor: hand;");

            // ADD THIS: Right-click context menu to delete
            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("Remove City");
            deleteItem.setStyle("-fx-text-fill: black;"); // Ensure text is visible
            deleteItem.setOnAction(e -> {
                UserSession.removeCity(city);
                loadSavedCities(); // Instantly refresh the UI!
            });
            contextMenu.getItems().add(deleteItem);

            // Attach the menu to the tile
            tile.setOnContextMenuRequested(e ->
                    contextMenu.show(tile, e.getScreenX(), e.getScreenY())
            );

            savedCitiesContainer.getChildren().add(tile);

            // Fetch the actual weather for this specific tile
            fetchWeather.getWeather(city,
                    jsonResponse -> {
                        org.json.JSONObject currentBlock = jsonResponse.getJSONArray("list").getJSONObject(0);
                        double temp = currentBlock.getJSONObject("main").getDouble("temp");
                        String desc = currentBlock.getJSONArray("weather").getJSONObject(0).getString("description");

                        tempLabel.setText(Math.round(temp) + "°C");
                        descLabel.setText(desc.substring(0, 1).toUpperCase() + desc.substring(1));
                    },
                    errorMessage -> {
                        descLabel.setText("Offline");
                    }
            );
        }
    }

    private void loadPopularSearches() {
        String[] popular = {"Paris", "Tokyo", "Sydney", "Dubai"};
        for (String city : popular) {
            Button btn = new Button(city);
            btn.getStyleClass().add("popular-btn");
            btn.setOnAction(e -> executeWeatherSearch(city));
            popularSearchesContainer.getChildren().add(btn);
        }
    }

    private void fetchAndDisplaySuggestions(String query) {
        new Thread(() -> {
            List<String> cities = fetchWeather.getCitySuggestions(query);
            Platform.runLater(() -> {
                if (searchBar.getScene() == null || searchBar.getScene().getWindow() == null){
                    return;
                }
                suggestionMenu.getItems().clear();
                if (!cities.isEmpty()) {
                    for (String city : cities) {
                        MenuItem item = new MenuItem(city);
                        item.setOnAction(e -> {
                            searchBar.setText(city);
                            suggestionMenu.hide();
                            executeWeatherSearch(city.split(",")[0]);
                        });
                        suggestionMenu.getItems().add(item);
                    }
                    suggestionMenu.show(searchBar, Side.BOTTOM, 0, 0);
                }
            });
        }).start();
    }
}