package com.kierasis.attendancemonitoring.teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kierasis.attendancemonitoring.R;

public class teacher_activity_class_view extends AppCompatActivity {

    public static Activity tacv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_class_view);
        tacv = this;
        ImageView img = findViewById(R.id.img);

        ViewPager2 viewPager2 = findViewById(R.id.viewPager);
        viewPager2.setAdapter(new teacher_adapter_pager_class(this));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0: {
                        tab.setText("Attendance");
                        tab.setIcon(R.drawable.icon_attendance);
                        break;
                    }
                    case 1: {
                        tab.setText("Students");
                        tab.setIcon(R.drawable.icon_student);
                        break;
                    }
                    case 2: {
                        tab.setText("Pending");
                        tab.setIcon(R.drawable.icon_pending);
                        break;
                    }
                    case 3: {
                        tab.setText("Notification");
                        tab.setIcon(R.drawable.icon_notification);
                        break;
                    }
                    case 4: {
                        tab.setText("Information");
                        tab.setIcon(R.drawable.icon_about);
                        break;
                    }
                }
            }
        }
        );
        tabLayoutMediator.attach();

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(position==1){
                    img.setVisibility(View.GONE);
                }else{
                    img.setVisibility(View.GONE);
                }

                Log.d("tag", "Position: " + position);
            }
        });
    }
}