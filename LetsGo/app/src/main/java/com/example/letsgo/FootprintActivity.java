package com.example.letsgo;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.Poi;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Feedback;
import model.Footprint;
import model.FootprintAdapter;
import model.History;
import model.Post;
import model.responseHistory;
import model.responseRegister;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.letsgo.MainActivity.myNickname;
import static util.httpUtil.sendHttpPost;


public class FootprintActivity extends AppCompatActivity {

    private TextView mPoi;
    private ListView mFootprint;
    private Button mCheckIn;
    private List<Footprint> footprintList = new ArrayList<>();
    private String pid;
    private String responseData;
    private responseHistory mResponseHistory;
    private responseRegister mResponseRegister;
    private FootprintAdapter adapter;
    private Feedback mFeedback;
    public static String myToken;
    public static String myUserid;
    private String myPOI_name;
    private double mLat;
    private double mLng;
    private Gson gson = new Gson();
    public static final int GETHISTORY = 3;
    public static final int GETFEEDBACK=4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_footprint);
        Intent intent = getIntent();
        myToken = intent.getStringExtra("token");
        myUserid = intent.getStringExtra("userid");
        myPOI_name=intent.getStringExtra("POI_name");
        pid = intent.getStringExtra("POI_id");
        mLat = intent.getDoubleExtra("mLat", 0.0);
        mLng = intent.getDoubleExtra("mLng", 0.0);
        //创建默认的ImageLoader参数
        //ImageLoaderConfiguration configuration=ImageLoaderConfiguration.createDefault(this);

        //创建可以打印log的ImageLoaderConfiguration
        ImageLoaderConfiguration configuration=new ImageLoaderConfiguration.Builder(this)
                .writeDebugLogs()
                .memoryCache(new LruMemoryCache(2*1024*1024))//可以通过自己的内存缓存实现
                .memoryCacheSize(2*1024*1024)//内存缓存的最大值
                .memoryCacheSizePercentage(13)
                .build();

        //初始化ImageLoader
        ImageLoader.getInstance().init(configuration);

        initViews();
        run();
    }

    @Override
    public void onBackPressed() {
        Log.d("******", "点了back键");
        sendFeedback();
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Log.d("**", "enterResult");
                    Log.d("**", data.getStringExtra("data_return"));
                    Footprint footprint = gson.fromJson(data.getStringExtra("data_return"), Footprint.class);
                    //footprintList.add(0, footprint);
                    //adapter.notifyDataSetChanged();
                    adapter.insert(footprint,0);
                } else if (resultCode == RESULT_CANCELED) {
                    Log.d("**", "留下足迹错误");
                }
                break;
            default:
        }
    }


    void initViews() {
        mPoi = (TextView) findViewById(R.id.Poi);
        mFootprint = (ListView) findViewById(R.id.footprint);
        mCheckIn = (Button) findViewById(R.id.checkIn);
        mCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FootprintActivity.this, CheckInActivity.class);
                i.putExtra("userid", myUserid);
                i.putExtra("token", myToken);
                i.putExtra("POI_id", pid);
                i.putExtra("mLat", mLat);
                i.putExtra("mLng", mLng);
                i.putExtra("nickname",myNickname);
                i.putExtra("POI_name",myPOI_name);
                startActivityForResult(i, 1);
            }
        });
    }

    void run() {
        History history = new History(pid, myToken, myUserid, 0);
        sendHttpPost("https://shiftlin.top/cgi-bin/History", gson.toJson(history, History.class), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseData = response.body().string();
                Log.d("***", responseData);
                Message message = new Message();
                message.what = GETHISTORY;
                message.obj = responseData;
                handler.sendMessage(message);
            }
        });
    }


    void initFootprint() {
        List<Post> mPost = mResponseHistory.getPosts();
        if (mPost.size() == 0) {
            mPoi.setText("【"+myPOI_name+"】");
            return;
        }
        for (int i = 0; i < mPost.size(); i++) {
            Footprint fi = new Footprint(mPost.get(i).getText(),
                    String.valueOf(mPost.get(i).getLike()),
                    String.valueOf(mPost.get(i).getDislike()),
                    mPost.get(i).getTimestamp(),
                    mPost.get(i).getPostid(),
                    mPost.get(i).getAttitude(),
                    mPost.get(i).getNickname(),
                    mPost.get(i).getImageUrl());
            footprintList.add(fi);
        }
    }

    protected void sendFeedback(){
        if (adapter!=null  && adapter.getAttitudeList().size() > 0) {
            mFeedback = new Feedback(myUserid, myToken, adapter.getAttitudeList().size(), adapter.getAttitudeList());
            Log.d("**test**", gson.toJson(mFeedback, Feedback.class));
            sendHttpPost("https://shiftlin.top/cgi-bin/Feedback", gson.toJson(mFeedback, Feedback.class), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    responseData = response.body().string();
                    Log.d("***", responseData);
                    Message message = new Message();
                    message.what = GETFEEDBACK;
                    message.obj = responseData;
                    handler.sendMessage(message);
                }
            });
        }
        else {
            Log.d("Feedback","无反馈");
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GETHISTORY:
                    mResponseHistory = gson.fromJson(msg.obj.toString(), responseHistory.class);
                    if (mResponseHistory.getStatus().equals("ERROR")) {
                        Log.d("Error***", mResponseHistory.getStatus());
                        new AlertDialog.Builder(FootprintActivity.this)
                                .setTitle("查看错误")
                                .setMessage("查看错误")
                                .setPositiveButton("确定", null)
                                .show();
                    } else {
                        mPoi.setText("【"+myPOI_name+"】");
                        initFootprint();
                        adapter = new FootprintAdapter(FootprintActivity.this, R.layout.footprint, footprintList);
                        mFootprint.setAdapter(adapter);
                    }
                    break;
                case GETFEEDBACK:
                    mResponseRegister=gson.fromJson(msg.obj.toString(),responseRegister.class);
                    if(mResponseRegister.getStatus().equals("ERROR")){
                        Log.d("Error***", mResponseRegister.getStatus());
                        new AlertDialog.Builder(FootprintActivity.this)
                                .setTitle("ERROR")
                                .setMessage(mResponseRegister.getMessage())
                                .setPositiveButton("确定", null)
                                .show();
                    }else{
                        Log.d("***","点赞/反对成功");
                    }
                    break;
                default:
                    break;
            }
        }
    };
}

