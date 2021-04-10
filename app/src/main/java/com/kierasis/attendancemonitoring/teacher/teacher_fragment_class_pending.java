package com.kierasis.attendancemonitoring.teacher;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.kierasis.attendancemonitoring.R;
import com.kierasis.attendancemonitoring.login;
import com.kierasis.attendancemonitoring.my_singleton;
import com.kierasis.attendancemonitoring.student.student_main_activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class teacher_fragment_class_pending extends Fragment {

    View v;
    ArrayList<HashMap<String,String>> getDatalist;
    private RecyclerView mrecyclerView;
    teacher_adapter_class_pending_rv mAdapter;

    SwipeRefreshLayout refresh;

    ProgressBar loader;
    ImageView no_net, no_data;

    int start_no = 0;
    int total = 0;

    boolean isUna, isRefresh22, isFinish, net = true;
    public SharedPreferences device_info, user_info, activity_info;

    public teacher_fragment_class_pending() {
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

        v = inflater.inflate(R.layout.teacher_fragment_class_pending, container, false);


        user_info = v.getContext().getSharedPreferences("user-info", MODE_PRIVATE);
        device_info = v.getContext().getSharedPreferences("device-info", MODE_PRIVATE);
        activity_info = v.getContext().getSharedPreferences("activity-info", MODE_PRIVATE);

        mrecyclerView = v.findViewById(R.id.tfcp_Recyclerview);
        refresh = v.findViewById(R.id.tfcp_refresh);
        loader = v.findViewById(R.id.tfcp_loader);
        no_net = v.findViewById(R.id.tfcp_no_net);
        no_data = v.findViewById(R.id.tfcp_no_data);

        isUna = true;

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


    private void total(boolean isfirst_run){

        Log.d("tag", "total: " + isfirst_run);
        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/teacher_get_class_student_list.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                total = Integer.parseInt(response);
                Log.d("tag", "count: " + total);
                isFinish = false;
                data(start_no,isfirst_run);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                refresh.setRefreshing(false);
                //Toast.makeText(v.getContext(),"Mali",Toast.LENGTH_SHORT).show();
                Log.d("tag", "No Connection: Total()");
                refresh.setRefreshing(false);
                loader.setVisibility(View.GONE);
                no_net.setVisibility(View.VISIBLE);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("get", "requesting_total");
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

        Log.d("tag", "RUN: Data()");
        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/teacher_get_class_student_list.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                JSONArray json_list = null;
                String error;

                mrecyclerView.setVisibility(View.GONE);

                try {
                    jsonObject = new JSONObject(response.toString());
                    error = jsonObject.getString("error");
                    if(error.equals("false")){
                        json_list = new JSONArray(jsonObject.getString("students"));
                        if(!isfirst_run&&getDatalist.size()!=0) {
                            //if(getDatalist.size() > 20) {
                            getDatalist.remove(getDatalist.size() - 1);
                            mAdapter.notifyItemRemoved(getDatalist.size());
                        }

                        //Log.d("tag", "isRefresh: "+isRefresh+getDatalist.size());
                        for(int i=0;i<json_list.length();i++){
                            JSONObject jresponse = json_list.getJSONObject(i);
                            String CScon_id = jresponse.getString("CScon_id");
                            String fullname = jresponse.getString("name");
                            String date = jresponse.getString("date");
                            String img_url = jresponse.getString("img_url");

                            HashMap<String,String> map = new HashMap<>();
                            map.put("CScon_id",CScon_id);
                            map.put("fullname",fullname);
                            map.put("date",date);
                            map.put("img_url",img_url);
                            getDatalist.add(map);

                            if(!isfirst_run) {
                                mAdapter.notifyDataSetChanged();
                                mAdapter.setLoaded();
                            }

                            start_no++;
                            net = true;
                            loader.setVisibility(View.GONE);
                            no_net.setVisibility(View.GONE);

                        }

                    }else{
                        loader.setVisibility(View.GONE);
                        no_data.setVisibility(View.VISIBLE);
                        //Toast.makeText(v.getContext(), jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(v.getContext(), "tacl: JSON Error", Toast.LENGTH_SHORT).show();
                    Log.d("tag", "Pending: "+ response);
                }
                refresh.setRefreshing(false);

                /*
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    //Log.d("tag", "isRefresh: "+isRefresh+getDatalist.size());
                    //Log.d("tag", "isfirst_run: "+isfirst_run+" isRefresh: "+isRefresh);
                    if(!isfirst_run&&getDatalist.size()!=0) {
                        //if(getDatalist.size() > 20) {
                        getDatalist.remove(getDatalist.size() - 1);
                        mAdapter.notifyItemRemoved(getDatalist.size());
                    }

                    //Log.d("tag", "isRefresh: "+isRefresh+getDatalist.size());
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jresponse = jsonArray.getJSONObject(i);
                        String nickname = jresponse.getString("name");
                        String number = jresponse.getString("date");
                        String url = jresponse.getString("img");

                        HashMap<String,String> map = new HashMap<>();
                        map.put("KEY_EMAIL",nickname);
                        map.put("KEY_PHONE",number);
                        map.put("KEY_URL",url);
                        getDatalist.add(map);

                        if(!isfirst_run) {
                            mAdapter.notifyDataSetChanged();
                            mAdapter.setLoaded();
                        }

                        start_no++;
                        net = true;
                        refresh.setRefreshing(false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                 */

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
                //Toast.makeText(v.getContext(),"Mali",Toast.LENGTH_SHORT).show();
                Log.d("tag", "No Connection: Data()");
                //getDatalist.remove(getDatalist.size() - 1);
                //mAdapter.notifyItemRemoved(getDatalist.size());
                if(!isUna) {
                    data(start_no, false);
                }
                if(net){
                    //Toast.makeText(v.getContext(),"No Connection.",Toast.LENGTH_SHORT).show();
                    net = false;
                }
                refresh.setRefreshing(false);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("start", String.valueOf(start));
                param.put("get", "requesting");
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
        mAdapter = new teacher_adapter_class_pending_rv(v.getContext(), getDatalist, mrecyclerView);
        mrecyclerView.setAdapter(mAdapter);

        // set RecyclerView on item click listner
        mAdapter.setOnItemListener(new teacher_adapter_class_pending_rv.OnItemClickListener() {
            @Override
            public void onItemClick(HashMap<String, String> item) {
                String mEmail = item.get("fullname");
                String mPhone = item.get("date");
                Toast.makeText(v.getContext(),"Name: "+mEmail+"\nFunction: Soon",Toast.LENGTH_LONG).show();
            }
            public void onAcceptClick(HashMap<String, String> item) {
                String CScon_id = item.get("CScon_id");
                decision("accepted",CScon_id,user_info.getString("account_id",""),device_info.getString("device_id",""));
                //Toast.makeText(v.getContext(),"Accept CScon_id: "+CScon_id,Toast.LENGTH_LONG).show();
            }
            public void onDeclineClick(HashMap<String, String> item) {
                String CScon_id = item.get("CScon_id");
                decision("declined",CScon_id,user_info.getString("account_id",""),device_info.getString("device_id",""));
                //Toast.makeText(v.getContext(),"Decline CScon_id: "+CScon_id,Toast.LENGTH_LONG).show();
            }
        });

        //set load more listener for the RecyclerView adapter
        mAdapter.setOnLoadMoreListener(new teacher_adapter_class_pending_rv.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (getDatalist.size() < total) {
                    getDatalist.add(null);
                    mAdapter.notifyItemInserted(getDatalist.size() - 1);
                    data(start_no, false);

                    //Log.d("tag", "Loading more "+isRefresh);
                } else {
                    //Toast.makeText(v.getContext(), "Loading data completed", Toast.LENGTH_SHORT).show();
                    Log.d("tag", "Loading data completed");
                    isFinish = true;
                }
            }
        });
    }



    private void decision(String decision, String CScon_id, String account_id, String device_id) {
        final ProgressDialog progressDialog = new ProgressDialog(v.getContext(), R.style.default_dialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/teacher_class_pending_decision.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                JSONArray json_list = null;
                String error;
                progressDialog.dismiss();

                try {
                    jsonObject = new JSONObject(response.toString());
                    error = jsonObject.getString("error");
                    if(error.equals("false")){

                        Toast.makeText(v.getContext(), "Success", Toast.LENGTH_SHORT).show();

                        refresh.setRefreshing(true);
                        mrecyclerView.setVisibility(View.GONE);
                        getDatalist.clear();
                        start_no = 0;
                        loader.setVisibility(View.VISIBLE);

                        Log.d("tag", "Clearing Data . . .");
                        if(isFinish || getDatalist.size()==0) {
                            total(false);
                        }

                    }else{
                        Toast.makeText(v.getContext(), jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(v.getContext(), "tacl: JSON Error", Toast.LENGTH_SHORT).show();

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
                param.put("decision", decision);
                param.put("CScon_id", CScon_id);
                param.put("account_id", account_id);
                param.put("device_id", device_id);
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(v.getContext()).addToRequestQueue(request);
    }
}