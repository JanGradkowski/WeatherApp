package com.example.weather_app;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.prefs.Preferences;

public class UserSession {
    public static Set<String> savedCities = new LinkedHashSet<>();
    private static Preferences prefs = Preferences.userNodeForPackage(UserSession.class);
    private static final String CITIES_KEY = "saved_cities_data";
    static {
        String citiesStr = prefs.get(CITIES_KEY, "London,New York");
        if (!citiesStr.isEmpty()) {
            savedCities.addAll(Arrays.asList(citiesStr.split(",")));
        }
    }

    public static void addCity(String city) {
        savedCities.add(city);
        prefs.put(CITIES_KEY, String.join(",", savedCities));
    }
    public static void removeCity(String city) {
        savedCities.remove(city);

        if (savedCities.isEmpty()) {
            prefs.remove(CITIES_KEY);
        } else {
            prefs.put(CITIES_KEY, String.join(",", savedCities));
        }
    }
}
