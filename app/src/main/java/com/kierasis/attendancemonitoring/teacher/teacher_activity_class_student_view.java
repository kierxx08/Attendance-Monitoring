package com.kierasis.attendancemonitoring.teacher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class teacher_activity_class_student_view extends AppCompatActivity {
    TextView fullname, srcode, email, phone, sex, present, absent;
    public SharedPreferences device_info, user_info, activity_info;
    public CircleImageView icon_profile;
    Button print;

    RelativeLayout relativeLayout;
    ProgressBar loader;
    ImageView no_net, server_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_class_student_view);

        user_info = getSharedPreferences("user-info", MODE_PRIVATE);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);
        activity_info = getSharedPreferences("activity-info", MODE_PRIVATE);

        Intent intent = getIntent();
        String student_id = intent.getStringExtra("student_id");

        relativeLayout = findViewById(R.id.tcasv_rl);
        loader = findViewById(R.id.tcasv_loader);
        no_net = findViewById(R.id.tcasv_no_net);
        server_error = findViewById(R.id.tcasv_server_error);

        icon_profile = findViewById(R.id.tacsv_img);
        fullname = findViewById(R.id.tv_fullname);
        srcode = findViewById(R.id.tv_sr);
        email = findViewById(R.id.tv_email);
        phone = findViewById(R.id.tv_phone);
        sex = findViewById(R.id.tv_sex);
        present = findViewById(R.id.tv_present);
        absent = findViewById(R.id.tv_absent);
        print = findViewById(R.id.print_button);

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String class_id = activity_info.getString("class_id","");
                String link = "https://atm-bsumalvar.000webhostapp.com/app/print_student_attendance.php?class_id="+class_id+"&student_id="+student_id;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            }
        });

        load_data(student_id);
    }

    private void load_data(final String student_id){

        Log.d("Notif", "I'm Enter present Function");
        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/teacher_get_student_full_info.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                String error, error_desc;


                //linearLayout.setVisibility(View.VISIBLE);

                try {
                    jsonObject = new JSONObject(response.toString());
                    error = jsonObject.getString("error");
                    if(error.equals("false")){
                        relativeLayout.setVisibility(View.VISIBLE);
                        loader.setVisibility(View.GONE);
                        Picasso.get()
                                .load(jsonObject.getString("img_url"))
                                .placeholder(getDrawable(R.drawable.icon_profile))
                                .into(icon_profile);

                        fullname.setText(jsonObject.getString("fullname"));
                        srcode.setText(jsonObject.getString("srcode"));
                        email.setText(jsonObject.getString("email"));
                        phone.setText(jsonObject.getString("phone_no"));
                        sex.setText(jsonObject.getString("sex"));
                        present.setText(jsonObject.getString("no_present"));
                        absent.setText(jsonObject.getString("no_absent"));



                    }else{
                        //server_error.setVisibility(View.VISIBLE);
                        loader.setVisibility(View.GONE);
                        server_error.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //server_error.setVisibility(View.VISIBLE);
                    loader.setVisibility(View.GONE);
                    server_error.setVisibility(View.VISIBLE);
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
                param.put("student_id", student_id);
                param.put("class_id", activity_info.getString("class_id",""));
                param.put("account_id", user_info.getString("account_id",""));
                param.put("device_id", device_info.getString("device_id",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(teacher_activity_class_student_view.this).addToRequestQueue(request);

    }

}