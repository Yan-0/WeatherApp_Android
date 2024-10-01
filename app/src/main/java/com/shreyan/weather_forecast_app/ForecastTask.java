package com.shreyan.weather_forecast_app;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class ForecastTask extends AsyncTask<String, Void, List<Forecast>> {

    @SuppressLint("StaticFieldLeak")
    private final HomeActivity activity;

    public ForecastTask(HomeActivity activity, String weatherbitApiKey) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.swipeRefreshLayout.setRefreshing(true); // Show refreshing indicator
    }

    @Override
    protected List<Forecast> doInBackground(String... params) {
        String forecastResponse = null;
        if (params.length == 1) {
            forecastResponse = fetchForecastByCity(params[0]);
        } else if (params.length == 2) {
            forecastResponse = fetchForecastByCoordinates(params[0], params[1]);
        }
        return parseForecastData(forecastResponse);
    }

    private String fetchForecastByCity(String city) {
        String latLonResponse = HttpRequest.executeGet("https://api.geoapify.com/v1/geocode/search?text=" + city + "&apiKey=adf60cf37d6c44b0ace5b52e2eacef92");

        if (latLonResponse != null) {
            try {
                JSONObject jsonObject = new JSONObject(latLonResponse);
                JSONArray features = jsonObject.getJSONArray("features"); // Get the "features" array

                if (features.length() > 0) {
                    double longitude = features.getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").getDouble(0);
                    double latitude = features.getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates").getDouble(1);

                    return fetchForecastByCoordinates(String.valueOf(latitude), String.valueOf(longitude));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null; // Handle null response
    }


    private String fetchForecastByCoordinates(String latitude, String longitude) {
        // Base URL for your Flask API
        String apiUrl = "https://561f-27-34-66-59.ngrok-free.app";
        String urlString = apiUrl + "/forecast?lat=" + latitude + "&lon=" + longitude;
        return HttpRequest.executeGet(urlString);
    }

    private List<Forecast> parseForecastData(String jsonResponse) {
        List<Forecast> forecastList = new ArrayList<>();
        if (jsonResponse != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonResponse);
                JSONArray forecastArray = jsonObject.getJSONArray("daily_forecasts");
                for (int i = 0; i < forecastArray.length(); i++) {
                    JSONObject dayForecast = forecastArray.getJSONObject(i);
                    String date = dayForecast.getString("date");
                    double highTemp = dayForecast.getDouble("high_temp");
                    double lowTemp = dayForecast.getDouble("low_temp");
                    double prcp = dayForecast.getDouble("prcp");
                    forecastList.add(new Forecast(date, highTemp, lowTemp, prcp));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return forecastList;
    }

    @Override
    protected void onPostExecute(List<Forecast> result) {
        super.onPostExecute(result);
        if (result != null && !result.isEmpty()) {
            activity.forecastAdapter.updateForecastList(result); // Update existing adapter
        } else {
            Toast.makeText(activity, "Error fetching forecast data or no data available", Toast.LENGTH_SHORT).show();
            activity.forecastAdapter.updateForecastList(new ArrayList<>()); // Clear adapter if no data
        }
        activity.swipeRefreshLayout.setRefreshing(false); // Stop refreshing indicator
    }
}
