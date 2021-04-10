package com.kierasis.attendancemonitoring.teacher;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.kierasis.attendancemonitoring.R;
import com.kierasis.attendancemonitoring.my_singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class teacher_fragment_class_information extends Fragment {

    View v;
    LinearLayout linearLayout;

    SwipeRefreshLayout refresh;

    ProgressBar loader;
    ImageView no_net,server_error;
    public SharedPreferences device_info, user_info, activity_info;

    TextView class_code,class_name, total_student, class_created;

    public teacher_fragment_class_information() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.teacher_fragment_class_information, container, false);

        user_info = v.getContext().getSharedPreferences("user-info", MODE_PRIVATE);
        device_info = v.getContext().getSharedPreferences("device-info", MODE_PRIVATE);
        activity_info = v.getContext().getSharedPreferences("activity-info", MODE_PRIVATE);

        class_code = v.findViewById(R.id.tfci_Ccode);
        class_name = v.findViewById(R.id.tfci_Cname);
        total_student = v.findViewById(R.id.tfci_Ctotal);
        class_created = v.findViewById(R.id.tfci_Ccreated);

        refresh = v.findViewById(R.id.tfci_refresh);
        loader = v.findViewById(R.id.tfci_loader);
        no_net = v.findViewById(R.id.tfci_no_net);
        server_error = v.findViewById(R.id.tfci_server_error);
        linearLayout = v.findViewById(R.id.tfci_ll);
        load_data();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                linearLayout.setVisibility(View.GONE);
                loader.setVisibility(View.VISIBLE);
                no_net.setVisibility(View.GONE);
                server_error.setVisibility(View.GONE);
                load_data();
            }
        });

        return v;
    }

    private void load_data() {

        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/get_class_information.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                String error;

                linearLayout.setVisibility(View.VISIBLE);

                try {
                    jsonObject = new JSONObject(response.toString());
                    error = jsonObject.getString("error");
                    if(error.equals("false")){
                        class_code.setText(jsonObject.getString("class_code"));
                        class_name.setText(jsonObject.getString("class_name"));
                        total_student.setText(jsonObject.getString("total"));
                        class_created.setText(jsonObject.getString("class_created"));

                    }else{
                        server_error.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    server_error.setVisibility(View.VISIBLE);
                }

                refresh.setRefreshing(false);
                loader.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);

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
                param.put("get", "class_info");
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