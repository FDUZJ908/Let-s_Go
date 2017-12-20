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
        Log.d("onGetPoiResult", String.valueOf(result.getTotalPoiNum()));
        SavePoi mSavePoi = new SavePoi();
        mSavePoi.setCategory(category_);
        ArrayList<MyPoiInfo> myPoiInfos = new ArrayList<>();
        List<PoiInfo> re = result.getAllPoi();
        int m = (re != null) ? re.size() : 0;
        mSavePoi.setPOI_num(m);
        try {
            for (int i = 0; i < m; i++) {
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
        }catch(Exception e)
        {
            return;
        }
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
    private int p_ = 7;
    private final String[] categoryList = {"餐饮美食", "教育机构", "文化艺术", "旅游景点", "购物商场",
            "休闲娱乐", "政府机关", "医疗卫生", "住宅小区", "生活服务"};
    boolean[] vis = new boolean[20];

    private Gson gson = new Gson();
    MyListener myListener = new MyListener(this);
    PoiSearch ps = PoiSearch.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        ps.setOnGetPoiSearchResultListener(myListener);
        int n = categoryList.length;
        for (int i = 0; i < n; i++) vis[i] = false;
        crawlData(p_);
    }

    private void crawlData(int p) {
        if (p >= categoryList.length) return;
        vis[p] = true;
        myListener.setCategory(categoryList[p]);
        request(0);
    }

    private void request(int k) {
        Log.d("***request***", String.valueOf(k));
        ps.searchNearby(new PoiNearbySearchOption()
                .keyword(categoryList[p_])
                .sortType(PoiSortType.distance_from_near_to_far)
                .location(new LatLng(31.24, 121.47))
                .radius(40000)
                .pageCapacity(50).pageNum(k));
    }

    public void save(SavePoi mSavePoi, int k, int pageNum) {
        if (mSavePoi.getPOI_num() > 0) {
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
        if (k + 1 < pageNum) request(k + 1);
        else {
            p_ += 1;
            crawlData(p_);
        }

    }
}