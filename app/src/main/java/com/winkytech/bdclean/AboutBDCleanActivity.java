package com.winkytech.bdclean;

import static com.winkytech.bdclean.MainActivity.MyPREFERENCES;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
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
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AboutBDCleanActivity extends AppCompatActivity {

    Button member_btn, honorable_member,goodwill_ambassador, log_out,  honorable_btn_2, goodwill_btn_2, continue_user_btn;
    TextView toast_message;
    public static final String Contact = "contactKey";
    SharedPreferences sharedpreferences;
    String contact;
    int event_counter = 0;
    Switch language_switch;
    String upazila_ref, union_ref, village_ref, division_ref, district_ref;
    int checkBox_flag = 0, org_level_pos, terms_condition_flag = 0;
    String photoUrl = "https://bdclean.winkytech.com/resources/event/";
    EventList event_list_class;
    SliderAdapter sliderAdapter;
    ArrayList<EventList> eventLists = new ArrayList<>();
    LinearLayout member_type_layout, member_type_layout_2;
//    SliderView sliderView ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_bdclean);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        sharedpreferences=getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        contact = (shared.getString(Contact,""));
        terms_condition_flag = shared.getInt("terms_condition_flag",0);
        language_switch = findViewById(R.id.language_toggle);


//        sharedpreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//        org_level_pos = (shared.getInt("org_level_pos",0));
//        contact = (shared.getString(Contact,""));
//        upazila_ref = shared.getString("upazilla_ref","");
//        village_ref = shared.getString("village_ref","");
//        union_ref = shared.getString("union_ref","");
//        district_ref = shared.getString("district_ref","");
//        division_ref = shared.getString("division_ref","");
//
//        if (!division_ref.equals("0") && !division_ref.equals("") && !division_ref.equals("null")){
//
//            getEventList();
//        }

        member_btn = findViewById(R.id.member_btn);
        honorable_member = findViewById(R.id.honorary_member);
        log_out = findViewById(R.id.log_out);
        goodwill_ambassador = findViewById(R.id.goodwill);
        honorable_btn_2 = findViewById(R.id.honorary_member2);
        goodwill_btn_2 = findViewById(R.id.goodwill2);
        member_type_layout = findViewById(R.id.member_type_layout);
        member_type_layout_2 = findViewById(R.id.member_type_layout_2);
        continue_user_btn = findViewById(R.id.continue_user_btn);

        if (terms_condition_flag == 1){

            member_type_layout.setVisibility(View.GONE);
            member_type_layout_2.setVisibility(View.GONE);
            continue_user_btn.setVisibility(View.VISIBLE);


        } else {
            member_type_layout.setVisibility(View.VISIBLE);
            member_type_layout_2.setVisibility(View.VISIBLE);
            continue_user_btn.setVisibility(View.GONE);
        }

//        sliderView =  findViewById(R.id.slider);
//        sliderView.setIndicatorEnabled(true);
//        sliderView.setIndicatorVisibility(true);
//        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
//        sliderView.setScrollTimeInSec(3);
//        sliderView.setAutoCycle(true);
//        sliderView.startAutoCycle();
////        getUserId();
//
//        sliderAdapter = new SliderAdapter(this, eventLists);
//        sliderView.setSliderAdapter(sliderAdapter);

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
                Intent intent = new Intent(getApplicationContext(),DonationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
            }
        });

        honorable_btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),DonationActivity.class);
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
                Intent intent = new Intent(getApplicationContext(),DonationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
            }
        });

    }

    private void getEventList() {
        String url= "https://bdclean.winkytech.com/backend/api/getNewUserEventList.php?upazila_ref="+upazila_ref +"&division_ref="+division_ref+"&district_ref="+district_ref+"&union_ref="+union_ref+"&village_ref="+village_ref;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onResponse(String response) {
                        eventLists.clear();
                        Log.d("New User Event = " , response);
                        try {
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

                        } catch (JSONException e){
                            e.printStackTrace();

                        } catch (ParseException e) {
                            e.printStackTrace();
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
                        System.out.println("counter response = " + response);
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

        // Constructor
        public SliderAdapter(Context context, ArrayList<EventList> eventLists) {
            this.eventLists = eventLists;
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

            // Glide is use to load image
            // from url in your imageview.
            Glide.with(viewHolder.itemView)
                    .load(event_list_data.getEvent_photo())
                    .fitCenter()
                    .into(viewHolder.event_photo);
            viewHolder.event_title.setText(event_list_data.getEvent_title());
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
                this.itemView = itemView;
            }
        }
    }

}