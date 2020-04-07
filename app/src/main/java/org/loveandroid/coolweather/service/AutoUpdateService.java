package org.loveandroid.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;

import org.jetbrains.annotations.NotNull;
import org.loveandroid.coolweather.gson.Weather;
import org.loveandroid.coolweather.util.HttpUtil;
import org.loveandroid.coolweather.util.Utility;

import java.io.IOException;
import java.util.prefs.Preferences;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    private SharedPreferences preferences;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,i,0);
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        manager.cancel(pi);
        int anHours = 1000*60*60*8;
        long featureTime = SystemClock.elapsedRealtime()+anHours;
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,featureTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    public void updateBingPic(){
        String address = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                SharedPreferences preferences = getSharedPreferences("boyQ",MODE_PRIVATE);
                SharedPreferences.Editor editor= preferences.edit();
                editor.putString("pic",response.body().string());
                editor.apply();
            }
        });
    }

    public void updateWeather(){
        preferences = getSharedPreferences("boyQ",MODE_PRIVATE);
        String json = preferences.getString("weather",null);
        Weather weather = Utility.handleWeatherResponse(json);
        String cityCode= weather.basic.weatherId;
        String address = "http://guolin.tech/api/weather?cityid="+cityCode+"&key=7546ecd6013e4ae6ac0223a45f7add37";
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String da = response.body().string();
                Weather weather = Utility.handleWeatherResponse(da);
                if(weather !=null) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("weather",da);
                    editor.apply();
                }
            }
        });

    }

}
