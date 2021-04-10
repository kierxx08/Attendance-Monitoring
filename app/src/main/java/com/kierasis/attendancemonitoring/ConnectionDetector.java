package com.kierasis.attendancemonitoring;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kierasis.attendancemonitoring.teacher.teacher_main_activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConnectionDetector {

    Context context;
    public ConnectionDetector(Context context){
        this.context = context;
    }

    static Boolean check;
    public Boolean volleyConnection() {
        String url = "https://bastaleakserver000.000webhostapp.com/QPass_App/api_laurel_covid.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(context,"Okay",Toast.LENGTH_SHORT).show();
                        check = true;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
                check = false;
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
        //Toast.makeText(context,check.toString(),Toast.LENGTH_SHORT).show();
        return false;
    }
}
