package com.example.weather_app;

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
import java.util.function.Consumer;

public class FetchWeather {

    // Added callbacks so the UI can react when data is ready or fails
    public void getWeather(String city, Consumer<JSONObject> onSuccess, Consumer<String> onError) {
        new Thread(() -> {
            try {
                String apiKey = "566988eea4abeb70f1caec04663b205b".trim();
                String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
                String URL = "https://api.openweathermap.org/data/2.5/forecast?q=" + encodedCity + "&units=metric&appid=" + apiKey;

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
            String apiKey = "566988eea4abeb70f1caec04663b205b".trim();
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String URL = "http://api.openweathermap.org/geo/1.0/direct?q=" + encodedQuery + "&limit=5&appid=" + apiKey;

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