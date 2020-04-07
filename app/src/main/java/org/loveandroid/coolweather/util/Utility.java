package org.loveandroid.coolweather.util;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveandroid.coolweather.db.City;
import org.loveandroid.coolweather.db.County;
import org.loveandroid.coolweather.db.Province;
import org.loveandroid.coolweather.gson.Weather;

public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvinces = new JSONArray(response);
                for(int i = 0 ; i <= allProvinces.length()-1;i++){
                    Province province = new Province();
                    JSONObject jsonObject = allProvinces.getJSONObject(i);
                    province.setProvinceName(jsonObject.getString("name"));
                    province.setProvinceCode(jsonObject.getInt("id"));
                    province.save();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     *  解析和处理服务器返回的市级数据
     */

    public static boolean handleCityResponse(String response,int ProvinceCode){

        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray jsonArray = new JSONArray(response);
                for(int i=0;i<=jsonArray.length()-1;i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    City city = new City();
                    city.setCityName(jsonObject.getString("name"));
                    city.setCityCode(jsonObject.getInt("id"));
                    city.setProvinceId(ProvinceCode);
                    city.save();
                }
                return true;
            }catch (Exception e){

                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     *  解析和处理服务器返回的县级数据
     */

    public static boolean handleCountyResponse(String response,int cityCode){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray jsonArray = new JSONArray(response);
                for(int i = 0;i<=jsonArray.length()-1;i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    County county = new County();
                    county.setCityId(cityCode);
                    county.setWeatherId(jsonObject.getString("weather_id"));
                    county.setId(jsonObject.getInt("id"));
                    county.setCountyName(jsonObject.getString("name"));
                    Log.d("one more time",jsonObject.getString("name")+"");

                    county.save();

                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return false;

    }

    /**
     *  将返回的 JSON 数据解析成 Weather 实体类
     */
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray= jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            Weather weather = new Gson().fromJson(weatherContent,Weather.class);
            Log.d("one more time",weather.status);
            Log.d("one more time",weather.now.more.nowWearher);
            return weather;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
