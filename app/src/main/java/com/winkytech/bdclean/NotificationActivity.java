package com.winkytech.bdclean;

import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
@SuppressLint("SetTextI18n")
public class NotificationActivity extends AppCompatActivity {

    ListView notification_list_view;
    Toolbar toolbar;
    ArrayList<NotificationList> notificationLists = new ArrayList<>();
    NotificationList notification_list_class;
    NotificationListAdapter notificationListAdapter;
    TextView toast_message;
    String user_id;
    int org_leveL_pos, notification_count;

    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = shared.getString("user_id","");
        org_leveL_pos = Integer.parseInt(shared.getString("org_level_ref","0"));
        notification_count = shared.getInt("not_count",0);
        System.out.println("user_id = " +user_id);

        toolbar = findViewById(R.id.custom_toolbar);
        notification_list_view = findViewById(R.id.notification_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getNotificationData();
        notificationListAdapter = new NotificationListAdapter(NotificationActivity.this, notificationLists);
        notification_list_view.setAdapter(notificationListAdapter);

        notification_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String type= notificationLists.get(position).getType();
                int function_ref = Integer.parseInt(notificationLists.get(position).getFunction_ref());
                int intent_flag;
                int activity_ref = Integer.parseInt(notificationLists.get(position).getId());
                view.setBackgroundResource(R.drawable.list_item_color);

                updateActivityStatus(activity_ref);

                switch (type){
                    case "Event Create" :
                        intent_flag = 10;
                        Intent intent = new Intent(getApplicationContext(), EventManageDetailsActivity.class);
                        intent.putExtra("function_ref",function_ref);
                        intent.putExtra("intent_flag",intent_flag);
                        startActivity(intent);
                        break;
                    case "Evaluation Create" :
                        intent_flag = 10;
                        Intent intent2 = new Intent(getApplicationContext(), EvaluationDetailsActivity.class);
                        intent2.putExtra("function_ref",function_ref);
                        intent2.putExtra("intent_flag",intent_flag);
                        startActivity(intent2);
                        break;
                    case "Complain Create" :
                        intent_flag = 10;
                        Intent intent3 = new Intent(getApplicationContext(), ComplainDetailsActivity.class);
                        intent3.putExtra("function_ref",function_ref);
                        intent3.putExtra("intent_flag",intent_flag);
                        startActivity(intent3);
                        break;

                    case "Post Create" :
                        intent_flag = 10;
                        Intent intent8 = new Intent(getApplicationContext(), ManagePostActivity.class);
                        intent8.putExtra("function_ref",function_ref);
                        intent8.putExtra("intent_flag",intent_flag);
                        startActivity(intent8);
                        break;

                    case "Complain Review" :
                        intent_flag = 11;
                        Intent intent4 = new Intent(getApplicationContext(), ComplainActivity.class);
                        intent4.putExtra("function_ref",function_ref);
                        intent4.putExtra("intent_flag",intent_flag);
                        startActivity(intent4);
                        break;

                    case "Evaluation Review" :
                        intent_flag = 11;
                        Intent intent5 = new Intent(getApplicationContext(), EvaluationDetailsActivity.class);
                        intent5.putExtra("function_ref",function_ref);
                        intent5.putExtra("intent_flag",intent_flag);
                        startActivity(intent5);
                        break;

                    case "Event Review" :
                        intent_flag = 11;
                        Intent intent6 = new Intent(getApplicationContext(), EventManageActivity.class);
                        intent6.putExtra("function_ref",function_ref);
                        intent6.putExtra("intent_flag",intent_flag);
                        startActivity(intent6);
                        break;

                    case "Event Join" :
                        intent_flag = 12;
                        Intent intent7 = new Intent(getApplicationContext(), EventManageDetailsActivity.class);
                        intent7.putExtra("function_ref",function_ref);
                        intent7.putExtra("intent_flag",intent_flag);
                        startActivity(intent7);
                        break;
                }
            }
        });

    }

    private void updateActivityStatus(int activity_ref) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {

                String[] field = new String[2];
                field[0] = "act_id";
                field[1] = "flag";

                //Creating array for data
                String[] data = new String[2];
                data[0] = String.valueOf(activity_ref);
                data[1] = "1";

                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/updateActivityRead.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();
                        if (result.equals("success")) {
                            Log.i("Activity Read flag = ", "SUCCESS");
                        } else {
                            Log.i("PutData", result);
                            Log.i("PutData", "FAILED");
                        }
                    }
                }
            }
        });
    }

    private void getNotificationData() {

        String url= "https://bdclean.winkytech.com/backend/api/getNotificationData.php?user_id="+user_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        notificationLists.clear();
                        System.out.println("notification response = " + response);

                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("id");
                                String function_ref = object.getString("function_ref");
                                String name = object.getString("notification_body");
                                String type = object.getString("activity_type");
                                String date = object.getString("create_date");
                                String read_flag = object.getString("read_flag");
                                String sender_name = object.getString("full_name");
                                String profile_pic = object.getString("profile_photo");
                                String remain_time = "";
                                LocalDateTime now = LocalDateTime.now();

                                // Parse the given date string
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                if (!date.equals("0000-00-00 00:00:00")){
                                    LocalDateTime createDate = LocalDateTime.parse(date, formatter);
                                    long minutesDiff = ChronoUnit.MINUTES.between(createDate, now);
                                    long hoursDiff = ChronoUnit.HOURS.between(createDate, now);
                                    long daysDiff = ChronoUnit.DAYS.between(createDate, now);

                                    if (daysDiff == 0){
                                        remain_time = hoursDiff + "  hours ago";
                                    } else if (hoursDiff == 0){
                                        remain_time =minutesDiff + " minutes ago";
                                    } else {
                                        remain_time =daysDiff + " days ago";
                                    }
                                } else {

                                    date = "2023-01-01 12:12:00";
                                    LocalDateTime createDate = LocalDateTime.parse(date, formatter);
                                    long minutesDiff = ChronoUnit.MINUTES.between(createDate, now);
                                    long hoursDiff = ChronoUnit.HOURS.between(createDate, now);
                                    long daysDiff = ChronoUnit.DAYS.between(createDate, now);

                                    if (daysDiff == 0){
                                        remain_time = hoursDiff + "  hours ago";
                                    } else if (hoursDiff == 0){
                                        remain_time =minutesDiff + " minutes ago";
                                    } else {
                                        remain_time =daysDiff + " days ago";
                                    }

                                }

                                if (type.equals("New Message") || type.equals("New Chat Sms")){
                                    Log.d("messenger notification ", "Avoid Messenger Notification");
                                } else {
                                    notification_list_class=new NotificationList(id, name, type,date, function_ref,read_flag, sender_name, profile_pic, remain_time);
                                    notificationLists.add(notification_list_class);
                                    notificationListAdapter.notifyDataSetChanged();
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
                toast_message.setText("Failed to get Complain List");
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

    private class NotificationList{
        String id, name, type, date, function_ref, read_flag, sender_name, profile_pic, remain_time;

        public NotificationList(String id, String name, String type, String date, String function_ref, String read_flag, String  sender_name, String profile_pic, String remain_time) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.date = date;
            this.function_ref = function_ref;
            this.read_flag = read_flag;
            this.sender_name = sender_name;
            this.profile_pic = profile_pic;
            this.remain_time = remain_time;
        }

        public String getRead_flag() {
            return read_flag;
        }

        public void setRead_flag(String read_flag) {
            this.read_flag = read_flag;
        }

        public String getFunction_ref() {
            return function_ref;
        }

        public void setFunction_ref(String function_ref) {
            this.function_ref = function_ref;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getSender_name() {
            return sender_name;
        }

        public void setSender_name(String sender_name) {
            this.sender_name = sender_name;
        }

        public String getProfile_pic() {
            return profile_pic;
        }

        public void setProfile_pic(String profile_pic) {
            this.profile_pic = profile_pic;
        }

        public String getRemain_time() {
            return remain_time;
        }

        public void setRemain_time(String remain_time) {
            this.remain_time = remain_time;
        }
    }

    private class NotificationListAdapter extends BaseAdapter{

        Context context;
        ArrayList<NotificationList> notificationLists;
        private static final String photo_path = "https://bdclean.winkytech.com/resources/user/profile_pic/";

        public NotificationListAdapter(Context context, ArrayList<NotificationList> notificationLists) {
            this.context = context;
            this.notificationLists = notificationLists;
        }

        @Override
        public int getCount() {
            return notificationLists.size();
        }

        @Override
        public Object getItem(int position) {
            return notificationLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return notificationLists.indexOf(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.notificaiton_list_layout,null,true);

            String flag_read = notificationLists.get(position).getRead_flag();
            switch (flag_read){
                case "1" :
                    view.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    break;
                case "0" :
                    view.setBackgroundColor(ContextCompat.getColor(context, R.color.notification_color));
                    break;
            }

            TextView name = view.findViewById(R.id.notifi_name);
            TextView date = view.findViewById(R.id.notifi_date);
            ImageView sender_iv = view.findViewById(R.id.sender_photo);

            String sender_name = notificationLists.get(position).getSender_name();
            String body = notificationLists.get(position).getName();

//            String combinedString = sender_name + " , " + body;
//            SpannableString spannableString = new SpannableString(combinedString);
//            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
//            spannableString.setSpan(boldSpan, 0, sender_name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            name.setText(body);
            date.setText(notificationLists.get(position).getDate() + " , " + notificationLists.get(position).getRemain_time());
            Picasso.get().load(photo_path+notificationLists.get(position).getProfile_pic()).into(sender_iv);

            return view;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister broadcast receiver when activity is destroyed
        unregisterReceiver(networkChangeListener);
    }
}