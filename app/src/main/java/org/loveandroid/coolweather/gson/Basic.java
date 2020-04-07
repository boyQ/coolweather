package org.loveandroid.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    @SerializedName("cid")
    public String weatherId;
    @SerializedName("location")
    public String cityName;

    @SerializedName("update")
    public UpData upData;

    public class UpData{
        @SerializedName("loc")
        public String updataTime;

    }

}
