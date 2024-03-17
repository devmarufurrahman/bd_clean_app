package com.winkytech.bdclean;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

@SuppressLint({"ResourceAsColor", "UseCompatLoadingForDrawables", "SetTextI18n"})
public class HomeActivity extends AppCompatActivity {
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ImageView user_photo, notification, messenger, status_iv;
    String user_contact,user_id = "", user_name="", designation = "", profile_photo = "";
    TextView profile_name,profile_position,toast_message, notification_counter, message_counter , profile_id, user_address;
    private final String photoUrl = "https://bdclean.winkytech.com/resources/user/profile_pic/";
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Contact = "contactKey";
    int org_level_pos = 0,upazila_ref = 0, event_count = 0, evaluation_count = 0, message_count = 0;
    int not_count = 0, post_count,completed_event_count;
    LinearLayout manage_post, layout_2, activity_log, my_activity, manage_events, member_status, chat_button,social_media, voting, complain, evaluation, events, member_layout, chat_layout, message_layout, notice_layout, manage_notice_layout;
    Switch language_switch ;

    SharedPreferences sharedpreferences;
    RequestQueue requestQueue;
    NetworkChangeListener networkChangeListener;
    NotificationUpdateReceiver receiver = new NotificationUpdateReceiver();
    CardView notification_layout, messenger_layout;
    BottomNavigationView bottomNavigationView;
    private boolean userTriggeredLanguageChange = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        IntentFilter notification_filter = new IntentFilter(NotificationUpdateReceiver.ACTION_UPDATE_NOTIFICATION_COUNT);
//        registerReceiver(receiver, notification_filter);   for android 14 update
        registerReceiver(receiver, notification_filter, RECEIVER_EXPORTED);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

//        handler.postDelayed(runnable = new Runnable() {
//            public void run() {
//                handler.postDelayed(runnable, delay);
//                getNotificationData();
//            }
//        }, delay);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_contact = (shared.getString(Contact, ""));
        user_id = shared.getString("user_id","");
        profile_photo = photoUrl+(shared.getString("profile_photo",""));
        org_level_pos = Integer.parseInt(shared.getString("org_level_ref","0"));
        user_name = shared.getString("name","");
        upazila_ref = Integer.parseInt(shared.getString("upazila_ref","0"));
        designation = shared.getString("designation","");
        not_count = shared.getInt("not_count",0);
        String division =shared.getString("division","");
        String district =shared.getString("district","");
        String upazila =shared.getString("upazila","");
        String union =shared.getString("union","");
        String village =shared.getString("village","");
        String member_code =shared.getString("member_code","");
        System.out.println("ORG LEVEL POSITION = " + org_level_pos);
        System.out.println("event_count = " + event_count);
        System.out.println("evaluation_count" + evaluation_count);
        System.out.println("message_count" + message_count);

        toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        navigationView= findViewById(R.id.nav_menu);
        drawerLayout= findViewById(R.id.drawer);
        user_address = findViewById(R.id.user_address);
        profile_id = findViewById(R.id.profile_id);
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.languageToggle);
        language_switch = menuItem.getActionView().findViewById(R.id.language_toggle);
        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        bottomNavigationView = findViewById(R.id.bottomNavView);

        bottomNavigationView.setItemIconTintList(null);
        navigationView.setItemIconTintList(null);

        events=findViewById(R.id.event_btn);
        evaluation=findViewById(R.id.evaluation_btn);
        complain=findViewById(R.id.complain_btn);
        voting=findViewById(R.id.vote_btn);
        profile_name = findViewById(R.id.profile_name);
        user_photo = findViewById(R.id.user_photo);
        profile_position = findViewById(R.id.profile_level);
        social_media=findViewById(R.id.social_media_btn);
        member_status=findViewById(R.id.member_status);
        member_layout = findViewById(R.id.member_layout);
        chat_layout = findViewById(R.id.chat_layout);
        manage_events= findViewById(R.id.event_mng_layout);
        message_layout = findViewById(R.id.message_layout);
        my_activity = findViewById(R.id.my_activity);
        manage_post = findViewById(R.id.post_mng_layout);
        notification = findViewById(R.id.notificationImg);
        messenger = findViewById(R.id.messengerImg);
        notification_counter = findViewById(R.id.counterText);
        message_counter = findViewById(R.id.messengerCounterText);
        status_iv = findViewById(R.id.status_iv);
        notification_layout = findViewById(R.id.notificationCountCard);
        messenger_layout = findViewById(R.id.messengerCountCard);
        activity_log = findViewById(R.id.activity_log);
        notice_layout = findViewById(R.id.notice_btn);
        manage_notice_layout = findViewById(R.id.manage_notice_btn);
        layout_2 = findViewById(R.id.layout_2);

