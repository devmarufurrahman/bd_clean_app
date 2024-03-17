package com.winkytech.bdclean;

import static com.winkytech.bdclean.MainActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import java.util.Locale;
import java.util.concurrent.TimeUnit;
@SuppressLint("SetTextI18n")
public class UserEventListActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView event_list_view;
    TextView event_count,toast_message, event_name, event_location, event_type, ending_time, response_error_tv;
    Button log_out, leave_btn;
    EventListAdapter eventListAdapter;
    List<EventList> eventLists = new ArrayList<>();
    Switch language_switch;
    EventList event_list_class;
    String photoUrl = "https://bdclean.winkytech.com/resources/event/", id_event, joined_event_name, start_date, end_date, name_event, details, type, district, upazilla, photo, location_event;
    int  org_level_pos, user_id, event_counter, joined_event_flag = 0;
    String upazila_ref, union_ref, village_ref, district_ref, division_ref;
    public static final String event_count_pref = "event_count_pref";
    public static final String Contact = "contactKey";
    SharedPreferences sharedpreferences;
    CardView joined_event_details;
    String contact, joined_event_id;
//    Handler handler = new Handler();
//    Runnable runnable;
//    int delay = 90000;
    String joined_event_start_date, joined_event_end_date;
    ProgressBar progressBar;
    NetworkChangeListener networkChangeListener;
    SwipeRefreshLayout swipeRefreshLayout;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_event_list);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        sharedpreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        org_level_pos = (shared.getInt("org_level_pos",0));
        contact = (shared.getString(Contact,""));
        upazila_ref = shared.getString("upazilla_ref","");
        village_ref = shared.getString("village_ref","");
        union_ref = shared.getString("union_ref","");
        district_ref = shared.getString("district_ref","");
        division_ref = shared.getString("division_ref","");
//        user_id = shared.getInt("user_id",0);

        System.out.println("village_ref = " + village_ref + " , upazila_ref = "+upazila_ref+" , union_ref = " + union_ref + " , district_ref = "+district_ref+ " , division_ref = "+division_ref+ ", user_id = "+user_id);

        toolbar = findViewById(R.id.custom_toolbar);
        event_count = findViewById(R.id.event_count);
        event_list_view = findViewById(R.id.user_event_list);
        event_name = findViewById(R.id.event_name);
        event_type = findViewById(R.id.event_type);
        event_location = findViewById(R.id.event_location);
        log_out = findViewById(R.id.log_out);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        progressBar = findViewById(R.id.progressbar);
        response_error_tv= findViewById(R.id.listview_error_tv);
        response_error_tv.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        leave_btn = findViewById(R.id.leave_event);
        ending_time = findViewById(R.id.remaining_time);
        joined_event_details = findViewById(R.id.card_1);
        joined_event_details.setVisibility(View.GONE);
        language_switch = findViewById(R.id.language_toggle);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getUserID();
        getEventCounterData();
        getOngoingEventList();

