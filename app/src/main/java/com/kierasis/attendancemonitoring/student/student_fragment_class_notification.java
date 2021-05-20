package com.kierasis.attendancemonitoring.student;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kierasis.attendancemonitoring.R;


public class student_fragment_class_notification extends Fragment {
    View v;

    public student_fragment_class_notification() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.student_fragment_class_notification, container, false);
        return v;
    }
}