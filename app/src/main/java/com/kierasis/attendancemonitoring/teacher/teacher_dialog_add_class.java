package com.kierasis.attendancemonitoring.teacher;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.kierasis.attendancemonitoring.R;

public class teacher_dialog_add_class extends AppCompatDialogFragment {
    private TextInputEditText ed_classname;
    private TextInputEditText ed_section;
    private TextInputEditText ed_subject;
    private TextInputEditText ed_room;
    private teacher_dialog_add_class_listener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.teacher_dialog_add_class, null);
        builder.setView(view)
                .setTitle("Add Class")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String classname = ed_classname.getText().toString();
                        String section = ed_section.getText().toString();
                        String subject = ed_subject.getText().toString();
                        String room = ed_room.getText().toString();
                        listener.applyTexts(classname, section,subject, room);
                    }
                })
                .setCancelable(false);
        ed_classname = view.findViewById(R.id.tdac_classname);
        ed_section = view.findViewById(R.id.tdac_section);
        ed_subject = view.findViewById(R.id.tdac_subject);
        ed_room = view.findViewById(R.id.tdac_room);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (teacher_dialog_add_class_listener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }
    public interface teacher_dialog_add_class_listener {
        void applyTexts(String classname, String section, String subject, String room );
    }
}
