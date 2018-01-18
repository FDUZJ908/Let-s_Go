package com.example.letsgo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class RawPictureActivity extends AppCompatActivity {

    private Uri picUri;
    private ImageView rawImage;
    private String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_raw_picture);
        Intent intent = getIntent();
        imgUrl=intent.getStringExtra("imgUrl");
        rawImage=(ImageView)findViewById(R.id.rawPciture);
        //Picasso.with(RawPictureActivity.this).load("http://downza.img.zz314.com/soft/bcgj-110/2017-01-12/653e5cc1c2d125434b1155cd63315d23.png")
        //        .into(rawImage);
        Picasso.with(RawPictureActivity.this).load(imgUrl)
                .into(rawImage);
        rawImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
