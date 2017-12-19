package com.example.letsgo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import model.MyPoiInfo;
import model.SavePoi;
import okhttp3.Call;
import okhttp3.Response;

import static util.httpUtil.sendHttpPost;

class MyListener implements OnGetPoiSearchResultListener {

    private Gson gson=new Gson();
    private String category_;

    public void setCategory(String category) {
        category_=category;
    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        Log.d("onGetPoiResult", String.valueOf(result.getTotalPoiNum()));
        //for (int j = 0; j < result.getTotalPageNum(); j++) {
        //   result.setCurrentPageNum(j);
        Log.d("**", String.valueOf(result.getCurrentPageNum()));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        crawlData(i_);
    }

    private void request(String category,double curLat,double curLng)
    {
        Log.d("Cur", String.valueOf(curLat) + ":" + String.valueOf(curLng));
        PoiSearch ps = PoiSearch.newInstance();
        MyListener myListener=new MyListener();
        myListener.setCategory(category);
        ps.setOnGetPoiSearchResultListener(myListener);
        ps.searchNearby(new PoiNearbySearchOption()
                .keyword(category)
                .sortType(PoiSortType.distance_from_near_to_far)
                .location(new LatLng(curLat, curLng))
                .radius(5000)
                .pageCapacity(50));
    }

    protected void crawlData(int i) {

        double curLat = latLow;
        while (curLat < latHigh) {
            double curLng = lngLow;
            while (curLng < lngHigh) {
                request(categoryList[i],curLat,curLng);
                curLng+=step;
            }
            curLat += step;
        }
    }
}
