package com.example.letsgo;

import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;
import java.nio.file.StandardWatchEventKinds;
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

import static util.ImageUtils.bitmapToString;
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
    private ImageView imgUpload;
    private Button ChooseButton;
    String filePath;
    Uri picUri;

    private responseRegister mResponseRegister;
    private Gson gson = new Gson();
    static final int PICK_IMAGE_REQUEST = 1;


    public static final int GETCHECKIN = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        Intent intent = getIntent();

        myToken = intent.getStringExtra("token");
        myUserid = intent.getStringExtra("userid");
        myNickname = intent.getStringExtra("nickname");
        POI_name = intent.getStringExtra("POI_name");
        pid = intent.getStringExtra("POI_id");
        mLat = intent.getDoubleExtra("mLat", 0.0);
        mLng = intent.getDoubleExtra("mLng", 0.0);
        initViews();
    }

    void initViews() {
        PostButton = (Button) findViewById(R.id.PostButton);
        PostContent = (EditText) findViewById(R.id.PostContent);
        POIName = (TextView) findViewById(R.id.PostName);
        POIName.setText("【" + POI_name + "】");
        imgUpload = (ImageView) findViewById(R.id.imgUpload);
        ChooseButton = (Button) findViewById(R.id.ChooseButton);
        ChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageBrowse();
            }
        });
        PostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PostContent.getText().toString().equals("")) {
                    new AlertDialog.Builder(CheckInActivity.this)
                            .setTitle("留下足迹错误")
                            .setMessage("请在文本框输入内容，不能为空")
                            .setPositiveButton("确定", null)
                            .show();
                } else {
                    if(filePath!=null) {
                        String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
                        checkIn = new CheckIn(pid, myUserid, myToken, mLat, mLng, PostContent.getText().toString(), bitmapToString(filePath), suffix, 0);
                    }
                    else
                        checkIn= new CheckIn(pid, myUserid, myToken, mLat, mLng, PostContent.getText().toString(), 0);
                    Log.d("send",gson.toJson(checkIn));
                    sendHttpPost("https://shiftlin.top/cgi-bin/Post", gson.toJson(checkIn), new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Message message = new Message();
                            message.obj = response.body().string();
                            Log.d("***", message.obj.toString());
                            message.what = GETCHECKIN;
                            handler.sendMessage(message);
                        }
                    });
                }
            }
        });
        //imgUpload.setImageURI(Uri.parse("http://downza.img.zz314.com/soft/bcgj-110/2017-01-12/653e5cc1c2d125434b1155cd63315d23.png"));
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL thumb_u = new URL("http://downza.img.zz314.com/soft/bcgj-110/2017-01-12/653e5cc1c2d125434b1155cd63315d23.png");
                    Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
                    imgUpload.setImageDrawable(thumb_d);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("ERROR!!!!!!!!!", "出错了");
                }
            }
        }).start();
        */

    }

    private void imageBrowse() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == PICK_IMAGE_REQUEST) {
                picUri = data.getData();
                filePath = getPath(picUri);
                Log.d("picUri", picUri.toString());
                Log.d("filePath", filePath);
                imgUpload.setImageURI(picUri);
                String imgString = bitmapToString(filePath);
                Log.d("imgString", imgString);
            }

        }

    }

    private String getPath(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            mResponseRegister = gson.fromJson(msg.obj.toString(), responseRegister.class);
            Log.d("***Post***",msg.obj.toString());
            switch (msg.what) {
                case GETCHECKIN:
                    if (mResponseRegister.getStatus().equals("ERROR")) {
                        new AlertDialog.Builder(CheckInActivity.this)
                                .setTitle("CheckIn错误")
                                .setMessage("CheckIn错误")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent();
                                        setResult(RESULT_CANCELED, intent);
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
                                        Intent intent = new Intent();
                                        Footprint footprint;
                                        if(picUri!=null) {
                                            footprint = new Footprint(checkIn.getText(), "0", "0", mResponseRegister.getTimestamp(),
                                                    mResponseRegister.getPostid(), 0, myNickname, picUri.toString());
                                        }
                                        else{
                                            footprint = new Footprint(checkIn.getText(), "0", "0", mResponseRegister.getTimestamp(),
                                                    mResponseRegister.getPostid(), 0, myNickname);
                                        }
                                        intent.putExtra("data_return", gson.toJson(footprint));
                                        setResult(RESULT_OK, intent);
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
