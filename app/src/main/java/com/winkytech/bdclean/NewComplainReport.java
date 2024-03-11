package com.winkytech.bdclean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
@SuppressLint({"ResourceAsColor", "SetTextI18n"})
public class NewComplainReport extends AppCompatActivity {

    Toolbar toolbar;
    EditText complain_subject,complain_body;
    Button select_for;
    Button submit_btn;
    ProgressBar progressBar;
    TextView toast_message;
    Dialog dialog;
    String user_id;

    public JSONArray parent_result,complain_for_result;
    int parent_ref,complain_for_ref,org_level_pos;
    List<String> parent, complain_for;

    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_complain_report);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        toolbar=findViewById(R.id.custom_toolbar);
        complain_subject=findViewById(R.id.subject);
        complain_body = findViewById(R.id.body);
        select_for = findViewById(R.id.select_complain_for_spinner);
        submit_btn = findViewById(R.id.submit);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        org_level_pos = intent.getIntExtra("org_level_pos",0);

        parent = new ArrayList<String>();
        complain_for = new ArrayList<String>();

        getSendToData();


        getComplainForData();
        select_for.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(NewComplainReport.this);
                dialog.setContentView(R.layout.custom_spinner_layout_2);
                dialog.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(NewComplainReport.this, android.R.layout.simple_list_item_1,complain_for);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        String item  = adapter.getItem(position);

                        try {
                            JSONObject jsonObject = complain_for_result.getJSONObject(position);
                            complain_for_ref = Integer.parseInt(jsonObject.getString("user_id"));
                            select_for.setText(jsonObject.getString("full_name"));
                            dialog.dismiss();
                            System.out.println(complain_for_ref);
                            select_for.setError(null);


                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        // Dismiss dialog
                    }
                });
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitComplain();
            }
        });

    }

    private void submitComplain() {

        String subject = complain_subject.getText().toString().trim();
        String body = complain_body.getText().toString().trim();

        if (subject.equals("")  ){
            complain_subject.setError("Please input Subject");
            complain_subject.requestFocus();
        } else if (body.equals("")){
            complain_body.setError("Please fill Details");
            complain_body.requestFocus();
        }  else if (parent_ref == 0){
            Toast toast = new Toast(getApplicationContext());
            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
            toast_message.setText("Approval Body not found");
            toast.setView(toast_view);
            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        } else if (complain_for_ref == 0 ) {
            select_for.setError("Please select a member");
            select_for.setFocusable(true);
            select_for.setFocusableInTouchMode(true);
            select_for.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {

                    //Starting Write and Read data with URL
                    //Creating array for parameters
                    String[] field = new String[5];
                    field[0] = "user_id";
                    field[1] = "send_to";
                    field[2] = "send_for";
                    field[3] = "subject";
                    field[4] = "body";


                    //Creating array for data
                    String[] data = new String[5];
                    data[0] = user_id;
                    data[1] = String.valueOf(parent_ref);
                    data[2] = String.valueOf(complain_for_ref);
                    data[3] = subject;
                    data[4] = body;

                    PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/createComplain.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult().trim();
                            if (result.equals("Complain Created successfully")) {
                                progressBar.setVisibility(View.GONE);

                                submitDialog("Complain Created successfully",R.drawable.success_image,R.color.bdclean_green);



                            } else {
                                progressBar.setVisibility(View.GONE);
                                Log.i("PutData", result);

                                submitDialog("Complain Created Failed",R.drawable.warning_image,R.color.red);

                            }
                        }
                    }
                    //End Write and Read data with URL
                }
            });
        }

    }


    private void submitDialog(String notice, int imgGet, int noticeColor) {
        Dialog dialog = new Dialog(NewComplainReport.this);
        dialog.setContentView(R.layout.custom_submit_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView text = dialog.findViewById(R.id.submitTextDialog);
        ImageView img = dialog.findViewById(R.id.submitImgDialog);
        Button okBtn = dialog.findViewById(R.id.submitOkBtn);

        text.setText(notice);
        img.setImageResource(imgGet);
        text.setTextColor(getResources().getColor(noticeColor));
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onBackPressed();
            }
        });
        dialog.show();
    }



    private void getComplainForData() {
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getComplainForData.php?org_level_pos="+org_level_pos+"&user_id="+user_id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                System.out.println("for response = " + response);
                progressBar.setVisibility(View.GONE);
                String name="";
                String level = "";

                try {

                    JSONArray obj = new JSONArray(response);
                    complain_for_result = obj;

                    for (int i=0;i<obj.length();i++){
                        JSONObject jsonObject = obj.getJSONObject(i);
                        name=jsonObject.getString("full_name");
                        level = jsonObject.getString("org_level_pos");
                        String designation = jsonObject.getString("designation");
                        System.out.println(name + " , "+designation);
                        complain_for.add(name+" ("+designation+")");
                    }

                }
                catch (JSONException e){
                    Log.e("anyText",response);
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(NewComplainReport.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(NewComplainReport.this, volleyError, Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                progressBar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(NewComplainReport.this);
        requestQueue.add(stringRequest);
    }

    private void getSendToData() {

        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getSendToData.php?org_level_pos="+org_level_pos + "&user_id=" +user_id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                System.out.println("send to response = " + response);
                progressBar.setVisibility(View.GONE);
                String name="";
                String level = "";

                try {

                    JSONArray obj = new JSONArray(response);

                    parent_result = obj;

                    for (int i=0;i<obj.length();i++){
                        JSONObject jsonObject = obj.getJSONObject(i);
                        name=jsonObject.getString("full_name");
                        level = jsonObject.getString("org_level_pos");
                        String designation = jsonObject.getString("designation");
                        parent.add(name+" ("+designation+")");


                        parent_ref = Integer.parseInt(jsonObject.getString("user_id"));
                        System.out.println(parent_ref + " auto get");
                    }

                }
                catch (JSONException e){
                    Log.e("anyText",response);
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(NewComplainReport.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(NewComplainReport.this, volleyError, Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                progressBar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(NewComplainReport.this);
        requestQueue.add(stringRequest);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister broadcast receiver when activity is destroyed
        unregisterReceiver(networkChangeListener);
    }
}