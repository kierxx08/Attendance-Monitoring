package com.kierasis.attendancemonitoring.teacher;

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
import com.kierasis.attendancemonitoring.my_singleton;
import com.kierasis.attendancemonitoring.teacher.teacher_adapter_class_notification_rv;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class teacher_fragment_class_notification extends Fragment {

    View v;
    ArrayList<HashMap<String,String>> getDatalist;
    private RecyclerView mrecyclerView;
    teacher_adapter_class_notification_rv mAdapter;

    SwipeRefreshLayout refresh;

    ProgressBar loader;
    ImageView no_net, no_data;

    int start_no = 0;
    int total = 0;

    boolean isUna, isFinish, net = true;

    public SharedPreferences device_info, user_info, activity_info;

    public teacher_fragment_class_notification() {
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
        v = inflater.inflate(R.layout.teacher_fragment_class_notification, container, false);

        mrecyclerView = v.findViewById(R.id.tfcn_Recyclerview);
        refresh = v.findViewById(R.id.tfcn_refresh);

        user_info = v.getContext().getSharedPreferences("user-info", MODE_PRIVATE);
        device_info = v.getContext().getSharedPreferences("device-info", MODE_PRIVATE);
        activity_info = v.getContext().getSharedPreferences("activity-info", MODE_PRIVATE);

        isUna = true;

        loader = v.findViewById(R.id.tfcn_loader);
        no_net = v.findViewById(R.id.tfcn_no_net);
        no_data = v.findViewById(R.id.tfcn_no_data);
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

    private void total(boolean isfirst_run){
        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/get_notification.php";
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
                param.put("get", "notif_class_total");
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

        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/get_notification.php";
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
                        json_list = new JSONArray(jsonObject.getString("notification"));
                        if(!isfirst_run&&getDatalist.size()!=0) {
                            getDatalist.remove(getDatalist.size() - 1);
                            mAdapter.notifyItemRemoved(getDatalist.size());
                        }

                        for(int i=0;i<json_list.length();i++){
                            JSONObject jresponse = json_list.getJSONObject(i);
                            String notification_id = jresponse.getString("notification_id");
                            String text = jresponse.getString("notif_text");
                            String read = jresponse.getString("notif_read");
                            String date = jresponse.getString("notif_date");
                            String img_url = jresponse.getString("img_url");


                            HashMap<String,String> map = new HashMap<>();
                            map.put("KEY_NOTIFICATION_ID",notification_id);
                            map.put("KEY_TEXT",text);
                            map.put("KEY_READ",read);
                            map.put("KEY_DATE",date);
                            map.put("KEY_IMG",img_url);
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
                param.put("get", "notif_class");
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
        mAdapter = new teacher_adapter_class_notification_rv(v.getContext(), getDatalist, mrecyclerView);
        mrecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemListener(new teacher_adapter_class_notification_rv.OnItemClickListener() {
            @Override
            public void onItemClick(HashMap<String, String> item) {
                String mEmail = item.get("KEY_NOTIFICATION_ID");
                Toast.makeText(v.getContext(),"Name: "+mEmail+"\nFunction: Soon",Toast.LENGTH_LONG).show();
            }
        });

        mAdapter.setOnLoadMoreListener(new teacher_adapter_class_notification_rv.OnLoadMoreListener() {
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
}