//        language_switch.setChecked(false);
        if (language_switch != null) {
            language_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        setLocale("bn");
                        drawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        setLocale("en");
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                }
            });
        }

        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        serviceIntent.putExtra("user_id", user_id);
        startForegroundService(serviceIntent);
        foregroundServiceRunning();
        getStatusData();

        switch (org_level_pos){
            case 1 :
                member_layout.setVisibility(View.VISIBLE);
                //message_layout.setVisibility(View.VISIBLE);
                manage_events.setVisibility(View.GONE);
                manage_post.setVisibility(View.GONE);
                voting.setVisibility(View.GONE);
                break;

                case 13:
                    layout_2.setVisibility(View.GONE);
                    member_layout.setVisibility(View.GONE);
                    voting.setVisibility(View.GONE);
                break;

            default:
                member_layout.setVisibility(View.GONE);
                //message_layout.setVisibility(View.GONE);
                voting.setVisibility(View.GONE);

        }

        Picasso.get().load(profile_photo).into(user_photo);
        profile_name.setText(user_name);
        profile_position.setText(designation);
        profile_id.setText(member_code);
        user_address.setText(division +" , "+district+" , "+upazila+" , "+union+" , "+village);

        user_photo.setOnClickListener(v -> {
            Intent profile  = new Intent(HomeActivity.this, PersonalProfileActivity.class);
            profile.putExtra("contact", user_contact);
            profile.putExtra("total_post", post_count);
            profile.putExtra("total_event", completed_event_count);
            profile.putExtra("code", member_code);
            startActivity(profile);
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.main_menu_about:
                        Intent runAbout = new Intent(HomeActivity.this,AboutActivity.class);
                        startActivity(runAbout);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.main_menu_logout:

                        SharedPreferences sharedpreferences =getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.clear();
                        editor.apply();
                        Intent intent=new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        finish();
                        break;

                    case R.id.main_menu_personal_profile:
                        Intent profile  = new Intent(HomeActivity.this, PersonalProfileActivity.class);
                        profile.putExtra("contact",user_contact);
                        profile.putExtra("total_post",post_count);
                        profile.putExtra("total_event",completed_event_count);
                        profile.putExtra("code", member_code);
                        startActivity(profile);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;


//                    case R.id.main_menu_member_profile:
//
//                        Toast toast = new Toast(getApplicationContext());
//                        View toast_view = getLayoutInflater().inflate(R.layout.custom_warning_dialog_layout,findViewById(R.id.custom_toast));
//                        toast_message=toast_view.findViewById(R.id.custom_toast_tv);
//                        toast_message.setText("Coming Soon");
//                        toast.setView(toast_view);
//                        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.FILL_HORIZONTAL,0,110);
//                        toast.setDuration(Toast.LENGTH_SHORT);
//                        toast.show();
//
////                        Intent history  = new Intent(HomeActivity.this, MemberProfileActivity.class);
////                        drawerLayout.closeDrawer(GravityCompat.START);
////                        history.putExtra("user_id",user_id);
////                        startActivity(history);
//                        break;

                    default:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
                return true;
            }
        });


        // bottom navigation view
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.homeBottom){

                    startActivity(new Intent(HomeActivity.this, HomeActivity.class ));
                    finish();
                    overridePendingTransition(0,0);
                } else if (id == R.id.eventBottom) {
                    startActivity(new Intent(HomeActivity.this, MyActivity.class));
                } else if (id == R.id.socialMediaBottom) {
//                    startActivity(new Intent(HomeActivity.this, Store.class));
                    Toast.makeText(HomeActivity.this, "Store", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.profileBottom) {
//                    startActivity(new Intent(HomeActivity.this, PersonalProfileActivity.class));
                    Toast.makeText(HomeActivity.this, "Admin", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });



        status_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.member_status_dialog_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);
                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                Button close_btn = dialog.findViewById(R.id.close_btn);
                TextView event_count_tv = dialog.findViewById(R.id.event_count);
                TextView post_count_tv = dialog.findViewById(R.id.post_count);
                TextView member_status = dialog.findViewById(R.id.member_status);

                event_count_tv.setText(String.valueOf(completed_event_count));
                post_count_tv.setText(String.valueOf(post_count));

                if (completed_event_count>= 4 && post_count>=16){
                    member_status.setText("Active Member");
                    member_status.setTextColor(getResources().getColor(R.color.green_1));
                } else if (completed_event_count>=3 && post_count>=12){
                    member_status.setText("Regular Member");
                    member_status.setTextColor(Color.parseColor("#0000FF"));

                } else if (completed_event_count>=2 && post_count>=8){
                    member_status.setText("Irregular Member");
                    member_status.setTextColor(Color.parseColor("#FFFF00"));

                } else if (completed_event_count>=1 && post_count>=4){

                    member_status.setText("Infrequent Member");
                    member_status.setTextColor(Color.parseColor("#FFA500"));

                } else if (completed_event_count>=0 && post_count>=0){

                    member_status.setText("Inactive Member");
                    member_status.setTextColor(Color.parseColor("#FF0000"));
                }

                close_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        manage_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ManagePostActivity.class);
                startActivity(intent);
            }
        });

        manage_notice_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, NoticeManageActivity.class);
                startActivity(intent);
            }
        });

        notice_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, NoticeActivity.class);
                startActivity(intent);
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        messenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, Messenger.class);
                startActivity(intent);
                messenger_layout.setVisibility(View.GONE);
            }
        });

        my_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),MyActivity.class);
                startActivity(intent);
            }
        });

        activity_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),UserActivityLog.class);
                startActivity(intent);
            }
        });

        member_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(getApplicationContext(), MemberStatusActivity.class);
                intent1.putExtra("user_id",user_id);
                startActivity(intent1);
            }
        });

        manage_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (org_level_pos==0){
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText("You have to be a member of BD Clean to view this option");
                toast.setView(toast_view);
                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                } else {
                    Intent intent1 = new Intent(getApplicationContext(), EventManageActivity.class);
                    intent1.putExtra("user_id",user_id);
                    intent1.putExtra("upazilla_ref",upazila_ref);
                    intent1.putExtra("intent_flag",1);
                    startActivity(intent1);
                }

            }
        });

        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(HomeActivity.this,EventActivity.class);
                intent.putExtra("user_id",user_id);
                intent.putExtra("org_level_pos",org_level_pos);
                intent.putExtra("upazila_ref",upazila_ref);
                startActivity(intent);
            }
        });

        message_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (org_level_pos==0){
                    Toast toast = new Toast(getApplicationContext());
                    View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                    toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                    toast_message.setText("You have to be a member of BD Clean to view this option");
                    toast.setView(toast_view);
                    toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.FILL_HORIZONTAL,0,110);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Intent intent = new Intent(HomeActivity.this,MessageActivity.class);
                    intent.putExtra("intent_flag", 0);
                    intent.putExtra("user_id",user_id);
                    intent.putExtra("org_level_pos",org_level_pos);
                    startActivity(intent);
                }
            }
        });

        evaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (org_level_pos==0){
                    Toast toast = new Toast(getApplicationContext());
                    View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                    toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                    toast_message.setText("You have to be a member of BD Clean to view this option");
                    toast.setView(toast_view);
                    toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.FILL_HORIZONTAL,0,110);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                } else {

                    Intent intent = new Intent(HomeActivity.this,EvaluationActivity.class);
                    intent.putExtra("user_id",user_id);
                    intent.putExtra("org_level_pos",org_level_pos);
                    startActivity(intent);
                }
            }
        });


        complain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (org_level_pos==0){
                    Toast toast = new Toast(getApplicationContext());
                    View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                    toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                    toast_message.setText("You have to be a member of BD Clean to view this option");
                    toast.setView(toast_view);
                    toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.FILL_HORIZONTAL,0,110);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                } else {

                    Intent intent = new Intent(HomeActivity.this,ComplainActivity.class);
                    intent.putExtra("user_id",user_id);
                    intent.putExtra("org_level_pos",org_level_pos);
                    startActivity(intent);
                }


            }
        });

        member_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (org_level_pos==0){
                    Toast toast = new Toast(getApplicationContext());
                    View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                    toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                    toast_message.setText("You have to be a member of BD Clean to view this option");
                    toast.setView(toast_view);
                    toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.FILL_HORIZONTAL,0,110);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                } else {

                    Intent intent = new Intent(HomeActivity.this,MemberActivity.class);
                    intent.putExtra("user_id",user_id);
                    intent.putExtra("org_level_pos",org_level_pos);
                    startActivity(intent);
                }

            }
        });

        voting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, PollingActivity.class);
                startActivity(intent);

            }
        });

        social_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),SocialMediaActivity.class);
                intent.putExtra("user_id",user_id);
                startActivity(intent);
            }
        });

        chat_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (org_level_pos==0){
                    Toast toast = new Toast(getApplicationContext());
                    View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                    toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                    toast_message.setText("You have to be a member of BD Clean to view this option");
                    toast.setView(toast_view);
                    toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.FILL_HORIZONTAL,0,110);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                } else {

                    Intent intent1 = new Intent(getApplicationContext(),ChatActivity.class);
                    intent1.putExtra("user_id",user_id);
                    startActivity(intent1);
                }

            }
        });
    }

