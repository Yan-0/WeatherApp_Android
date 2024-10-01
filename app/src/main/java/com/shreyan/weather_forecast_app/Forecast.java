package com.shreyan.weather_forecast_app;

public class Forecast {
    private final String date;
    private final double highTemp;
    private final double lowTemp;
    private final double prcp;

    public Forecast(String date, double highTemp, double lowTemp, double prcp) {
        this.date = date;
        this.highTemp = highTemp;
        this.lowTemp = lowTemp;
        this.prcp = prcp;
    }

    public String getDate() {
        return date;
    }

    public double getHighTemp() {
        return highTemp;
    }

    public double getLowTemp() {
        return lowTemp;
    }

    public double getPrcp() {
        return prcp;
    }
}
