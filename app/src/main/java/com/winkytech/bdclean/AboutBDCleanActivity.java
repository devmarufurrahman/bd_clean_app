package com.winkytech.bdclean;

import static com.winkytech.bdclean.MainActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;
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

public class AboutBDCleanActivity extends AppCompatActivity {

    Button member_btn, honorable_member,goodwill_ambassador, log_out,  honorable_btn_2, goodwill_btn_2, continue_user_btn, leave_event;
    TextView toast_message, event_location, event_type, event_name, ending_time;
    ImageView about_cover_photo;
    public static final String Contact = "contactKey";
    SharedPreferences sharedpreferences;
    String contact, joined_event_name, joined_event_start_date, joined_event_end_date, location_event;
    int event_counter = 0, user_id, joined_event_flag = 0, joined_event_id;
    Switch language_switch;
    String upazila_ref, union_ref, village_ref, division_ref, district_ref, fb_link, occupation, date_of_birth;
    int checkBox_flag = 0, org_level_pos, terms_condition_flag = 0;
    String photoUrl = "https://bdclean.winkytech.com/resources/event/";
    EventList event_list_class;
    SliderAdapter sliderAdapter;
    ArrayList<EventList> eventLists = new ArrayList<>();
    LinearLayout member_type_layout, member_type_layout_2, member_btn_layout;
    CardView joined_event_details;
    SliderView sliderView ;
    SwipeRefreshLayout about_refresh;
    ScrollView about_scrollV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_bdclean);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        sharedpreferences=getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        contact = (shared.getString(Contact,""));
        terms_condition_flag = shared.getInt("terms_condition_flag",0);
        upazila_ref = shared.getString("upazilla_ref","null");
        village_ref = shared.getString("village_ref","null");
        union_ref = shared.getString("union_ref","null");
        district_ref = shared.getString("district_ref","null");
        division_ref = shared.getString("division_ref","null");
        String user_type = (shared.getString("user_type", ""));
        String designation = (shared.getString("designation", ""));

        language_switch = findViewById(R.id.language_toggle);
        about_cover_photo = findViewById(R.id.about_cover_photo);
        sliderView =  findViewById(R.id.event_slider_layout);

        fb_link = (shared.getString("fb_link", ""));
        date_of_birth = (shared.getString("date_of_birth", ""));
        occupation = (shared.getString("occupation", ""));

        member_btn = findViewById(R.id.member_btn);
        honorable_member = findViewById(R.id.honorary_member);
        log_out = findViewById(R.id.log_out);
        goodwill_ambassador = findViewById(R.id.goodwill);
        honorable_btn_2 = findViewById(R.id.honorary_member2);
        goodwill_btn_2 = findViewById(R.id.goodwill2);
        member_type_layout = findViewById(R.id.member_type_layout);
        member_type_layout_2 = findViewById(R.id.member_type_layout_2);
        continue_user_btn = findViewById(R.id.continue_user_btn);
        member_btn_layout = findViewById(R.id.member_btn_layout);
        ending_time = findViewById(R.id.remaining_time);
        event_name = findViewById(R.id.event_name);
        event_type = findViewById(R.id.event_type);
        event_location = findViewById(R.id.event_location);
        joined_event_details = findViewById(R.id.joined_event_layout);
        about_refresh = findViewById(R.id.about_refresh);
        leave_event = findViewById(R.id.leave_event);
        about_scrollV = findViewById(R.id.scrollV);
        joined_event_details.setVisibility(View.GONE);

        getUserID();

        Log.d("Division Data = ", division_ref + " & terms condition_flag = "+terms_condition_flag);

