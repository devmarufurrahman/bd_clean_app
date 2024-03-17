package com.winkytech.bdclean;

import static com.winkytech.bdclean.NewEventActivity.MyPREFERENCES;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@SuppressLint({"ResourceAsColor", "SetTextI18n"})
public class EventDetailsActivity extends AppCompatActivity {

    int org_level_pos, joined_event_flag, event_flag, event_joining_distance, intent_flag, function_ref,event_id,user_id;
    String  end_date, start_date, user_type;
    Toolbar toolbar;
    ImageView event_photo;
    TextView event_name, event_join_time, event_details,event_date,event_division,toast_message,status ;
    Button join_btn,leave_button;
    ProgressBar progressBar;
    double user_latitude, user_longitude;
    double event_latitude, event_longitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    float distance;
    Intent intent;
    private  static String photoUrl = "https://bdclean.winkytech.com/resources/event/";

    NetworkChangeListener networkChangeListener;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = Integer.parseInt(((shared.getString("user_id", ""))));
        user_type = shared.getString("designation", "");

        toolbar=findViewById(R.id.custom_toolbar);
        event_photo=findViewById(R.id.event_photo);
        event_name=findViewById(R.id.event_name);
        event_details=findViewById(R.id.details_tv);
        event_join_time=findViewById(R.id.join_time);
        event_date = findViewById(R.id.date_time);
        event_division = findViewById(R.id.event_address);
        join_btn = findViewById(R.id.join_btn);
        leave_button = findViewById(R.id.leave_btn);
        progressBar = findViewById(R.id.progressbar);
        status=findViewById(R.id.status);
        swipeRefreshLayout = findViewById(R.id.swiperefresh2);
        progressBar.setVisibility(View.GONE);
        leave_button.setEnabled(false);

