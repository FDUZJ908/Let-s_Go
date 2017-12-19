package com.example.letsgo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;

import model.MyPoiInfo;
import model.SavePoi;
import okhttp3.Call;
import okhttp3.Response;

import static util.httpUtil.sendHttpPost;

class MyListener implements OnGetPoiSearchResultListener {

    private String category_;
    private DataActivity dispatcher_;

    MyListener(String category,DataActivity dispatcher){
        category_=category;
        dispatcher_=dispatcher;
    }

    public void setCategory(String category) {
        category_ = category;
    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        synchronized (MyListener.class) {
            Log.d("onGetPoiResult", String.valueOf(result.getTotalPoiNum()));
            //for (int j = 0; j < result.getTotalPageNum(); j++) {
            //   result.setCurrentPageNum(j);
            SavePoi mSavePoi = new SavePoi();
            mSavePoi.setCategory(category_);
            ArrayList<MyPoiInfo> myPoiInfos = new ArrayList<>();
            List<PoiInfo> re = result.getAllPoi();
            mSavePoi.setPOI_num(re.size());
            for (int i = 0; i < re.size(); i++) {
                MyPoiInfo curInfo = new MyPoiInfo();
                curInfo.setCity(re.get(i).city);
                curInfo.setLat(re.get(i).location.latitude);
                curInfo.setLng(re.get(i).location.longitude);
                curInfo.setName(re.get(i).name);
                curInfo.setUid(re.get(i).uid);
                curInfo.setType(re.get(i).type.ordinal());
                myPoiInfos.add(curInfo);
            }
            mSavePoi.setPOIs(myPoiInfos);
            dispatcher_.save(mSavePoi);
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }
}

public class DataActivity extends AppCompatActivity {

    private final double latLow = 30.85;
    private final double latHigh = 31.45;
    private final double lngLow = 121.00;
    private final double lngHigh = 121.90;
    private final double step = 0.05;
    private int interval = 2000;
    private int i_ = 0;
    private final String[] categoryList = {"餐饮美食", "教育机构", "文化艺术", "旅游景点", "购物商场",
            "休闲娱乐", "政府机关", "医疗卫生", "住宅小区", "生活服务"};

    private Gson gson = new Gson();
    MyListener myListener = new MyListener(categoryList[0],this);
    PoiSearch ps = PoiSearch.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        try {
            ps.setOnGetPoiSearchResultListener(myListener);
            synchronized (MyListener.class) {
                crawlData(i_);
            }
        } catch (Exception e) {
        }
    }

    private void request(String category, double curLat, double curLng,int k) throws Exception {
        Log.d("Cur", String.valueOf(curLat) + ":" + String.valueOf(curLng));
        ps.searchNearby(new PoiNearbySearchOption()
                .keyword(category)
                .sortType(PoiSortType.distance_from_near_to_far)
                .location(new LatLng(curLat, curLng))
                .radius(500)
                .pageCapacity(50).pageNum(k));
        Log.d("****","send request");
    }

    public void save(SavePoi mSavePoi) {
        synchronized (DataActivity.class) {
            String postData = gson.toJson(mSavePoi);
            Log.d("***PostData***", "正在发送数据...");
            Log.d("***PostData***", postData);
            sendHttpPost("https://shiftlin.top/cgi-bin/Save", postData, new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d("**PostData**", "Success:" + response.body().string());
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("**PostData**", "Failure");
                    e.printStackTrace();
                }

            });
        }
    }

    protected void crawlData(int i) throws Exception {
        for (int p = 0; p < 3; p++)
        {
            request(categoryList[i],31.3,121.5,p);
            //Thread.sleep(1000);
            //long s=100000000,t=5;
            //long cnt=s*t;
            //for(long k=0;k<cnt;k++);
        }
           /* long s=100000000,t=20;
            long cnt=s*t;
            for(long k=0;k<cnt;k++);*/
        //request(categoryList[i], 31.1, 121.6);
      /*  request(categoryList[i],31.2,121.7);
        request(categoryList[i],31,121.5);
        request(categoryList[i],31.1,121.6);
        request(categoryList[i],31.2,121.7);*/

       /* double curLat = latLow;
        while (curLat < latHigh) {
            double curLng = lngLow;
            while (curLng < lngHigh) {
                request(categoryList[i], curLat, curLng);
                curLng += step;
              *//*  long s = 100000000, t = 20;
                long cnt = s * t;
                for (long k = 0; k < cnt; k++) ;
                //break;*//*
            }
            curLat += step;
            break;
        }*/
    }
}
