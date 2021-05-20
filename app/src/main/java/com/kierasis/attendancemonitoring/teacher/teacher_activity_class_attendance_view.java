package com.kierasis.attendancemonitoring.teacher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kierasis.attendancemonitoring.R;
import com.kierasis.attendancemonitoring.my_singleton;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class teacher_activity_class_attendance_view extends AppCompatActivity {
    ListView present_list, absent_list;
    ArrayList<String> present, absent;
    ArrayAdapter<String> presentAdapter, absentAdapter;
    TextView tv_present, tv_absent, absent_lbl;
    Button print;
    Boolean present_done = false, absent_done = false;
    String present_total = "0", absent_total = "0";

    PieChart pieChart;

    RelativeLayout relativeLayout;
    ProgressBar loader;
    ImageView no_net;

    Long cur_timestamp, end_timestamp;

    public SharedPreferences device_info, user_info, activity_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_class_attendance_view);
        user_info = getSharedPreferences("user-info", MODE_PRIVATE);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);
        activity_info = getSharedPreferences("activity-info", MODE_PRIVATE);

        Intent intent = getIntent();
        String attendance_id = intent.getStringExtra("attendance_id");

        tv_present = findViewById(R.id.tcav_present_tv);
        tv_absent = findViewById(R.id.tcav_absent_tv);
        absent_lbl = findViewById(R.id.tcav_absent_text);
        relativeLayout = findViewById(R.id.tcav_rl);
        loader = findViewById(R.id.tcav_loader);
        no_net = findViewById(R.id.tcav_no_net);

        pieChart = findViewById(R.id.piechart);

        present_list = findViewById(R.id.tcav_present_list);
        absent_list = findViewById(R.id.tcav_absent_list);
        present=new ArrayList<>();
        presentAdapter = new ArrayAdapter<>( this, android.R.layout.simple_list_item_1, present);

        absent=new ArrayList<>();
        absentAdapter = new ArrayAdapter<>( this, android.R.layout.simple_list_item_1, absent);
        present(attendance_id);
        absent(attendance_id);

        print = findViewById(R.id.print_button);
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String class_id = activity_info.getString("class_id","");
                String link = "https://atm-bsumalvar.000webhostapp.com/app/print_attendance_view.php?class_id="+class_id+"&attendance_id="+attendance_id+"&present_total="+present_total+"&absent_total="+absent_total;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            }
        });

    }
    private void present(final String attendance_id){

        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/teacher_get_attendance_view.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                JSONArray json_list = null;
                String error, error_desc;

                try {
                    jsonObject = new JSONObject(response.toString());
                    error = jsonObject.getString("error");
                    if(error.equals("false")){

                        present_total = jsonObject.getString("total");
                        cur_timestamp = Long.valueOf(jsonObject.getString("cur_timestamp"));
                        end_timestamp = Long.valueOf(jsonObject.getString("end_timestamp"));
                        json_list = new JSONArray(jsonObject.getString("student"));


                        for(int i=0;i<json_list.length();i++){
                            JSONObject jresponse = json_list.getJSONObject(i);
                            String student_name = jresponse.getString("student_name");
                            present.add(student_name);
                        }

                        present_list.setAdapter(presentAdapter);
                    }else{
                        //Toast.makeText(teacher_activity_class_attendance_view.this, jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();
                        //error_desc = jsonObject.getString("error_desc");

                    }
                    present_done = true;
                    if(present_done && absent_done) {
                        pie_graph(present_total, absent_total);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("tag", "Error: "+ response);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //No Connection
                loader.setVisibility(View.GONE);
                no_net.setVisibility(View.VISIBLE);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("attendance_id", attendance_id);
                param.put("get", "present");
                param.put("class_id", activity_info.getString("class_id",""));
                param.put("account_id", user_info.getString("account_id",""));
                param.put("device_id", device_info.getString("device_id",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(teacher_activity_class_attendance_view.this).addToRequestQueue(request);

    }
    private void absent(final String attendance_id){

        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/teacher_get_attendance_view.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                JSONArray json_list = null;
                String error, error_desc;

                try {
                    jsonObject = new JSONObject(response.toString());
                    error = jsonObject.getString("error");
                    if(error.equals("false")){
                        absent_total = jsonObject.getString("total");
                        cur_timestamp = Long.valueOf(jsonObject.getString("cur_timestamp"));
                        end_timestamp = Long.valueOf(jsonObject.getString("end_timestamp"));
                        json_list = new JSONArray(jsonObject.getString("student"));


                        for(int i=0;i<json_list.length();i++){
                            JSONObject jresponse = json_list.getJSONObject(i);
                            String student_name = jresponse.getString("student_name");
                            absent.add(student_name);
                        }

                        absent_list.setAdapter(absentAdapter);
                    }else{
                        //Toast.makeText(teacher_activity_class_attendance_view.this, jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();
                        //error_desc = jsonObject.getString("error_desc");


                    }
                    absent_done = true;
                    if(present_done && absent_done) {
                        pie_graph(present_total, absent_total);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("tag", "Error: "+ response);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //No Connection
                loader.setVisibility(View.GONE);
                no_net.setVisibility(View.VISIBLE);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("attendance_id", attendance_id);
                param.put("get", "absent");
                param.put("class_id", activity_info.getString("class_id",""));
                param.put("account_id", user_info.getString("account_id",""));
                param.put("device_id", device_info.getString("device_id",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(teacher_activity_class_attendance_view.this).addToRequestQueue(request);

    }

    private void pie_graph(String present_total, String absent_total) {
        loader.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.VISIBLE);

        tv_present.setText(present_total + " Present");
        tv_absent.setText(absent_total + " Absent");

        if(cur_timestamp<end_timestamp){
            print.setVisibility(View.GONE);
            absent_lbl.setText("Waiting");
            tv_absent.setText(absent_total + " Waiting");
        }

        pieChart.setVisibility(View.VISIBLE);
        pieChart.addPieSlice(new PieModel("Recovered",Integer.parseInt(present_total), Color.parseColor("#66bb6a")));
        pieChart.addPieSlice(new PieModel("Deaths",Integer.parseInt(absent_total), Color.parseColor("#ef5350")));

        pieChart.startAnimation();

    }

}