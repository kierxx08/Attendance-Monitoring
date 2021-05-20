package com.kierasis.attendancemonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

public class about extends AppCompatActivity {
    ImageView cover;
    TextView about_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        Log.d("tag", "type " + type);

        cover = findViewById(R.id.about_cover);
        about_version = findViewById(R.id.about_version);
        about_version.setText(BuildConfig.VERSION_NAME);

        if(!type.equals("teacher")){
            Glide.with(this).load(getDrawable(R.drawable.student_cover)).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                    cover.setBackground(resource);
                }
            });
        }
    }
}