package com.kierasis.attendancemonitoring;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class signup extends AppCompatActivity {
    FloatingActionButton back;

    SwitchMaterial switchu;
    String type = "student";
    TextInputEditText srcode, fname, lname, email, phone, password;
    TextInputLayout til_srcode, til_fname, til_lname, til_email, til_phone, til_sex, til_password;
    AutoCompleteTextView sex;
    ArrayList<String> arrayList_sex;
    ArrayAdapter<String> arrayAdapter_sex;
    Button btn_signup;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z-.]+\\.+[a-z]+";

    public SharedPreferences device_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        device_info = getSharedPreferences("device-info", MODE_PRIVATE);

        back = findViewById(R.id.signup_back);
        switchu = findViewById(R.id.switchu);
        srcode = findViewById(R.id.signup_tie_srcode);
        fname = findViewById(R.id.signup_tie_firstname);
        lname = findViewById(R.id.signup_tie_lastname);
        email = findViewById(R.id.signup_tie_email);
        phone = findViewById(R.id.signup_tie_phone);
        sex = findViewById(R.id.signup_act_sex);
        password = findViewById(R.id.signup_tie_password);

        til_srcode = findViewById(R.id.signup_til_srcode);
        til_fname = findViewById(R.id.signup_til_firstname);
        til_lname = findViewById(R.id.signup_til_lastname);
        til_email = findViewById(R.id.signup_til_email);
        til_phone = findViewById(R.id.signup_til_phone);
        til_sex = findViewById(R.id.signup_til_sex);
        til_password = findViewById(R.id.signup_til_password);

        btn_signup = findViewById(R.id.signup_button);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        switchu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(switchu.isChecked()){
                    switchu.setText("Teacher");
                    til_srcode.setVisibility(View.GONE);
                    type = "teacher";
                }else{
                    switchu.setText("Student");
                    til_srcode.setVisibility(View.VISIBLE);
                    type = "student";
                }
            }

        });

        arrayList_sex=new ArrayList<>();
        arrayList_sex.add("Female");
        arrayList_sex.add("Male");
        arrayAdapter_sex=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,arrayList_sex);
        sex.setAdapter(arrayAdapter_sex);


        srcode.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                til_srcode.setErrorEnabled(false);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        fname.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                til_fname.setErrorEnabled(false);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        lname.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                til_lname.setErrorEnabled(false);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        email.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                til_email.setErrorEnabled(false);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        phone.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                til_phone.setErrorEnabled(false);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        sex.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                til_sex.setErrorEnabled(false);
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

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int error = 0;
                String txt_srcode = srcode.getText().toString();
                String txt_fname = fname.getText().toString();
                String txt_lname = lname.getText().toString();
                String txt_email = email.getText().toString();
                String txt_phone = phone.getText().toString();
                String txt_sex = sex.getText().toString();
                String txt_password = password.getText().toString();

                if (TextUtils.isEmpty(txt_srcode) && (type.equals("student"))){
                    til_srcode.setError("You need to enter a srcode");
                    error += 1;
                }else if(((8>txt_srcode.length()) || (txt_srcode.length()>9)) && (type.equals("student"))){
                    til_srcode.setError("Enter valid srcode");
                    error += 1;
                }
                if (TextUtils.isEmpty(txt_fname)){
                    til_fname.setError("You need to enter a firstname");
                    error += 1;
                }
                if (TextUtils.isEmpty(txt_lname)){
                    til_lname.setError("You need to enter a lastname");
                    error += 1;
                }
                if (TextUtils.isEmpty(txt_email)){
                    til_email.setError("You need to enter an email");
                    error += 1;
                }else if (!email.getText().toString().trim().matches(emailPattern)) {
                    til_email.setError("Enter valid email");
                    error += 1;
                }

                if (TextUtils.isEmpty(txt_phone)){
                    til_phone.setError("You need to enter a phone number");
                    error += 1;
                }else if((txt_phone.length()!=11) || (!txt_phone.substring(0, 2).equals("09"))){
                    til_phone.setError("Enter valid Phone Number");
                    error += 1;
                }
                if (TextUtils.isEmpty(txt_sex)){
                    til_sex.setError("You need to enter an sex");
                    error += 1;
                }
                if  (TextUtils.isEmpty(txt_password)){
                    til_password.setError("You need to enter a password");
                    error += 1;
                }else if(txt_password.length()<6){
                    til_password.setError("Choose more secured password.");
                    error += 1;
                }
                if(error==0){
                    signingup(type,txt_srcode,txt_fname,txt_lname,txt_email,txt_phone,txt_sex,txt_password);
                    hideKeybaord();
                }
            }
        });

    }

    private void signingup(String type, String txt_srcode, String txt_fname, String txt_lname,
                           String txt_email, String txt_phone, String txt_sex, String txt_password) {

        final ProgressDialog progressDialog = new ProgressDialog(signup.this, R.style.default_dialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Signing Up");
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/signup.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                String error;

                try {
                    progressDialog.dismiss();
                    jsonObject = new JSONObject(response.toString());
                    error = jsonObject.getString("error");
                    if(error.equals("true")) {
                        if (jsonObject.has("srcode")) {
                            til_srcode.setError("SR Code Already registered");
                        }
                        if (jsonObject.has("email")) {
                            til_email.setError("Email Already registered");
                        }
                        if (jsonObject.has("phone")) {
                            til_phone.setError("Phone Already registered");
                        }
                        if(jsonObject.has("error_desc")){
                            if(!jsonObject.getString("error_desc").equals("other_error")) {
                                Toast.makeText(signup.this, jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else{
                        String username;
                        if(type.equals("student")){
                            username = txt_srcode;
                        }else{
                            username = txt_lname;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(signup.this);
                        builder.setTitle("Success");

                        builder.setMessage("Your default username: "+ jsonObject.getString("username"));

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {

                                    case DialogInterface.BUTTON_POSITIVE:
                                        // User clicked the Yes button
                                        dialog.cancel();
                                        onBackPressed();
                                        break;
                                }
                            }
                        };

                        // Set the alert dialog yes button click listener
                        builder.setPositiveButton("Ok", dialogClickListener);

                        builder.setCancelable(false);
                        AlertDialog dialog = builder.create();
                        // Display the three buttons alert dialog on interface
                        dialog.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(signup.this, "signingup: JSON Error", Toast.LENGTH_SHORT).show();
                    Log.d("tag", "onErrorResponse: " + response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("type", type);
                param.put("srcode", txt_srcode);
                param.put("fname", txt_fname);
                param.put("lname", txt_lname);
                param.put("email", txt_email);
                param.put("phone", txt_phone);
                param.put("sex", txt_sex);
                param.put("password", txt_password);
                param.put("device_id", device_info.getString("device_id",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        my_singleton.getInstance(signup.this).addToRequestQueue(request);

    }

    private void hideKeybaord() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager  = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}