//        handler.postDelayed(runnable = new Runnable() {
//            public void run() {
//                handler.postDelayed(runnable, delay);
//                getOngoingEventList();
//            }
//        }, delay);



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserID();
                getEventCounterData();
                getOngoingEventList();
            }
        });



        eventListAdapter = new EventListAdapter(getApplicationContext(),eventLists);
        event_list_view.setAdapter(eventListAdapter);

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserEventListActivity.this);
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
                    Intent intent=new Intent(UserEventListActivity.this, LoginActivity.class);
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


        language_switch.setChecked(false);

        language_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b){
                    setLocale("bn");
                } else {
                    setLocale("en");
                }

            }
        });


        event_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (joined_event_flag == 0){

                    if (event_counter==0){
                        Intent intent1 = new Intent(getApplicationContext(), UserInfoActivity_1.class);
                        intent1.putExtra("ev_id", eventLists.get(position).getId());
                        intent1.putExtra("cover_pic", eventLists.get(position).getPhoto());
                        intent1.putExtra("ev_name", eventLists.get(position).getName());
                        intent1.putExtra("ev_details",details);
                        intent1.putExtra("ev_address", eventLists.get(position).getLocation());
                        intent1.putExtra("date_start", eventLists.get(position).getStart_date());
                        intent1.putExtra("end_date", eventLists.get(position).getEnd_date());
                        intent1.putExtra("counter", event_counter);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
                    } else if (event_counter==1){
                        Intent intent1 = new Intent(getApplicationContext(), UserInfoActivity_2.class);
                        intent1.putExtra("ev_id", (eventLists.get(position).getId()));
                        intent1.putExtra("cover_pic", eventLists.get(position).getPhoto());
                        intent1.putExtra("ev_name", eventLists.get(position).getName());
                        intent1.putExtra("ev_details",details);
                        intent1.putExtra("ev_address", eventLists.get(position).getLocation());
                        intent1.putExtra("date_start", eventLists.get(position).getStart_date());
                        intent1.putExtra("end_date", eventLists.get(position).getEnd_date());
                        intent1.putExtra("counter", event_counter);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
                    } else if (event_counter==2){
                        Intent intent1 = new Intent(getApplicationContext(), UserInfoActivity_3.class);
                        intent1.putExtra("ev_id", eventLists.get(position).getId());
                        intent1.putExtra("cover_pic", eventLists.get(position).getPhoto());
                        intent1.putExtra("ev_name", eventLists.get(position).getName());
                        intent1.putExtra("ev_details",details);
                        intent1.putExtra("ev_address", eventLists.get(position).getLocation());
                        intent1.putExtra("date_start", eventLists.get(position).getStart_date());
                        intent1.putExtra("end_date", eventLists.get(position).getEnd_date());
                        intent1.putExtra("counter", event_counter);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
                    } else if (event_counter > 2){

                        Intent intent1 = new Intent(getApplicationContext(), NewUsersEventActivity.class);
                        intent1.putExtra("ev_id", eventLists.get(position).getId());
                        intent1.putExtra("cover_pic", eventLists.get(position).getPhoto());
                        intent1.putExtra("ev_name", eventLists.get(position).getName());
                        intent1.putExtra("ev_details",details);
                        intent1.putExtra("ev_address", eventLists.get(position).getLocation());
                        intent1.putExtra("start_date", eventLists.get(position).getStart_date());
                        intent1.putExtra("end_date", eventLists.get(position).getEnd_date());
                        intent1.putExtra("counter", event_counter);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);

                    }

                } else if (joined_event_flag == 1){

                    Toast.makeText(UserEventListActivity.this, "JOINED EVENT ID = "+ joined_event_id, Toast.LENGTH_SHORT).show();
                    int intent_flag = 1;
                    Intent intent = new Intent(getApplicationContext(), NewUsersEventActivity.class);
                    intent.putExtra("ev_id",joined_event_id);
                    intent.putExtra("ev_name",joined_event_name);
                    intent.putExtra("ev_details",details);
                    intent.putExtra("ev_address",location_event);
                    intent.putExtra("start_date",joined_event_start_date);
                    intent.putExtra("end_date",joined_event_end_date);
                    intent.putExtra("cover_pic",photo);
                    intent.putExtra("counter",event_counter);
                    startActivity(intent);
                }

            }
        });

        joined_event_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(UserEventListActivity.this, "JOINED EVENT ID = "+ joined_event_id, Toast.LENGTH_SHORT).show();
                int intent_flag = 1;
                Intent intent = new Intent(getApplicationContext(), NewUsersEventActivity.class);
                intent.putExtra("ev_id",joined_event_id);
                intent.putExtra("ev_name",joined_event_name);
                intent.putExtra("ev_details",details);
                intent.putExtra("ev_address",location_event);
                intent.putExtra("start_date",joined_event_start_date);
                intent.putExtra("end_date",joined_event_end_date);
                intent.putExtra("cover_pic",photo);
                intent.putExtra("counter",event_counter);
                startActivity(intent);
            }
        });

        leave_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentDateTime = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("E : dd MMM yyyy, ( hh:mm: a)");
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
                        Dialog dialog = new Dialog(UserEventListActivity.this);
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
                        Dialog dialog = new Dialog(UserEventListActivity.this);
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
                            getJoinedEventDetails(String.valueOf(user_id));

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

                        event_counter = event_counter+1;
                        updateCounter(event_counter);
                        showLeaveDialog();

                        progressBar.setVisibility(View.GONE);
                        getJoinedEventDetails(String.valueOf(user_id));
                        Toast toast = new Toast(UserEventListActivity.this);
                        View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout,findViewById(R.id.custom_toast));
                        toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                        toast_message.setText("EVENT LEFT");
                        toast.setView(toast_view);
                        toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();

                        //Toast.makeText(EventDetailsActivity.this, "Left Event", Toast.LENGTH_SHORT).show();

                    } else {
                        progressBar.setVisibility(View.GONE);
                        Log.i("PutData", result);
                        // Toast.makeText(EventDetailsActivity.this, "Failed To leave event!!!", Toast.LENGTH_SHORT).show();
                        getJoinedEventDetails(String.valueOf(user_id));
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

    private void showLeaveDialog() {
        Dialog dialog = new Dialog(UserEventListActivity.this);

        if(event_counter<=3){
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
            dialog.setCancelable(false);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
            dialog.show();
            Button apply_btn= dialog.findViewById(R.id.apply_btn);
            //ImageView gif= dialog.findViewById(R.id.dialog_gif);

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
                                        toast_message.setText("PROFILE UPDATED");
                                        toast.setView(toast_view);
                                        toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                                        toast.setDuration(Toast.LENGTH_SHORT);
                                        toast.show();

                                        SharedPreferences sharedpreferences =getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.clear();
                                        editor.apply();
                                        Intent intent=new Intent(UserEventListActivity.this, LoginActivity.class);
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

    private void getEventCounterData() {
        String url= "https://bdclean.winkytech.com/backend/api/getEventCounterData.php?contact="+contact;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onResponse(String response) {
                        System.out.println("counter response = " + response);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                event_counter = Integer.parseInt(object.getString("event_counter"));
                                event_count.setText(String.valueOf(event_counter) + "  Events");

                                if (event_counter == 4){
                                    Dialog dialog = new Dialog(UserEventListActivity.this);
                                    dialog.setContentView(R.layout.all_event_complete_dialog_layout);
                                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    dialog.setCancelable(false);
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
                                                                toast_message.setText("PROFILE UPDATED");
                                                                toast.setView(toast_view);
                                                                toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                                                                toast.setDuration(Toast.LENGTH_SHORT);
                                                                toast.show();

                                                                SharedPreferences sharedpreferences =getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                                                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                                                editor.clear();
                                                                editor.apply();
                                                                Intent intent=new Intent(UserEventListActivity.this, LoginActivity.class);
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
                toast_message.setText("Please check your internet connection or try again later");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

    }
    private void getOngoingEventList() {
        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getOngoingEventList.php?upazila_ref="+upazila_ref + "&org_level_pos="+org_level_pos+"&division_ref="+division_ref+"&district_ref="+district_ref+"&union_ref="+union_ref+"&village_ref="+village_ref;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        eventLists.clear();
                        System.out.println("ongoing response = " + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                 id_event = object.getString("id");
                                 name_event = object.getString("name");
                                 details = object.getString("details");
                                 start_date = object.getString("start_date");
                                 String ongoing_end_date = object.getString("end_date");
                                 location_event = object.getString("event_location");
                                 type = object.getString("type");
                                 district = object.getString("district");
                                 upazilla = object.getString("upazilla");
                                 photo = photoUrl+(object.getString("event_cover"));

                                Log.d("details1", "onResponse: "+details);

                                @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_date);
                                @SuppressLint("SimpleDateFormat") Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ongoing_end_date);

                                String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String from_date = simpleDateFormat.format(date1);
                                String to_date = simpleDateFormat.format(date2);

                                String date_status = "ONGOING";

                                event_list_class=new EventList(id_event,name_event,photo,from_date,to_date,location_event,type,district,upazilla,date_status, details);
                                eventLists.add(event_list_class);
                                eventListAdapter.notifyDataSetChanged();

                            }

                        } catch (JSONException e){
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                            response_error_tv.setVisibility(View.VISIBLE);
                            response_error_tv.setText("No event found!!");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                response_error_tv.setVisibility(View.VISIBLE);
                response_error_tv.setText("No event found!!");
                progressBar.setVisibility(View.GONE);
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText("Please check your internet connection or try again later");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

        // Signal SwipeRefreshLayout to start the progress indicator.
        swipeRefreshLayout.setRefreshing(false);
    }

    private void getJoinedEventDetails(String user_id) {
        String url= "https://bdclean.winkytech.com/backend/api/getJoinedEventData.php?user_id="+user_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onResponse(String response) {
                        //System.out.println("response = " + response);
                        String response_data = response.toString().trim();
                        Log.d("JOINED EVENT DATA = ", response_data);

                        if (response_data.equals("") || response_data.equals("null")){
                            joined_event_details.setVisibility(View.GONE);
                        } else {
                            joined_event_details.setVisibility(View.VISIBLE);
                            joined_event_flag = 1;
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject object = jsonArray.getJSONObject(0);
                                joined_event_id = object.getString("id");
                                joined_event_name = object.getString("event_name");
                                String end_date = object.getString("end_date");
                                String start_date = object.getString("start_date");
                                location_event = object.getString("event_location");
                                String type = object.getString("event_type");
                                event_name.setText(joined_event_name);
                                event_type.setText(type);
                                event_location.setText(location_event);

                                Log.d("JOINED EVENT ID", String.valueOf(joined_event_id));

                                 Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_date);
                                 Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_date);

                                String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                joined_event_start_date = simpleDateFormat.format(date1);
                                joined_event_end_date = simpleDateFormat.format(date2);

                                Date currentDate = new Date();
                                Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_date);

                                // Calculate the remaining time in milliseconds
                                long remainingTimeMillis = endDate.getTime() - currentDate.getTime();

                                // Calculate the remaining time in seconds, minutes, hours, and days
                                long seconds = remainingTimeMillis / 1000 % 60;
                                long minutes = remainingTimeMillis / (1000 * 60) % 60;
                                long hours = remainingTimeMillis / (1000 * 60 * 60) % 24;
                                long days = remainingTimeMillis / (1000 * 60 * 60 * 24);

                                // Display the remaining time
                                String remainingTime = String.format("%d days, %02d:%02d:%02d", days, hours, minutes, seconds);
                                System.out.println("Remaining time: " + remainingTime);

                                ending_time.setText(hours+" hours"+", " + minutes+ " minutes remaining");

                            } catch (JSONException | ParseException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText("Please check your internet connection or try again later");
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
                Intent intent = new Intent(UserEventListActivity.this, AboutBDCleanActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setLocale(String languageCode) {
        // Check if the selected language is different from the current language
        if (!getCurrentLanguage().equals(languageCode)) {
            // Change the app's locale based on the selected language code
            Locale locale = new Locale(languageCode);
            Configuration configuration = getResources().getConfiguration();
            configuration.setLocale(locale);
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

            // Recreate the activity
            recreate();
        }
    }

    // Helper method to get the current language code
    public  String getCurrentLanguage() {
        return getResources().getConfiguration().locale.getLanguage();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserEventListActivity.this, AboutBDCleanActivity.class);
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