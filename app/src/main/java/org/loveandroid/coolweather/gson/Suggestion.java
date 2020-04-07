package org.loveandroid.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {
    public Comf comf;
    public Sport sport;
    public Cw cw;

    public class Comf{
        @SerializedName("txt")
        public String info;
    }

    public class Sport{
        @SerializedName("txt")
        public String info;
    }

    public class Cw{
        @SerializedName("txt")
        public String info;
    }

}
