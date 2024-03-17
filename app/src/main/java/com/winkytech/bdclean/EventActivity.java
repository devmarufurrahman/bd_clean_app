package com.winkytech.bdclean;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
@SuppressLint("SetTextI18n")
public class EventActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView eventListView;
    EventListAdapter eventListAdapter;
    List<EventList> eventLists = new ArrayList<>();
    EventList event_list_class;
    String photoUrl = "https://bdclean.winkytech.com/resources/event/";
    String user_id, joined_event_id, joined_event_end_date;
    int org_level_pos,upazila_ref,joined_event_flag, event_flag =0, division_ref,district_ref, union_ref, village_ref;
    ProgressBar progressBar;
    TextView toast_message, event_name, event_location, event_type, record_status;
    Button ongoing_event, upcoming_event, leave_btn;
    CardView joined_event_details;
    SwipeRefreshLayout swipeRefreshLayout;


//    Handler handler = new Handler();
//    Runnable runnable;
//    int delay = 180000;

    public static final String MyPREFERENCES = "MyPrefs" ;

    NetworkChangeListener networkChangeListener;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = shared.getString("user_id","");
        upazila_ref = Integer.parseInt(shared.getString("upazila_ref",""));
        district_ref = Integer.parseInt(shared.getString("district_ref",""));
        division_ref = Integer.parseInt(shared.getString("division_ref",""));
        union_ref = Integer.parseInt(shared.getString("union_ref",""));
        village_ref = Integer.parseInt(shared.getString("village_ref",""));
        org_level_pos = Integer.parseInt(shared.getString("org_level_ref",""));

        Log.d("User data ", (user_id+","+upazila_ref+","+district_ref+","+division_ref+","+union_ref+","+village_ref));

        eventListView=findViewById(R.id.event_listView);
        toolbar=findViewById(R.id.custom_toolbar);
        progressBar = findViewById(R.id.progressbar);
        ongoing_event = findViewById(R.id.ongoing_event);
        upcoming_event = findViewById(R.id.upcoming_event);
        joined_event_details = findViewById(R.id.event_card_1);
        joined_event_details.setVisibility(View.GONE);
        event_name = findViewById(R.id.event_name);
        event_type = findViewById(R.id.event_type);
        event_location = findViewById(R.id.event_location);
        leave_btn = findViewById(R.id.leave_event);
        progressBar.setVisibility(View.GONE);
        record_status = findViewById(R.id.record_status);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        record_status.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getJoinedEventDetails();
        getOngoingEventList();
        eventListAdapter = new EventListAdapter(getApplicationContext(),eventLists);
        eventListView.setAdapter(eventListAdapter);
        ongoing_event.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0D235")));
        ongoing_event.setTextColor(Color.BLACK);
        upcoming_event.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
        upcoming_event.setTextColor(Color.BLACK);



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                getJoinedEventDetails();
//                getJoinedEventDetails();
//                getOngoingEventList();
//                eventListAdapter = new EventListAdapter(getApplicationContext(),eventLists);
//                eventListView.setAdapter(eventListAdapter);
//                ongoing_event.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0D235")));
//                ongoing_event.setTextColor(Color.BLACK);
//                upcoming_event.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
//                upcoming_event.setTextColor(Color.BLACK);

                recreate();
                // Signal SwipeRefreshLayout to start the progress indicator.
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        ongoing_event.setOnClickListener(v -> {
            event_flag = 1;
            getOngoingEventList();
            eventListAdapter = new EventListAdapter(getApplicationContext(),eventLists);
            eventListView.setAdapter(eventListAdapter);

            upcoming_event.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
            ongoing_event.setTextColor(Color.BLACK);
            ongoing_event.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0D235")));
            upcoming_event.setTextColor(getResources().getColor(R.color.black));
        });

        upcoming_event.setOnClickListener(v -> {
            event_flag = 2;
            getUpcomingEventList();
            eventListAdapter = new EventListAdapter(getApplicationContext(),eventLists);
            eventListView.setAdapter(eventListAdapter);

            upcoming_event.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0D235")));
            upcoming_event.setTextColor(getResources().getColor(R.color.black));
            ongoing_event.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
            ongoing_event.setTextColor(getResources().getColor(R.color.black));
        });

        eventListView.setOnItemClickListener((parent, view, position, id) -> {

            int intent_flag = 1;
            String event_id = eventLists.get(position).getId();
            Intent intent1 = new Intent(getApplicationContext(),EventDetailsActivity.class);
            intent1.putExtra("org_level_pos",org_level_pos);
            intent1.putExtra("user_id",user_id);
            intent1.putExtra("event_id",event_id);
            intent1.putExtra("joined_flag",joined_event_flag);
            intent1.putExtra("event_flag",event_flag);
            intent1.putExtra("intent_flag",intent_flag);
            startActivity(intent1);

        });

        joined_event_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int intent_flag = 1;
                Intent intent = new Intent(getApplicationContext(), EventDetailsActivity.class);
                intent.putExtra("org_level_pos",org_level_pos);
                intent.putExtra("user_id",user_id);
                intent.putExtra("event_id",joined_event_id);
                intent.putExtra("joined_flag",joined_event_flag);
                intent.putExtra("event_flag",event_flag);
                intent.putExtra("intent_flag",intent_flag);
                startActivity(intent);
            }
        });

        leave_btn.setOnClickListener(v -> {
            Calendar currentDateTime = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date end_datetime = dateFormat.parse(joined_event_end_date);
                Calendar event_endTime = Calendar.getInstance();
                event_endTime.setTime(end_datetime);

                long remainingTimeInMillis = event_endTime.getTimeInMillis() - currentDateTime.getTimeInMillis();

                long hours = TimeUnit.MILLISECONDS.toHours(remainingTimeInMillis);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeInMillis - TimeUnit.HOURS.toMillis(hours));
                long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTimeInMillis - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes));

                //String remainingTimeString = String.format("%02d hours %02d minutes %02d seconds", hours, minutes, seconds);

                if (currentDateTime.compareTo(event_endTime) >= 0 ){
                    Dialog dialog = new Dialog(EventActivity.this);
                    dialog.setContentView(R.layout.leave_confiramtion_dialog_layout);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(true);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                    Button yes_btn = dialog.findViewById(R.id.yes_btn);
                    Button no_btn = dialog.findViewById(R.id.no_btn);

                    yes_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            leaveEvent();
                            dialog.dismiss();
                        }
                    });

                    no_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                } else {
                    Dialog dialog = new Dialog(EventActivity.this);
                    dialog.setContentView(R.layout.event_warning_dialog_layout);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(true);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                    Button close_btn = dialog.findViewById(R.id.close_btn);
                    TextView dialog_tv = dialog.findViewById(R.id.dialog_tv);

                    if (hours==0 && minutes !=0 && seconds !=0){
                        String remainingTimeString = String.format(" %02d minutes %02d seconds", minutes, seconds);
                        dialog_tv.setText("Sorry Can't leave event now. Please wait " + remainingTimeString +" to leave from this event");
                    } else if (hours==0 && minutes ==0 && seconds!=0){
                        String remainingTimeString = String.format(" %02d seconds", seconds);
                        dialog_tv.setText("Sorry Can't leave event now. Please wait " + remainingTimeString +" to leave from this event");
                    } else {
                        String remainingTimeString = String.format("%02d hours %02d minutes %02d seconds", hours , minutes , seconds);
                        dialog_tv.setText("Sorry Can't leave event now. Please wait " + remainingTimeString +" to leave from this event");
                    }

                    close_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        });


    }

    @SuppressLint("SetTextI18n")
    private void leaveEvent() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            progressBar.setVisibility(View.VISIBLE);
            //Starting Write and Read data with URL
            //Creating array for parameters
            String[] field = new String[2];
            field[0] = "user_id";
            field[1] = "event_id";

            //Creating array for data
            String[] data = new String[2];
            data[0] = String.valueOf(user_id);
            data[1] = String.valueOf(joined_event_id);

            PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/leaveEvent.php", "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult().trim();
                    if (result.equals("Left Event")) {
                        progressBar.setVisibility(View.GONE);
                        getJoinedEventDetails();
                        Toast toast = new Toast(EventActivity.this);
                        View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout,findViewById(R.id.custom_toast));
                        toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                        toast_message.setText("Event left");
                        toast.setView(toast_view);
                        toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();

                        //Toast.makeText(EventDetailsActivity.this, "Left Event", Toast.LENGTH_SHORT).show();

                    } else {
                        progressBar.setVisibility(View.GONE);
                        Log.i("PutData", result);
                        // Toast.makeText(EventDetailsActivity.this, "Failed To leave event!!!", Toast.LENGTH_SHORT).show();
                        getJoinedEventDetails();
                        Toast toast = new Toast(getApplicationContext());
                        View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
                        toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                        toast_message.setText("Failed To leave event!!!");
                        toast.setView(toast_view);
                        toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();

                    }
                }
            }
            //End Write and Read data with URL
        });
    }

    private void getJoinedEventDetails() {
        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getJoinedEventData.php?user_id="+user_id;
        @SuppressLint("SetTextI18n") StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    System.out.println("JOINED EVENT RESPONSE = " + response);
                    String response_data = response.trim();

                    if (response_data.equals("") || response_data.equals("null")){
                        joined_event_details.setVisibility(View.GONE);
                        joined_event_flag = 0;
                    } else {
                        joined_event_details.setVisibility(View.VISIBLE);
                        System.out.println(response);
                        try {
                            joined_event_flag = 1;
                            progressBar.setVisibility(View.GONE);
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject object = jsonArray.getJSONObject(0);
                            joined_event_id = object.getString("id");
                            String name = object.getString("event_name");
                            joined_event_end_date = object.getString("end_date");
                            String location = object.getString("event_location");
                            String type = object.getString("event_type");

                            event_name.setText(name);
                            event_type.setText(type);
                            event_location.setText(location);

                        } catch (JSONException e){
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                }, error -> {
                    progressBar.setVisibility(View.GONE);
                    //Toast.makeText(getApplicationContext(), "Failed to get Event List", Toast.LENGTH_LONG).show();
                    Toast toast = new Toast(getApplicationContext());
                    View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                    toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                    toast_message.setText("Please check your internet connection or try again later");
                    toast.setView(toast_view);
                    toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);


    }

    private void getUpcomingEventList() {
        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getUpcomingEventLIst.php?upazila_ref="+upazila_ref + "&org_level_pos="+org_level_pos+"&division_ref="+division_ref+"&district_ref="+district_ref+"&union_ref="+union_ref+"&village_ref="+village_ref;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    eventLists.clear();
                    Log.d("UPCOMING EVENT",response);
                    //System.out.println("response = " + response);
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
                            String name = object.getString("name");
                            String details = object.getString("details");
                            String start_date = object.getString("start_date");
                            String end_date = object.getString("end_date");
                            String event_location = object.getString("event_location");
                            String type = object.getString("type");
                            String district = object.getString("district");
                            String upazilla = object.getString("upazilla");
                            String photo = photoUrl+(object.getString("event_cover"));

                           @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_date);
                           @SuppressLint("SimpleDateFormat") Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_date);

                            String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                            String from_date = simpleDateFormat.format(date1);
                            String to_date = simpleDateFormat.format(date2);
                            String date_status = "UPCOMING";

                            System.out.println("FROM = " + from_date);
                            System.out.println("TO = " + to_date);

                            event_list_class=new EventList(id,name,photo,from_date,to_date,event_location,type,district,upazilla,date_status, details);
                            eventLists.add(event_list_class);
                            eventListAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);

                        }

                    } catch (JSONException e){
                        progressBar.setVisibility(View.GONE);
                        record_status.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    progressBar.setVisibility(View.GONE);

                    //Toast.makeText(getApplicationContext(), "Failed to get Event List", Toast.LENGTH_LONG).show();
                    Toast toast = new Toast(getApplicationContext());
                    View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                    toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                    toast_message.setText("Please check your internet connection or try again later");
                    toast.setView(toast_view);
                    toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                    progressBar.setVisibility(View.GONE);
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }


    private void getOngoingEventList() {

        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getOngoingEventList.php?upazila_ref="+upazila_ref + "&org_level_pos="+org_level_pos+"&division_ref="+division_ref+"&district_ref="+district_ref+"&union_ref="+union_ref+"&village_ref="+village_ref;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    eventLists.clear();
                    Log.d("ONGOING EVENT",response);
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
                            String name = object.getString("name");
                            String details = object.getString("details");
                            String start_date = object.getString("start_date");
                            String end_date = object.getString("end_date");
                            String event_location = object.getString("event_location");
                            String type = object.getString("type");
                            String district = object.getString("district");
                            String upazilla = object.getString("upazilla");
                            String photo = photoUrl+(object.getString("event_cover"));

                            @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_date);
                            @SuppressLint("SimpleDateFormat") Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_date);

                            String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                            String from_date = simpleDateFormat.format(date1);
                            String to_date = simpleDateFormat.format(date2);

                            String date_status = "ONGOING";

                            event_list_class=new EventList(id,name,photo,from_date,to_date,event_location,type,district,upazilla,date_status, details);
                            eventLists.add(event_list_class);
                            eventListAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);

                        }

                    } catch (JSONException e){
                        progressBar.setVisibility(View.GONE);
                        record_status.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    progressBar.setVisibility(View.GONE);

                    //Toast.makeText(getApplicationContext(), "Failed to get Event List", Toast.LENGTH_LONG).show();
                    Toast toast = new Toast(getApplicationContext());
                    View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                    toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                    toast_message.setText("Please check your internet connection or try again later");
                    toast.setView(toast_view);
                    toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                    progressBar.setVisibility(View.GONE);
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