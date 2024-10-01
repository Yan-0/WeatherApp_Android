package com.shreyan.weather_forecast_app;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherUtils {

    public static void updateUI(HomeActivity activity, String result) {
        WeatherData weatherData = parseWeatherData(result);
        if (weatherData != null) {
            setWeatherData(activity, weatherData);
        } else {
            Toast.makeText(activity, "Error: Cannot fetch weather for that location", Toast.LENGTH_SHORT).show();
        }
        activity.swipeRefreshLayout.setRefreshing(false);
    }

    public static WeatherData parseWeatherData(String result) {
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONObject dataObj = jsonObj.getJSONArray("data").getJSONObject(0);
            JSONObject weatherObj = dataObj.getJSONObject("weather");

            String cityName = dataObj.getString("city_name");
            String country = dataObj.getString("country_code");
            double appTemp = dataObj.getDouble("app_temp");
            double temperature = dataObj.getDouble("temp");
            double windSpeed = dataObj.getDouble("wind_spd");
            int humidity = dataObj.getInt("rh");
            String weatherDescription = weatherObj.getString("description");
            double precipitation = dataObj.optDouble("precip", 0.0);
            String sunriseTime = dataObj.getString("sunrise");
            String sunsetTime = dataObj.getString("sunset");

            // Get the source timezone (you may need to determine the correct timezone based on latitude/longitude)
            TimeZone sourceTimeZone = TimeZone.getTimeZone("GMT");  // Replace "GMT" with actual location timezone

            // Format the sunrise and sunset times according to the device timezone
            String formattedSunriseTime = formatSunriseTime(sunriseTime, sourceTimeZone);
            String formattedSunsetTime = formatSunsetTime(sunsetTime, sourceTimeZone);

            double uvIndex = dataObj.getDouble("uv");

            // Return the parsed weather data
            return new WeatherData(cityName, country, appTemp, temperature, windSpeed, humidity, weatherDescription, precipitation, formattedSunriseTime, formattedSunsetTime, uvIndex);
        } catch (Exception e) {
            Log.e("WeatherUtils", "Error parsing weather data", e);
            return null;
        }
    }

    @SuppressLint({"NewApi", "SetTextI18n"})
    private static void setWeatherData(HomeActivity activity, WeatherData data) {
        activity.city.setText(data.getCityName() + ", " + data.getCountry());
        activity.appTemp.setText(String.format(Locale.ENGLISH, "%d", Math.round(data.getAppTemperature())));
        activity.temp.setText(String.format(Locale.ENGLISH, "%d", Math.round(data.getTemperature())));
        activity.humidity.setText(data.getHumidity() + "%");

        // Use formatUnixTime for sunrise and sunset
        activity.sunrises.setText((data.getSunrise()));
        activity.sunsets.setText((data.getSunset()));

        activity.wind_speed.setText(data.getWindSpeed() + " m/s");
        activity.uv_index.setText(String.valueOf(data.getUvIndex()));
        activity.precipitation.setText(String.format(Locale.ENGLISH, "%.2f mm", data.getPrecipitation()));

        WeatherIconMapper weatherIconMapper = new WeatherIconMapper();
        activity.weatherBackground.setImageResource(weatherIconMapper.getWeatherBackground(data.getWeatherDescription()));
    }

    private static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    private static String formatSunriseTime(String sunriseTime, TimeZone sourceTimeZone) {
        return formatTimeString(sunriseTime, sourceTimeZone);
    }

    private static String formatSunsetTime(String sunsetTime, TimeZone sourceTimeZone) {
        return formatTimeString(sunsetTime, sourceTimeZone);
    }

    private static String formatTimeString(String time, TimeZone sourceTimeZone) {
        try {
            // Parse the "HH:mm" format provided by the API
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            inputFormat.setTimeZone(sourceTimeZone);  // Set the source location timezone
            Date date = inputFormat.parse(time);  // Parse the time string

            // Get the device's current timezone
            TimeZone deviceTimeZone = TimeZone.getDefault();

            // Create a Calendar instance and set it to the parsed time
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.setTimeZone(sourceTimeZone);  // Set calendar time to source timezone

            // Adjust the time to the device's timezone
            calendar.setTimeZone(deviceTimeZone);

            // Format the time according to the device's timezone (12-hour format with AM/PM)
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            outputFormat.setTimeZone(deviceTimeZone);  // Set the output format to the device timezone
            return outputFormat.format(calendar.getTime());

        } catch (Exception e) {
            Log.e("WeatherUtils", "Error formatting time: " + time, e);
            // Return the original time if there's an error
            return time;
        }
    }

}
