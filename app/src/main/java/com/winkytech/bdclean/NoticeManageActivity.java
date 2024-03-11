package com.winkytech.bdclean;

import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoticeManageActivity extends AppCompatActivity {

    NetworkChangeListener networkChangeListener;
    String user_id, designation, user_name;
    int org_leveL_pos;
    TextView toast_message;
    NoticeList notice_list_class;
    NoticeListAdapter noticeListAdapter;
    ArrayList<NoticeList> noticeLists = new ArrayList<>();
    ListView notice_list_view;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_manage);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = shared.getString("user_id","");
        org_leveL_pos = Integer.parseInt(shared.getString("org_level_ref","0"));
        designation = shared.getString("designation", "");
        user_name = shared.getString("name", "");

        notice_list_view = findViewById(R.id.manage_notice_list_view);
        toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getManageNoticeData();
        noticeListAdapter = new NoticeListAdapter(NoticeManageActivity.this, noticeLists);
        notice_list_view.setAdapter(noticeListAdapter);

        notice_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String subject = noticeLists.get(i).getSubject();
                String message = noticeLists.get(i).getMessage();
                String sender_name = noticeLists.get(i).getSender_name();
                String sender_designation = noticeLists.get(i).getSender_designation();
                String date = noticeLists.get(i).getCreate_date();
                String id = noticeLists.get(i).getId();
                String receiver_type = noticeLists.get(i).getReceiver_type();
                String approve_status = noticeLists.get(i).getApprove_status();

                displayDialog(subject, message, sender_name, sender_designation, date, id, receiver_type, approve_status);

            }
        });
    }

    private void displayDialog(String subject, String message, String senderName, String senderDesignation, String date, String id, String receiver_type, String approve_status) {
        Dialog notice_view_dialog = new Dialog(NoticeManageActivity.this);
        notice_view_dialog.setContentView(R.layout.notice_manage_dialog_layout);
        notice_view_dialog.getWindow().setLayout(1000, ViewGroup.LayoutParams.WRAP_CONTENT);
        notice_view_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        notice_view_dialog.setCancelable(true);
        notice_view_dialog.show();

        TextView subject_tv = notice_view_dialog.findViewById(R.id.notice_subject);
        TextView sender_name = notice_view_dialog.findViewById(R.id.notice_send_by);
        TextView notice_date = notice_view_dialog.findViewById(R.id.notice_date);
        TextView notice_details = notice_view_dialog.findViewById(R.id.notice_details);
        TextView approve_btn = notice_view_dialog.findViewById(R.id.approve_btn);
        TextView reject_btn = notice_view_dialog.findViewById(R.id.reject_btn);
        TextView notice_status = notice_view_dialog.findViewById(R.id.notice_status);

        switch (approve_status){

            case "Pending":
                approve_btn.setVisibility(View.VISIBLE);
                reject_btn.setVisibility(View.VISIBLE);
                break;
            case "Approved":
            case "Rejected":
                approve_btn.setVisibility(View.GONE);
                reject_btn.setVisibility(View.GONE);
                break;

        }

        subject_tv.setText(subject);
        sender_name.setText(senderName + " ( " + senderDesignation +" )");
        notice_date.setText(date);
        notice_details.setText(message);
        notice_status.setText(approve_status);

        approve_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int approve_flag = 1;
                reviewNotice(id, approve_flag, subject, message, senderName, senderDesignation, date, receiver_type);
                notice_view_dialog.dismiss();
            }
        });

        reject_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int approve_flag = 2;
                reviewNotice(id, approve_flag, subject, message, senderName, senderDesignation, date, receiver_type);
                notice_view_dialog.dismiss();
            }
        });
    }

    private void reviewNotice(String noticeID, int approve_flag, String subject, String message, String senderName, String senderDesignation, String date, String receiver_type) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                //Starting Write and Read data with URL
                //Creating array for parameters
                String[] field = new String[3];
                field[0] = "notice_id";
                field[1] = "approval_flag";
                field[2] = "user_id";

                //Creating array for data
                String[] data = new String[3];
                data[0] = noticeID;
                data[1] = String.valueOf(approve_flag);
                data[2] = user_id;

                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/updateNoticeStatus.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();
                        if (result.equals("success")) {

                            getManageNoticeData();
                            if (approve_flag == 1){
                                saveInFirebase(noticeID, subject, message, senderName, senderDesignation, date, receiver_type);
                            }

                            Dialog dialog = new Dialog(NoticeManageActivity.this);
                            dialog.setContentView(R.layout.custom_submit_dialog);
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.setCancelable(true);
                            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                            TextView text = dialog.findViewById(R.id.submitTextDialog);
                            ImageView img = dialog.findViewById(R.id.submitImgDialog);
                            Button okBtn = dialog.findViewById(R.id.submitOkBtn);

                            text.setText("Notice reviewed successfully");
                            img.setImageResource(R.drawable.success_image);
                            text.setTextColor(getResources().getColor(R.color.bdclean_green));
                            okBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();

                        } else {
                            Log.i("PutData", result);
                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Failed to update status");
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

    private void saveInFirebase(String noticeID, String subject, String message, String senderName, String senderDesignation, String date, String receiver_type) {

        reference = database.getReference("notice/receiver_type/"+receiver_type);
        String uniqueId = reference.push().getKey();

        NoticeModel noticeModel = new NoticeModel();
        noticeModel.setNoticeID(noticeID);
        noticeModel.setSubject(subject);
        noticeModel.setMessage(message);
        noticeModel.setSenderName(senderName);
        noticeModel.setSenderDesignation(senderDesignation);
        noticeModel.setDate(date);

        // Use the generated unique ID to push the message
        DatabaseReference uniqueMessageReference = reference.child(uniqueId);
        uniqueMessageReference.setValue(noticeModel);

    }

    private void getManageNoticeData() {

        String url = "https://bdclean.winkytech.com/backend/api/getManageNoticeData.php?user_id="+user_id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Manage notice list :", response);
                noticeLists.clear();

                try {

                    JSONArray obj = new JSONArray(response);
                    for (int i=0;i<obj.length();i++){
                        JSONObject jsonObject = obj.getJSONObject(i);
                        String id = jsonObject.getString("id");
                        String receiver_type = jsonObject.getString("receiver_type");
                        String sender_ref = jsonObject.getString("sender_ref");
                        String sender_name = jsonObject.getString("sender_name");
                        String sender_designation = jsonObject.getString("sender_designation");
                        String create_date = jsonObject.getString("create_date");
                        String subject = jsonObject.getString("subject");
                        String message = jsonObject.getString("message");
                        String approval_body = jsonObject.getString("approval_body");
                        String approve_flag = jsonObject.getString("approval_status");
                        String active_flag = jsonObject.getString("active_flag");

                        @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(create_date);
                        String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                        String date = simpleDateFormat.format(date1);

                        String approval_status = "";

                        switch (approve_flag){

                            case "0":
                                approval_status = "Pending";
                                break;

                            case "1":
                                approval_status = "Approved";
                                break;
                            case "2":
                                approval_status = "Rejected";
                                break;
                        }

                        notice_list_class = new NoticeList(id, receiver_type, sender_ref, sender_name, sender_designation,date, subject, message,approval_body, approval_status);
                        noticeLists.add(notice_list_class);
                        noticeListAdapter.notifyDataSetChanged();

                    }

                }
                catch (JSONException e){
                    Log.e("anyText",response);
                    e.printStackTrace();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText("Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

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

    public class NoticeModel {

        String noticeID, subject, message, senderName, senderDesignation, date;

        public NoticeModel() {
        }

        public NoticeModel(String noticeID, String subject, String message, String senderName, String senderDesignation, String date) {
            this.noticeID = noticeID;
            this.subject = subject;
            this.message = message;
            this.senderName = senderName;
            this.senderDesignation = senderDesignation;
            this.date = date;
        }

        public String getNoticeID() {
            return noticeID;
        }

        public void setNoticeID(String noticeID) {
            this.noticeID = noticeID;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public String getSenderDesignation() {
            return senderDesignation;
        }

        public void setSenderDesignation(String senderDesignation) {
            this.senderDesignation = senderDesignation;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }


    private class NoticeList{

        String id, receiver_type, sender_ref, sender_name,sender_designation, create_date, subject, message, approval_body, approve_status;

        public NoticeList(String id, String receiver_type, String sender_ref, String sender_name,String sender_designation, String create_date, String subject, String message, String approval_body, String approve_status) {
            this.id = id;
            this.receiver_type = receiver_type;
            this.sender_name = sender_name;
            this.sender_ref = sender_ref;
            this.sender_designation = sender_designation;
            this.create_date = create_date;
            this.subject = subject;
            this.message = message;
            this.approval_body = approval_body;
            this.approve_status = approve_status;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getReceiver_type() {
            return receiver_type;
        }

        public void setReceiver_type(String receiver_type) {
            this.receiver_type = receiver_type;
        }

        public String getSender_name() {
            return sender_name;
        }

        public void setSender_name(String sender_name) {
            this.sender_name = sender_name;
        }

        public String getSender_ref() {
            return sender_ref;
        }

        public void setSender_ref(String sender_ref) {
            this.sender_ref = sender_ref;
        }

        public String getSender_designation() {
            return sender_designation;
        }

        public void setSender_designation(String sender_designation) {
            this.sender_designation = sender_designation;
        }

        public String getCreate_date() {
            return create_date;
        }

        public void setCreate_date(String create_date) {
            this.create_date = create_date;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getApproval_body() {
            return approval_body;
        }

        public void setApproval_body(String approval_body) {
            this.approval_body = approval_body;
        }

        public String getApprove_status() {
            return approve_status;
        }

        public void setApprove_status(String approve_status) {
            this.approve_status = approve_status;
        }
    }

    private class NoticeListAdapter extends BaseAdapter {

        Context context;
        List<NoticeList> noticeLists;

        public NoticeListAdapter(Context context, List<NoticeList> noticeLists) {
            this.context = context;
            this.noticeLists = noticeLists;
        }

        @Override
        public int getCount() {
            return noticeLists.size();
        }

        @Override
        public Object getItem(int i) {
            return noticeLists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return noticeLists.indexOf(i);
        }

        @Override
        public View getView(int i, View v, ViewGroup parent) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_list_layout,null,true);

            TextView notice_subject = view.findViewById(R.id.notice_subject);
            TextView sender_name = view.findViewById(R.id.sender_name);
            TextView notice_date = view.findViewById(R.id.notice_date);
            TextView notice_status = view.findViewById(R.id.notice_status);

            notice_subject.setText(noticeLists.get(i).getSubject());
            sender_name.setText(noticeLists.get(i).getSender_name() + " ( " + noticeLists.get(i).getSender_designation() + " ) ");
            notice_date.setText(noticeLists.get(i).getCreate_date());
            notice_status.setText(noticeLists.get(i).getApprove_status());

            return view;
        }
    }

}