package org.loveandroid.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {
    @SerializedName("tmp")
    public String teperature;


    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String nowWearher;
    }

}
