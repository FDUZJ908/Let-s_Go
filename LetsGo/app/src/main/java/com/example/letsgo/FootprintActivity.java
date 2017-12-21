package com.example.letsgo;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.Poi;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Footprint;
import model.FootprintAdapter;
import model.History;
import model.responseHistory;
import model.responseRegister;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static util.httpUtil.sendHttpPost;


public class FootprintActivity extends AppCompatActivity {

    private TextView mPoi;
    private ListView mFootprint;
    private List<Footprint> footprintList=new ArrayList<>();
    private String pid;
    private String responseData;
    private responseHistory mResponseHistory;
    public static String myToken;
    public static String myUserid;
    private Gson gson=new Gson();
    public static final int GETHISTORY=3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_footprint);
        Intent intent=getIntent();

        myToken=intent.getStringExtra("token");
        myUserid=intent.getStringExtra("userid");
        pid=intent.getStringExtra("POI_id");
        initViews();
        run();
    }


    void initViews(){
        mPoi=(TextView)findViewById(R.id.Poi);
        mFootprint=(ListView)findViewById(R.id.footprint);
    }

    void run(){
        History history=new History(pid,myToken,myUserid,0);
            sendHttpPost("https://shiftlin.top/cgi-bin/History", gson.toJson(history, History.class), new Callback() {
                @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseData=response.body().string();
                Log.d("***",responseData);
                Message message=new Message();
                message.what=GETHISTORY;
                message.obj=responseData;
                handler.sendMessage(message);
            }
        });
    }



    void initFootprint(){
        for(int i =0;i<10;i++){
            Footprint f1=new Footprint("第一条","第一条的评论");
            footprintList.add(f1);
            Footprint f2=new Footprint("第二条","第二条的评论");
            footprintList.add(f2);
            Footprint f3=new Footprint("第三条","第三条的评论");
            footprintList.add(f3);
        }
    }

    private Handler handler=new Handler() {
        public void handleMessage(Message msg){

            mResponseHistory=gson.fromJson(msg.obj.toString(), responseHistory.class);
            switch (msg.what){
                case GETHISTORY:
                    if(mResponseHistory.getStatus().equals("ERROR")){
                        Log.d("Error***",mResponseHistory.getStatus());
                        new AlertDialog.Builder(FootprintActivity.this)
                                .setTitle("查看错误")
                                .setMessage("查看错误")
                                .setPositiveButton("确定",null)
                                .show();
                    }
                    else{
                        mPoi.setText("这是POI信息");
                        initFootprint();
                        FootprintAdapter adapter=new FootprintAdapter(FootprintActivity.this,R.layout.footprint,footprintList);
                        mFootprint.setAdapter(adapter);
                    }
                    break;
                default:
                    break;
            }
        }
    };
}

