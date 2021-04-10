package com.kierasis.attendancemonitoring;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class sample extends AppCompatActivity {
    TextView text;
    ImageView image;
    Bitmap bitmap;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample);

        text = findViewById(R.id.textView);
        image = findViewById(R.id.imageView);
        button = findViewById(R.id.button);
        get_json("J18-61128");
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();

        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String mobile_mac_addres = getMacAddress();
        text.setText(androidId);

        //

    }
    public String getMacAddress(){
        try{
            List<NetworkInterface> networkInterfaceList = Collections.list(NetworkInterface.getNetworkInterfaces());
            String stringMac = "";
            for(NetworkInterface networkInterface : networkInterfaceList)
            {
                if(networkInterface.getName().equalsIgnoreCase("wlon0"));
                {
                    for(int i = 0 ;i <networkInterface.getHardwareAddress().length; i++){
                        String stringMacByte = Integer.toHexString(networkInterface.getHardwareAddress()[i]& 0xFF);
                        if(stringMacByte.length() == 1)
                        {
                            stringMacByte = "0" +stringMacByte;
                        }
                        stringMac = stringMac + stringMacByte.toUpperCase() + ":";
                    }
                    break;
                }
            }
            return stringMac;
        }catch (SocketException e)
        {
            e.printStackTrace();
        }
        return  "0";
    }

    private void get_json(final String srcode) {

        String uRl = "https://bastaleakserver000.000webhostapp.com/AttendanceMonitoring_App/get_info.php";
        StringRequest request = new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("tag", "onErrorResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    //JSONObject jsonObject2 = new JSONObject(jsonObject.getString("personal"));
                    text.setText(jsonObject.getString("name"));
                    Toast.makeText(sample.this,"try",Toast.LENGTH_SHORT).show();
                    String img = "https://bastaleakserver000.000webhostapp.com/BastaLeak_App/IDPic.php?sr=J18-61128";
                    String encodedImage  = "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";
                    byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    saveToInternalStorage(decodedByte);
                    loadImageFromStorage("/data/user/0/com.kierasis.attendancemonitoring/app_imageDir/");
                    /*
                    Picasso.get().load(img).into(image, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
                            saveToInternalStorage(bitmap);
                            loadImageFromStorage("/data/user/0/com.kierasis.attendancemonitoring/app_imageDir/");

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

                     */
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(sample.this,"catch",Toast.LENGTH_SHORT).show();
                    Log.d("tag", "onErrorResponse: " + e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(sample.this,"volley error",Toast.LENGTH_SHORT).show();
                Log.d("tag", "onErrorResponse: " + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("srcode", srcode);
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(sample.this).addToRequestQueue(request);
    }

    /*
    private void get_json(final String srcode) {

        String url = "https://dione.batstate-u.edu.ph/student/backend/public/index.php/data/api_get?service=acad%252Ffetch%252Facademic_records&srcode=J18-61128";


        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            JSONObject jsonObject2 = new JSONObject(jsonObject.getString("personal"));
                            text.setText(jsonObject2.getString("fullname"));
                            String img = "https://bastaleakserver000.000webhostapp.com/BastaLeak_App/IDPic.php?sr=J18-61128";
                            Picasso.get().load(img).into(image, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
                                    saveToInternalStorage(bitmap);
                                    loadImageFromStorage("/data/user/0/com.kierasis.attendancemonitoring/app_imageDir/");

                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
                                    saveToInternalStorage(bitmap);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            text.setText("Error 1");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                text.setText("Error 2: "+error);
                Log.d("tag", "onErrorResponse: " + error);
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
    */
    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Toast.makeText(this,"Isa",Toast.LENGTH_SHORT).show();

            Log.d("tag", "onErrorResponse: " + mypath);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"Dalawa",Toast.LENGTH_SHORT).show();
        } finally {
            try {
                fos.close();
                Toast.makeText(this,"Tatlo",Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this,"Apat",Toast.LENGTH_SHORT).show();
            }
        }
        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img=(ImageView)findViewById(R.id.imageView3);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

}