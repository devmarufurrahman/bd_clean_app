package com.winkytech.bdclean;

import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventManageDetailsActivity extends AppCompatActivity {

    String  e_name,e_type,e_location, e_status, user_id, e_district, e_upazilla,e_photo, e_start_date, e_end_date, e_supervisor, e_village, e_union,e_division, comment;
    TextView event_name, comment_tv,event_location, start_date, end_date,event_status, event_type,toast_message,member_list_tv, supervisor, event_upazila, event_union, event_village, event_division, event_district;
    ImageView event_photo;
    FloatingActionButton editEvent;
    Toolbar toolbar;
    Button approve_btn, disapprove_btn;
    ListView member_list_view;
    JoinedMemberList joinedMemberList_class;
    JoinedMemberListAdapter joinedMemberListAdapter;
    List<JoinedMemberList> joinedMemberLists = new ArrayList<>();
    ProgressBar progressBar;
    LinearLayout approval_layout;
    int flag, intent_flag,e_id,org_level_ref = 0;
    private  static String photoUrl = "https://bdclean.winkytech.com/resources/event/";
    private final String profilePhotoUrl = "https://bdclean.winkytech.com/resources/user/profile_pic/";

    NetworkChangeListener networkChangeListener;

    CardView event_details_card;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_manage_details);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = shared.getString("user_id","");
        org_level_ref = Integer.parseInt(shared.getString("user_position", ""));

        Log.d("ORG LEVEL POS = ", String.valueOf(org_level_ref));

        event_name = findViewById(R.id.event_name);
        editEvent = findViewById(R.id.edit_event);
        event_location = findViewById(R.id.event_location);
        event_district = findViewById(R.id.event_district);
        event_upazila = findViewById(R.id.event_upazila);
        event_village = findViewById(R.id.event_village);
        event_union = findViewById(R.id.event_union);
        event_division = findViewById(R.id.event_division);
        start_date = findViewById(R.id.event_start_date);
        end_date = findViewById(R.id.event_end_date);
        event_status = findViewById(R.id.event_active_status);
        event_type = findViewById(R.id.event_type);
        event_photo = findViewById(R.id.event_photo);
        toolbar = findViewById(R.id.custom_toolbar);
        approve_btn = findViewById(R.id.approve);
        comment_tv = findViewById(R.id.event_comment);
        disapprove_btn = findViewById(R.id.reject);
        progressBar = findViewById(R.id.progressbar);
        member_list_view = findViewById(R.id.member_list_view);
        approval_layout = findViewById(R.id.layout_2);
        event_details_card = findViewById(R.id.event_details_card_2);
        progressBar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        intent_flag = intent.getIntExtra("intent_flag",0);
        System.out.println("intent_flag = " + intent_flag);
        if (intent_flag==1 || intent_flag==3){
            e_id = Integer.parseInt(intent.getStringExtra("event_id"));
            e_name = intent.getStringExtra("e_name");
            e_type = intent.getStringExtra("type");
            e_location = intent.getStringExtra("location");
            e_status = intent.getStringExtra("status");
            e_district = intent.getStringExtra("district");
            e_upazilla = intent.getStringExtra("upzilla");
            e_start_date = intent.getStringExtra("from_date");
            e_photo = intent.getStringExtra("photo");
            e_end_date = intent.getStringExtra("to_date");
            e_village = intent.getStringExtra("village");
            e_union = intent.getStringExtra("union");
            e_division = intent.getStringExtra("division");
            e_supervisor = intent.getStringExtra("supervisor");
            flag = intent.getIntExtra("flag",0);
            comment = intent.getStringExtra("comment");
            System.out.println("user_id = " + user_id +" , COMMENT = "+comment);

            Picasso.get().load(e_photo).into(event_photo);
            event_name.setText(e_name);
            event_location.setText(e_location);
            event_type.setText( e_type);
            start_date.setText(e_start_date);
            end_date.setText(e_end_date);
            event_status.setText(e_status);
            event_division.setText(e_division);
            event_district.setText(e_district);
            event_upazila.setText(e_upazilla);
            event_union.setText(e_union);
            event_village.setText(e_village);
            comment_tv.setText(comment);

            switch (flag){
                case 1 :
                    getEventApprovalStatus();
                    approval_layout.setVisibility(View.VISIBLE);
                    member_list_view.setVisibility(View.GONE);
                    break;
                case 2 :
                    getJoinedMembers();
                    joinedMemberListAdapter = new JoinedMemberListAdapter(EventManageDetailsActivity.this, joinedMemberLists, e_id, user_id);
                    member_list_view.setAdapter(joinedMemberListAdapter);
                    approval_layout.setVisibility(View.GONE);
                    member_list_view.setVisibility(View.VISIBLE);
                    event_details_card.setVisibility(View.GONE);
                    editEvent.setVisibility(View.VISIBLE);

                    break;

                case 3 :
                    editEvent.setVisibility(View.VISIBLE);
                    approval_layout.setVisibility(View.GONE);
                    break;

                default:
                    approval_layout.setVisibility(View.GONE);
                    member_list_view.setVisibility(View.GONE);
            }

        } else if (intent_flag==12){
            e_id = intent.getIntExtra("function_ref",0);
            getEventData(e_id);
            getJoinedMembers();
            joinedMemberListAdapter = new JoinedMemberListAdapter(EventManageDetailsActivity.this, joinedMemberLists, e_id, user_id);
            member_list_view.setAdapter(joinedMemberListAdapter);
            approval_layout.setVisibility(View.GONE);
            member_list_view.setVisibility(View.VISIBLE);

        } else if (intent_flag==10){
            e_id = intent.getIntExtra("function_ref",0);
            getEventData(e_id);
            approval_layout.setVisibility(View.VISIBLE);
        }

        approve_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = 1;
                String comment = "";
                approveEvent(status, comment);
            }
        });

        disapprove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = 2;
                displayCommentDialog(status);
            }
        });

        editEvent.setOnClickListener(view -> {
            Intent intent1 = new Intent(EventManageDetailsActivity.this, UpdateEventDetails.class);
            intent1.putExtra("event_id", e_id);
            startActivity(intent1);
        });
    }

    private void displayCommentDialog(int status) {
        Dialog dialog = new Dialog(EventManageDetailsActivity.this);
        dialog.setContentView(R.layout.comment_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.show();

        EditText comment_et = dialog.findViewById(R.id.comment_et);
        Button submit_btn = dialog.findViewById(R.id.submit_btn);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = comment_et.getText().toString().trim();
                approveEvent(status, comment);
                dialog.dismiss();

            }
        });

    }

    private void getEventData(int event_id) {
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getEventDetails.php?event_id=" + event_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        System.out.println("Event response = " + response);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("id");
                                String name = object.getString("name");
                                String spec = object.getString("spec");
                                String date_start = object.getString("start_date");
                                String date_end = object.getString("end_date");
                                String location = object.getString("event_location");
                                String type = object.getString("event_type");
                                String division = object.getString("division");
                                String district = object.getString("district");
                                String upazilla = object.getString("upazila");
                                String union = object.getString("union_name");
                                String village = object.getString("village");
                                String photo = photoUrl + (object.getString("event_cover"));

                                @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date_start);
                                @SuppressLint("SimpleDateFormat") Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date_end);

                                String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String from_date = simpleDateFormat.format(date1);
                                String to_date = simpleDateFormat.format(date2);

                                event_name.setText(name);
                                start_date.setText(from_date);
                                end_date.setText(to_date);
                                event_location.setText(location);
                                event_district.setText(district);
                                event_division.setText(division);
                                event_upazila.setText(upazilla);
                                event_union.setText(union);
                                event_village.setText(village);
                                event_type.setText(type);
                                Picasso.get().load(photo).into(event_photo);

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

    private void getEventApprovalStatus() {
        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getEventApproveStatus.php?event_code="+e_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("response = " + response);
                        progressBar.setVisibility(View.GONE);
                        String response_data = response.toString().trim();

                        if (response_data.equals("0") || response_data.equals("null")){

                            approve_btn.setEnabled(true);
                            disapprove_btn.setEnabled(true);
                            approve_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#91040A")));
                            disapprove_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#91040A")));

                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Sorry status not found");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();

                        } else {

                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject object = jsonArray.getJSONObject(0);
                                String approve_status = object.getString("approve_status");

                                if (approve_status.equals("1") || approve_status.equals("2")){
                                    approve_btn.setEnabled(false);
                                    approve_btn.setVisibility(View.GONE);
                                    disapprove_btn.setEnabled(false);
                                    disapprove_btn.setVisibility(View.GONE);
                                    approve_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4EDDA")));
                                    approve_btn.setTextColor(getResources().getColor(R.color.green_1));
                                    disapprove_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F8D7DA")));
                                    disapprove_btn.setTextColor(getResources().getColor(R.color.red));
                                }

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
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText("Failed to get Event List");
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

    private void approveEvent(int status, String comment) {
        progressBar.setVisibility(View.VISIBLE);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @SuppressLint({"ResourceAsColor", "SetTextI18n"})
            @Override
            public void run() {

                String[] field = new String[4];
                field[0] = "event_id";
                field[1] = "user_id";
                field[2] = "status";
                field[3] = "comment";

                //Creating array for data
                String[] data = new String[4];
                data[0] = String.valueOf(e_id);
                data[1] = user_id;
                data[2] = String.valueOf(status);
                data[3] = comment;

                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/updateEventStatus.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();
                        Log.d("Approval Response", result);
                        if (result.equals("notified")) {
                            switch (status){
                                case 1 :
                                    event_status.setText("STATUS : Approved");
                                    approval_layout.setVisibility(View.GONE);
                                    break;
                                case 2 :
                                    event_status.setText("STATUS : Rejected");
                                    approval_layout.setVisibility(View.GONE);
                                    break;
                            }
                            getEventApprovalStatus();
                            progressBar.setVisibility(View.GONE);
                            Toast toast = new Toast(EventManageDetailsActivity.this);
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout,findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Event Status Updated");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();

                        } else if (result.equals("updated")){
                            progressBar.setVisibility(View.GONE);
                            Log.i("PutData", result);
                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout, findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Event Reviewed");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();

                            switch (status){
                                case 1 :
                                    event_status.setText("STATUS : Approved");
                                    approval_layout.setVisibility(View.GONE);
                                    break;
                                case 2 :
                                    event_status.setText("STATUS : Rejected");
                                    approval_layout.setVisibility(View.GONE);
                                    break;
                            }

                        }
                    }
                }
                //End Write and Read data with URL
            }
        });
    }

    private void getJoinedMembers() {
        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getJoinedMember.php?event_id="+e_id+"&user_position="+org_level_ref;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        joinedMemberLists.clear();
                        System.out.println("response = " + response);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("user_ref");
                                String name = object.getString("full_name");
                                String join_date = object.getString("joining_date");
                                String leave_date = object.getString("leave_date");
                                String address = object.getString("address");
                                String photo = profilePhotoUrl+ object.getString("profile_photo");
                                String status_ref =object.getString("approve_status");
                                String status = "";

                                switch (status_ref){
                                    case "0":
                                        status = "Pending";
                                        break;

                                    case "1":
                                        status = "Approved";
                                        break;

                                    case "2":
                                        status = "Rejected";
                                        break;
                                }

                                joinedMemberList_class= new JoinedMemberList(id,name,join_date,leave_date,address, photo, status);
                                joinedMemberLists.add(joinedMemberList_class);
                                joinedMemberListAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);

                            }

                        } catch (JSONException e){
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText("Failed to get joined member");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    private  class JoinedMemberList{

        String id, name, join_date, leave_date, address, photo, status;

        public JoinedMemberList(String id, String name, String join_date, String leave_date, String address, String photo, String status) {
            this.id = id;
            this.name = name;
            this.join_date = join_date;
            this.leave_date = leave_date;
            this.address = address;
            this.photo = photo;
            this.status = status;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
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

        public String getJoin_date() {
            return join_date;
        }

        public void setJoin_date(String join_date) {
            this.join_date = join_date;
        }

        public String getLeave_date() {
            return leave_date;
        }

        public void setLeave_date(String leave_date) {
            this.leave_date = leave_date;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    private class JoinedMemberListAdapter extends BaseAdapter{

        Context context;
        List<JoinedMemberList> memberLists;
        String user_id;
        int e_id;

        public JoinedMemberListAdapter(Context context, List<JoinedMemberList> memberLists, int e_id, String user_id) {
            this.context = context;
            this.memberLists = memberLists;
            this.e_id = e_id;
            this.user_id = user_id;
        }


        @Override
        public int getCount() {
            return memberLists.size();
        }

        @Override
        public Object getItem(int position) {
            return memberLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return memberLists.indexOf(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_joined_list_layout,null,true);

            TextView member_name = view.findViewById(R.id.member_name);
            TextView member_join = view.findViewById(R.id.member_join_date);
            TextView status = view.findViewById(R.id.status);
            TextView member_address = view.findViewById(R.id.member_address);
            ImageView photo = view.findViewById(R.id.member_pic);
            Button approve_member = view.findViewById(R.id.approve_member);
            Button disapprove_member = view.findViewById(R.id.disapprove_member);
            LinearLayout approval_layout = view.findViewById(R.id.approval_layout);


            member_name.setText(memberLists.get(position).getName());
            member_join.setText(memberLists.get(position).getJoin_date());
            status.setText(memberLists.get(position).getStatus());
            member_address.setText(memberLists.get(position).getAddress());
            Picasso.get().load(memberLists.get(position).getPhoto()).into(photo);

            String member_ref = memberLists.get(position).getId();
            String approve_status = memberLists.get(position).getStatus();

            if (approve_status.equals("Approved")){

                approve_member.setVisibility(View.GONE);
                disapprove_member.setVisibility(View.GONE);
                status.setTextColor(getResources().getColor(R.color.green_2));

            } else if (approve_status.equals("Rejected")){
                approve_member.setVisibility(View.GONE);
                disapprove_member.setVisibility(View.GONE);
                status.setTextColor(getResources().getColor(R.color.red));
            }

            approve_member.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String status = "1";
//                    approveMember(status,member_ref,user_id, String.valueOf(e_id), view_position);

                    if (status.equals("") || member_ref.equals("") || user_id.equals("") || e_id==0){
                        Toast.makeText(context, "something missing", Toast.LENGTH_SHORT).show();
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {

                                String[] field = new String[4];
                                field[0] = "event_id";
                                field[1] = "member_ref";
                                field[2] = "user_ref";
                                field[3] = "status_ref";

                                //Creating array for data
                                String[] data = new String[4];
                                data[0] = String.valueOf(e_id);
                                data[1] = member_ref;
                                data[2] = user_id;
                                data[3] = status;

                                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/updateEventMemberApproval.php", "POST", field, data);
                                if (putData.startPut()) {
                                    if (putData.onComplete()) {
                                        String result = putData.getResult().trim();
                                        if (result.equals("success")) {
                                            progressBar.setVisibility(View.GONE);
                                            joinedMemberListAdapter.notifyDataSetChanged();
                                            getJoinedMembers();
                                            Toast toast = new Toast(getApplicationContext());
                                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout, findViewById(R.id.custom_toast));
                                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                                            toast_message.setText("Member Approved");
                                            toast.setView(toast_view);
                                            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                                            toast.setDuration(Toast.LENGTH_SHORT);
                                            toast.show();

                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                            Log.i("PutData", result);
                                            Toast toast = new Toast(getApplicationContext());
                                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
                                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                                            toast_message.setText("Failed To Change Status!!!");
                                            toast.setView(toast_view);
                                            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                                            toast.setDuration(Toast.LENGTH_SHORT);
                                            toast.show();

                                        }
                                    }
                                }
                            }
                        });
                    }

                }
            });

            disapprove_member.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String status = "2";
