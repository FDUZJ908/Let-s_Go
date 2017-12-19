package layout;

import android.content.Context;
import android.icu.text.LocaleDisplayNames;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.example.letsgo.MainActivity;
import com.example.letsgo.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.logging.StreamHandler;

import model.MyPoiInfo;
import model.SavePoi;
import okhttp3.Call;
import okhttp3.Response;

import static util.httpUtil.sendHttpPost;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment2 extends Fragment implements OnGetPoiSearchResultListener, OnGetGeoCoderResultListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private final String[] categoryList = {"餐饮美食", "教育机构", "文化艺术", "旅游景点", "购物商场",
            "休闲娱乐", "政府机关", "医疗卫生", "住宅小区", "生活服务"};
    private final double latLow = 30.85;
    private final double latHigh = 31.45;
    private final double lngLow = 121.00;
    private final double lngHigh = 121.90;
    private final double step = 0.05;
    private int interval = 2000;
    private int i_ = 0;

    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private UiSettings mUiSettings;
    private List<Poi> mPoiList;
    private PoiSearch mPoiSearch;
    private GeoCoder mSearch;
    private Gson gson = new Gson();

    public Fragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment2 newInstance(String param1, String param2) {
        Fragment2 fragment = new Fragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mLocationClient = new LocationClient(getContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        //SDKInitializer.initialize(getContext());

        mPoiSearch = PoiSearch.newInstance();
        mSearch = GeoCoder.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mSearch.setOnGetGeoCodeResultListener(this);
        /*
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener(){
            @Override
            public void onGetPoiResult(PoiResult result){
                //获取POI检索结果
            }
            @Override
            public void onGetPoiDetailResult(PoiDetailResult result){
                if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Log.d("***检索失败***","失败");
                    //详情检索失败
                    // result.error请参考SearchResult.ERRORNO
                } else {
                    //检索成功
                    Log.d("***检索成功***",result.getName());
                }
            }
            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult result){

            }
        });
        */

        //绘制界面
        TextureMapView mMapView = new TextureMapView(getActivity());
        //mMapView.setLogoPosition(LogoPosition.logoPostionCenterBottom);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);

        //设置指南针
        mUiSettings = mBaiduMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.addView(mMapView);

        //开始定位
        requestLocation();


        crawlData(i_);

        return linearLayout;
        //return inflater.inflate(R.layout.fragment_fragment2, container, false);
    }

    public void onGetPoiResult(PoiResult result) {
        Log.d("onGetPoiResult", String.valueOf(result.getTotalPoiNum()));
        //for (int j = 0; j < result.getTotalPageNum(); j++) {
        //   result.setCurrentPageNum(j);
        Log.d("**", String.valueOf(result.getCurrentPageNum()));
        SavePoi mSavePoi = new SavePoi();
        mSavePoi.setCategory(categoryList[i_]);
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
        //i_++;
        //crawlData(i_);

        // }
    }

    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Log.d("***检索失败***", String.valueOf(result.error));
        } else {
            Log.d("***检索成功***", result.getName());
        }
    }

    public void onGetGeoCodeResult(GeoCodeResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            //没有检索到结果
        }

        //获取地理编码结果
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            //没有找到检索结果
        }
        List<PoiInfo> re = result.getPoiList();
        Log.d("ReverseGeoCodeResult", String.valueOf(re.size()));
        for (int i = 0; i < re.size(); i++)
            Log.d("**", re.get(i).name);
        //获取反向地理编码结果
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    private void requestLocation() {
        LocationClientOption option = new LocationClientOption();
        //option.setScanSpan(500000);//每500秒获得一次定位
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//高精度定位
        option.setIsNeedLocationPoiList(true);//获得POI
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        //option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);//只有GPS定位
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }


    protected class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.d("******", "ReceiveLocation");

            if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                locateTo(location);
            }
            Log.d("******", "Start Work");


            /*
            while (i_<categoryList.length){
                crawlData(i_);
                i_++;
            }*/


            /*
            mPoiSearch.searchNearby(new PoiNearbySearchOption()
                    .keyword("景点")
                    .sortType(PoiSortType.distance_from_near_to_far)
                    .location(new LatLng(location.getLatitude(), location.getLongitude()))
                    .radius(200000)
                    .pageNum(10));
             */
            //mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid("5bb757e8485e2135e6e96549"));

            //mSearch.reverseGeoCode(new ReverseGeoCodeOption()
            //        .location(new LatLng(location.getLatitude(),location.getLongitude())));
/*
            try {
                mPoiList = location.getPoiList();
                for (int i = 0; i < mPoiList.size(); i++) {
                    Log.d("***Poi***", mPoiList.get(i).getId());
                    if( mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(mPoiList.get(i).getId())) )
                        Log.d("搜索结果","True");
                    else Log.d("搜索结果","False");
                }
            } catch (Exception e) {
                Log.d("***Poi***", e.getMessage());
            }
*/


        }
    }

    protected void locateTo(BDLocation location) {
        MyLocationData locationData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                .direction(100).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locationData); //设置定位
        centerToLocation(location, 250); //设置中心“我”
    }

    protected void centerToLocation(BDLocation location, int duration) {
        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        MapStatus newStatus = new MapStatus.Builder()
                .target(ll).zoom(16).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(newStatus);
        mBaiduMap.animateMapStatus(mMapStatusUpdate, duration);//duration为动画的时间
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    protected void crawlData(int i) {

        double curLat = latLow;
        while (curLat < latHigh) {
            double curLng = lngLow;
            while (curLng < lngHigh) {
                Log.d("Cur", String.valueOf(curLat) + ":" + String.valueOf(curLng));
                PoiSearch ps = PoiSearch.newInstance();
                ps.setOnGetPoiSearchResultListener(this);
                ps.searchNearby(new PoiNearbySearchOption()
                        .keyword(categoryList[i])
                        .sortType(PoiSortType.distance_from_near_to_far)
                        .location(new LatLng(curLat, curLng))
                        .radius(5000)
                        .pageCapacity(50));
            }
            curLng += step;
        }
        curLat += step;
    }
        /*
        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .keyword(categoryList[i])
                .sortType(PoiSortType.distance_from_near_to_far)
                .location(new LatLng(31.303, 121.517))
                .radius(5000)
                .pageCapacity(50));
        try {
            Thread.sleep(interval);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
}


