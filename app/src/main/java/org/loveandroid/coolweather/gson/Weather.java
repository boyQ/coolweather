package org.loveandroid.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    public Basic basic;
    public String status;
    public Now now;
    @SerializedName("daily_forecast")
    public List<Forecast> forecasts;
    public AQI aqi;
    public Suggestion suggestion;

}
