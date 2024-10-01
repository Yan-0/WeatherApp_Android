package com.shreyan.weather_forecast_app;

public class WeatherIconMapper {

    int getWeatherBackground(String weatherDescription) {
        if (weatherDescription.contains("sunny")) {
            return R.drawable.sunny_background1;
        } else if (weatherDescription.contains("cloud")) {
            return R.drawable.cloud_background;
        } else if (weatherDescription.contains("rain")) {
            return R.drawable.rain_background;
        } else if (weatherDescription.contains("storm")) {
            return R.drawable.rain_background;
        } else if (weatherDescription.contains("snow")) {
            return R.drawable.snow_background;
        } else if (weatherDescription.contains("partly")) {
            return R.drawable.cloud_background;
        } else {
            return R.drawable.sunny_background1; // Fallback background

        }
    }
}