        // swipe refresh method
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                recreate();
                swipeRefreshLayout.setRefreshing(false);

            }
        });



        intent = getIntent();

        intent_flag = intent.getIntExtra("intent_flag",0);

        if (intent_flag == 1){
            org_level_pos = intent.getIntExtra("org_level_pos",0);
            event_id = Integer.parseInt(intent.getStringExtra("event_id"));
            joined_event_flag = intent.getIntExtra("joined_flag",0);
            event_flag = intent.getIntExtra("event_flag",0);
            System.out.println(org_level_pos);
            System.out.println("user_id = "+ user_id);
            System.out.println("event_id = "+event_id);
            getEventStatus(user_id, event_id);
            getCurrentLocation();
            getEventList(event_id);


        } else if (intent_flag ==2) {
            function_ref = intent.getIntExtra("function_ref",0);
            System.out.println("intent_flag = " + intent_flag);
            System.out.println("function_ref = " + function_ref);
            getEventList(function_ref);
            getEventStatus(user_id, function_ref);


        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        join_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                Calendar currentDateTime = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date start_time = dateFormat.parse(start_date);
                    Calendar event_start_time = Calendar.getInstance();
                    event_start_time.setTime(start_time);

                    Calendar final_start_time = (Calendar) event_start_time.clone();
                    final_start_time.add(Calendar.MINUTE, -30);

                    long remainingTimeInMillis = event_start_time.getTimeInMillis() - currentDateTime.getTimeInMillis();

                    long hours = TimeUnit.MILLISECONDS.toHours(remainingTimeInMillis);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeInMillis - TimeUnit.HOURS.toMillis(hours));
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTimeInMillis - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes));

                    if (currentDateTime.before(event_start_time) && currentDateTime.after(final_start_time)){

                        if (joined_event_flag==1){
                            displayDialogue(remainingTimeInMillis,hours,minutes,seconds);
                        } else if(event_flag==2) {
                            displayDialogue(remainingTimeInMillis,hours,minutes,seconds);
                        }else if (distance<=event_joining_distance){
                            Dialog dialog = new Dialog(EventDetailsActivity.this);
                            dialog.setContentView(R.layout.joining_confirmation_dialog_layout);
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.setCancelable(true);
                            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                            Button yes_btn = dialog.findViewById(R.id.yes_btn);
                            Button no_btn = dialog.findViewById(R.id.no_btn);

                            yes_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    joinUserToEvent();
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
                        } else if (distance>event_joining_distance){
                            Dialog dialog = new Dialog(EventDetailsActivity.this);
                            dialog.setContentView(R.layout.event_warning_dialog_layout);
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.setCancelable(true);
                            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                            Button close_btn = dialog.findViewById(R.id.close_btn);
                            TextView dialog_tv = dialog.findViewById(R.id.dialog_tv);

                            dialog_tv.setText("You are  " + (Math.abs(distance) - event_joining_distance) + "  meters away" + "from event location. Please go to event location and try again");

                            close_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                        }

                    } else {
                        Dialog dialog = new Dialog(EventDetailsActivity.this);
                        dialog.setContentView(R.layout.event_warning_dialog_layout);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.setCancelable(true);
                        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                        Button close_btn = dialog.findViewById(R.id.close_btn);
                        TextView dialog_tv = dialog.findViewById(R.id.dialog_tv);

                        dialog_tv.setText("Sorry, You can not join this event");

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
            }
        });

        leave_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar currentDateTime = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date end_datetime = dateFormat.parse(end_date);
                    Calendar event_endTime = Calendar.getInstance();
                    event_endTime.setTime(end_datetime);

                    long remainingTimeInMillis = event_endTime.getTimeInMillis() - currentDateTime.getTimeInMillis();

                    long hours = TimeUnit.MILLISECONDS.toHours(remainingTimeInMillis);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeInMillis - TimeUnit.HOURS.toMillis(hours));
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTimeInMillis - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes));

                    if (currentDateTime.compareTo(event_endTime) >= 0 ){
                        Dialog dialog = new Dialog(EventDetailsActivity.this);
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
                        Dialog dialog = new Dialog(EventDetailsActivity.this);
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
            }
        });





    }

    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {

            @Override
            public void onComplete(@NonNull Task<Location> task) {

                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(EventDetailsActivity.this, Locale.getDefault());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        //List<Address> addressList = geocoder.getFromLocation(23.755247, 90.393884, 1);

                        user_latitude = addressList.get(0).getLatitude();
                        user_longitude = addressList.get(0).getLongitude();

                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void displayDialogue(long remainingTime, long hours, long minutes, long seconds) {
        join_btn.setEnabled(true);
        leave_button.setEnabled(false);
//        join_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4EDDA")));
//        join_btn.setTextColor(getResources().getColor(R.color.green_1));
        leave_button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F8D7DA")));
        leave_button.setTextColor(getResources().getColor(R.color.red));
        Dialog dialog = new Dialog(EventDetailsActivity.this);
        dialog.setContentView(R.layout.event_warning_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        Button close_btn = dialog.findViewById(R.id.close_btn);
        TextView dialog_tv = dialog.findViewById(R.id.dialog_tv);

        if (joined_event_flag==1){
            dialog_tv.setText(R.string.join_event_warning);
        } else if (event_flag==2){
            if (hours==0 && minutes !=0 && seconds !=0){
                String remainingTimeString = String.format(" %02d minutes %02d seconds", minutes, seconds);
                dialog_tv.setText("Sorry Can't join event now. Please wait " + remainingTimeString +" to join this event");
            } else if (hours==0 && minutes ==0 && seconds!=0){
                String remainingTimeString = String.format(" %02d seconds", seconds);
                dialog_tv.setText("Sorry Can't join event now. Please wait " + remainingTimeString +" to join this event");
            } else {
                String remainingTimeString = String.format("%02d hours %02d minutes %02d seconds", hours , minutes , seconds);
                dialog_tv.setText("Sorry Can't join event now. Please wait " + remainingTimeString +" to join this event");
            }
        }

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void getEventStatus(int user_ref, int function_ref) {

        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getEventStatus.php?user_id="+user_ref+"&event_code="+function_ref;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("response = " + response);
                        String response_data = response.toString().trim();

                        if (response_data.equals("")|| response_data.equals("null")){
                            join_btn.setEnabled(true);
//                            join_btn.setTextColor(getResources().getColor(R.color.white));
//                            join_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00974A")));
                            leave_button.setEnabled(false);
//                            leave_button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F8D7DA")));
//                            leave_button.setTextColor(getResources().getColor(R.color.red));
                            status.setText("Not Joined yet");

                        } else {

                            try {

                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject object = jsonArray.getJSONObject(0);
                                String join_status = object.getString("join_status");

                                switch (join_status) {
                                    case "1":
                                        join_btn.setEnabled(false);
                                        //join_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4EDDA")));
                                        //join_btn.setTextColor(getResources().getColor(R.color.green_1));
                                        //join_btn.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#00974A")));
                                        leave_button.setEnabled(true);
                                        //leave_button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EB1C24")));
                                        //leave_button.setTextColor(getResources().getColor(R.color.white));
                                        status.setText("Event Joined");
                                        break;
                                    case "2":
                                        join_btn.setEnabled(false);
                                        leave_button.setEnabled(false);
//                                        join_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4EDDA")));
//                                        join_btn.setTextColor(getResources().getColor(R.color.green_1));
//                                        leave_button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F8D7DA")));
//                                        leave_button.setTextColor(getResources().getColor(R.color.red));
                                        status.setText("Completed");
                                        break;
                                }

                                progressBar.setVisibility(View.GONE);

                            } catch (JSONException e){
                                progressBar.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "Failed to get Event List", Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText("Failed to get Event STATUS");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                progressBar.setVisibility(View.GONE);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    private void leaveEvent() {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                //Starting Write and Read data with URL
                //Creating array for parameters
                String[] field = new String[2];
                field[0] = "user_id";
                field[1] = "event_id";

                //Creating array for data
                String[] data = new String[2];
                data[0] = String.valueOf(user_id);
                data[1] = String.valueOf(event_id);

                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/leaveEvent.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();
                        if (result.equals("Left Event")) {
                            progressBar.setVisibility(View.GONE);
                            getEventStatus(user_id, event_id);
                            Toast toast = new Toast(EventDetailsActivity.this);
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
                            getEventStatus(user_id, event_id);
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
            }
        });

    }

    private void joinUserToEvent() {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                //Starting Write and Read data with URL
                //Creating array for parameters
                String[] field = new String[3];
                field[0] = "user_id";
                field[1] = "event_id";
                field[2] = "user_type";

                //Creating array for data
                String[] data = new String[3];
                data[0] = String.valueOf(user_id);
                data[1] = String.valueOf(event_id);
                data[2] = "member";

                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/joinEvent.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();
                        if (result.equals("Event Joined")) {
                            progressBar.setVisibility(View.GONE);
                            getEventStatus(user_id, event_id);
                            // Toast.makeText(getActivity(), "Login Success", Toast.LENGTH_SHORT).show();
                            Toast toast = new Toast(EventDetailsActivity.this);
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout,findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Event Joined");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();

                            //Toast.makeText(EventDetailsActivity.this, "Event Joined", Toast.LENGTH_SHORT).show();

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.i("PutData", result);
                            //Toast.makeText(EventDetailsActivity.this, "Failed To join event!!!", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Failed To join event!!!");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
//                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }
                //End Write and Read data with URL
            }
        });
    }

    private void getEventList(int function_ref) {
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getEventDetails.php?event_id=" + function_ref;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        Log.d("EVENT DETAILS = " , response);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("id");
                                String name = object.getString("name");
                                String spec = object.getString("spec");
                                start_date = object.getString("start_date");
                                end_date = object.getString("end_date");
                                String location = object.getString("event_location");
                                String type = object.getString("event_type");
                                String division = object.getString("division");
                                String district = object.getString("district");
                                String upazilla = object.getString("upazila");
                                String union = object.getString("union_name");
                                String village = object.getString("village");
                                String event_area_distance = object.getString("area_distance");
                                String joining_distance_event = object.getString("joining_distance");
                                event_latitude = Double.parseDouble(object.getString("location_latitude"));
                                event_longitude = Double.parseDouble(object.getString("location_longitude"));
                                event_joining_distance = Integer.parseInt(object.getString("joining_distance"));
                                String photo = photoUrl + (object.getString("event_cover"));
                                Location even_location = new Location("");

                                even_location.setLatitude(event_latitude);
                                even_location.setLongitude(event_longitude);
                                float[] results = new float[1];
                                Location.distanceBetween(user_latitude, user_longitude,event_latitude, event_longitude, results);
                                distance = results[0];
                                System.out.println("event join distance = " + event_joining_distance);
                                System.out.println("user distance is = " + distance);

                                @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_date);
                                @SuppressLint("SimpleDateFormat") Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_date);

                                String pattern = "E, dd MMM yyyy, ( hh:mm: a)";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String from_date = simpleDateFormat.format(date1);
                                String to_date = simpleDateFormat.format(date2);


//                              joinin date time calculation
                                Calendar event_join = Calendar.getInstance();
                                event_join.setTime(date1);

                                Calendar final_join_time = (Calendar) event_join.clone();
                                final_join_time.add(Calendar.MINUTE, -30);
                                String joinTime = simpleDateFormat.format(final_join_time.getTime());
                                event_join_time.setText("Event Join Time: "+ joinTime);



                                event_name.setText(name);
                                event_details.setText(spec);
                                event_date.setText("Starts from : " + from_date + "\n\n" + "Ends on : " + to_date);
                                event_division.setText("Address: "+location);
                                Picasso.get().load(photo).into(event_photo);
                                progressBar.setVisibility(View.GONE);

                            }
                        } catch (JSONException e) {
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText("Failed to get Event List");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
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
        swipeRefreshLayout.setRefreshing(false);

    }
}