//        if ((division_ref.equals("") || division_ref.equals("null") && terms_condition_flag == 0) ){
//
//            member_type_layout.setVisibility(View.VISIBLE);
//            member_type_layout_2.setVisibility(View.VISIBLE);
//            member_btn_layout.setVisibility(View.VISIBLE);
//            continue_user_btn.setVisibility(View.GONE);
//            about_cover_photo.setVisibility(View.VISIBLE);
//            sliderView.setVisibility(View.GONE);
//
////            if ((!division_ref.equals("") && !division_ref.equals("null"))){
////
////                member_type_layout.setVisibility(View.VISIBLE);
////                member_type_layout_2.setVisibility(View.VISIBLE);
////                member_btn_layout.setVisibility(View.VISIBLE);
////                continue_user_btn.setVisibility(View.GONE);
////                about_cover_photo.setVisibility(View.VISIBLE);
////                sliderView.setVisibility(View.GONE);
////
////            } else {
////
////                member_type_layout.setVisibility(View.GONE);
////                member_type_layout_2.setVisibility(View.GONE);
////                member_btn_layout.setVisibility(View.GONE);
////                continue_user_btn.setVisibility(View.VISIBLE);
////                about_cover_photo.setVisibility(View.GONE);
////                sliderView.setVisibility(View.VISIBLE);
////            }
//
//
//        } else {
//
//            if (terms_condition_flag == 1){
//
//                member_type_layout.setVisibility(View.GONE);
//                member_type_layout_2.setVisibility(View.GONE);
//                member_btn_layout.setVisibility(View.GONE);
//                continue_user_btn.setVisibility(View.GONE);
//                about_cover_photo.setVisibility(View.GONE);
//                sliderView.setVisibility(View.VISIBLE);
//
//            } else {
//
//                member_type_layout.setVisibility(View.GONE);
//                member_type_layout_2.setVisibility(View.GONE);
//                member_btn_layout.setVisibility(View.GONE);
//                continue_user_btn.setVisibility(View.VISIBLE);
//                about_cover_photo.setVisibility(View.GONE);
//                sliderView.setVisibility(View.GONE);
//            }
////
////            member_type_layout.setVisibility(View.GONE);
////            member_type_layout_2.setVisibility(View.GONE);
////            member_btn_layout.setVisibility(View.GONE);
////            continue_user_btn.setVisibility(View.VISIBLE);
////            about_cover_photo.setVisibility(View.VISIBLE);
////            sliderView.setVisibility(View.GONE);
//        }

        if (terms_condition_flag == 0 && division_ref.equals("null")){

            member_type_layout.setVisibility(View.VISIBLE);
            member_type_layout_2.setVisibility(View.VISIBLE);
            member_btn_layout.setVisibility(View.VISIBLE);
            continue_user_btn.setVisibility(View.GONE);
            about_cover_photo.setVisibility(View.VISIBLE);
            sliderView.setVisibility(View.GONE);

        } else if (terms_condition_flag == 1 && division_ref.equals("null")){

            member_type_layout.setVisibility(View.GONE);
            member_type_layout_2.setVisibility(View.GONE);
            member_btn_layout.setVisibility(View.GONE);
            continue_user_btn.setVisibility(View.VISIBLE);
            about_cover_photo.setVisibility(View.VISIBLE);
            sliderView.setVisibility(View.GONE);

        } else {

            member_type_layout.setVisibility(View.GONE);
            member_type_layout_2.setVisibility(View.GONE);
            member_btn_layout.setVisibility(View.GONE);
            continue_user_btn.setVisibility(View.GONE);
            about_cover_photo.setVisibility(View.GONE);
            sliderView.setVisibility(View.VISIBLE);
        }

        about_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
                sharedpreferences=getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                contact = (shared.getString(Contact,""));
                terms_condition_flag = shared.getInt("terms_condition_flag",0);
                upazila_ref = shared.getString("upazilla_ref","null");
                village_ref = shared.getString("village_ref","null");
                union_ref = shared.getString("union_ref","null");
                district_ref = shared.getString("district_ref","null");
                division_ref = shared.getString("division_ref","null");

                if (terms_condition_flag == 0 && division_ref.equals("null")){

                    member_type_layout.setVisibility(View.VISIBLE);
                    member_type_layout_2.setVisibility(View.VISIBLE);
                    member_btn_layout.setVisibility(View.VISIBLE);
                    continue_user_btn.setVisibility(View.GONE);
                    about_cover_photo.setVisibility(View.VISIBLE);
                    sliderView.setVisibility(View.GONE);

                } else if (terms_condition_flag == 1 && division_ref.equals("null")){

                    member_type_layout.setVisibility(View.GONE);
                    member_type_layout_2.setVisibility(View.GONE);
                    member_btn_layout.setVisibility(View.GONE);
                    continue_user_btn.setVisibility(View.VISIBLE);
                    about_cover_photo.setVisibility(View.VISIBLE);
                    sliderView.setVisibility(View.GONE);

                } else {

                    member_type_layout.setVisibility(View.GONE);
                    member_type_layout_2.setVisibility(View.GONE);
                    member_btn_layout.setVisibility(View.GONE);
                    continue_user_btn.setVisibility(View.GONE);
                    about_cover_photo.setVisibility(View.GONE);
                    sliderView.setVisibility(View.VISIBLE);
                }

                getUserID();

            }
        });

        about_refresh.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {

                about_scrollV.canScrollVertically(-1);
                return false;
            }
        });

        sliderView.setIndicatorEnabled(true);
        sliderView.setIndicatorVisibility(true);
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(false);
        sliderView.startAutoCycle();
