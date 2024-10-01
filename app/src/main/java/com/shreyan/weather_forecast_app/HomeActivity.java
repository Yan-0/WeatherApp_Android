package com.shreyan.weather_forecast_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;

    private final String API = "2c2f66f576214a88ae92184d4134b59c";

    private ImageView search;
    private EditText etCity;
    TextView city, appTemp, temp, humidity, sunrises, sunsets, wind_speed, precipitation, uv_index;
    ImageView weatherBackground;
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView forecastRecyclerView;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    ForecastAdapter forecastAdapter;
    private List<Forecast> forecastList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        // Initialize UI elements
        initializeUI();

        // Initialize forecast list and adapter
        forecastList = new ArrayList<>();
        forecastAdapter = new ForecastAdapter(forecastList);
        forecastRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        forecastRecyclerView.setAdapter(forecastAdapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Search button handler
        search.setOnClickListener(v -> {
            String cityName = etCity.getText().toString().trim();
            if (!cityName.isEmpty()) {
                fetchWeatherAndForecast(cityName);
            } else {
                showToast("Please enter a city name.");
            }
        });

        // Location permissions check
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }

        // Pull-to-refresh handler
        swipeRefreshLayout.setOnRefreshListener(() -> {
            String cityName = etCity.getText().toString().trim();
            if (!cityName.isEmpty()) {
                fetchWeatherAndForecast(cityName);
            } else {
                getCurrentLocation();
            }
        });
    }

    // Initialize UI elements
    private void initializeUI() {
        etCity = findViewById(R.id.etCity);
        city = findViewById(R.id.location);
        search = findViewById(R.id.search);
        appTemp = findViewById(R.id.app_temp);
        temp = findViewById(R.id.temp);
        humidity = findViewById(R.id.humidity);
        sunrises = findViewById(R.id.sunrises);
        sunsets = findViewById(R.id.sunsets);
        wind_speed = findViewById(R.id.wind_speed);
        precipitation = findViewById(R.id.precipitation);
        uv_index = findViewById(R.id.uv_index);
        weatherBackground = findViewById(R.id.weather_background);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        forecastRecyclerView = findViewById(R.id.forecastRecyclerView);
    }

    // Fetch weather and forecast based on city or current location
    @SuppressLint("StaticFieldLeak")
    private void fetchWeatherAndForecast(String cityName) {
        // Log the city name being searched
        Log.d("HomeActivity", "Searching for city: " + cityName);

        // Fetch current weather for the searched city
        WeatherTask weatherTask = new WeatherTask(HomeActivity.this, API);
        executorService.submit(() -> weatherTask.getWeatherByCity(cityName));

        // Fetch forecast for the searched city
        new ForecastTask(HomeActivity.this, API) {
            @Override
            protected void onPostExecute(List<Forecast> result) {
                // Update the forecast list and notify the adapter
                if (result != null && !result.isEmpty()) {
                    forecastAdapter.updateForecastList(result);
                } else {
                    Log.e("HomeActivity", "No forecast data available for city: " + cityName);
                    showToast("No forecast data available for " + cityName);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, cityName);

        swipeRefreshLayout.setRefreshing(false); // Stop the refreshing animation
    }


    // Handle location permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                showToast("Location permission denied. Please allow location access to use this feature.");
            }
        }
    }

    // Fetch current location and update weather and forecast
    @SuppressLint("StaticFieldLeak")
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.d("HomeActivity", "Location fetched: Latitude = " + latitude + ", Longitude = " + longitude);

                        // Fetch current weather and forecast based on the location
                        WeatherTask weatherTask = new WeatherTask(this, API);
                        executorService.submit(() -> weatherTask.getWeatherByCoordinates(latitude, longitude));

                        new ForecastTask(this, API) {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            protected void onPostExecute(List<Forecast> result) {
                                if (result != null && !result.isEmpty()) {
                                    forecastList.clear();
                                    forecastList.addAll(result);
                                    forecastAdapter.notifyDataSetChanged();
                                } else {
                                    showToast("No forecast data available.");
                                }
                                swipeRefreshLayout.setRefreshing(false); // Stop the refreshing animation
                            }
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(latitude), String.valueOf(longitude));
                    } else {
                        Log.e("HomeActivity", "Location is null.");
                        showToast("Unable to get location. Please try again.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("HomeActivity", "Error fetching location: ", e);
                    showToast("Error fetching location: " + e.getMessage());
                });
    }

    // Helper method to show a toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Shutdown the executor service when activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
