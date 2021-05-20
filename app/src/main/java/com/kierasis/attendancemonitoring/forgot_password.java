package com.kierasis.attendancemonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class forgot_password extends AppCompatActivity {
    LinearLayout step01, step02, step03, step04, step05;
    TextView fp_text;
    TextInputEditText et_phone, et_otp, et_pass, et_repass;
    TextInputLayout til_phone, til_otp;
    Button bt_find, bt_send, bt_verify, bt_update, bt_login;

    //String emailPattern = "[a-zA-Z0-9._-]+@[a-z-.]+\\.+[a-z]+";

    public ProgressDialog progressDialog;

    public SharedPreferences device_info, user_info;

    String apicode, apipass, fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        user_info = getSharedPreferences("user-info", MODE_PRIVATE);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);

        progressDialog = new ProgressDialog(forgot_password.this, R.style.default_dialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);

        step01 = findViewById(R.id.fp_ll1);
        step02 = findViewById(R.id.fp_ll2);
        step03 = findViewById(R.id.fp_ll3);
        step04 = findViewById(R.id.fp_ll4);
        step05 = findViewById(R.id.fp_ll5);

        fp_text = findViewById(R.id.fp_text);

        et_phone = findViewById(R.id.fp_tie_phone);
        et_otp = findViewById(R.id.fp_tie_otp);
        et_pass = findViewById(R.id.fp_tie_newpassword);
        et_repass = findViewById(R.id.fp_tie_retypepassword);

        til_phone = findViewById(R.id.fp_til_phone);
        til_otp = findViewById(R.id.fp_til_otp);

        bt_find = findViewById(R.id.fp_find);
        bt_send = findViewById(R.id.fp_send);
        bt_verify = findViewById(R.id.fp_verify);
        bt_update = findViewById(R.id.fp_update);
        bt_login = findViewById(R.id.fp_login);

        et_phone.setText(user_info.getString("user_phone", ""));

        et_phone.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                til_phone.setErrorEnabled(false);
                fp_text.setText("Forgot Password");
                bt_find.setVisibility(View.VISIBLE);
                step02.setVisibility(View.GONE);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        et_otp.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                til_otp.setErrorEnabled(false);
                fp_text.setText("Enter a Code you Received");
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        bt_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeybaord();
                String txt_phone = et_phone.getText().toString();
                fp_text.setText("Forgot Password");

                if (TextUtils.isEmpty(txt_phone)){
                    til_phone.setError("You need to enter a Phone Number");
                }else if((txt_phone.length()!=11) || (!txt_phone.substring(0, 2).equals("09"))){
                    til_phone.setError("Enter valid Phone Number");
                }else{
                    find_phone(txt_phone);
                }
            }
        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void find_phone(String txt_phone) {
        //Toast.makeText(forgot_password.this, txt_email, Toast.LENGTH_SHORT).show();
        progressDialog.setTitle("Finding Account");
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/forgot_password.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                String error, error_desc, account_id;

                try {
                    jsonObject = new JSONObject(response.toString());
                    error = jsonObject.getString("error");
                    if(error.equals("false")){
                        account_id = jsonObject.getString("account_id");
                        fullname = jsonObject.getString("fullname");
                        apicode = jsonObject.getString("apicode");
                        apipass = jsonObject.getString("apipass");
                        bt_find.setVisibility(View.GONE);
                        fp_text.setText(fullname + ",\n We will send otp to your phone?");
                        step02.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                        bt_send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                check_sms_server(account_id,txt_phone);
                            }
                        });
                    }else{
                        error_desc = jsonObject.getString("error_desc");
                        fp_text.setText(error_desc);
                        progressDialog.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(forgot_password.this, "JSON: Error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

                Log.d("tag", "find_phone Response: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                fp_text.setText("No Internet Connection\nPlease Try Again");
                progressDialog.dismiss();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("phone", txt_phone);
                param.put("action", "find");
                param.put("device_id", device_info.getString("device_id",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(forgot_password.this).addToRequestQueue(request);
    }

    private void check_sms_server(String account_id, String txt_phone) {
        progressDialog.setTitle("Sending OTP");
        progressDialog.setMessage("Checking SMS Server...");
        progressDialog.show();
        //String pnumber = txtfp_pnumber;

        String url = "https://www.itexmo.com/php_api/apicode_info.php?apicode="+apicode;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null, jsonResult = null;

                        if(response.contains("INVALID APICODE")){
                            //Toast.makeText(forgot_password.this, "Invalid Server Code.\nPlease Contact the Admin", Toast.LENGTH_SHORT).show();
                            fp_text.setText("Invalid Server Code.\nPlease Contact the Admin");
                            progressDialog.dismiss();
                        }else {

                            try {
                                jsonObject = new JSONObject(response.toString());
                                jsonResult = new JSONObject(jsonObject.getString("Result "));

                                if (Integer.parseInt(jsonResult.getString("MaxMessages")) > 0) {
                                    generate_code(txt_phone,account_id);
                                } else {
                                    fp_text.setText("Server Reach maximum Forgot Password\nTry Again Tomorrow");
                                    progressDialog.dismiss();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                        }

                        Log.d("tag", "check_sms_server Response: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(forgot_password.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void generate_code(String txt_phone, String account_id) {
        progressDialog.setMessage("Generating Code from Server...");

        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/forgot_password.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                String error, error_desc;

                try {
                    jsonObject = new JSONObject(response.toString());
                    error = jsonObject.getString("error");

                    if(error.equals("false")){
                        sending_otp(jsonObject.getString("code"),txt_phone,account_id);
                    }else{
                        error_desc = jsonObject.getString("error_desc");
                        if(error_desc.equals("forgot_reached")){
                            fp_text.setText("You Reach maximum unverified\nOTP Code request. Contact the Admin.");
                        }else{
                            Toast.makeText(forgot_password.this, "Unknown Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(forgot_password.this, "JSON: Error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

                Log.d("tag", "generate_code Response: " + response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(forgot_password.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("account_id", account_id);
                param.put("action", "generate_code");
                param.put("device_id", device_info.getString("device_id",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(forgot_password.this).addToRequestQueue(request);
    }

    private void sending_otp(final String Code, final String txt_phone, final String account_id) {
        progressDialog.setMessage("Sending OTP...");
        String uRl = "https://www.itexmo.com/php_api/api.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("0")){
                    Toast.makeText(forgot_password.this, "Sending OTP is Success", Toast.LENGTH_SHORT).show();
                    step01.setVisibility(View.GONE);
                    step02.setVisibility(View.GONE);
                    step03.setVisibility(View.VISIBLE);

                    progressDialog.dismiss();
                    fp_text.setText("Note: Receiving OTP may takes 10 mins.");

                    bt_verify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String txtCode = et_otp.getText().toString();
                            if(TextUtils.isEmpty(txtCode)){
                                til_otp.setError("You need to enter a OTP");
                            }else if(txtCode.length()!=6){
                                til_otp.setError("Type valid Code");
                            }else{
                                verify_code(txtCode,account_id);
                            }
                        }
                    });

                }else if(response.equals("6")){
                    Toast.makeText(forgot_password.this, "SMS Server is OFFLINE", Toast.LENGTH_SHORT).show();
                    fp_text.setText("SMS Server is OFFLINE");
                    progressDialog.dismiss();
                }else{
                    Toast.makeText(forgot_password.this, "Unknown Error Occur", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

                Log.d("tag", "sending_otp Response: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(forgot_password.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("1", txt_phone);
                param.put("2", "Hi "+fullname+",\nThis is Attendance Monitoring App\nYour OTP is: "+Code+"\n\n");
                param.put("3", apicode);
                param.put("passwd", apipass);
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(forgot_password.this).addToRequestQueue(request);

    }

    private void verify_code(String txtCode, String account_id) {
        progressDialog.setTitle("Verifying OTP");
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/forgot_password.php";
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
                        step03.setVisibility(View.GONE);
                        step04.setVisibility(View.VISIBLE);
                        fp_text.setText("Enter New Password");
                        bt_update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String txtNew = et_pass.getText().toString();
                                String txtConf = et_repass.getText().toString();

                                if(txtNew.isEmpty() || txtNew.trim().isEmpty() || txtConf.isEmpty() || txtConf.trim().isEmpty()){
                                    Toast.makeText(forgot_password.this, "All Fields Required", Toast.LENGTH_SHORT).show();
                                }else if(txtNew.contains(" ") || txtConf.contains(" ")){
                                    Toast.makeText(forgot_password.this, "Space is not allowed.", Toast.LENGTH_SHORT).show();
                                }else if(!txtNew.equals(txtConf)){
                                    Toast.makeText(forgot_password.this, "You must enter same password twice.", Toast.LENGTH_SHORT).show();
                                }else{
                                    update_password(txtNew,account_id);
                                }
                            }
                        });
                    }else{
                        error_desc = jsonObject.getString("error_desc");
                        if(error_desc.equals("wrong_otp")){
                            fp_text.setText("Wrong OTP\nCheck the code you Received");
                        }else{
                            Toast.makeText(forgot_password.this, "Unknown Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(forgot_password.this, "JSON: Error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

                Log.d("tag", "verify_code Response: " + response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(forgot_password.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("code", txtCode);
                param.put("account_id", account_id);
                param.put("action", "verify_code");
                param.put("device_id", device_info.getString("device_id",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(forgot_password.this).addToRequestQueue(request);
    }

    private void update_password(final String txtNew, String account_id) {

        progressDialog.setTitle("Changing Password");
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/forgot_password.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                JSONObject jsonObject = null;
                String error, error_desc;

                try {
                    jsonObject = new JSONObject(response.toString());
                    error = jsonObject.getString("error");
                    if(error.equals("false")){
                        fp_text.setText("Password Update Successfully\nPlease Login Using new Password");
                        Toast.makeText(forgot_password.this, "Password Update", Toast.LENGTH_SHORT).show();
                        step04.setVisibility(View.GONE);
                        step05.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }else{
                        Toast.makeText(forgot_password.this, "Unknown Error Occurred", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(forgot_password.this, "JSON: Error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

                Log.d("tag", "update_password Response: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(forgot_password.this, "No Internet", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("new_password", txtNew);
                param.put("account_id", account_id);
                param.put("action", "update_password");
                param.put("device_id", device_info.getString("device_id",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(forgot_password.this).addToRequestQueue(request);
    }

    private void hideKeybaord() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager  = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}