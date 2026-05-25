package com.example.weather_app;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.prefs.Preferences;

public class UserSession {
    public static Set<String> savedCities = new LinkedHashSet<>();
    // This creates a dedicated storage node on your computer for this app
    private static Preferences prefs = Preferences.userNodeForPackage(UserSession.class);
    private static final String CITIES_KEY = "saved_cities_data";

    // This static block runs once when the app starts, loading your saved data
    static {
        String citiesStr = prefs.get(CITIES_KEY, "London,New York");
        if (!citiesStr.isEmpty()) {
            savedCities.addAll(Arrays.asList(citiesStr.split(",")));
        }
    }

    // Use this method to add a city and instantly save it to the computer
    public static void addCity(String city) {
        savedCities.add(city);
        prefs.put(CITIES_KEY, String.join(",", savedCities));
    }
    // Use this method to remove a city and update the saved data
    public static void removeCity(String city) {
        savedCities.remove(city);

        // If the list is empty, we clear the preference.
        // Otherwise, we save the newly updated list.
        if (savedCities.isEmpty()) {
            prefs.remove(CITIES_KEY);
        } else {
            prefs.put(CITIES_KEY, String.join(",", savedCities));
        }
    }
}