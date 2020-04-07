package org.loveandroid.coolweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.SharedPreferencesCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.loveandroid.coolweather.gson.Forecast;
import org.loveandroid.coolweather.gson.Weather;
import org.loveandroid.coolweather.service.AutoUpdateService;
import org.loveandroid.coolweather.util.HttpUtil;
import org.loveandroid.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private NestedScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private SwipeRefreshLayout srl;
    private Toolbar mToolbar;
    private DrawerLayout drawerLayout;
    private FrameLayout frameLayout;

    private Weather weather = null;
    private String weatherId = null;

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private Button qiehuan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        manager= getSupportFragmentManager();
        transaction= manager.beginTransaction();

        mToolbar = findViewById(R.id.weather_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        srl = findViewById(R.id.srl);
        drawerLayout = findViewById(R.id.drawer_layout);
        frameLayout = findViewById(R.id.choose_frame_layout);
        qiehuan = findViewById(R.id.qiehuan);
        qiehuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qiehuan.setVisibility(View.GONE);
                SharedPreferences preferences= getSharedPreferences("boyQ",MODE_PRIVATE);
                preferences.edit().remove("weather").apply();
                frameLayout.setVisibility(View.VISIBLE);
                drawerLayout.openDrawer(GravityCompat.START);

                ChooseAreaFragment fragment = new ChooseAreaFragment();
                transaction.replace(R.id.choose_frame_layout,fragment);
                transaction.commit();

            }
        });

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(weather != null) {
                    String weatherId = weather.basic.weatherId;
                    requestWeatherInfo(weatherId);
                    srl.setRefreshing(false);

                }
            }
        });

        SharedPreferences preferences = getSharedPreferences("boyQ", MODE_PRIVATE);

        String weatherString = preferences.getString("weather",null);

        if(weatherString != null){
            weather = Utility.handleWeatherResponse(weatherString);
            Log.d("one more time",weatherString+"");
            showWeatherInfo(weather);
        }else{
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeatherInfo(weatherId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater =getMenuInflater();
        menuInflater.inflate(R.menu.weather_toolber,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       switch (item.getItemId()){
           case R.id.select_city:
               qiehuan.setVisibility(View.GONE);
                SharedPreferences preferences= getSharedPreferences("boyQ",MODE_PRIVATE);
                preferences.edit().remove("weather").apply();
                frameLayout.setVisibility(View.VISIBLE);
                drawerLayout.openDrawer(GravityCompat.START);

                ChooseAreaFragment fragment = new ChooseAreaFragment();
                transaction.replace(R.id.choose_frame_layout,fragment);
                transaction.commit();

                break;
       }

        return super.onOptionsItemSelected(item);
    }

    public void requestWeatherInfo(final String weaherId){
        String requestAddress = "http://guolin.tech/api/weather?cityid="+weaherId+"&key=7546ecd6013e4ae6ac0223a45f7add37";
        HttpUtil.sendOkHttpRequest(requestAddress, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(WeatherActivity.this,"获取天气失败",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null & "ok".equals(weather.status)) {
                            SharedPreferences preferences = getSharedPreferences("boyQ", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }
                    }
                });
            }
        });
    }

    public void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.upData.updataTime.split(" ")[1];
        String degree = weather.now.more.nowWearher;
        String weatherInfo = weather.now.teperature+"℃";
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast : weather.forecasts){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = view.findViewById(R.id.data_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.featureTime);
            infoText.setText(forecast.more.featureWeather);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }

        if(weather.aqi != null){
            aqiText.setText(weather.aqi.info.aqi);
            pm25Text.setText(weather.aqi.info.pm);
        }
        String comfort = "舒适度" + weather.suggestion.comf.info;
        String carWash = "洗车指数"+ weather.suggestion.cw.info;
        String sport = "活动建议" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
        ImageView weatherBg = findViewById(R.id.weather_bg);
        SharedPreferences preferences = getSharedPreferences("boyQ",MODE_PRIVATE);
        String imgUrl = preferences.getString("pic",null);
        if(imgUrl != null){
            Glide.with(this).load(imgUrl).into(weatherBg);
        }
    }

}
