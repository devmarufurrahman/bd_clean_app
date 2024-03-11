package com.winkytech.bdclean;

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

public class EventManageActivity extends AppCompatActivity {

    String user_id, user_position;
    int upazila_ref, org_level_pos;
    ListView event_list_view,event_list_view_2, my_event_list_view;
    ProgressBar progressBar;
    EventList event_list_class;
    EventListAdapter eventListAdapter;
    Button approve_event, approve_member, my_events, createEvent;
    TextView toast_message, manage_tv;
    List<EventList> eventLists = new ArrayList<>();
    Toolbar toolbar;
    String photoUrl = "https://bdclean.winkytech.com/resources/event/";
    public static final String MyPREFERENCES = "MyPrefs" ;

    NetworkChangeListener networkChangeListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_manage);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        Intent intent = getIntent();
        int intent_flag = intent.getIntExtra("intent_flag",0);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = shared.getString("user_id","");
        upazila_ref = Integer.parseInt(shared.getString("upazila_ref",""));
        org_level_pos = Integer.parseInt(shared.getString("org_level_ref",""));
        user_position = shared.getString("user_position","");
        System.out.println("user_id = " + user_id + " user_position = "+user_position + " ORG LEVEL POS = "+org_level_pos);

        toolbar = findViewById(R.id.custom_toolbar);
        progressBar = findViewById(R.id.progressbar);
        createEvent = findViewById(R.id.newEvent);
        createEvent.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        event_list_view = findViewById(R.id.event_list_view);
        approve_event=findViewById(R.id.event_approve);
        approve_member = findViewById(R.id.member_approve);
        event_list_view_2 = findViewById(R.id.event_list_view_2);
        my_event_list_view = findViewById(R.id.my_event_listView);
        manage_tv = findViewById(R.id.manage_tv);
        manage_tv.setVisibility(View.GONE);
        my_events = findViewById(R.id.my_event);

        if (org_level_pos <= 10 || user_position.equals("5")){
            createEvent.setVisibility(View.VISIBLE);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (intent_flag==1 ||intent_flag==10){
            getEventList();
            manage_tv.setVisibility(View.GONE);
            eventListAdapter = new EventListAdapter(getApplicationContext(),eventLists);
            event_list_view.setAdapter(eventListAdapter);
            event_list_view_2.setVisibility(View.GONE);
            my_event_list_view.setVisibility(View.GONE);
            approve_event.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0D235")));
            approve_member.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
            my_events.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
            approve_event.setTextColor(getResources().getColor(R.color.black));
            approve_member.setTextColor(getResources().getColor(R.color.black));
            my_events.setTextColor(getResources().getColor(R.color.black));
        } else if (intent_flag==11){
            getMyEvents();
            manage_tv.setVisibility(View.GONE);
            eventListAdapter = new EventListAdapter(getApplicationContext(),eventLists);
            my_event_list_view.setAdapter(eventListAdapter);
            my_event_list_view.setVisibility(View.VISIBLE);
            event_list_view_2.setVisibility(View.GONE);
            event_list_view.setVisibility(View.GONE);
            my_events.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0D235")));
            approve_event.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
            approve_member.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
            approve_event.setTextColor(getResources().getColor(R.color.black));
            approve_member.setTextColor(getResources().getColor(R.color.black));
            my_events.setTextColor(getResources().getColor(R.color.black));
        } else if (intent_flag==12){
            getEventMemberList();
            manage_tv.setVisibility(View.GONE);
            eventListAdapter = new EventListAdapter(getApplicationContext(),eventLists);
            event_list_view_2.setAdapter(eventListAdapter);
            event_list_view_2.setVisibility(View.VISIBLE);
            event_list_view.setVisibility(View.GONE);
            my_event_list_view.setVisibility(View.GONE);
            approve_event.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
            approve_member.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0D235")));
            my_events.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
            approve_event.setTextColor(getResources().getColor(R.color.black));
            approve_member.setTextColor(getResources().getColor(R.color.black));
            my_events.setTextColor(getResources().getColor(R.color.black));
        }

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),NewEventActivity.class);
                startActivity(intent);
            }
        });

        event_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int flag = 1;
                String event_id =  eventLists.get(position).getId();
                String e_name = eventLists.get(position).getName();
                String type = eventLists.get(position).getType();
                String from_date = eventLists.get(position).getStart_date();
                String to_date = eventLists.get(position).getEnd_date();
                String location = eventLists.get(position).getLocation();
                String district = eventLists.get(position).getDistrict();
                String upzilla = eventLists.get(position).getUpazilla();
                String photo = eventLists.get(position).getPhoto();
                String status = eventLists.get(position).getStatus();
                String supervisor = eventLists.get(position).getSupervisor();
                String village = eventLists.get(position).getVillage();
                String union = eventLists.get(position).getUnion();
                String division = eventLists.get(position).getDivision();

                Intent intent1 = new Intent(getApplicationContext(), EventManageDetailsActivity.class);
                intent1.putExtra("event_id",event_id);
                intent1.putExtra("e_name",e_name);
                intent1.putExtra("type",type);
                intent1.putExtra("from_date",from_date);
                intent1.putExtra("to_date",to_date);
                intent1.putExtra("location",location);
                intent1.putExtra("district",district);
                intent1.putExtra("upzilla",upzilla);
                intent1.putExtra("photo",photo);
                intent1.putExtra("user_id",user_id);
                intent1.putExtra("status",status);
                intent1.putExtra("supervisor",supervisor);
                intent1.putExtra("village",village);
                intent1.putExtra("union",union);
                intent1.putExtra("division",division);
                intent1.putExtra("flag",flag);
                intent1.putExtra("intent_flag", intent_flag);
                startActivity(intent1);

            }
        });

        event_list_view_2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int flag = 2;
                String event_id =  eventLists.get(position).getId();
                String e_name = eventLists.get(position).getName();
                String type = eventLists.get(position).getType();
                String from_date = eventLists.get(position).getStart_date();
                String to_date = eventLists.get(position).getEnd_date();
                String location = eventLists.get(position).getLocation();
                String district = eventLists.get(position).getDistrict();
                String upzilla = eventLists.get(position).getUpazilla();
                String photo = eventLists.get(position).getPhoto();
                String status = eventLists.get(position).getStatus();

                Intent intent1 = new Intent(getApplicationContext(), EventManageDetailsActivity.class);
                intent1.putExtra("event_id",event_id);
                intent1.putExtra("e_name",e_name);
                intent1.putExtra("type",type);
                intent1.putExtra("from_date",from_date);
                intent1.putExtra("to_date",to_date);
                intent1.putExtra("location",location);
                intent1.putExtra("district",district);
                intent1.putExtra("upzilla",upzilla);
                intent1.putExtra("photo",photo);
                intent1.putExtra("user_id",user_id);
                intent1.putExtra("status",status);
                intent1.putExtra("flag",flag);
                intent1.putExtra("intent_flag", intent_flag);
                startActivity(intent1);

            }
        });

        my_event_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int flag = 3;
                String event_id =  eventLists.get(position).getId();
                String e_name = eventLists.get(position).getName();
                String type = eventLists.get(position).getType();
                String from_date = eventLists.get(position).getStart_date();
                String to_date = eventLists.get(position).getEnd_date();
                String location = eventLists.get(position).getLocation();
                String district = eventLists.get(position).getDistrict();
                String upzilla = eventLists.get(position).getUpazilla();
                String photo = eventLists.get(position).getPhoto();
                String status = eventLists.get(position).getStatus();
                String supervisor = eventLists.get(position).getSupervisor();
                String village = eventLists.get(position).getVillage();
                String union = eventLists.get(position).getUnion();
                String division = eventLists.get(position).getDivision();
                String comment = eventLists.get(position).getComment();

                Intent intent1 = new Intent(getApplicationContext(), EventManageDetailsActivity.class);
                intent1.putExtra("event_id",event_id);
                intent1.putExtra("e_name",e_name);
                intent1.putExtra("type",type);
                intent1.putExtra("from_date",from_date);
                intent1.putExtra("to_date",to_date);
                intent1.putExtra("location",location);
                intent1.putExtra("district",district);
                intent1.putExtra("upzilla",upzilla);
                intent1.putExtra("photo",photo);
                intent1.putExtra("user_id",user_id);
                intent1.putExtra("status",status);
                intent1.putExtra("supervisor",supervisor);
                intent1.putExtra("village",village);
                intent1.putExtra("union",union);
                intent1.putExtra("division",division);
                intent1.putExtra("flag",flag);
                intent1.putExtra("intent_flag", intent_flag);
                intent1.putExtra("comment", comment);
                startActivity(intent1);
            }
        });

        approve_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEventList();
                createEvent.setVisibility(View.VISIBLE);
                manage_tv.setVisibility(View.GONE);
                eventListAdapter = new EventListAdapter(getApplicationContext(),eventLists);
                event_list_view.setAdapter(eventListAdapter);
                event_list_view.setVisibility(View.VISIBLE);

                event_list_view_2.setVisibility(View.GONE);
                my_event_list_view.setVisibility(View.GONE);
                approve_event.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0D235")));
                approve_member.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
                my_events.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
                approve_event.setTextColor(getResources().getColor(R.color.black));
                approve_member.setTextColor(getResources().getColor(R.color.black));
                my_events.setTextColor(getResources().getColor(R.color.black));
            }
        });

        approve_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEventMemberList();
                createEvent.setVisibility(View.GONE);
                manage_tv.setVisibility(View.GONE);
                eventListAdapter = new EventListAdapter(getApplicationContext(),eventLists);
                event_list_view_2.setAdapter(eventListAdapter);
                event_list_view_2.setVisibility(View.VISIBLE);
                event_list_view.setVisibility(View.GONE);
                my_event_list_view.setVisibility(View.GONE);
                approve_event.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
                approve_member.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0D235")));
                my_events.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
                approve_event.setTextColor(getResources().getColor(R.color.black));
                approve_member.setTextColor(getResources().getColor(R.color.black));
                my_events.setTextColor(getResources().getColor(R.color.black));
            }
        });

        my_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyEvents();
                createEvent.setVisibility(View.VISIBLE);
                manage_tv.setVisibility(View.GONE);
                eventListAdapter = new EventListAdapter(getApplicationContext(),eventLists);
                my_event_list_view.setAdapter(eventListAdapter);
                my_event_list_view.setVisibility(View.VISIBLE);
                event_list_view_2.setVisibility(View.GONE);
                event_list_view.setVisibility(View.GONE);
                my_events.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0D235")));
                approve_event.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
                approve_member.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
                approve_event.setTextColor(getResources().getColor(R.color.black));
                approve_member.setTextColor(getResources().getColor(R.color.black));
                my_events.setTextColor(getResources().getColor(R.color.black));

            }
        });

    }

    private void getMyEvents() {
        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getMyEventList.php?user_id="+user_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        eventLists.clear();
                        System.out.println("response = " + response);
                        try {

                            progressBar.setVisibility(View.GONE);

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("id");
                                String name = object.getString("name");
                                String start_date = object.getString("start_date");
                                String end_date = object.getString("end_date");
                                String event_location = object.getString("event_location");
                                String type = object.getString("type");
                                String district = object.getString("district");
                                String upazilla = object.getString("upazilla");
                                String union = object.getString("union_name");
                                String village = object.getString("village_name");
                                String division = object.getString("division_name");
                                String photo = photoUrl+(object.getString("event_cover"));
                                String approve_status = object.getString("approve_status");
                                String supervisor = object.getString("supervisor");
                                String comment = object.getString("comment");

                                @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_date);
                                @SuppressLint("SimpleDateFormat") Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_date);

                                String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String from_date = simpleDateFormat.format(date1);
                                String to_date = simpleDateFormat.format(date2);
                                String status = null;

                                switch (approve_status){
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

                                event_list_class=new EventList(id,name,photo,from_date,to_date,event_location,type,district,upazilla,status, supervisor, union,village, division, comment);
                                eventLists.add(event_list_class);
                                eventListAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);

                            }

                        } catch (JSONException e){
                            manage_tv.setText("No Event Found");
                            manage_tv.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                manage_tv.setText("No Event Found");
                manage_tv.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    private void getEventMemberList() {
        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getEventMemberApproval.php?upazila_ref="+upazila_ref+"&user_id=" + user_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        eventLists.clear();
                        System.out.println("response = " + response);
                        progressBar.setVisibility(View.GONE);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("id");
                                String name = object.getString("name");
                                String start_date = object.getString("start_date");
                                String end_date = object.getString("end_date");
                                String event_location = object.getString("event_location");
                                String type = object.getString("type");
                                String district = object.getString("district");
                                String upazilla = object.getString("upazilla");
                                String union = object.getString("union_name");
                                String village = object.getString("village_name");
                                String division = object.getString("division_name");
                                String photo = photoUrl+(object.getString("event_cover"));
                                String approve_status = object.getString("approve_status");
                                String supervisor = object.getString("supervisor");

                                @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_date);
                                @SuppressLint("SimpleDateFormat") Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_date);

                                String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String from_date = simpleDateFormat.format(date1);
                                String to_date = simpleDateFormat.format(date2);
                                String status = null;

                                switch (approve_status){
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

                                event_list_class=new EventList(id,name,photo,from_date,to_date,event_location,type,district,upazilla,status, supervisor, union,village, division, "");
                                eventLists.add(event_list_class);
                                eventListAdapter.notifyDataSetChanged();

                            }

                        } catch (JSONException e){
                            manage_tv.setText("No Event Found");
                            manage_tv.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                manage_tv.setText("No Event Found");
                manage_tv.setVisibility(View.VISIBLE);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    private void getEventList() {

        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getEventList.php?upazila_ref="+upazila_ref+"&user_id=" + user_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        eventLists.clear();
                        System.out.println("response = " + response);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("id");
                                String name = object.getString("name");
                                String start_date = object.getString("start_date");
                                String end_date = object.getString("end_date");
                                String event_location = object.getString("event_location");
                                String type = object.getString("type");
                                String district = object.getString("district");
                                String upazilla = object.getString("upazilla");
                                String union = object.getString("union_name");
                                String village = object.getString("village_name");
                                String division = object.getString("division_name");
                                String photo = photoUrl+(object.getString("event_cover"));
                                String approve_status = object.getString("approve_status");
                                String supervisor = object.getString("supervisor");

                                @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_date);
                                @SuppressLint("SimpleDateFormat") Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_date);

                                String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String from_date = simpleDateFormat.format(date1);
                                String to_date = simpleDateFormat.format(date2);
                                String status = null;

                                switch (approve_status){
                                    case "0" :
                                        status = "Pending Approval";
                                        break;
                                    case "1" :
                                        status = "Approved";
                                        break;
                                    case "2" :
                                        status = "Rejected";
                                        break;
                                }

                                event_list_class=new EventList(id,name,photo,from_date,to_date,event_location,type,district,upazilla,status, supervisor, union,village, division, "");
                                eventLists.add(event_list_class);
                                eventListAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);

                            }

                        } catch (JSONException e){
                            manage_tv.setText("No Event Found");
                            manage_tv.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                manage_tv.setText("No Event Found");
                manage_tv.setVisibility(View.VISIBLE);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
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