package com.shreyan.weather_forecast_app;

import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherTask {

    private final HomeActivity activity;
    private final String apiKey;

    // ExecutorService for handling async requests
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public WeatherTask(HomeActivity activity, String apiKey) {
        this.activity = activity;
        this.apiKey = apiKey;
    }

    // Fetch weather by city name
    public void getWeatherByCity(String city) {
        executorService.submit(() -> {
            String urlString = "https://api.weatherbit.io/v2.0/current?city=" + city + "&key=" + apiKey;
            fetchWeatherData(urlString);
        });
    }

    // Fetch weather by geographic coordinates
    public void getWeatherByCoordinates(double latitude, double longitude) {
        executorService.submit(() -> {
            String urlString = "https://api.weatherbit.io/v2.0/current?lat=" + latitude + "&lon=" + longitude + "&key=" + apiKey;
            fetchWeatherData(urlString);
        });
    }

    // Fetch weather data from the URL
    private void fetchWeatherData(String urlString) {
        try {
            // Start showing refresh icon
            activity.runOnUiThread(() -> activity.swipeRefreshLayout.setRefreshing(true));

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    result.append(inputLine);
                }
                in.close();

                // Process the result in UI thread
                activity.runOnUiThread(() -> processWeatherData(result.toString()));
            } else {
                activity.runOnUiThread(() -> showErrorMessage("Error: Failed to fetch weather data."));
            }
        } catch (Exception e) {
            activity.runOnUiThread(() -> showErrorMessage("Error: " + e.getMessage()));
        } finally {
            activity.runOnUiThread(() -> activity.swipeRefreshLayout.setRefreshing(false));
        }
    }

    // Process and update UI with the fetched weather data
    private void processWeatherData(String result) {
        if (result != null) {
            try {
                // Parse the JSON result and update UI
                JSONObject jsonResponse = new JSONObject(result);
                WeatherUtils.updateUI(activity, String.valueOf(jsonResponse));
            } catch (Exception e) {
                showErrorMessage("Error: Parsing weather data.");
            }
        } else {
            showErrorMessage("Error: Cannot fetch weather for that location.");
        }
    }

    // Show error messages via Toast
    private void showErrorMessage(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }
}
