package layout;

import android.content.Context;
import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
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
import com.example.letsgo.FootprintActivity;
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
import model.Search;
import model.responseRegister;
import model.responseSearch;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.letsgo.MainActivity.myToken;
import static com.example.letsgo.MainActivity.myUserid;
import static util.httpUtil.sendHttpPost;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private UiSettings mUiSettings;
    private Gson gson = new Gson();
    private String responseData;
    private responseSearch mResponseSearch;
    private List<MyPoiInfo> PoiList;
    private List<OverlayOptions> options=new ArrayList<>();

    public static final int GETSEARCH=2;

    private boolean isLocated=false;

    private Handler handler=new Handler() {
        public void handleMessage(Message msg){
            mResponseSearch=gson.fromJson(msg.obj.toString(), responseSearch.class);
            switch (msg.what){
                case GETSEARCH:
                    if(mResponseSearch.getStatus().equals("ERROR")){
                        new AlertDialog.Builder(getActivity())
                                .setTitle("搜索错误")
                                .setMessage("搜索错误")
                                .setPositiveButton("确定",null)
                                .show();
                    }
                    else{
                        PoiList=mResponseSearch.getPOIs();
                        Marker();
                    }
                    break;
                default:
                    break;
            }
        }
    };

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

        return linearLayout;
        //return inflater.inflate(R.layout.fragment_fragment2, container, false);
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
            if(!isLocated) {
                Log.d("******", "ReceiveLocation");
                if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    locateTo(location);
                }
                search(location);
                isLocated=true;
            }
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

    protected void search(BDLocation location){
        Search mSearch=new Search(myUserid,location.getLatitude(),location.getLongitude(),myToken);
        sendHttpPost("https://shiftlin.top/cgi-bin/Search", gson.toJson(mSearch, Search.class), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseData = response.body().string();
                Message message=new Message();
                message.what=GETSEARCH;
                message.obj=responseData;
                handler.sendMessage(message);
            }
        });
    }

    protected void Marker(){

        for(int i=0;i<PoiList.size()&&i<=10;i++) {
            //定义Maker坐标点
            LatLng point = new LatLng(PoiList.get(i).getLat(), PoiList.get(i).getLng());
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.marker2);
            //构建MarkerOption，用于在地图上添加Marker
            Bundle mBundle = new Bundle();
            mBundle.putString("POI_id", PoiList.get(i).getUid());
            OverlayOptions option = new MarkerOptions()
                    .extraInfo(mBundle)
                    .perspective(true)
                    .position(point)
                    .icon(bitmap);
            options.add(option);
        }
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlays(options);

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent i = new Intent(getActivity(), FootprintActivity.class);
                i.putExtra("POI_id",marker.getExtraInfo().getString("POI_id"));
                i.putExtra("token",myToken);
                i.putExtra("userid",myUserid);
                startActivity(i);
                return true;
            }
        });

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
}


