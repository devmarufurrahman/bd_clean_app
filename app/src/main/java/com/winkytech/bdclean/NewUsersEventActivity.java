package com.winkytech.bdclean;

import static com.winkytech.bdclean.LoginActivity.Contact;
import static com.winkytech.bdclean.LoginActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.dynamic.IFragmentWrapper;
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

@SuppressLint({"MissingInflatedId", "SetTextI18n"})
public class NewUsersEventActivity extends AppCompatActivity {

    ImageView cover_photo;
    TextView event_name, event_address, event_date, event_status, toast_message, event_count_no, join_time, details_tv;
    Button join_btn, leave_btn, log_out;
    int event_count, user_id,event_joining_distance, ev_id;
    String contact;
    public static final String event_count_pref = "event_count_pref";
    SharedPreferences sharedpreferences;
    String cover_pic, ev_name, ev_address, start_date, end_date, ev_details;
    Toolbar toolbar;
    float distance;
    double user_latitude, user_longitude;
    double event_latitude, event_longitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    private  static final String photoUrl = "https://bdclean.winkytech.com/resources/event/";
    NetworkChangeListener networkChangeListener;
    SwipeRefreshLayout event_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_users_event);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        sharedpreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences shared = getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE);
        contact = (shared.getString(Contact,""));
        getUserID();

        Intent intent = getIntent();
        ev_id = intent.getIntExtra("ev_id", 0);
        event_count = intent.getIntExtra("counter",0);
//        ev_name = intent.getStringExtra("ev_name");
//        ev_address = intent.getStringExtra("ev_address");
//        ev_details = intent.getStringExtra("ev_details");
//        start_date = intent.getStringExtra("start_date");
//        end_date = intent.getStringExtra("end_date");
//        cover_pic = intent.getStringExtra("cover_pic");


        cover_photo = findViewById(R.id.cover_photo);
        event_name = findViewById(R.id.event_name);
        event_date = findViewById(R.id.date_time);
        event_address = findViewById(R.id.event_address);
        event_status = findViewById(R.id.status);
        join_btn = findViewById(R.id.join_btn);
        join_time = findViewById(R.id.join_time);
        leave_btn = findViewById(R.id.leave_btn);
        event_count_no = findViewById(R.id.joined_count);
//        event_tv = findViewById(R.id.event_tv);
        details_tv = findViewById(R.id.details_tv);
        log_out = findViewById(R.id.log_out);
        toolbar = findViewById(R.id.custom_toolbar);
        event_refresh = findViewById(R.id.event_refresh);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        join_btn.setEnabled(false);
        leave_btn.setEnabled(false);

        getEventDetails(ev_id);

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewUsersEventActivity.this);
                // Set the message show for the Alert time
                builder.setMessage("Do you want to logout ?");
                // Set Alert Title
                builder.setTitle("Logout!!");
                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);
                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
