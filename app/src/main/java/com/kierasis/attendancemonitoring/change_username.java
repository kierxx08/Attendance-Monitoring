package com.kierasis.attendancemonitoring;

import androidx.appcompat.app.AppCompatActivity;

import static com.kierasis.attendancemonitoring.profile.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class change_username extends AppCompatActivity {
    TextView username, fullname;
    public CircleImageView icon_profile;
    TextInputEditText et_username;
    TextInputLayout til_username;
    LinearLayout profile_ll_00;

    Button bt_update;

    public SharedPreferences device_info, user_info, activity_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_username);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        Log.d("tag", "type " + type);

        user_info = getSharedPreferences("user-info", MODE_PRIVATE);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);
        activity_info = getSharedPreferences("activity-info", MODE_PRIVATE);

        icon_profile = findViewById(R.id.profile_img);
        username = findViewById(R.id.tv_username);
        fullname = findViewById(R.id.tv_fullname);

        et_username = findViewById(R.id.change_tie_username);
        et_username.setText(user_info.getString("user_name",""));

        username.setText(user_info.getString("user_name",""));
        fullname.setText(user_info.getString("user_fname","")+" "+user_info.getString("user_lname",""));

        profile_ll_00 = findViewById(R.id.profile_ll_00);
        if(!type.equals("teacher")){
            Glide.with(this).load(getDrawable(R.drawable.student_cover)).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                    profile_ll_00.setBackground(resource);
                }
            });
        }

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("profile", Context.MODE_PRIVATE);;
        try {
            File f=new File(directory, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            //ImageView img=(ImageView)findViewById(R.id.imageView3);
            icon_profile.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        bt_update = findViewById(R.id.change_username);
        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtUsername = et_username.getText().toString();
                if(txtUsername.isEmpty() || txtUsername.trim().isEmpty() || txtUsername.equals(user_info.getString("user_name",""))){
                    Toast.makeText(change_username.this, "Type New Username", Toast.LENGTH_SHORT).show();
                }else if(txtUsername.contains(" ")){
                    Toast.makeText(change_username.this, "Space is not allowed.", Toast.LENGTH_SHORT).show();
                }else{
                    update_username(type,txtUsername);
                }
            }
        });
    }

    private void update_username(String type, final String txtUsername) {
        ProgressDialog progressDialog = new ProgressDialog(change_username.this, R.style.default_dialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Changing Username");
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/change_username.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                String error, error_desc;

                try {
                    jsonObject = new JSONObject(response.toString());
                    error = jsonObject.getString("error");

                    if(error.equals("false")){
                        progressDialog.dismiss();
                        SharedPreferences.Editor user_editor = user_info.edit();
                        user_editor.putString("user_name",txtUsername);
                        user_editor.apply();
                        profile.finish();

                        Intent intent = new Intent(change_username.this, profile.class);
                        intent.putExtra("type",type);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        finish();
                    }else{
                        error_desc = jsonObject.getString("error_desc");
                        if(error_desc.equals("duplicate_username")){
                            Toast.makeText(change_username.this, "Choose other Username", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }else{
                            Toast.makeText(change_username.this, "Unknown Error Occurred", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(change_username.this, "Unknown Error Occurred", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

                Log.d("tag", "update_username Response: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(change_username.this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("new_username", txtUsername);
                param.put("account_id", user_info.getString("account_id",""));
                param.put("device_id", device_info.getString("device_id",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(change_username.this).addToRequestQueue(request);
    }

}