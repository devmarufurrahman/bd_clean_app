package com.winkytech.bdclean;

import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ComplainActivity extends AppCompatActivity {

    Toolbar toolbar;
    Button self_complain,other_complain,newComplain, reload_btn;
    ListView complain_list_view_1,complain_list_view_2;
    List<ComplainList> complainList = new ArrayList<>();
    ComplainListAdapter complainListAdapter;
    ProgressBar progressBar;
    ComplainList complain_list_class;
    TextView toast_message, record_status;

    String user_id;
    int org_leveL_pos;

    SharedPreferences sharedpreferences;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10000;

    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        complain_list_view_1=findViewById(R.id.complain_list);
        complain_list_view_2=findViewById(R.id.complain_list_2);
        newComplain=findViewById(R.id.new_complain);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        self_complain=findViewById(R.id.self_complain);
        other_complain=findViewById(R.id.other_complain);
        record_status = findViewById(R.id.record_status);
        reload_btn = findViewById(R.id.reload_btn);
        record_status.setVisibility(View.GONE);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = shared.getString("user_id","");
        org_leveL_pos = Integer.parseInt(shared.getString("org_level_ref","0"));
        System.out.println("user_id = " +user_id);

        toolbar=findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (org_leveL_pos==1){
            self_complain.setVisibility(View.GONE);
            other_complain.setVisibility(View.GONE);
            newComplain.setVisibility(View.GONE);
            getComplainToList();
            complainListAdapter=new ComplainListAdapter(getApplicationContext(),complainList);
            complain_list_view_2.setAdapter(complainListAdapter);

        } else {
            newComplain.setVisibility(View.VISIBLE);
            self_complain.setVisibility(View.VISIBLE);
            other_complain.setVisibility(View.VISIBLE);
            complain_list_view_1.setVisibility(View.VISIBLE);
            complain_list_view_2.setVisibility(View.GONE);
            getComplainByList();
            complainListAdapter=new ComplainListAdapter(getApplicationContext(),complainList);
            complain_list_view_1.setAdapter(complainListAdapter);
            self_complain.setTextColor(Color.BLACK);
        }

        reload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (org_leveL_pos==1){
                    self_complain.setVisibility(View.GONE);
                    other_complain.setVisibility(View.GONE);
                    newComplain.setVisibility(View.GONE);
                    getComplainToList();
                    complainListAdapter=new ComplainListAdapter(getApplicationContext(),complainList);
                    complain_list_view_2.setAdapter(complainListAdapter);

                } else {
                    newComplain.setVisibility(View.VISIBLE);
                    self_complain.setVisibility(View.VISIBLE);
                    other_complain.setVisibility(View.VISIBLE);
                    complain_list_view_1.setVisibility(View.VISIBLE);
                    complain_list_view_2.setVisibility(View.GONE);
                    getComplainByList();
                    complainListAdapter=new ComplainListAdapter(getApplicationContext(),complainList);
                    complain_list_view_1.setAdapter(complainListAdapter);
                    self_complain.setTextColor(Color.BLACK);
                }
            }
        });


        newComplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),NewComplainReport.class);
                intent.putExtra("user_id",user_id);
                intent.putExtra("org_level_pos",org_leveL_pos);
                startActivity(intent);
            }
        });

        self_complain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                complain_list_view_1.setVisibility(View.VISIBLE);
                complain_list_view_2.setVisibility(View.GONE);
                getComplainByList();
                complainListAdapter=new ComplainListAdapter(getApplicationContext(),complainList);
                complain_list_view_1.setAdapter(complainListAdapter);
                self_complain.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0D235")));
                self_complain.setTextColor(Color.BLACK);
                other_complain.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
                other_complain.setTextColor(Color.BLACK);

            }
        });

        other_complain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complain_list_view_2.setVisibility(View.VISIBLE);
                complain_list_view_1.setVisibility(View.GONE);
                getComplainToList();
                complainListAdapter=new ComplainListAdapter(getApplicationContext(),complainList);
                complain_list_view_2.setAdapter(complainListAdapter);
                other_complain.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0D235")));
                other_complain.setTextColor(Color.BLACK);
                self_complain.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
                self_complain.setTextColor(Color.BLACK);

            }
        });

        complain_list_view_1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String ev_body = complainList.get(position).getBody();
                String subject = complainList.get(position).getSubject();
                String from = complainList.get(position).getSend_from();
                String to = complainList.get(position).getSend_to();
                String ev_for = complainList.get(position).getSend_for();
                String date = complainList.get(position).getDate();
                String status = complainList.get(position).getStatus();
                String comment = complainList.get(position).getComment();

                Intent intent1 = new Intent(getApplicationContext(),ComplainDetailsActivity.class);
                intent1.putExtra("cm_body",ev_body);
                intent1.putExtra("subject",subject);
                intent1.putExtra("from",from);
                intent1.putExtra("to",to);
                intent1.putExtra("cm_for",ev_for);
                intent1.putExtra("date",date);
                intent1.putExtra("intent_flag",1);
                intent1.putExtra("status", status);
                intent1.putExtra("comment", comment);
                intent1.putExtra("complain_by_ref", user_id);
                startActivity(intent1);

            }
        });

        complain_list_view_2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String ev_body = complainList.get(position).getBody();
                String subject = complainList.get(position).getSubject();
                String from = complainList.get(position).getSend_from();
                String to = complainList.get(position).getSend_to();
                String ev_for = complainList.get(position).getSend_for();
                String date = complainList.get(position).getDate();
                int com_id = Integer.parseInt(complainList.get(position).getId());
                String status = complainList.get(position).getStatus();
                String comment = complainList.get(position).getComment();
                String complain_by_ref = complainList.get(position).getComplain_by();

                Intent intent1 = new Intent(getApplicationContext(),ComplainDetailsActivity.class);
                intent1.putExtra("cm_body",ev_body);
                intent1.putExtra("subject",subject);
                intent1.putExtra("from",from);
                intent1.putExtra("to",to);
                intent1.putExtra("cm_for",ev_for);
                intent1.putExtra("date",date);
                intent1.putExtra("intent_flag",2);
                intent1.putExtra("id",com_id);
                intent1.putExtra("status", status);
                intent1.putExtra("comment", comment);
                intent1.putExtra("complain_by_ref", complain_by_ref);
                startActivity(intent1);

            }
        });

    }

    private void getComplainToList() {

        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getComplainToList.php?user_id="+user_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        complainList.clear();
                        System.out.println("response = " + response);
                        String data = response.toString().trim();
                        if (data.equals("null") || data.equals("")){
                            record_status.setVisibility(View.VISIBLE);
                        } else {
                            record_status.setVisibility(View.GONE);
                        }

                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("id");
                                String subject = object.getString("complain_subject");
                                String send_by = object.getString("complain_by_name");
                                String send_to = object.getString("complain_to_name");
                                String send_for = object.getString("complain_for_name");
                                String date = object.getString("complain_date");
                                String body = object.getString("complain_body");
                                String approve_flag = object.getString("approve_flag");
                                String complain_by_ref = object.getString("complain_by");
                                String Comment = object.getString("comment");
                                String status = "";

                                switch (approve_flag){
                                    case "0" :
                                        status = "Pending";
                                        break;
                                    case "1" :
                                        status = "Approved";
                                        break;
                                    case "2" :
                                        status = "Rejected";
                                        break;
                                }

                                @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);

                                String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String from_date = simpleDateFormat.format(date1);

                                complain_list_class=new ComplainList(subject,send_by,send_to,send_for,from_date,body,id,status,Comment, complain_by_ref);
                                complainList.add(complain_list_class);
                                complainListAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);

                            }

                        } catch (JSONException e){
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            record_status.setVisibility(View.VISIBLE);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                record_status.setVisibility(View.VISIBLE);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

    }

    private void getComplainByList() {

        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getComplainByList.php?user_id="+user_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        complainList.clear();
                        System.out.println("response = " + response);
                        String data = response.toString().trim();
                        if (data.equals("null") || data.equals("")){
                            record_status.setVisibility(View.VISIBLE);
                        } else {
                            record_status.setVisibility(View.GONE);
                        }
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("id");
                                String subject = object.getString("complain_subject");
                                String send_by = object.getString("complain_by_name");
                                String send_to = object.getString("complain_to_name");
                                String send_for = object.getString("complain_for_name");
                                String date = object.getString("complain_date");
                                String body = object.getString("complain_body");
                                String approve_flag = object.getString("approve_flag");
                                String comment = object.getString("comment");
                                String status = "";
                                switch (approve_flag){
                                    case "0" :
                                        status = "Pending";
                                        break;
                                    case "1" :
                                        status = "Approved";
                                        break;
                                    case "2" :
                                        status = "Rejected";
                                        break;
                                }

                                @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);

                                String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String from_date = simpleDateFormat.format(date1);

                                complain_list_class=new ComplainList(subject,send_by,send_to,send_for,from_date,body,id,status,comment,user_id);
                                complainList.add(complain_list_class);
                                complainListAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);

                            }

                        } catch (JSONException e){
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            record_status.setVisibility(View.VISIBLE);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                record_status.setVisibility(View.VISIBLE);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
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