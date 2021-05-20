package com.kierasis.attendancemonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kierasis.attendancemonitoring.student.student_main_activity;
import com.kierasis.attendancemonitoring.teacher.teacher_main_activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {
    private long backPressedTime;
    private Toast backToast;
    TextView signup_text, login_forgot;
    TextInputEditText username, password;
    TextInputLayout til_username, til_password;
    Button btn_login;

    ImageView logo;

    public SharedPreferences device_info, user_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            //w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        user_info = getSharedPreferences("user-info", Context.MODE_PRIVATE);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);

        logo = findViewById(R.id.login_logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(login.this, sample.class));

            }
        });

        til_username = findViewById(R.id.login_til_username);
        til_password = findViewById(R.id.login_til_pssword);
        username = findViewById(R.id.login_tie_username);
        password = findViewById(R.id.login_tie_password);
        signup_text = findViewById(R.id.login_signup);
        login_forgot = findViewById(R.id.login_forgot);
        btn_login = findViewById(R.id.login_button);

        username.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                til_username.setErrorEnabled(false);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        password.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                til_password.setErrorEnabled(false);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int error = 0;
                String txt_username = username.getText().toString();
                String txt_password = password.getText().toString();

                if (TextUtils.isEmpty(txt_username)){
                    til_username.setError("You need to enter a username");
                    error += 1;
                }
                if  (TextUtils.isEmpty(txt_password)){
                    til_password.setError("You need to enter a password");
                    error += 1;
                }
                if(error==0){
                    //Toast.makeText(login.this, txt_srcode + txt_password, Toast.LENGTH_SHORT).show();
                    loggingin(txt_username,txt_password);
                    hideKeybaord();
                }

            }
        });

        signup_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login.this, signup.class));
                hideKeybaord();
            }
        });

        login_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login.this, forgot_password.class));
                hideKeybaord();
            }
        });

        String loginStatus = user_info.getString("login_state","");

        if(loginStatus.equals("loggedin")){
            String type = user_info.getString("user_type","");
            if(type.equals("student")){
                startActivity(new Intent(login.this, student_main_activity.class));
                finish();
            }else if(type.equals("teacher")){
                startActivity(new Intent(login.this, teacher_main_activity.class));
                finish();
            }else{
                Toast.makeText(login.this, "unknown user type", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void loggingin(final String txt_username,final String txt_password) {
        final ProgressDialog progressDialog = new ProgressDialog(login.this, R.style.default_dialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Logging in");
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/login.php";
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
                        SharedPreferences.Editor user_editor = user_info.edit();
                        user_editor.putString("login_state","loggedin");
                        if(jsonObject.getString("type").equals("student")){
                            user_editor.putString("user_srcode",jsonObject.getString("srcode"));
                        }
                        user_editor.putString("account_id",jsonObject.getString("account_id"));
                        user_editor.putString("user_name",txt_username);
                        user_editor.putString("user_fname",jsonObject.getString("fname"));
                        user_editor.putString("user_lname",jsonObject.getString("lname"));
                        user_editor.putString("user_email",jsonObject.getString("email"));
                        user_editor.putString("user_phone",jsonObject.getString("phone"));
                        user_editor.putString("user_type",jsonObject.getString("type"));
                        user_editor.putString("user_sex",jsonObject.getString("sex"));
                        user_editor.apply();

                        String encodedImage = jsonObject.getString("photo_profile");

                        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        saveToInternalStorage(decodedByte);

                        Toast.makeText(login.this, "Success", Toast.LENGTH_SHORT).show();
                        if(jsonObject.getString("type").equals("student")){
                            startActivity(new Intent(login.this, student_main_activity.class));
                            finish();
                        }else if(jsonObject.getString("type").equals("teacher")){
                            startActivity(new Intent(login.this, teacher_main_activity.class));
                            finish();
                        }else{
                            Toast.makeText(login.this, "unknown user type", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(login.this, jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(login.this, "login: JSON Error", Toast.LENGTH_SHORT).show();
                    Log.d("tag", "onErrorResponse: " + response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(login.this, "No Connection", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("username", txt_username);
                param.put("psw", txt_password);
                param.put("device_id", device_info.getString("device_id",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        my_singleton.getInstance(login.this).addToRequestQueue(request);
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("profile", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Log.d("tag", "onErrorResponse: " + mypath);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }


    private void hideKeybaord() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager  = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 >System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            return;
        }else{
            backToast = Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}