//                  log_out from account
                    SharedPreferences sharedpreferences =getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent=new Intent(NewUsersEventActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // If user click no then dialog box is canceled.
                    dialog.cancel();
                });
                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();
            }
        });

        getCurrentLocation();

        event_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getEventDetails(ev_id);

            }
        });

        join_btn.setOnClickListener(new View.OnClickListener() {
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

                        if (distance<=event_joining_distance){
                            Dialog dialog = new Dialog(NewUsersEventActivity.this);
                            dialog.setContentView(R.layout.joining_confirmation_dialog_layout);
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.setCancelable(true);
                            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                            Button yes_btn = dialog.findViewById(R.id.yes_btn);
                            Button no_btn = dialog.findViewById(R.id.no_btn);

                            yes_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    joinEvent();
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
                            Dialog dialog = new Dialog(NewUsersEventActivity.this);
                            dialog.setContentView(R.layout.event_warning_dialog_layout);
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.setCancelable(true);
                            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                            Button close_btn = dialog.findViewById(R.id.close_btn);
                            TextView dialog_tv = dialog.findViewById(R.id.dialog_tv);

                            dialog_tv.setText("You are  " + (distance - event_joining_distance) + "  meters away" + "from event location. Please go to event location and try again");

                            close_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                        }

                    } else {
                        Dialog dialog = new Dialog(NewUsersEventActivity.this);
                        dialog.setContentView(R.layout.event_warning_dialog_layout);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.setCancelable(true);
                        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                        Button close_btn = dialog.findViewById(R.id.close_btn);
                        TextView dialog_tv = dialog.findViewById(R.id.dialog_tv);

                        dialog_tv.setText("Sorry, You can not join this event now.");

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

        leave_btn.setOnClickListener(new View.OnClickListener() {
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

                    //String remainingTimeString = String.format("%02d hours %02d minutes %02d seconds", hours, minutes, seconds);

                    if (currentDateTime.compareTo(event_endTime) >= 0 ){
                        Dialog dialog = new Dialog(NewUsersEventActivity.this);
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
                        Dialog dialog = new Dialog(NewUsersEventActivity.this);
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

//        apply_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                applyMemberShip();
//            }
//        });


    }

    private void getEventDetails(int event_ref) {

        Log.d("event_id:", String.valueOf(event_ref));

        String url = "https://bdclean.winkytech.com/backend/api/getEventDetails.php?event_id=" + event_ref;
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
//                                String id = object.getString("id");
                                String name = object.getString("name");
                                String spec = object.getString("spec");
                                start_date = object.getString("start_date");
                                end_date = object.getString("end_date");
                                String location = object.getString("event_location");
//                                String type = object.getString("event_type");
//                                String division = object.getString("division");
//                                String district = object.getString("district");
//                                String upazilla = object.getString("upazila");
//                                String union = object.getString("union_name");
//                                String village = object.getString("village");
//                                String event_area_distance = object.getString("area_distance");
                                event_latitude = Double.parseDouble(object.getString("location_latitude"));
                                event_longitude = Double.parseDouble(object.getString("location_longitude"));
                                event_joining_distance = Integer.parseInt(object.getString("joining_distance"));
                                String photo = photoUrl + (object.getString("event_cover"));

                                @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_date);
                                @SuppressLint("SimpleDateFormat") Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_date);

                                String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String from_date = simpleDateFormat.format(date1);
                                String to_date = simpleDateFormat.format(date2);

                                SimpleDateFormat dateFormat = new SimpleDateFormat("E : dd MMM yyyy, ( hh:mm: a)");
                                Date start_time = null;
                                try {
                                    start_time = dateFormat.parse(from_date);
                                    Calendar event_start_time = Calendar.getInstance();
                                    event_start_time.setTime(start_time);

                                    Calendar final_start_time = (Calendar) event_start_time.clone();
                                    final_start_time.add(Calendar.MINUTE, -30);
                                    String joinTime = dateFormat.format(final_start_time.getTime());
                                    join_time.setText("From  "+joinTime);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }

                                event_name.setText(name);
                                event_address.setText("Address: "+location);
                                event_date.setText("Starts from : " + from_date + "\n\n" + "Ends on : " + to_date);
                                Picasso.get().load(photo).into(cover_photo);
                                event_count_no.setText(String.valueOf(event_count));
                                details_tv.setText(spec);

                            }
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {

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

        event_refresh.setRefreshing(false);

    }


    private void getUserID() {
        String url= "https://bdclean.winkytech.com/backend/api/getNewUserID.php?contact="+contact;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("EVENT STATUS RESPONSE = ", response);
                        String response_data = response.toString().trim();

                            try {

                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject object = jsonArray.getJSONObject(0);
                                user_id = Integer.parseInt(object.getString("id"));
                                System.out.println("USER ID = "+user_id);
                                getEventStatus();

                            } catch (JSONException e){
                                e.printStackTrace();
                            }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Failed to get Event List", Toast.LENGTH_LONG).show();
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

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(NewUsersEventActivity.this, Locale.getDefault());
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

    private void getEventStatus() {

        String url= "https://bdclean.winkytech.com/backend/api/getEventStatus.php?user_id="+user_id+"&event_code="+ev_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("EVENT STATUS RESPONSE = ", response);
                        String response_data = response.toString().trim();

                        if (response_data.equals("")|| response_data.equals("null")){
                            join_btn.setEnabled(true);
                            join_btn.setTextColor(getResources().getColor(R.color.white));
                            join_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00974A")));
                            leave_btn.setEnabled(false);
                            leave_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F8D7DA")));
                            leave_btn.setTextColor(getResources().getColor(R.color.red));
                            event_status.setText("Status: Not Joined yet");
                        } else {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject object = jsonArray.getJSONObject(0);
                                String join_status = object.getString("join_status");

                                if (event_count<4){

                                    switch (join_status) {
                                        case "1":
                                            join_btn.setEnabled(false);
                                            join_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4EDDA")));
                                            join_btn.setTextColor(getResources().getColor(R.color.green_1));
                                            join_btn.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#00974A")));
                                            leave_btn.setEnabled(true);
                                            leave_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EB1C24")));
                                            leave_btn.setTextColor(getResources().getColor(R.color.white));
                                            event_status.setText("Status: Event Joined");
                                            event_status.setTextColor(Color.parseColor("#EB1C24"));
                                            break;
                                        case "2":
                                            join_btn.setEnabled(false);
                                            leave_btn.setEnabled(false);
                                            join_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4EDDA")));
                                            join_btn.setTextColor(getResources().getColor(R.color.green_1));
                                            leave_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F8D7DA")));
                                            leave_btn.setTextColor(getResources().getColor(R.color.red));
                                            event_status.setText("Status: Completed");
                                            event_status.setTextColor(Color.parseColor("#3865a3"));
                                            break;
                                    }

                                } else {
                                    join_btn.setEnabled(false);
                                    leave_btn.setEnabled(true);
                                    join_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4EDDA")));
                                    join_btn.setTextColor(getResources().getColor(R.color.green_1));
                                    leave_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EB1C24")));
                                    leave_btn.setTextColor(getResources().getColor(R.color.white));
                                    event_status.setText("you have completed 4 events. please apply for membership");
                                    event_status.setTextColor(Color.parseColor("#3865a3"));
                                }

                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Failed to get Event List", Toast.LENGTH_LONG).show();
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

    private void joinEvent() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {
                //Starting Write and Read data with URL
                //Creating array for parameters
                String[] field = new String[3];
                field[0] = "user_id";
                field[1] = "event_id";
                field[2] = "user_type";

                //Creating array for data
                String[] data = new String[3];
                data[0] = String.valueOf(user_id);
                data[1] = String.valueOf(ev_id);
                data[2] = "user";

                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/joinEvent.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();

                        Log.d("Joining result", result);

                        if (result.equals("Event Joined")) {
                            event_status.setText("EVENT JOINED");
                            showJoinDialog();
                            join_btn.setEnabled(false);
                            join_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4EDDA")));
                            join_btn.setTextColor(getResources().getColor(R.color.green_1));
                            join_btn.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#00974A")));
                            leave_btn.setEnabled(true);
                            leave_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EB1C24")));
                            leave_btn.setTextColor(getResources().getColor(R.color.white));
                            event_status.setText("Event Joined");
                            event_status.setTextColor(Color.parseColor("#EB1C24"));

                        } else {
                            Log.i("PutData", result);
                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Failed To join event!!!");
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

    private void showJoinDialog() {
        Dialog dialog = new Dialog(NewUsersEventActivity.this);
        dialog.setContentView(R.layout.join_even_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        Button continue_btn= dialog.findViewById(R.id.continue_btn);

        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void leaveEvent() {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {
                //Starting Write and Read data with URL
                //Creating array for parameters
                String[] field = new String[2];
                field[0] = "user_id";
                field[1] = "event_id";

                //Creating array for data
                String[] data = new String[2];
                data[0] = String.valueOf(user_id);
                data[1] = String.valueOf(ev_id);

                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/leaveEvent.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();
                        if (result.equals("Left Event")) {


                            getEventStatus();
                            event_count = event_count+1;
                            updateCounter(event_count);
                            showLeaveDialog();

                            event_status.setText("EVENT COMPLETED");
                            Toast toast = new Toast(NewUsersEventActivity.this);
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout,findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Event left");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();

                        } else {

                            Log.i("PutData", result);
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

    private void showLeaveDialog() {
        Dialog dialog = new Dialog(NewUsersEventActivity.this);

        if(event_count<=3){
            dialog.setContentView(R.layout.leave_event_dialog_layout);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(true);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
            dialog.show();
            Button yes_btn= dialog.findViewById(R.id.yes_btn);
            Button no_btn= dialog.findViewById(R.id.no_btn);

            yes_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),UserEventListActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.no_anim,R.anim.push_right_in);
                    finish();
                    dialog.dismiss();
                }
            });

            no_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        } else {
            dialog.setContentView(R.layout.all_event_complete_dialog_layout);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(true);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
            dialog.show();
            Button apply_btn= dialog.findViewById(R.id.apply_btn);
            ImageView gif= dialog.findViewById(R.id.dialog_gif);

            apply_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String url= "https://bdclean.winkytech.com/backend/api/updateUserDesignation.php?contact="+contact;
                    StringRequest request = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    Log.d("MEMBERSHIP RESPONSE = ", response);
                                    String response_data = response.toString().trim();
                                    if (response_data.equals("updated")){

                                        Toast toast = new Toast(getApplicationContext());
                                        View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout,findViewById(R.id.custom_toast));
                                        toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                                        toast_message.setText("PROFILE UPDATES");
                                        toast.setView(toast_view);
                                        toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                                        toast.setDuration(Toast.LENGTH_SHORT);
                                        toast.show();

                                        SharedPreferences sharedpreferences =getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.clear();
                                        editor.apply();
                                        Intent intent=new Intent(NewUsersEventActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                        dialog.dismiss();

                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Toast.makeText(getApplicationContext(), "Failed to get Event List", Toast.LENGTH_LONG).show();
                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Failed to update profile");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(request);

                }
            });
        }

    }

    private void updateCounter(int count) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {
                //Starting Write and Read data with URL
                //Creating array for parameters
                String[] field = new String[2];
                field[0] = "contact";
                field[1] = "counter";

                //Creating array for data
                String[] data = new String[2];
                data[0] = String.valueOf(contact);
                data[1] = String.valueOf(count);

                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/updateEventCounter.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();
                        if (result.equals("success")) {
                            Log.d("UPDATE COUNTER", result);
                            //Toast.makeText(EventDetailsActivity.this, "Left Event", Toast.LENGTH_SHORT).show();

                        } else {

                            Log.i("PutData", result);
                            // Toast.makeText(EventDetailsActivity.this, "Failed To leave event!!!", Toast.LENGTH_SHORT).show();

                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Failed To update counter!!!");
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(),AboutBDCleanActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),AboutBDCleanActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister broadcast receiver when activity is destroyed
        unregisterReceiver(networkChangeListener);
    }
}