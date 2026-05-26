package com.example.weather_app;

import java.io.FileInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
import org.json.JSONArray;
import javafx.application.Platform;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class FetchWeather {
    private static final String API_KEY = loadApiKey();

    private static String loadApiKey() {
        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
            return props.getProperty("OPENWEATHER_API_KEY");
        } catch (Exception e) {
            throw new RuntimeException("Could not load API key.");
        }
    }

    // Added callbacks so the UI can react when data is ready or fails
    public void getWeather(String city, Consumer<JSONObject> onSuccess, Consumer<String> onError) {
        new Thread(() -> {
            try {
                if (API_KEY == null || API_KEY.isBlank()) {
                    throw new RuntimeException(
                            "OPENWEATHER_API_KEY environment variable not set."
                    );
                }
                String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
                String URL = "https://api.openweathermap.org/data/2.5/forecast?q=" + encodedCity + "&units=metric&appid=" + API_KEY;

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JSONObject jsonResponse = new JSONObject(response.body());
                    // Pass the data back to the main thread
                    Platform.runLater(() -> onSuccess.accept(jsonResponse));
                } else {
                    Platform.runLater(() -> onError.accept("City not found or API error."));
                }
            } catch (Exception e) {
                Platform.runLater(() -> onError.accept("Network error: " + e.getMessage()));
            }
        }).start();
    }

    public List<String> getCitySuggestions(String query) {
        List<String> suggestions = new ArrayList<>();
        try {

            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String URL = "http://api.openweathermap.org/geo/1.0/direct?q=" + encodedQuery + "&limit=5&appid=" + API_KEY;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONArray jsonArray = new JSONArray(response.body());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject cityObj = jsonArray.getJSONObject(i);
                String name = cityObj.getString("name");
                String country = cityObj.getString("country");
                String formattedCity = name + ", " + country;
                if (!suggestions.contains(formattedCity)) {
                    suggestions.add(formattedCity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suggestions;
    }
}
