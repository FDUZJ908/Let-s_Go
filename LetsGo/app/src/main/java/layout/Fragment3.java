package layout;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.example.letsgo.DataActivity;
import com.example.letsgo.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import model.MyPoiInfo;
import model.Recommend;
import model.Register;
import model.responseSearch;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.letsgo.MainActivity.myLat;
import static com.example.letsgo.MainActivity.myLng;
import static com.example.letsgo.MainActivity.myTags;
import static com.example.letsgo.MainActivity.myToken;
import static com.example.letsgo.MainActivity.myUserid;
import static util.httpUtil.sendHttpPost;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment3.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment3 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextureMapView mMapView;
    private TextView RecommendInfo;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private Gson gson = new Gson();
    private String responseData;
    private responseSearch mResponseSearch;
    private List<MyPoiInfo> MyPoiInfoList;
    private int ListIndex;

    public static final int GETRECOMMEND = 7;

    public Fragment3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment3.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment3 newInstance(String param1, String param2) {
        Fragment3 fragment = new Fragment3();
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
        return inflater.inflate(R.layout.fragment_fragment3, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    protected void initViews() {
        mMapView = getView().findViewById(R.id.Recommend);
        RecommendInfo=getView().findViewById(R.id.RecommendInfo);
        mBaiduMap = mMapView.getMap();
        mLocationClient = new LocationClient(getContext());
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        mBaiduMap.setOnMapClickListener(MapClickListener);
        RecommendInfo.setText(GetInfo());
        locateTo(myLat,myLng);
    }

    protected void locateTo(double Lat,double Lng) {

        Log.d("***","开始定位:"+String.valueOf(Lat)+":"+String.valueOf(Lng));
        MyLocationData locationData = new MyLocationData.Builder()
                .direction(100).latitude(Lat)
                .longitude(Lng).build();
        mBaiduMap.setMyLocationData(locationData); //设置定位
        LatLng ll = new LatLng(Lat, Lng);
        MapStatus newStatus = new MapStatus.Builder()
                .target(ll).zoom(16).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(newStatus);
        mBaiduMap.animateMapStatus(mMapStatusUpdate, 250);//duration为动画的时间
    }



    private BaiduMap.OnMapClickListener MapClickListener = new BaiduMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            Log.d("***","地图被点");
            GetRecommend();
        }

        @Override
        public boolean onMapPoiClick(MapPoi mapPoi) {
            return false;
        }
    };

    private void GetRecommend() {
        if (MyPoiInfoList != null && ListIndex < MyPoiInfoList.size()) {
            ++ListIndex;
            RecommendMarker();
        }
        else
            sendHttpPost("https://shiftlin.top/cgi-bin/Recommend", gson.toJson(new Recommend(myUserid, myLat, myLng, myToken, myTags)), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    responseData = response.body().string();
                    Log.d("***",responseData);
                    Message message = new Message();
                    message.what = GETRECOMMEND;
                    message.obj = responseData;
                    handler.sendMessage(message);
                }
            });
    }

    private void RecommendMarker() {
        if(MyPoiInfoList!=null && ListIndex<MyPoiInfoList.size()){
            RecommendInfo.setText(GetInfo());
            mBaiduMap.clear();
            LatLng point=new LatLng(MyPoiInfoList.get(ListIndex).getLat(),MyPoiInfoList.get(ListIndex).getLng());
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.marker2);
            OverlayOptions option = new MarkerOptions()
                    .perspective(true)
                    .position(point)
                    .icon(bitmap);
            mBaiduMap.addOverlay(option);
            //定位到marker
            locateTo(MyPoiInfoList.get(ListIndex).getLat(),MyPoiInfoList.get(ListIndex).getLng());
        }
        else {
            GetRecommend();
        }

    }

    private String GetInfo(){
        String title="点击地图即可探索神秘地点↓"+"\n";
        if(MyPoiInfoList!=null && MyPoiInfoList.size()>0) {
            String name = "【名称】:" + MyPoiInfoList.get(ListIndex).getName() + "\n";
            String category = "【类别】:" + MyPoiInfoList.get(ListIndex).getCategory() + "\n";
            String city = "【城市】:" + MyPoiInfoList.get(ListIndex).getCity() + "\n";
            return  title+name + category + city;
        }
        else
            return title;
    }


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            mResponseSearch = gson.fromJson(msg.obj.toString(), responseSearch.class);
            switch (msg.what) {
                case GETRECOMMEND:
                    if (mResponseSearch.getStatus().equals("ERROR")) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("推荐错误")
                                .setMessage("推荐错误")
                                .setPositiveButton("确定", null)
                                .show();
                    } else {
                        MyPoiInfoList = mResponseSearch.getPOIs();
                        ListIndex=0;
                        RecommendMarker();
                    }
                    break;
                default:
                    break;
            }
        }
    };

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
    public void onDestroy(){
        if(mLocationClient!=null)
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

    /*
        private Button buttonTest;
        private TextView textTest;
    */

    private View.OnClickListener RecommendListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

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
