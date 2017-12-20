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

import model.MyPoiInfo;
import model.SavePoi;
import okhttp3.Call;
import okhttp3.Response;

import static util.httpUtil.sendHttpPost;

class MyListener implements OnGetPoiSearchResultListener {

    private String category_;
    private DataActivity dispatcher_;

    MyListener(DataActivity dispatcher) {
        dispatcher_ = dispatcher;
    }

    public void setCategory(String category) {
        category_ = category;
    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        Log.d("onGetPoiResult", String.valueOf(result.getTotalPoiNum()) + " " + String.valueOf(result.getTotalPageNum()));
        SavePoi mSavePoi = new SavePoi();
        mSavePoi.setCategory(category_);
        ArrayList<MyPoiInfo> myPoiInfos = new ArrayList<>();
        List<PoiInfo> re = result.getAllPoi();
        int m = (re != null) ? re.size() : 0;
        int cnt=m;
        for (int i = 0; i < m; i++) {
            try {
                MyPoiInfo curInfo = new MyPoiInfo();
                curInfo.setCity(re.get(i).city);
                curInfo.setLat(re.get(i).location.latitude);
                curInfo.setLng(re.get(i).location.longitude);
                curInfo.setName(re.get(i).name);
                curInfo.setUid(re.get(i).uid);
                curInfo.setType(re.get(i).type.ordinal());
                myPoiInfos.add(curInfo);
            } catch (Exception e){
                cnt--;
            }
        }
        mSavePoi.setPOI_num(cnt);
        mSavePoi.setPOIs(myPoiInfos);
        dispatcher_.save(mSavePoi, result.getCurrentPageNum(), result.getTotalPageNum());
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }
}

public class DataActivity extends AppCompatActivity {
    //private final double latLow = 30.85;
    private final double latLow = 31.05;
    private final double latHigh = 31.45;
    private final double lngLow = 121.00;
    private final double lngHigh = 121.85;
    private final double step = 0.02;
    private double lat_;
    private double lng_;

    SavePoi savePoi_=new SavePoi();

    private int p_ = 9;
    private final String[] categoryList = {"餐饮美食", "教育学校", "文化艺术", "旅游景点", "购物商场",
            "休闲娱乐", "政府机关", "医疗卫生", "住宅小区", "生活服务"};

    private Gson gson = new Gson();
    MyListener myListener = new MyListener(this);
    PoiSearch ps = PoiSearch.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        ps.setOnGetPoiSearchResultListener(myListener);
        crawlData(p_);
        System.out.println(p_);
    }

    private void crawlData(int p) {
        if (p >= categoryList.length) return;
        myListener.setCategory(categoryList[p]);
        LatLoop(latLow);
    }

    private void LatLoop(double lat) {
        lat_ = lat;
        if (lat_ <= latHigh)
            LngLoop(lngLow);
        else send();
    }

    private void LngLoop(double lng) {
        lng_ = lng;
        if (lng_ <= lngHigh)
            request(0);
        else
            LatLoop(lat_ + step);
    }

    private void request(int k) {
        Log.d("***request***", String.valueOf(k));
        Log.d("***request***", String.valueOf(lat_) + " " + String.valueOf(lng_));
        int r=(int)(step*100000);
        ps.searchNearby(new PoiNearbySearchOption()
                .keyword(categoryList[p_])
                .sortType(PoiSortType.distance_from_near_to_far)
                .location(new LatLng(lat_, lng_))
                .radius(r)
                .pageCapacity(50).pageNum(k));
    }

    private void merge(SavePoi mSavePoi) {
        ArrayList<MyPoiInfo> POIs=savePoi_.getPOIs();
        POIs.addAll(mSavePoi.getPOIs());
        savePoi_.setPOIs(POIs);
        savePoi_.setPOI_num(savePoi_.getPOI_num()+mSavePoi.getPOI_num());
        savePoi_.setCategory(mSavePoi.getCategory());
    }

    private void send() {
        String postData = gson.toJson(savePoi_);
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
        savePoi_.clear();
        long x=0,t=10,s=100000000;
        for (long i = 1; i <= t*s; i++) x++;
    }

    public void save(SavePoi mSavePoi, int k, int pageNum) {
        if (mSavePoi.getPOI_num() > 0) {
            merge(mSavePoi);
        }
        if (k + 1 < pageNum) request(k + 1);
        else {
            if(savePoi_.getPOI_num()>200) send();
            LngLoop(lng_ + step);
        }
    }
}
