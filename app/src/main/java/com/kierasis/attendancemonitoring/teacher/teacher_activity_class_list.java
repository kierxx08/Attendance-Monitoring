package com.kierasis.attendancemonitoring.teacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.kierasis.attendancemonitoring.R;
import com.kierasis.attendancemonitoring.adapter_00;
import com.kierasis.attendancemonitoring.ext_00;
import com.kierasis.attendancemonitoring.login;
import com.kierasis.attendancemonitoring.my_singleton;
import com.kierasis.attendancemonitoring.sample;
import com.kierasis.attendancemonitoring.signup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class teacher_activity_class_list extends AppCompatActivity implements adapter_00.OnDataListener,teacher_dialog_add_class.teacher_dialog_add_class_listener{
    MaterialButton btnAddclass, back;

    RecyclerView rv_classlist;
    List<ext_00> classlist;
    adapter_00 adapter;

    public SharedPreferences device_info, user_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_class_list);

        user_info = getSharedPreferences("user-info", Context.MODE_PRIVATE);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);

        back = findViewById(R.id.tacl_btnBack);
        btnAddclass = findViewById(R.id.tacl_btnAdd);

        rv_classlist = findViewById(R.id.tacl_rv);
        classlist = new ArrayList<>();
        
        load_class();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnAddclass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });



    }

    private void load_class() {

        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/teacher_get_class_list.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                JSONArray json_list = null;
                String error;
                String class_name;
                String section;
                String subject;
                String room;
                String img_url;

                try {
                    jsonObject = new JSONObject(response.toString());
                    error = jsonObject.getString("error");
                    if(error.equals("false")){
                        json_list = new JSONArray(jsonObject.getString("class"));
                        for (int i = 0; i < json_list.length(); i++) {
                            JSONObject caseObject = json_list.getJSONObject(i);
                            ext_00 cl = new ext_00();
                            cl.setTitle(caseObject.getString("class_name").toString());
                            cl.setSubtitle(caseObject.getString("section").toString());
                            cl.setSubtitle2(caseObject.getString("subject").toString());
                            cl.setImgURL(caseObject.getString("img_url").toString());
                            classlist.add(cl);
                        }
                        rv_classlist.setLayoutManager(new LinearLayoutManager(teacher_activity_class_list.this));
                        adapter = new adapter_00(teacher_activity_class_list.this,classlist,teacher_activity_class_list.this);
                        rv_classlist.setAdapter(adapter);

                        Toast.makeText(teacher_activity_class_list.this, "HAHA", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(teacher_activity_class_list.this, jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(teacher_activity_class_list.this, "tacl: JSON Error", Toast.LENGTH_SHORT).show();
                    Log.d("tag", "onErrorResponse: " + response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(teacher_activity_class_list.this, "No Connection", Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("account_id", user_info.getString("account_id",""));
                param.put("device_id", device_info.getString("device_id",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(teacher_activity_class_list.this).addToRequestQueue(request);
    }


    @Override
    public void onNoteClick(int position) {
        Log.d("tag","onNoteClick: clicked.");

        Intent intent = new Intent(teacher_activity_class_list.this, sample.class);
        intent.putExtra("code",String.valueOf(position));
        startActivity(intent);
    }

    public void openDialog() {
        teacher_dialog_add_class exampleDialog = new teacher_dialog_add_class();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String classname, String section, String subject, String room) {
        //Toast.makeText(teacher_activity_class_list.this,classname+" : "+section+" : "+subject+" : "+room,Toast.LENGTH_SHORT).show();
        add_class(classname,section,subject,room);
    }

    private void add_class(String classname, String section, String subject, String room) {
        final ProgressDialog progressDialog = new ProgressDialog(teacher_activity_class_list.this, R.style.default_dialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Creating Class");
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/teacher_add_class.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                String error;

                try {
                    progressDialog.dismiss();
                    jsonObject = new JSONObject(response.toString());
                    error = jsonObject.getString("error");
                    if(error.equals("false")){
                        Toast.makeText(teacher_activity_class_list.this, "Success", Toast.LENGTH_SHORT).show();
                    }else{
                        if(jsonObject.getString("error_desc").equals("Classname is not valid")){
                            Toast.makeText(teacher_activity_class_list.this, jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();
                            openDialog();
                        }else {
                            Toast.makeText(teacher_activity_class_list.this, jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(teacher_activity_class_list.this, "tacl: JSON Error", Toast.LENGTH_SHORT).show();
                    Log.d("tag", "onErrorResponse: " + response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(teacher_activity_class_list.this, "No Connection", Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("classname", classname);
                param.put("section", section);
                param.put("subject", subject);
                param.put("room", room);
                param.put("account_id", user_info.getString("account_id",""));
                param.put("device_id", device_info.getString("device_id",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(teacher_activity_class_list.this).addToRequestQueue(request);
    }

}