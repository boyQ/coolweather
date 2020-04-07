package org.loveandroid.coolweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String json = getSharedPreferences("boyQ",MODE_PRIVATE).getString("weather",null);
        if(json != null){
            Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
