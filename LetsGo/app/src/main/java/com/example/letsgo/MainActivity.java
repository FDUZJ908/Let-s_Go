package com.example.letsgo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.TextureMapView;

import java.util.ArrayList;
import java.util.List;

import layout.Fragment1;
import layout.Fragment2;
import layout.Fragment3;
import layout.Fragment4;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        Fragment1.OnFragmentInteractionListener,
        Fragment2.OnFragmentInteractionListener,
        Fragment3.OnFragmentInteractionListener,
        Fragment4.OnFragmentInteractionListener {
    public LocationClient mLocationClient;
    private TextView positionText;
    private TextureMapView mapView;
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;

    //定义3个Fragment的对象
    private Fragment1 fg1;
    private Fragment2 fg2;
    private Fragment3 fg3;
    private Fragment4 fg4;
    //帧布局对象,就是用来存放Fragment的容器
    private FrameLayout flayout;
    //定义底部导航栏的三个布局 监听点击
    private RelativeLayout course_layout;
    private RelativeLayout found_layout;
    private RelativeLayout settings_layout;
    //定义底部导航栏中的ImageView与TextView 方便改变状态
    private ImageView course_image;
    private ImageView found_image;
    private ImageView settings_image;
    private TextView course_text;
    private TextView settings_text;
    private TextView found_text;
    //定义要用的颜色值
    private int whirt = 0xFFFFFFFF;
    private int gray = 0xFF7597B3;
    private int blue = 0xFF0AB2FB;
    //定义FragmentManager对象
    FragmentManager fManager;

    public static String myToken;
    public static String myUserid;
    public static String myNickname;
    public static long myTags;
    public static double myLat;
    public static double myLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        */
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        initViews();
        checkStatus();
        setPrivileges();
        initFragment();
        //requestLocation();
    }

    public void initViews() {
        fManager = getSupportFragmentManager();
        course_image = (ImageView) findViewById(R.id.course_image);
        found_image = (ImageView) findViewById(R.id.found_image);
        settings_image = (ImageView) findViewById(R.id.setting_image);
        course_text = (TextView) findViewById(R.id.course_text);
        found_text = (TextView) findViewById(R.id.found_text);
        settings_text = (TextView) findViewById(R.id.setting_text);
        course_layout = (RelativeLayout) findViewById(R.id.course_layout);
        found_layout = (RelativeLayout) findViewById(R.id.found_layout);
        settings_layout = (RelativeLayout) findViewById(R.id.setting_layout);
        course_layout.setOnClickListener(this);
        found_layout.setOnClickListener(this);
        settings_layout.setOnClickListener(this);
    }

    protected void checkStatus(){
        SharedPreferences sp=getSharedPreferences("UserInfo",MODE_PRIVATE);
        myUserid=sp.getString("UserName",null);
        myToken=sp.getString("UserToken",null);
    }

    protected void setPrivileges() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }
    }

    protected void initFragment() {
        if (myToken == null && myUserid == null) {
            //登录或者注册
            Log.d("***","未登录,set0");
            setChioceItem(0);
        } else {
            //有登录缓存
            Log.d("***","已登录,set1");
            setChioceItem(1);
        }
    }

    @Override
    public void onClick(View view) {
        if (myUserid == null && myToken == null) {
            if (view.getId() == R.id.course_layout)
                return;
            else
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("")
                        .setMessage("请先登录或注册")
                        .setPositiveButton("确定", null)
                        .show();
        } else {
            switch (view.getId()) {
                case R.id.course_layout:
                    setChioceItem(0);
                    Log.d("***","点击0set0");
                    break;
                case R.id.found_layout:
                    setChioceItem(1);
                    Log.d("***","点击1set1");
                    break;
                case R.id.setting_layout:
                    setChioceItem(2);
                    Log.d("***","点击2set2");
                    break;
                default:
                    break;
            }
        }

    }

    //定义一个选中一个item后的处理
    public void setChioceItem(int index) {
        //重置选项+隐藏所有Fragment
        FragmentTransaction transaction = fManager.beginTransaction();
        clearChioce();
        hideFragments(transaction);
        switch (index) {
            case 0:
                course_image.setImageResource(R.drawable.user_0);
                course_text.setTextColor(blue);
                //course_layout.setBackgroundResource(R.drawable.ic_tabbar_bg_click);
                if (myUserid == null && myToken == null) {
                    if (fg1 == null) {
                        fg1 = new Fragment1();
                        transaction.add(R.id.content, fg1);
                    } else {
                        transaction.show(fg1);
                    }
                } else {
                    if (fg4 == null) {
                        fg4 = new Fragment4();
                        transaction.add(R.id.content, fg4);
                    } else {
                        transaction.show(fg4);
                    }
                }
                /*
                if (fg1 == null) {
                    transaction.show(fg4);
                } else {
                    transaction.show(fg1);
                }*/
                break;

            case 1:
                found_image.setImageResource(R.drawable.footprint_0);
                found_text.setTextColor(blue);
                //found_layout.setBackgroundResource(R.drawable.ic_tabbar_bg_click);
                if (fg2 == null) {
                    fg2 = new Fragment2();
                    transaction.add(R.id.content, fg2);
                } else {
                    transaction.show(fg2);
                }
                break;

            case 2:
                settings_image.setImageResource(R.drawable.finding_0);
                settings_text.setTextColor(blue);
                //settings_layout.setBackgroundResource(R.drawable.ic_tabbar_bg_click);
                if (fg3 == null) {
                    fg3 = new Fragment3();
                    transaction.add(R.id.content, fg3);
                } else {
                    transaction.show(fg3);
                }
                break;
        }
        transaction.commit();
    }

    //隐藏所有的Fragment,避免fragment混乱
    private void hideFragments(FragmentTransaction transaction) {
        if (fg1 != null) {
            transaction.hide(fg1);
        }
        if (fg2 != null) {
            transaction.hide(fg2);
        }
        if (fg3 != null) {
            transaction.hide(fg3);
        }
        if (fg4 != null) {
            transaction.hide(fg4);
        }
    }


    //定义一个重置所有选项的方法
    public void clearChioce() {
        //course_image.setImageResource(R.drawable.ic_tabbar_course_normal);
        course_layout.setBackgroundColor(whirt);
        course_text.setTextColor(gray);
        //found_image.setImageResource(R.drawable.ic_tabbar_found_normal);
        found_layout.setBackgroundColor(whirt);
        found_text.setTextColor(gray);
        //settings_image.setImageResource(R.drawable.ic_tabbar_settings_normal);
        settings_layout.setBackgroundColor(whirt);
        settings_text.setTextColor(gray);
    }

    public void SetLogIn(String userid_, String usertoken_) {
        myUserid = userid_;
        myToken = usertoken_;
        FragmentTransaction transaction = fManager.beginTransaction();
        transaction.remove(fg1);
        fg1 = null;
        fg4 = new Fragment4();
        transaction.add(R.id.content, fg4);
        transaction.commit();
    }

    /*
    private void requestLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);//每5秒获得一次定位
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//高精度定位
        //option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);//只有GPS定位
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }
    */

    @Override
    protected void onResume() {
        super.onResume();
        //mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*mLocationClient.stop();
        baiduMap.setMyLocationEnabled(false);*/
        //mapView.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("获取权限失败")
                                    .setMessage("必须获得所有权限才能使用此应用")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .show();
                            return;
                        }
                    }
                    //requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }


    /*
    protected void locateTo(BDLocation location) {
        MyLocationData locationData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                .direction(100).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        baiduMap.setMyLocationData(locationData); //设置定位
        centerToLocation(location,250); //设置中心“我”
    }

    protected void centerToLocation(BDLocation location,int duration) {
        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        MapStatus newStatus = new MapStatus.Builder()
                .target(ll).zoom(16).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(newStatus);
        baiduMap.animateMapStatus(mMapStatusUpdate,duration);//duration为动画的时间
    }


    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("维度:").append(location.getLatitude()).append("\n");
            currentPosition.append("经度:").append(location.getLongitude()).append("\n");
            currentPosition.append("定位方式:");
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                currentPosition.append("GPS");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                currentPosition.append("网络");
            }
            positionText.setText(currentPosition);

            if(location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType()==BDLocation.TypeNetWorkLocation){
                locateTo(location);
            }
        }
    }
    */

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}