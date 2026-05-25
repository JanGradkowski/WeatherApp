package com.example.weather_app;
import java.io.IOException;
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

public class FetchWeather {

    public void getWeather(String city) {
        new Thread( () -> {
            try {
                String apiKey = "566988eea4abeb70f1caec04663b205b".trim();
                String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
                String URL = "https://api.openweathermap.org/data/2.5/forecast?q=" + encodedCity + "&units=metric&appid=" + apiKey;
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request =  HttpRequest.newBuilder().uri(URI.create(URL)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String rawJsonData = response.body();
                JSONObject jsonResponse = new JSONObject(rawJsonData);
                JSONArray forecastList = jsonResponse.getJSONArray("list");
                System.out.println("--- UPCOMING FORECAST FOR " + city.toUpperCase() + " ---");
                for (int i = 0; i < 4; i++) {
                    // Open the specific 3-hour block
                    JSONObject forecastBlock = forecastList.getJSONObject(i);
                    String dateTime = forecastBlock.getString("dt_txt");
                    JSONObject mainObject = forecastBlock.getJSONObject("main");
                    double temperature = mainObject.getDouble("temp");
                    int humidity = mainObject.getInt("humidity");
                    JSONArray weatherArray = forecastBlock.getJSONArray("weather");
                    JSONObject firstCondition = weatherArray.getJSONObject(0);
                    String description = firstCondition.getString("description");
                    System.out.println(dateTime + " | Temp: " + temperature + " °C | Humidity: " + humidity + "% | " + description);
                }
                Platform.runLater(() -> {
                    // This is where you will update your actual screen.
                    // example: tempLabel.setText(temperature + " °C");
                });
            }

            catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }).start();
    }

    public List<String> getCitySuggestions(String query) {
        List<String> suggestions = new ArrayList<>();
        try {
            String apiKey = "566988eea4abeb70f1caec04663b205b".trim();
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            // Notice this is the geo/1.0/direct endpoint!
            String URL = "http://api.openweathermap.org/geo/1.0/direct?q=" + encodedQuery + "&limit=5&appid=" + apiKey;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON Array
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
            System.out.println("Error fetching suggestions");
            e.printStackTrace();
        }
        return suggestions;
    }
}
