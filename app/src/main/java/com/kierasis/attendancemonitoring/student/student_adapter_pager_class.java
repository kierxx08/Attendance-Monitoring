package com.kierasis.attendancemonitoring.student;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class student_adapter_pager_class extends FragmentStateAdapter {
    public student_adapter_pager_class(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case  0:
                return new student_fragment_class_attendance();
            default:
                return new student_fragment_class_information();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