//    private void getNotificationData() {
//        String url= "https://bdclean.winkytech.com/backend/api/getNotificationCount.php?user_ref="+user_id;
//        StringRequest request = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @SuppressLint("ResourceAsColor")
//                    @Override
//                    public void onResponse(String response) {
//                        System.out.println("Notification_counter = " + response);
//                        try {
//                            JSONArray jsonArray = new JSONArray(response);
//                            JSONObject object = jsonArray.getJSONObject(0);
//                            int notification_count = Integer.parseInt(object.getString("notifi_count"));
//
//                            if (notification_count !=0){
//                                notification_counter.setText(String.valueOf(notification_count));
//                                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
//
//                                PendingIntent pendingIntent = PendingIntent.getActivity(HomeActivity.this, 2, intent, PendingIntent.FLAG_IMMUTABLE);
//                                NotificationChannel channel = new NotificationChannel("act_notification", "act_notification", NotificationManager.IMPORTANCE_HIGH);
//                                NotificationManager manager = getSystemService(NotificationManager.class);
//                                manager.createNotificationChannel(channel);
//                                NotificationCompat.Builder builder = new NotificationCompat.Builder(HomeActivity.this, "act_notification");
//                                builder.setContentTitle("New Notification");
//                                builder.setContentText("Tap to see what's new");
//                                builder.setSmallIcon(R.drawable.bd_clean_logo);
//                                builder.setContentIntent(pendingIntent);
//                                builder.setAutoCancel(true);
//                                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(HomeActivity.this);
//                                managerCompat.notify(2, builder.build());
//
////                                Dialog dialog = new Dialog(HomeActivity.this);
////                                dialog.setContentView(R.layout.event_warning_dialog_layout);
////                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////                                dialog.setCancelable(true);
////                                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
////                                Button close_btn = dialog.findViewById(R.id.close_btn);
////                                TextView dialog_tv = dialog.findViewById(R.id.dialog_tv);
////
////                                dialog.show();
//
//                                updateNotificationCount();
//                            } else {
//                                notification_counter.setVisibility(View.GONE);
//                            }
//
//                        } catch (JSONException e){
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (error != null){
//                    Log.e("NOTIFICATION", error.getMessage());
//                } else {
//                    Log.e("NOTIFICATION", "NETWORK ERROR. PLEASE CHECK CONNECTION");
//                }
//
//            }
//        });
//
//        requestQueue = Volley.newRequestQueue(getApplicationContext());
//        requestQueue.add(request);
//    }
//
//    private void updateNotificationCount() {
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//            @SuppressLint("ResourceAsColor")
//            @Override
//            public void run() {
//                String[] field = new String[1];
//                field[0] = "user_id";
//                //Creating array for data
//                String[] data = new String[1];
//                data[0] = String.valueOf(user_id);
//                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/updateNotificationFlag.php", "POST", field, data);
//                if (putData.startPut()) {
//                    if (putData.onComplete()) {
//                        String result = putData.getResult().trim();
//                        if (result.equals("updated")) {
//                            System.out.println("NOTIFICATION FLAG = UPDATED" );
//                        } else {
//                            Log.i("PutData", result);
//
//                        }
//                    }
//                }
//            }
//        });
//    }



    private void getStatusData() {

        String url= "https://bdclean.winkytech.com/backend/api/getMemberStatus.php?user_id="+user_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("response = " + response);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                completed_event_count = Integer.parseInt(object.getString("join_count"));
                                post_count = Integer.parseInt(object.getString("post_count"));

                                if (completed_event_count>= 4 && post_count>=16){
                                    status_iv.setImageDrawable(getResources().getDrawable(R.drawable.active_status));
                                } else if (completed_event_count>=3 && post_count>=12){
                                    status_iv.setImageDrawable(getResources().getDrawable(R.drawable.regular_status));

                                } else if (completed_event_count>=2 && post_count>=8){
                                    status_iv.setImageDrawable(getResources().getDrawable(R.drawable.irregular_status));


                                } else if (completed_event_count>=1 && post_count>=4){

                                    status_iv.setImageDrawable(getResources().getDrawable(R.drawable.infrequent_status));

                                } else if (completed_event_count>=0 && post_count>=0){

                                    status_iv.setImageDrawable(getResources().getDrawable(R.drawable.inactive_status));

                                }
                            }

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText("FAILED TO GET USER STATUS");
                toast.setView(toast_view);
                toast.setGravity(Gravity.CENTER_VERTICAL| Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    public boolean foregroundServiceRunning(){

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){

            if (MyForegroundService.class.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister broadcast receiver when activity is destroyed
        unregisterReceiver(networkChangeListener);
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("WARNING")
                .setMessage("Are you sure you want to close the app?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(101);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public class NotificationUpdateReceiver extends BroadcastReceiver {
        public static final String ACTION_UPDATE_NOTIFICATION_COUNT = "com.winkytech.bdclean.UPDATE_NOTIFICATION_COUNT";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_UPDATE_NOTIFICATION_COUNT)) {
                int notificationCount = intent.getIntExtra("read_count", 0);
                int activity_type = intent.getIntExtra("activity_type", 0);
                Log.d("NOTIFICATION DATA RECEIVED", String.valueOf(notificationCount));
                // Update your TextView with the new notification count

                if (activity_type == 14 || activity_type == 15){
                    if (notificationCount > 0){
                        messenger_layout.setVisibility(View.VISIBLE);
                        message_counter.setText(String.valueOf(notificationCount));
                    } else {
                        messenger_layout.setVisibility(View.GONE);
                    }
                } else {

                    if (notificationCount > 0){
                        notification_layout.setVisibility(View.VISIBLE);
                        notification_counter.setText(String.valueOf(notificationCount));
                    } else {
                        notification_layout.setVisibility(View.GONE);
                    }
                }


            }
        }
    }

    private void setLocale(String languageCode) {
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
    private String getCurrentLanguage() {
        return getResources().getConfiguration().locale.getLanguage();
    }
}