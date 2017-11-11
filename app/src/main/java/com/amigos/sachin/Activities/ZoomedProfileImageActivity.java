package com.amigos.sachin.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.amigos.sachin.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;

public class ZoomedProfileImageActivity extends AppCompatActivity {

    ImageView profileImageZoomed;
    Context context;
    RelativeLayout zoom_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomed_profile_image);
        context = getApplicationContext();

        getSupportActionBar().hide();

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("imageUrl");

        zoom_layout = (RelativeLayout) findViewById(R.id.zoom_layout);

        profileImageZoomed = (ImageView) findViewById(R.id.profileImageZoomed);
        Glide.with(context).load(imageUrl).error(R.drawable.ic_user)
                .bitmapTransform(new CropSquareTransformation(context))
                .thumbnail(0.01f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(profileImageZoomed);

        zoom_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        profileImageZoomed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