//        getUserId();

        sliderAdapter = new SliderAdapter(this, eventLists);
        sliderView.setSliderAdapter(sliderAdapter);

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

        continue_user_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),UserLocationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);

            }
        });

        joined_event_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "JOINED EVENT ID = "+ joined_event_id, Toast.LENGTH_SHORT).show();
                int intent_flag = 1;
                Intent intent = new Intent(getApplicationContext(), NewUsersEventActivity.class);
                intent.putExtra("ev_id",joined_event_id);
                intent.putExtra("counter",event_counter);
                startActivity(intent);

            }
        });

        leave_event.setOnClickListener(new View.OnClickListener() {
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
                        Dialog dialog = new Dialog(AboutBDCleanActivity.this);
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
                        Dialog dialog = new Dialog(AboutBDCleanActivity.this);
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

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AboutBDCleanActivity.this);
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
                    Intent intent=new Intent(AboutBDCleanActivity.this, LoginActivity.class);
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

        member_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(AboutBDCleanActivity.this);
                dialog.setContentView(R.layout.terms_condition_dialog_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);
                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                Button continue_btn= dialog.findViewById(R.id.continue_btn);

                CheckBox terms_check_box = dialog.findViewById(R.id.terms_check_box);

                terms_check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            // Checkbox is checked
                            checkBox_flag = 1;

                        } else {
                            // Checkbox is unchecked
                            checkBox_flag = 0;
                        }
                    }
                });

                continue_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (checkBox_flag == 1){

                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putInt("terms_condition_flag", checkBox_flag);
                            editor.apply();

                            Intent intent = new Intent(getApplicationContext(),UserLocationActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
                            dialog.dismiss();

                        } else {

                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Please check the check box to confirm");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                        }

                    }
                });

                dialog.show();
            }
        });

        honorable_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),HonorableRegistration.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
            }
        });

        honorable_btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),HonorableRegistration.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
            }
        });

        goodwill_ambassador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AmbassadorRegistrationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
            }
        });

        goodwill_btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AmbassadorRegistrationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
            }
        });

    }

    private class EventList {

        String event_id, event_title, start_date, end_date, event_status, event_location, event_photo;

        public EventList(String event_id, String event_title, String start_date, String end_date, String event_status, String event_location, String event_photo) {
            this.event_id = event_id;
            this.event_title = event_title;
            this.start_date = start_date;
            this.end_date = end_date;
            this.event_status = event_status;
            this.event_location = event_location;
            this.event_photo = event_photo;
        }

        public String getEvent_photo() {
            return event_photo;
        }

        public void setEvent_photo(String event_photo) {
            this.event_photo = event_photo;
        }

        public String getEvent_id() {
            return event_id;
        }

        public void setEvent_id(String event_id) {
            this.event_id = event_id;
        }

        public String getEvent_title() {
            return event_title;
        }

        public void setEvent_title(String event_title) {
            this.event_title = event_title;
        }

        public String getStart_date() {
            return start_date;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public String getEnd_date() {
            return end_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

        public String getEvent_status() {
            return event_status;
        }

        public void setEvent_status(String event_status) {
            this.event_status = event_status;
        }

        public String getEvent_location() {
            return event_location;
        }

        public void setEvent_location(String event_location) {
            this.event_location = event_location;
        }
    }

    public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder> {

        // list for storing urls of images.
        private final List<EventList> eventLists;
        Context context;

        // Constructor
        public SliderAdapter(Context context, ArrayList<EventList> eventLists) {
            this.eventLists = eventLists;
            this.context = context;
        }

        // We are inflating the slider_layout
        // inside on Create View Holder method.
        @Override
        public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_autoslider_item_layout, null);
            return new SliderAdapterViewHolder(inflate);
        }

        // Inside on bind view holder we will
        // set data to item of Slider View.
        @Override
        public void onBindViewHolder(SliderAdapterViewHolder viewHolder, final int position) {

            final EventList event_list_data = eventLists.get(position);
            Log.d("event image = ", event_list_data.getEvent_photo());

            if (!event_list_data.getEvent_photo().equals("null") && !event_list_data.getEvent_photo().isEmpty()) {
                // Load image from URL if available
                Picasso.get().load(R.drawable.cover_photo).error(R.drawable.cover_photo).into(viewHolder.event_photo);
            } else {

                // Load default image if no image URL is provided
                Picasso.get().load(R.drawable.cover_photo).error(R.drawable.cover_photo).into(viewHolder.event_photo);
            }

            viewHolder.event_title.setText(event_list_data.getEvent_title());
            viewHolder.date.setText(event_list_data.getStart_date());

            if (position == 0){

                viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.red));

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                    Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();

                        int event_id = Integer.parseInt(eventLists.get(position).getEvent_id());
                        Toast.makeText(context, "Event ID : " + event_id + " , event counter: " + event_counter, Toast.LENGTH_SHORT).show();

                        if (joined_event_flag == 0){

                            if (event_counter==0){

                                if (fb_link.equals("") || fb_link.equals("null")){

                                    Intent intent1 = new Intent(getApplicationContext(), UserInfoActivity_1.class);
                                    intent1.putExtra("ev_id", event_id);
                                    intent1.putExtra("counter", event_counter);
                                    startActivity(intent1);
                                    overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);

                                } else {

                                    Intent intent1 = new Intent(getApplicationContext(), NewUsersEventActivity.class);
                                    intent1.putExtra("ev_id", event_id);
                                    intent1.putExtra("counter", event_counter);
                                    startActivity(intent1);
                                    overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
                                }

                            } else if (event_counter==1){

                                if (date_of_birth.equals("") || date_of_birth.equals("null")){

                                    Intent intent1 = new Intent(getApplicationContext(), UserInfoActivity_2.class);
                                    intent1.putExtra("ev_id", event_id);
                                    intent1.putExtra("counter", event_counter);
                                    startActivity(intent1);
                                    overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);

                                } else {

                                    Intent intent1 = new Intent(getApplicationContext(), NewUsersEventActivity.class);
                                    intent1.putExtra("ev_id", event_id);
                                    intent1.putExtra("counter", event_counter);
                                    startActivity(intent1);
                                    overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
                                }

                            } else if (event_counter==2){

                                if (occupation.equals("") || occupation.equals("null")){

                                    Intent intent1 = new Intent(getApplicationContext(), UserInfoActivity_3.class);
                                    intent1.putExtra("ev_id", event_id);
                                    intent1.putExtra("counter", event_counter);
                                    startActivity(intent1);
                                    overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);

                                } else {

                                    Intent intent1 = new Intent(getApplicationContext(), NewUsersEventActivity.class);
                                    intent1.putExtra("ev_id", event_id);
                                    intent1.putExtra("counter", event_counter);
                                    startActivity(intent1);
                                    overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);

                                }

                            } else if (event_counter > 2){

                                Intent intent1 = new Intent(getApplicationContext(), NewUsersEventActivity.class);
                                intent1.putExtra("ev_id", event_id);
                                intent1.putExtra("counter", event_counter);
                                startActivity(intent1);
                                overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);

                            }

                        } else if (joined_event_flag == 1){

                            Toast.makeText(context, "JOINED EVENT ID = "+ joined_event_id, Toast.LENGTH_SHORT).show();
                            int intent_flag = 1;
                            Intent intent = new Intent(getApplicationContext(), NewUsersEventActivity.class);
                            intent.putExtra("ev_id",joined_event_id);
                            intent.putExtra("counter",event_counter);
                            startActivity(intent);
                        }

                    }
                });

            } else {

                viewHolder.itemView.setClickable(false);
                viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.text_color));
            }

        }

        // this method will return
        // the count of our list.
        @Override
        public int getCount() {
            return eventLists.size();
        }

        class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {
            // Adapter class for initializing
            // the views of our slider view.
            View itemView;
            ImageView event_photo;
            TextView event_title;
            TextView date;

            public SliderAdapterViewHolder(View itemView) {
                super(itemView);
                event_photo = itemView.findViewById(R.id.auto_slider_image);
                event_title = itemView.findViewById(R.id.event_name);
                date = itemView.findViewById(R.id.event_date);
                this.itemView = itemView;
            }
        }
    }


    private void leaveEvent() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
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

                        getJoinedEventDetails(String.valueOf(user_id));
                        Toast toast = new Toast(AboutBDCleanActivity.this);
                        View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout,findViewById(R.id.custom_toast));
                        toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                        toast_message.setText("EVENT LEFT");
                        toast.setView(toast_view);
                        toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();

                        //Toast.makeText(EventDetailsActivity.this, "Left Event", Toast.LENGTH_SHORT).show();

                    } else {
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
        Dialog dialog = new Dialog(AboutBDCleanActivity.this);

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
                                        Intent intent=new Intent(AboutBDCleanActivity.this, LoginActivity.class);
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

    private void getUserID() {
        String url= "https://bdclean.winkytech.com/backend/api/getNewUserID.php?contact="+contact;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.d("User id in About BDCLEAN = ", response);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject object = jsonArray.getJSONObject(0);
                            user_id = Integer.parseInt(object.getString("id"));
                            division_ref = object.getString("division_ref");
                            district_ref = object.getString("district_ref");
                            upazila_ref = object.getString("upazila_ref");
                            union_ref = object.getString("union_ref");
                            village_ref = object.getString("village_ref");

                            if (!division_ref.equals("null")){
                                getEventList();
                            }

                            getJoinedEventDetails(String.valueOf(user_id));
                            getEventCounterData(user_id);

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
                                joined_event_id = Integer.parseInt(object.getString("id"));
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

    private void getEventList() {
        String url= "https://bdclean.winkytech.com/backend/api/getNewUserEventList.php?upazila_ref="+upazila_ref +"&division_ref="+division_ref+"&district_ref="+district_ref+"&union_ref="+union_ref+"&village_ref="+village_ref;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onResponse(String response) {
                        eventLists.clear();
                        Log.d("New User Event List" , response);
                        try {

                            about_cover_photo.setVisibility(View.GONE);
                            sliderView.setVisibility(View.VISIBLE);

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id_event = object.getString("id");
                                String name_event = object.getString("name");
                                String start_date = object.getString("start_date");
                                String ongoing_end_date = object.getString("end_date");
                                String location_event = object.getString("event_location");
                                String type = object.getString("type");
                                String district = object.getString("district");
                                String upazilla = object.getString("upazilla");
                                String photo = photoUrl+(object.getString("event_cover"));
                                String event_status = photoUrl+(object.getString("district"));

                                @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_date);
                                @SuppressLint("SimpleDateFormat") Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ongoing_end_date);

                                String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String from_date = simpleDateFormat.format(date1);
                                String to_date = simpleDateFormat.format(date2);

                                String event_location = upazilla +" , " + district;

                                event_list_class=new EventList(id_event, name_event, from_date, to_date, event_status, event_location, photo);
                                eventLists.add(event_list_class);
                                sliderAdapter.notifyDataSetChanged();

                            }

                        } catch (JSONException | ParseException e){
                            e.printStackTrace();
                            about_cover_photo.setVisibility(View.VISIBLE);
                            sliderView.setVisibility(View.GONE);
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


    private void getEventCounterData(int id) {
        String url= "https://bdclean.winkytech.com/backend/api/getEventCounterData.php?user_id="+id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onResponse(String response) {
                        Log.d("Event Counter = " , response);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                event_counter = Integer.parseInt(object.getString("event_counter"));
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

        about_refresh.setRefreshing(false);

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

}