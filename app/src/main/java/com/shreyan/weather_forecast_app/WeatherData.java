package com.shreyan.weather_forecast_app;

public class WeatherData {
    private final String cityName;
    private final String country;
    private final double app_temp;
    private final double temperature;
    private final double windSpeed;
    private final int humidity;
    private final String weatherDescription;
    private final double precipitation;
    private final String sunrise;
    private final String sunset;
    private final double uv_index;

    public WeatherData(String cityName, String country, double app_temp, double temperature,double windSpeed, int humidity, String weatherDescription, double precipitation, String sunrise, String sunset ,double uv_index) {
        this.cityName = cityName;
        this.country = country;
        this.app_temp = app_temp;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.weatherDescription = weatherDescription;
        this.precipitation = precipitation;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.uv_index = uv_index;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountry() {
        return country;
    }

    public double getAppTemperature() { return app_temp; }

    public double getTemperature() {
        return temperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getHumidity() {
        return humidity;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public double getPrecipitation() {  return precipitation; }

    public String getSunrise() { return sunrise; }

    public String getSunset() {
        return sunset;
    }

    public double getUvIndex() { return uv_index; }
}
