package com.kierasis.attendancemonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.kierasis.attendancemonitoring.teacher.teacher_activity_class_student_view;
import com.kierasis.attendancemonitoring.teacher.teacher_main_activity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends AppCompatActivity {
    TextView username, fullname, srcode, email, phone, sex;
    public SharedPreferences device_info, user_info, activity_info;
    public CircleImageView icon_profile;
    Button change_username, change_password;

    LinearLayout prfl_student, profile_ll_00;

    public static Activity profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        profile = this;

        user_info = getSharedPreferences("user-info", MODE_PRIVATE);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);
        activity_info = getSharedPreferences("activity-info", MODE_PRIVATE);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        Log.d("tag", "type " + type);

        icon_profile = findViewById(R.id.profile_img);
        username = findViewById(R.id.tv_username);
        fullname = findViewById(R.id.tv_fullname);
        srcode = findViewById(R.id.tv_sr);
        email = findViewById(R.id.tv_email);
        phone = findViewById(R.id.tv_phone);
        sex = findViewById(R.id.tv_sex);

        username.setText(user_info.getString("user_name",""));
        fullname.setText(user_info.getString("user_fname","")+" "+user_info.getString("user_lname",""));

        profile_ll_00 = findViewById(R.id.profile_ll_00);
        prfl_student = findViewById(R.id.prfl_student);
        if(type.equals("teacher")){
            prfl_student.setVisibility(View.GONE);

        }else{
            srcode.setText(user_info.getString("user_srcode",""));
            Glide.with(this).load(getDrawable(R.drawable.student_cover)).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                    profile_ll_00.setBackground(resource);
                }
            });
        }

        email.setText(user_info.getString("user_email",""));
        phone.setText(user_info.getString("user_phone",""));
        sex.setText(user_info.getString("user_sex",""));

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("profile", Context.MODE_PRIVATE);;
        try {
            File f=new File(directory, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            //ImageView img=(ImageView)findViewById(R.id.imageView3);
            icon_profile.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        change_username = findViewById(R.id.change_username);
        change_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profile.this, change_username.class);
                intent.putExtra("type",type);
                startActivity(intent);
            }
        });

        change_password =findViewById(R.id.change_password);
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profile.this, change_password.class);
                intent.putExtra("type",type);
                startActivity(intent);
            }
        });
    }
}