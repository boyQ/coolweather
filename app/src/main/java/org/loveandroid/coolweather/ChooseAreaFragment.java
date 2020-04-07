package org.loveandroid.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;
import org.loveandroid.coolweather.db.City;
import org.loveandroid.coolweather.gson.Weather;
import org.loveandroid.coolweather.util.HttpUtil;
import org.loveandroid.coolweather.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    public static final int ZT_Province=0;
    public static final int ZT_City=1;
    public static final int ZT_County=2;
    public int zt=-1;

    private int Province_id;
    private int City_id;

    private TextView areaTitle;
    private Button areaBack;
    private ListView areaLv;
    private View view;
    private ArrayAdapter<String> mAdapter;

    private int zz;

    private List<String> dataList;
    private String ProvinceName;
    private List<Integer> numberCounty;
    private List<String> weatherIds;

    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.choose_area,container,false);
        initView();

        return view;
    }

    public void initView(){
        areaBack = view.findViewById(R.id.area_back);
        areaLv = view.findViewById(R.id.area_list);
        areaTitle = view.findViewById(R.id.area_title);

        numberCounty = new ArrayList<Integer>();
        dataList = new ArrayList<String>();
        dataList.add("111");
        mAdapter = new ArrayAdapter<String>(getContext(),R.layout.area_item,dataList);
        areaLv.setAdapter(mAdapter);
        weatherIds = new ArrayList<String>();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        areaLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (zt == ZT_Province){
                    Province_id = position + 1;
                    queryCity(position + 1);
                }else if(zt == ZT_City) {
                    zz=position;
                    queryCounty(zz);
                }else if(zt == ZT_County){
                    String weatherCode = weatherIds.get(position);
                    Intent intent = new Intent(getContext(),WeatherActivity.class);
                    intent.putExtra("weather_id",weatherCode);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        areaBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(zt==ZT_City){
                    ProvinceName=null;
                    queryProvince();
                }else if(zt == ZT_County){
                    Log.d("one more time",Province_id+"");
                    queryCity(Province_id);
                }
            }
        });

        if(zt== -1) {
            queryProvince();
        }
    }

    public void queryProvince(){
        Cursor cursor= LitePal.findBySQL("select * from Province");
        if(cursor.moveToFirst()) {
            areaBack.setVisibility(View.GONE);
            areaTitle.setText("中国");
            areaLv.setSelection(0);
            dataList.clear();
            do {
                String Name = cursor.getString(cursor.getColumnIndex("provincename"));
                dataList.add(Name);
            } while (cursor.moveToNext());
            mAdapter.notifyDataSetChanged();
            areaLv.setSelection(0);
            zt=ZT_Province;
        }else{
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"Province");

        }
    }

    public void queryCity(int parent_id){

        Cursor cursor=LitePal.findBySQL("select * from City where provinceId ="+parent_id);
        if(cursor.moveToFirst()) {
            if(ProvinceName == null) {
                areaTitle.setText(dataList.get(parent_id - 1));
                ProvinceName = dataList.get(parent_id-1);
            }else{
                areaTitle.setText(ProvinceName);
            }
            areaLv.setSelection(0);
            dataList.clear();
            numberCounty.clear();

            do {
                areaBack.setVisibility(View.VISIBLE);
                String cityName = cursor.getString(cursor.getColumnIndex("cityname"));
                dataList.add(cityName);
                int code = cursor.getInt(cursor.getColumnIndex("citycode"));
                Log.d("one more time",code+"");
                numberCounty.add(code);
            } while (cursor.moveToNext());
            mAdapter.notifyDataSetChanged();
            areaLv.setSelection(0);
            zt = ZT_City;
        }else {
            String address="http://guolin.tech/api/china/"+Province_id;
            queryFromServer(address,"City");

        }

    }

    public void queryCounty(int parent_id){
        City_id = numberCounty.get(parent_id);
        Cursor cursor=LitePal.findBySQL("select * from County where cityId ="+City_id);

        if(cursor.moveToFirst()) {
            areaTitle.setText(dataList.get(parent_id));
            dataList.clear();
            weatherIds.clear();
            do {
                String countyName=cursor.getString(cursor.getColumnIndex("countyname"));
                String weatherId = cursor.getString(cursor.getColumnIndex("weatherid"));
                Log.d("one more time",weatherId);
                weatherIds.add(weatherId);
                dataList.add(countyName);
            } while (cursor.moveToNext());
            mAdapter.notifyDataSetChanged();
            areaLv.setSelection(0);
            zt = ZT_County;
        }else {
            String address="http://guolin.tech/api/china/"+Province_id+"/"+City_id;
            queryFromServer(address,"County");

        }

    }

    private void showProgeress(){
        if(progressDialog == null){
            progressDialog =new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
    }
    private void closeProgress(){
        if(progressDialog != null){
            progressDialog.dismiss();

        }
    }

    public void queryFromServer(String address, final String type){
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                closeProgress();
                Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String json = response.body().string();
                Log.d("one more time",json);
                boolean result = false;

                if(type.equals("Province")){
                    result =Utility.handleProvinceResponse(json);
                }else if(type.equals("City")){
                    result =Utility.handleCityResponse(json,Province_id);
                }else if(type.equals("County")){
                    result = Utility.handleCountyResponse(json, City_id);
                }

                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgress();

                            if(type.equals("Province")){
                                queryProvince();

                            }else if(type.equals("City")){
                                queryCity(Province_id);

                            }else if(type.equals("County")){
                                queryCounty(zz);

                            }
                        }
                    });
                }

            }
        });

    }

}
