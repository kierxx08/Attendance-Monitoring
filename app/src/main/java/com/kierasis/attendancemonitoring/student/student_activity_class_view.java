package com.kierasis.attendancemonitoring.student;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kierasis.attendancemonitoring.R;

public class student_activity_class_view extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_activity_class_view);


        ViewPager2 viewPager2 = findViewById(R.id.sacv_viewPager);
        viewPager2.setAdapter(new student_adapter_pager_class(this));

        TabLayout tabLayout = findViewById(R.id.sacv_tabLayout);
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


                Log.d("tag", "Position: " + position);
            }
        });
    }
}