//                    approveMember(status,member_ref,user_id, String.valueOf(e_id), view_position);

                    Dialog dialog = new Dialog(EventManageDetailsActivity.this);
                    dialog.setContentView(R.layout.comment_dialog_layout);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(false);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                    dialog.show();

                    EditText comment_et = dialog.findViewById(R.id.comment_et);
                    Button submit_btn = dialog.findViewById(R.id.submit_btn);

                    submit_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String comment = comment_et.getText().toString().trim();

                            if (status.equals("") || member_ref.equals("") || user_id.equals("") || e_id==0){
                                Toast.makeText(context, "something missing", Toast.LENGTH_SHORT).show();
                            } else {
                                progressBar.setVisibility(View.VISIBLE);
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void run() {

                                        String[] field = new String[5];
                                        field[0] = "event_id";
                                        field[1] = "member_ref";
                                        field[2] = "user_ref";
                                        field[3] = "status_ref";
                                        field[4] = "comment";

                                        //Creating array for data
                                        String[] data = new String[5];
                                        data[0] = String.valueOf(e_id);
                                        data[1] = member_ref;
                                        data[2] = user_id;
                                        data[3] = status;
                                        data[4] = comment;

                                        PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/updateEventMemberApproval.php", "POST", field, data);
                                        if (putData.startPut()) {
                                            if (putData.onComplete()) {
                                                String result = putData.getResult().trim();
                                                if (result.equals("success")) {
                                                    progressBar.setVisibility(View.GONE);
                                                    joinedMemberListAdapter.notifyDataSetChanged();
                                                    getJoinedMembers();
                                                    Toast toast = new Toast(getApplicationContext());
                                                    View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout, findViewById(R.id.custom_toast));
                                                    toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                                                    toast_message.setText("Member Approved");
                                                    toast.setView(toast_view);
                                                    toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                                                    toast.setDuration(Toast.LENGTH_SHORT);
                                                    toast.show();

                                                } else {
                                                    progressBar.setVisibility(View.GONE);
                                                    Log.i("PutData", result);
                                                    Toast toast = new Toast(getApplicationContext());
                                                    View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
                                                    toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                                                    toast_message.setText("Failed To Change Status!!!");
                                                    toast.setView(toast_view);
                                                    toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                                                    toast.setDuration(Toast.LENGTH_SHORT);
                                                    toast.show();

                                                }
                                            }
                                        }
                                    }
                                });
                            }
                            dialog.dismiss();

                        }
                    });
                }
            });

            return view;
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
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