package layout;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
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
import com.example.letsgo.FootprintActivity;
import com.example.letsgo.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.MyPoiInfo;
import model.Search;
import model.responseSearch;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.letsgo.MainActivity.myLat;
import static com.example.letsgo.MainActivity.myLng;
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

    private TextureMapView mMapView;
    private Button Refresh;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private UiSettings mUiSettings;
    private Gson gson = new Gson();
    private String responseData;
    private responseSearch mResponseSearch;
    private List<MyPoiInfo> PoiList;
    private List<OverlayOptions> options = new ArrayList<>();
    private int markerI = 0;
    private double mLng;
    private double mLat;

    public static final int GETSEARCH = 2;

    private boolean isLocated = false;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            mResponseSearch = gson.fromJson(msg.obj.toString(), responseSearch.class);
            Log.d("****Search***", msg.obj.toString());
            switch (msg.what) {
                case GETSEARCH:
                    if (mResponseSearch.getStatus().equals("ERROR")) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("搜索错误")
                                .setMessage("搜索错误")
                                .setPositiveButton("确定", null)
                                .show();
                    } else {
                        PoiList = mResponseSearch.getPOIs();
                        Marker(markerI);
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
        return inflater.inflate(R.layout.fragment_fragment2, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        mLocationClient = new LocationClient(getContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        //绘制界面
        mMapView = getView().findViewById(R.id.MainMap);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        Refresh = getView().findViewById(R.id.Refresh);

        //设置指南针
        mUiSettings = mBaiduMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);
        Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Marker(++markerI);
            }
        });
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent i = new Intent(getActivity(), FootprintActivity.class);
                i.putExtra("POI_id", marker.getExtraInfo().getString("POI_id"));
                i.putExtra("POI_name", marker.getExtraInfo().getString("POI_name"));
                i.putExtra("token", myToken);
                i.putExtra("userid", myUserid);
                i.putExtra("mLat", mLat);
                i.putExtra("mLng", mLng);
                startActivity(i);
                return true;
            }
        });

        //开始定位
        requestLocation();

    }

    private void requestLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(10000);//每10秒获得一次定位
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
            if (!isLocated) {
                Log.d("******", "第一次定位");
                if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    locateTo(location);
                }
                mLat = location.getLatitude();
                mLng = location.getLongitude();
                myLat = mLat;
                myLng = mLng;
                search(location);
                isLocated = true;
            } else if (isMoved(location)) {
                //Log.d("******", "移动超过200定位");
                locateTo(location);
            } else
                ;
                //Log.d("******", "移动不足200不定位");
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

    protected void search(BDLocation location) {
        Search mSearch = new Search(myUserid, location.getLatitude(), location.getLongitude(), myToken);
        sendHttpPost("https://shiftlin.top/cgi-bin/Search", gson.toJson(mSearch, Search.class), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseData = response.body().string();
                Message message = new Message();
                message.what = GETSEARCH;
                message.obj = responseData;
                handler.sendMessage(message);
            }
        });
    }

    protected void Marker(int j) {
        if(PoiList.size()==0)
            // no result
            return;
        int d = PoiList.size() / 10;
        if(d==0){
            // num<10
            j=0;
            d=1;
        }
        else
            j = j % d;
        mBaiduMap.clear();
        options.clear();
        for (int i = j; i < PoiList.size(); i += d) {
            //定义Maker坐标点
            LatLng point = new LatLng(PoiList.get(i).getLat(), PoiList.get(i).getLng());
            //构建Marker图标
            int marker = R.drawable.marker1_0;
            int popularity = PoiList.get(i).getPopularity();
            if (popularity >= 30) marker = R.drawable.marker1_4;
            else if (popularity >= 15) marker = R.drawable.marker1_3;
            else if (popularity >= 5) marker = R.drawable.marker1_2;
            else if (popularity >0) marker = R.drawable.marker1_1;
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(marker);
            //构建MarkerOption，用于在地图上添加Marker
            Bundle mBundle = new Bundle();
            mBundle.putString("POI_id", PoiList.get(i).getUid());
            mBundle.putString("POI_name", PoiList.get(i).getName());
            OverlayOptions option = new MarkerOptions()
                    .extraInfo(mBundle)
                    .perspective(true)
                    .position(point)
                    .icon(bitmap);
            options.add(option);
        }
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlays(options);


    }

    protected boolean isMoved(BDLocation location) {
        if (Math.sqrt((location.getLatitude() - myLat) * (location.getLatitude() - myLat)
                + (location.getLongitude() - myLng) * (location.getLongitude() - myLng)) > 0.002) {
            myLat = location.getLatitude();
            myLng = location.getLongitude();
            return true;
        }
        return false;
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
    public void onDestroy() {
        if (mLocationClient != null)
            mLocationClient.stop();
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
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


