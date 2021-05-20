package com.kierasis.attendancemonitoring.teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.kierasis.attendancemonitoring.ConnectionDetector;
import com.kierasis.attendancemonitoring.R;
import com.kierasis.attendancemonitoring.about;
import com.kierasis.attendancemonitoring.adapter_00;
import com.kierasis.attendancemonitoring.ext_00;
import com.kierasis.attendancemonitoring.login;
import com.kierasis.attendancemonitoring.my_singleton;
import com.kierasis.attendancemonitoring.profile;
import com.kierasis.attendancemonitoring.sample;
import com.kierasis.attendancemonitoring.student.student_main_activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class teacher_main_activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,adapter_00.OnDataListener,teacher_dialog_add_class.teacher_dialog_add_class_listener {
    private long backPressedTime;
    private Toast backToast;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    CardView add_class;

    public SharedPreferences device_info, user_info, activity_info;
    public CircleImageView icon_profile;

    MaterialButton btnAddclass;

    RecyclerView rv_classlist;
    List<ext_00> classlist;
    adapter_00 adapter;

    SwipeRefreshLayout refresh;

    ProgressBar loader;
    ImageView no_net, no_data, add_class_tut;

    public static Activity teacher_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_main_activity);
        /*
        ConnectionDetector mConnectionDetector;
        mConnectionDetector = new ConnectionDetector(getApplicationContext());
        if (mConnectionDetector.volleyConnection() == false) {
            Toast.makeText(teacher_main_activity.this,"Haha",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(teacher_main_activity.this,"Hakdog",Toast.LENGTH_SHORT).show();
        }

         */

        teacher_main = this;

        user_info = getSharedPreferences("user-info", MODE_PRIVATE);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);
        activity_info = getSharedPreferences("activity-info", MODE_PRIVATE);

        final String fname = user_info.getString("user_fname", "");
        final String lname = user_info.getString("user_lname", "");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.teacher_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        Window window = teacher_main_activity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.bsu_color_red), PorterDuff.Mode.SRC_ATOP);

        TextView nav_user = (TextView)hView.findViewById(R.id.header_fullname);
        nav_user.setText(fname + " " + lname);
        TextView nav_address = (TextView)hView.findViewById(R.id.header_type);
        nav_address.setText("Professor");

        final LinearLayout header_bg = (LinearLayout) hView.findViewById(R.id.header_bg);

        Glide.with(this).load(getDrawable(R.drawable.teacher_cover)).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                header_bg.setBackground(resource);
            }
        });

        icon_profile = (CircleImageView)hView.findViewById(R.id.icon_profile);

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        loadImageFromStorage();

        //Start Code Here
        btnAddclass = findViewById(R.id.tma_btnAddClass);
        rv_classlist = findViewById(R.id.tacl_rv);
        classlist = new ArrayList<>();

        load_class();

        btnAddclass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
                //startActivity(new Intent(teacher_main_activity.this, teacher_activity_create_attendance.class));
            }
        });

        loader = findViewById(R.id.tma_loader);
        no_net = findViewById(R.id.tma_no_net);
        no_data = findViewById(R.id.tma_no_data);
        add_class_tut = findViewById(R.id.tma_add_class);
        refresh = findViewById(R.id.tma_refresh);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //rv_classlist.setVisibility(View.GONE);
                reload_list();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_home:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_profile:
                //Toast.makeText(teacher_main_activity.this," Soon",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(teacher_main_activity.this, profile.class);
                intent.putExtra("type","teacher");
                startActivity(intent);
                break;
            case R.id.nav_logout:
                String login_status = user_info.getString("login_state","");
                if(login_status.equals("loggedin")) {
                    logout();
                }
                break;
            case R.id.nav_about:
                //Toast.makeText(teacher_main_activity.this," Soon ",Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(teacher_main_activity.this, about.class);
                intent2.putExtra("type","teacher");
                startActivity(intent2);
                break;
        }

        return false;
    }


    private void logout() {
        SharedPreferences.Editor editor = user_info.edit();
        editor.putString("login_state","loggedout");
        editor.apply();
        Toast.makeText(teacher_main_activity.this,"Logout Success",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(teacher_main_activity.this, login.class));
        finish();
    }


    private void loadImageFromStorage(){
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

    }

    private void load_class() {

        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/get_class_list.php";
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
                            cl.setId(caseObject.getString("class_id").toString());
                            cl.setTitle(caseObject.getString("class_name").toString());
                            cl.setSubtitle(caseObject.getString("section").toString());
                            cl.setSubtitle2(caseObject.getString("subject").toString());
                            cl.setImgURL(caseObject.getString("img_url").toString());
                            classlist.add(cl);
                        }
                        rv_classlist.setLayoutManager(new LinearLayoutManager(teacher_main_activity.this));
                        adapter = new adapter_00(teacher_main_activity.this,classlist,teacher_main_activity.this);
                        rv_classlist.setAdapter(adapter);

                        loader.setVisibility(View.GONE);
                        no_net.setVisibility(View.GONE);

                    }else{
                        Toast.makeText(teacher_main_activity.this, jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();
                        loader.setVisibility(View.GONE);
                        no_data.setVisibility(View.VISIBLE);
                        add_class_tut.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(teacher_main_activity.this, "tacl: JSON Error", Toast.LENGTH_SHORT).show();
                    Log.d("tag", "onErrorResponse: " + response);
                }

                refresh.setRefreshing(false);

                rv_classlist.setVisibility(View.VISIBLE);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                refresh.setRefreshing(false);
                loader.setVisibility(View.GONE);
                no_net.setVisibility(View.VISIBLE);
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
        my_singleton.getInstance(teacher_main_activity.this).addToRequestQueue(request);
    }


    @Override
    public void onNoteClick(int position) {
        Log.d("tag","onNoteClick: clicked.:"+ classlist.get(position).getId());

        SharedPreferences.Editor editor = activity_info.edit();
        editor.putString("class_id",classlist.get(position).getId());
        editor.apply();

        Intent intent = new Intent(teacher_main_activity.this, teacher_activity_class_view.class);
        //intent.putExtra("code",String.valueOf(position));
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
        final ProgressDialog progressDialog = new ProgressDialog(teacher_main_activity.this, R.style.default_dialog);
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
                        Toast.makeText(teacher_main_activity.this, "Success", Toast.LENGTH_SHORT).show();
                        reload_list();
                    }else{
                        if(jsonObject.getString("error_desc").equals("Classname is not valid")){
                            Toast.makeText(teacher_main_activity.this, jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();
                            openDialog();
                        }else {
                            Toast.makeText(teacher_main_activity.this, jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(teacher_main_activity.this, "tacl: JSON Error", Toast.LENGTH_SHORT).show();
                    Log.d("tag", "onErrorResponse: " + response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(teacher_main_activity.this, "No Connection", Toast.LENGTH_SHORT).show();

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
        my_singleton.getInstance(teacher_main_activity.this).addToRequestQueue(request);
    }

    private void reload_list() {
        classlist.clear();
        rv_classlist.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
        no_net.setVisibility(View.GONE);
        no_data.setVisibility(View.GONE);
        add_class_tut.setVisibility(View.GONE);
        load_class();
    }

}