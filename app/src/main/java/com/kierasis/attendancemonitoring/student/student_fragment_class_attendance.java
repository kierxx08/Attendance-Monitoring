package com.kierasis.attendancemonitoring.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kierasis.attendancemonitoring.R;
import com.kierasis.attendancemonitoring.my_singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class student_fragment_class_attendance extends Fragment {
    View v;

    ArrayList<HashMap<String,String>> getDatalist;
    private RecyclerView mrecyclerView;
    student_adapter_class_attendance_rv mAdapter;

    SwipeRefreshLayout refresh;

    ProgressBar loader;
    ImageView no_net, no_data;

    int start_no = 0;
    int total = 0;

    boolean isUna, isFinish, net = true;

    public SharedPreferences device_info, user_info, activity_info;

    public student_fragment_class_attendance() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDatalist = new ArrayList<>();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.student_fragment_class_attendance, container, false);

        mrecyclerView = v.findViewById(R.id.sfca_Recyclerview);
        refresh = v.findViewById(R.id.sfca_refresh);

        user_info = v.getContext().getSharedPreferences("user-info", MODE_PRIVATE);
        device_info = v.getContext().getSharedPreferences("device-info", MODE_PRIVATE);
        activity_info = v.getContext().getSharedPreferences("activity-info", MODE_PRIVATE);

        isUna = true;

        loader = v.findViewById(R.id.sfca_loader);
        no_net = v.findViewById(R.id.sfca_no_net);
        no_data = v.findViewById(R.id.sfca_no_data);
        refresh.setRefreshing(true);



        total(true);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mrecyclerView.setVisibility(View.GONE);
                getDatalist.clear();
                start_no = 0;
                loader.setVisibility(View.VISIBLE);
                no_net.setVisibility(View.GONE);
                no_data.setVisibility(View.GONE);

                Log.d("tag", "Clearing Data . . .");

                if(isUna){
                    total(true);
                }else if(isFinish || getDatalist.size()==0) {
                    total(false);
                }
            }
        });

        return v;
    }

    private void reload_list() {
        mrecyclerView.setVisibility(View.GONE);
        getDatalist.clear();
        start_no = 0;
        loader.setVisibility(View.VISIBLE);
        no_net.setVisibility(View.GONE);
        no_data.setVisibility(View.GONE);

        Log.d("tag", "Clearing Data . . .");

        if(isUna){
            total(true);
        }else if(isFinish || getDatalist.size()==0) {
            total(false);
        }
    }


    private void total(boolean isfirst_run){
        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/get_attendance_list.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                total = Integer.parseInt(response);
                isFinish = false;
                data(start_no,isfirst_run);


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
                param.put("get", "total");
                param.put("class_id", activity_info.getString("class_id",""));
                param.put("account_id", user_info.getString("account_id",""));
                param.put("device_id", device_info.getString("device_id",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(v.getContext()).addToRequestQueue(request);

    }

    private void data(final int start, boolean isfirst_run){

        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/get_attendance_list.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                JSONArray json_list = null;
                String error, error_desc;

                mrecyclerView.setVisibility(View.GONE);

                try {
                    jsonObject = new JSONObject(response.toString());
                    error = jsonObject.getString("error");
                    if(error.equals("false")){
                        Log.d("tag", "I'm Enter this Section Right Now . . .");
                        json_list = new JSONArray(jsonObject.getString("attendance"));
                        if(!isfirst_run&&getDatalist.size()!=0) {
                            getDatalist.remove(getDatalist.size() - 1);
                            mAdapter.notifyItemRemoved(getDatalist.size());
                        }

                        for(int i=0;i<json_list.length();i++){
                            JSONObject jresponse = json_list.getJSONObject(i);
                            String attendance_id = jresponse.getString("attendance_id");
                            String title = jresponse.getString("title");
                            String color = jresponse.getString("color");
                            String month = jresponse.getString("month");
                            String day = jresponse.getString("day");
                            String start = jresponse.getString("start");
                            String end = jresponse.getString("end");
                            String edited = jresponse.getString("edited");
                            String date = jresponse.getString("date");
                            String status = jresponse.getString("status");


                            HashMap<String,String> map = new HashMap<>();
                            map.put("KEY_ATTENDANCE_ID",attendance_id);
                            map.put("KEY_TITLE",title);
                            map.put("KEY_COLOR",color);
                            map.put("KEY_MONTH",month);
                            map.put("KEY_DAY",day);
                            map.put("KEY_START",start);
                            map.put("KEY_END",end);
                            map.put("KEY_EDITED",edited);
                            map.put("KEY_DATE",date);
                            map.put("KEY_STATUS",status);
                            getDatalist.add(map);

                            if(!isfirst_run) {
                                mAdapter.notifyDataSetChanged();
                                mAdapter.setLoaded();
                            }

                            start_no++;
                            net = true;
                            loader.setVisibility(View.GONE);
                            no_net.setVisibility(View.GONE);
                            no_data.setVisibility(View.GONE);

                        }

                        Log.d("tag", "I'm Here in the Last Section Right Now . . .");
                    }else{
                        //Toast.makeText(v.getContext(), jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();

                        error_desc = jsonObject.getString("error_desc");
                        if(error_desc.equals("No Data") && getDatalist.size()==0) {
                            no_data.setVisibility(View.VISIBLE);
                        }
                        loader.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(v.getContext(), "Error here", Toast.LENGTH_SHORT).show();
                    Log.d("tag", "Pending: "+ response);
                }
                refresh.setRefreshing(false);

                mrecyclerView.setVisibility(View.VISIBLE);

                if(isfirst_run) {
                    isUna = false;
                    call();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //No Connection
                Log.d("tag", "No Connection: Data()");
                if(!isUna) {
                    data(start_no, false);
                }
                if(net){
                    net = false;
                }
                refresh.setRefreshing(false);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("start", String.valueOf(start));
                param.put("get", "attendance");
                param.put("class_id", activity_info.getString("class_id",""));
                param.put("account_id", user_info.getString("account_id",""));
                param.put("device_id", device_info.getString("device_id",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(v.getContext()).addToRequestQueue(request);

    }

    private void call() {
        mrecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new student_adapter_class_attendance_rv(v.getContext(), getDatalist, mrecyclerView);
        mrecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemListener(new student_adapter_class_attendance_rv.OnItemClickListener() {
            @Override
            public void onItemClick(HashMap<String, String> item) {
                String mEmail = item.get("KEY_ATTENDANCE_ID");
                Toast.makeText(v.getContext(),"Name: "+mEmail+"\nFunction: Soon",Toast.LENGTH_LONG).show();
            }
            public void onCheckClick(HashMap<String, String> item) {
                String attendance_id = item.get("KEY_ATTENDANCE_ID");
                //Toast.makeText(v.getContext(),"Accept CScon_id: "+CScon_id,Toast.LENGTH_LONG).show();
                checking_in(attendance_id);
            }
        });

        mAdapter.setOnLoadMoreListener(new student_adapter_class_attendance_rv.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (getDatalist.size() < total) {
                    getDatalist.add(null);
                    mAdapter.notifyItemInserted(getDatalist.size() - 1);
                    Log.d("tag", "Loading data from "+start_no+" - "+(start_no+10));
                    data(start_no, false);

                } else {
                    //Toast.makeText(v.getContext(), "Loading data completed", Toast.LENGTH_SHORT).show();
                    Log.d("tag", "Loading data completed");
                    isFinish = true;
                }
            }
        });
    }

    private void checking_in(String attendance_id) {

        //Toast.makeText(v.getContext(),"Check in: "+attendance_id+"\nFunction: Soon",Toast.LENGTH_LONG).show();

        final ProgressDialog progressDialog = new ProgressDialog(v.getContext(), R.style.default_dialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Attending Class");
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/student_attend_class.php";
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
                        Toast.makeText(v.getContext(), "Successful Attended", Toast.LENGTH_SHORT).show();
                        refresh.setRefreshing(true);
                        reload_list();
                    }else{
                        Toast.makeText(v.getContext(), jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(v.getContext(), "tacl: JSON Error", Toast.LENGTH_SHORT).show();
                    Log.d("tag", "onErrorResponse: " + response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(v.getContext(), "No Connection", Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("attendance_id", attendance_id);
                param.put("class_id", activity_info.getString("class_id",""));
                param.put("account_id", user_info.getString("account_id",""));
                param.put("device_id", device_info.getString("device_id",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(v.getContext()).addToRequestQueue(request);
    }


}