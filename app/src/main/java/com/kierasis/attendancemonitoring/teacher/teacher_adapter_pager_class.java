package com.kierasis.attendancemonitoring.teacher;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class teacher_adapter_pager_class extends FragmentStateAdapter {
    public teacher_adapter_pager_class(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case  0:
                return new teacher_fragment_class_attendance();
            case 1:
                return new teacher_fragment_class_students();
            case 2:
                return new teacher_fragment_class_pending();
            case 3:
                return new teacher_fragment_class_notification();
            default:
                return new teacher_fragment_class_information();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
