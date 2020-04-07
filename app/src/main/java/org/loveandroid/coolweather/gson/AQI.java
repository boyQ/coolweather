package org.loveandroid.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class AQI {
    @SerializedName("city")
    public Info info;

    public class Info{
        @SerializedName("aqi")
        public String aqi;

        @SerializedName("pm25")
        public String pm;

    }
}
