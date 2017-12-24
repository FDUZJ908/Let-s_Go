package com.example.letsgo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.security.Timestamp;
import java.util.Date;

import model.CheckIn;
import model.Footprint;
import model.FootprintAdapter;
import model.responseHistory;
import model.responseRegister;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static util.httpUtil.sendHttpPost;
public class CheckInActivity extends AppCompatActivity {
    private String pid;
    private String responseData;
    public static String myToken;
    public static String myUserid;
    public static String myNickname;
    public static String POI_name;
    private double mLat;
    private double mLng;

    private CheckIn checkIn;

    private Button PostButton;
    private EditText PostContent;
    private TextView POIName;

    private responseRegister mResponseRegister;
    private Gson gson=new Gson();


    public static final int GETCHECKIN= 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        Intent intent=getIntent();

        myToken = intent.getStringExtra("token");
        myUserid = intent.getStringExtra("userid");
        myNickname=intent.getStringExtra("nickname");
        POI_name=intent.getStringExtra("POI_name");
        pid = intent.getStringExtra("POI_id");
        mLat=intent.getDoubleExtra("mLat",0.0);
        mLng=intent.getDoubleExtra("mLng",0.0);
        initViews();
    }
    void initViews(){
        PostButton=(Button)findViewById(R.id.PostButton);
        PostContent=(EditText)findViewById(R.id.PostContent);
        POIName=(TextView)findViewById(R.id.PostName);
        POIName.setText("【"+POI_name+"】");
        PostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PostContent.getText().toString().equals("")) {
                    new AlertDialog.Builder(CheckInActivity.this)
                            .setTitle("留下足迹错误")
                            .setMessage("请在文本框输入内容，不能为空")
                            .setPositiveButton("确定", null)
                            .show();
                }
                else {
                    checkIn = new CheckIn(pid, myUserid, myToken, mLat, mLng, PostContent.getText().toString(),0);
                    sendHttpPost("https://shiftlin.top/cgi-bin/Post", gson.toJson(checkIn), new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Message message = new Message();
                            message.obj=response.body().string();
                            Log.d("***",message.obj.toString());
                            message.what=GETCHECKIN;
                            handler.sendMessage(message);
                        }
                    });
                }
            }
        });
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            mResponseRegister = gson.fromJson(msg.obj.toString(), responseRegister.class);
            switch (msg.what) {
                case GETCHECKIN:
                    if (mResponseRegister.getStatus().equals("ERROR")) {
                        new AlertDialog.Builder(CheckInActivity.this)
                                .setTitle("CheckIn错误")
                                .setMessage("CheckIn错误")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent =new Intent();
                                        setResult(RESULT_CANCELED,intent);
                                        finish();
                                    }
                                })
                                .show();
                    } else {
                        new AlertDialog.Builder(CheckInActivity.this)
                                .setTitle("留下足迹成功")
                                .setMessage("你已经留下了你的足迹")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent=new Intent();
                                        Footprint footprint=new Footprint(checkIn.getText(),"0","0",mResponseRegister.getTimestamp(),
                                                mResponseRegister.getPostid(),0,myNickname);
                                        intent.putExtra("data_return",gson.toJson(footprint));
                                        setResult(RESULT_OK,intent);
                                        finish();
                                    }
                                })
                                .show();
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
