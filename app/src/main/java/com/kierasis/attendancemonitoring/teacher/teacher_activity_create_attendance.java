package com.kierasis.attendancemonitoring.teacher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kierasis.attendancemonitoring.R;
import com.kierasis.attendancemonitoring.my_singleton;
import com.kierasis.attendancemonitoring.signup;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.kierasis.attendancemonitoring.teacher.teacher_activity_class_view.tacv;

public class teacher_activity_create_attendance extends AppCompatActivity {
    SwitchMaterial switches;
    LinearLayout schedule_ll;
    TextView start_date, start_time, end_date, end_time, text;

    int sYear, sMonth, sDay, sHour, sMinute;
    int eYear, eMonth, eDay, eHour, eMinute;

    Button btn_create;

    Long start_timestamp, end_timestamp;

    String hour_12;

    public SharedPreferences device_info, user_info, activity_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_create_attendance);

        user_info = getSharedPreferences("user-info", MODE_PRIVATE);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);
        activity_info = getSharedPreferences("activity-info", MODE_PRIVATE);

        switches = findViewById(R.id.atca_switch);
        schedule_ll = findViewById(R.id.atca_sched_ll);
        text = findViewById(R.id.text);

        start_date = findViewById(R.id.atca_start_date);
        start_time = findViewById(R.id.atca_start_time);
        end_date = findViewById(R.id.atca_end_date);
        end_time = findViewById(R.id.atca_end_time);

        btn_create = findViewById(R.id.atca_create);

        switches.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(switches.isChecked()){
                    switches.setText("Schedule");
                    schedule_ll.setVisibility(View.VISIBLE);
                }else{
                    switches.setText("Assign");
                    schedule_ll.setVisibility(View.GONE);
                }
            }

        });

        Calendar calendar = Calendar.getInstance();
        sYear = calendar.get(Calendar.YEAR);
        sMonth = calendar.get(Calendar.MONTH);
        sDay = calendar.get(Calendar.DAY_OF_MONTH);
        sHour = calendar.get(Calendar.HOUR_OF_DAY);
        sMinute = calendar.get(Calendar.MINUTE);
        eHour = sHour;
        hour_12 = new SimpleDateFormat("hh", Locale.getDefault()).format(new Date());
        if(sMinute+10>59){
            eHour = sHour + 1;
            eMinute = (sMinute+9) % 59;
            if(eHour>12){
                hour_12 = Integer.toString(eHour - 12);
            }else{
                hour_12 = Integer.toString(eHour);
            }
        }else{
            eMinute = sMinute + 10;
        }

        String sdate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());
        String shour = new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date());
        String abbreviation = new SimpleDateFormat("aa", Locale.getDefault()).format(new Date());

        start_date.setText(sdate);
        start_time.setText(shour);
        end_date.setText(sdate);
        end_time.setText(hour_12+":"+String.format("%02d", eMinute)+" "+abbreviation);

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(sYear,sMonth,sDay,sHour,sMinute);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(sYear,sMonth,sDay,eHour,eMinute);

        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog start_datePickerDialog = new DatePickerDialog(teacher_activity_create_attendance.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        sYear = year;
                        sMonth = month;
                        sDay = dayOfMonth;

                        startCalendar.set(Calendar.YEAR, year);
                        startCalendar.set(Calendar.MONTH, month);
                        startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

                        start_date.setText(sdf.format(startCalendar.getTime()));

                        if (endCalendar.getTimeInMillis()/1000 < startCalendar.getTimeInMillis() / 1000) {
                            end_date.setText(sdf.format(startCalendar.getTime()));
                            eYear = year;
                            eMonth = month;
                            eDay = dayOfMonth;

                            endCalendar.set(year,month,dayOfMonth);
                        }

                    }
                },sYear,sMonth,sDay
                );

                start_datePickerDialog.updateDate(sYear,sMonth,sDay);
                start_datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                start_datePickerDialog.show();

            }
        });



        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog end_datePickerDialog = new DatePickerDialog(teacher_activity_create_attendance.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        eYear = year;
                        eMonth = month;
                        eDay = dayOfMonth;

                        endCalendar.set(Calendar.YEAR, year);
                        endCalendar.set(Calendar.MONTH, month);
                        endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

                        //Long tsLong = endCalendar.getTimeInMillis()/1000;
                        end_date.setText(sdf.format(endCalendar.getTime()));

                        //end_date.setText(tsLong.toString());

                    }
                },sYear,sMonth,sDay
                );

                end_datePickerDialog.updateDate(eYear,eMonth,eDay);
                end_datePickerDialog.getDatePicker().setMinDate(startCalendar.getTimeInMillis() - 1000);
                end_datePickerDialog.show();

            }
        });

        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(teacher_activity_create_attendance.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {


                        //Calendar calendar1 = Calendar.getInstance();

                        //calendar1.set(Calendar.DAY_OF_MONTH,sDay);
                        startCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        startCalendar.set(Calendar.MINUTE,minute);
                        if (calendar.getTimeInMillis()/1000 < startCalendar.getTimeInMillis() / 1000) {
                            start_time.setText(DateFormat.format("hh:mm aa", startCalendar));

                            if((startCalendar.getTimeInMillis()/1000)+600 > endCalendar.getTimeInMillis() / 1000){
                                if((eHour<hourOfDay)||(minute+10>59)){
                                    eHour = sHour + 1;
                                    eMinute = (minute+10) % 59;
                                    if(eHour>12){
                                        hour_12 = Integer.toString(eHour - 12);
                                    }
                                }else{
                                    eMinute = minute + 10;
                                }
                                endCalendar.set(Calendar.HOUR_OF_DAY,eHour);
                                endCalendar.set(Calendar.MINUTE,eMinute);
                                end_time.setText(hour_12+":"+String.format("%02d", eMinute)+" "+abbreviation);
                            }

                            sHour = hourOfDay;
                            sMinute = minute;
                        }else{
                            Toast.makeText(teacher_activity_create_attendance.this,"Selected Time is not Valid",Toast.LENGTH_SHORT).show();
                            startCalendar.set(Calendar.HOUR_OF_DAY,sHour);
                            startCalendar.set(Calendar.MINUTE,sMinute);
                        }

                    }
                },sHour,sMinute,false
                );
                timePickerDialog.show();
            }
        });

        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(teacher_activity_create_attendance.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {


                        //Calendar calendar1 = Calendar.getInstance();

                        //calendar1.set(Calendar.DAY_OF_MONTH,sDay);
                        endCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        endCalendar.set(Calendar.MINUTE,minute);


                        if (startCalendar.getTimeInMillis()/1000 < endCalendar.getTimeInMillis() / 1000) {
                            end_time.setText(DateFormat.format("hh:mm aa",endCalendar));
                            eHour = hourOfDay;
                            eMinute = minute;
                        }else{
                            Toast.makeText(teacher_activity_create_attendance.this,"Selected Time is not Valid",Toast.LENGTH_SHORT).show();
                            endCalendar.set(Calendar.HOUR_OF_DAY,eHour);
                            endCalendar.set(Calendar.MINUTE,eMinute);
                        }
                    }
                },eHour,eMinute,false
                );
                timePickerDialog.show();
            }
        });



        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                start_timestamp = startCalendar.getTimeInMillis() / 1000;
                end_timestamp = endCalendar.getTimeInMillis()/1000;
                Long longcalendar = calendar.getTimeInMillis()/1000;

                Long result = end_timestamp - start_timestamp;
                text.setText(result.toString());

                if(result>=300) {
                    cteate_attendance(start_timestamp, end_timestamp);
                }else{
                    Toast.makeText(teacher_activity_create_attendance.this,"Due below 5 minutes is not available.",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void cteate_attendance(Long start_timestamp, Long end_timestamp) {
        final ProgressDialog progressDialog = new ProgressDialog(teacher_activity_create_attendance.this, R.style.default_dialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Creating Attendance");
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String uRl = "https://atm-bsumalvar.000webhostapp.com/app/teacher_create_attendance.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                String error;

                try {
                    progressDialog.dismiss();
                    jsonObject = new JSONObject(response.toString());
                    error = jsonObject.getString("error");
                    if(error.equals("false")) {
                        tacv.finish();
                        Toast.makeText(teacher_activity_create_attendance.this, "Success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(teacher_activity_create_attendance.this, teacher_activity_class_view.class));
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        finish();

                    }else{

                        Toast.makeText(teacher_activity_create_attendance.this, jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(teacher_activity_create_attendance.this, "signingup: JSON Error", Toast.LENGTH_SHORT).show();
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
                param.put("start_timestamp", String.valueOf(start_timestamp));
                param.put("end_timestamp", String.valueOf(end_timestamp));
                param.put("class_id", activity_info.getString("class_id",""));
                param.put("account_id", user_info.getString("account_id",""));
                param.put("device_id", device_info.getString("device_id",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        my_singleton.getInstance(teacher_activity_create_attendance.this).addToRequestQueue(request);
    }


}