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
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoticeActivity extends AppCompatActivity {

    NetworkChangeListener networkChangeListener;
    String user_id, designation, user_name;
    int org_leveL_pos, receiver_type_ref = 1001;
    Switch language_switch;
    Button my_notice_list, received_notice_list, new_notice_button;
    ListView notice_list_view;
    TextView toast_message;
    private JSONArray receiver_type_result;
    ArrayList<String> receiver_type = new ArrayList<>();
    NoticeList notice_list_class;
    NoticeListAdapter noticeListAdapter;
    ArrayList<NoticeList> noticeLists = new ArrayList<>();

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

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

        language_switch = findViewById(R.id.language_toggle);
        my_notice_list = findViewById(R.id.my_notice_list);
        received_notice_list = findViewById(R.id.received_notice_list);
        new_notice_button = findViewById(R.id.new_notice);
        notice_list_view = findViewById(R.id.notice_list_view);
        toolbar = findViewById(R.id.custom_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getReceiverTypeData();
        getUserNoticeData();
        noticeListAdapter = new NoticeListAdapter(NoticeActivity.this, noticeLists);
        notice_list_view.setAdapter(noticeListAdapter);

        language_switch.setChecked(false);
        language_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b){
                    setLocale("bn");
                    language_switch.getThumbDrawable().setTint(getResources().getColor(R.color.red));
                } else {
                    setLocale("en");
                }

            }
        });

        new_notice_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog notice_form_dialog = new Dialog(NoticeActivity.this);
                notice_form_dialog.setContentView(R.layout.notice_form_dialog_layout);
                notice_form_dialog.getWindow().setLayout(1000, ViewGroup.LayoutParams.WRAP_CONTENT);
                notice_form_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                notice_form_dialog.setCancelable(true);
                notice_form_dialog.show();

                Button receiver_type_btn = notice_form_dialog.findViewById(R.id.select_receiver);
                Button clear_btn = notice_form_dialog.findViewById(R.id.clear_btn);
                Button submit_btn = notice_form_dialog.findViewById(R.id.submit_btn);
                EditText notice_subject_et = notice_form_dialog.findViewById(R.id.notice_subject);
                EditText notice_body_et = notice_form_dialog.findViewById(R.id.notice_body);

                receiver_type_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Dialog receiver_type_dialog = new Dialog(NoticeActivity.this);
                        receiver_type_dialog.setContentView(R.layout.custom_spinner_layout);
                        receiver_type_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        receiver_type_dialog.show();

                        ListView listView=receiver_type_dialog.findViewById(R.id.list_view);
                        EditText editText=receiver_type_dialog.findViewById(R.id.edit_text);
                        ArrayAdapter<String> adapter=new ArrayAdapter<>(NoticeActivity.this, android.R.layout.simple_list_item_1,receiver_type);
                        listView.setAdapter(adapter);

                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                adapter.getFilter().filter(s);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        listView.setOnItemClickListener((parent, v, position, id) -> {
                            String item  = adapter.getItem(position);

                            for (int i =0; i<receiver_type_result.length();i++){
                                try {
                                    JSONObject jsonObject = receiver_type_result.getJSONObject(i);

                                    if (jsonObject.getString("name").equals(item)){
                                        receiver_type_ref = Integer.parseInt(jsonObject.getString("receiver_type"));
                                        receiver_type_btn.setText(jsonObject.getString("name"));
                                        receiver_type_dialog.dismiss();
                                        Log.d("Receiver_type :", String.valueOf(receiver_type_ref));
                                    }

                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            // Dismiss dialog
                        });

                    }
                });
                clear_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        receiver_type_btn.setText("Select");
                        notice_subject_et.setText("Notice Subject");
                        notice_body_et.setText("Notice Body");
                        notice_form_dialog.dismiss();
                    }
                });
                submit_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String notice_body = notice_body_et.getText().toString().trim();
                        String notice_subject = notice_subject_et.getText().toString().trim();

                        if (notice_body.equals("")){
                            notice_body_et.setError("Please add the Body");
                        } else if (notice_subject.equals("")){
                            notice_subject_et.setError("Please add the subject");
                        } else if (receiver_type_ref == 1001) {
                            receiver_type_btn.setError("Please select a receiver type");
                        } else {

                            submitNotice(notice_body, notice_subject, receiver_type_ref);
                            notice_form_dialog.dismiss();

                        }

                    }
                });

            }
        });

        notice_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String subject = noticeLists.get(i).getSubject();
                String message = noticeLists.get(i).getMessage();
                String sender_name = noticeLists.get(i).getSender_name();
                String sender_designation = noticeLists.get(i).getSender_designation();
                String date = noticeLists.get(i).getCreate_date();
                displayDialog(subject, message, sender_name, sender_designation, date);

            }
        });

    }

    private void displayDialog(String subject, String message, String senderName, String senderDesignation, String date) {

        Dialog notice_view_dialog = new Dialog(NoticeActivity.this);
        notice_view_dialog.setContentView(R.layout.notice_view_dialog_layout);
        notice_view_dialog.getWindow().setLayout(1000, ViewGroup.LayoutParams.WRAP_CONTENT);
        notice_view_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        notice_view_dialog.setCancelable(true);
        notice_view_dialog.show();

        TextView close_btn = notice_view_dialog.findViewById(R.id.close_btn);
        TextView subject_tv = notice_view_dialog.findViewById(R.id.notice_subject);
        TextView sender_name = notice_view_dialog.findViewById(R.id.notice_send_by);
        TextView notice_date = notice_view_dialog.findViewById(R.id.notice_date);
        TextView notice_details = notice_view_dialog.findViewById(R.id.notice_details);

        subject_tv.setText(subject);
        sender_name.setText(senderName + " ( " + senderDesignation +" )");
        notice_date.setText(date);
        notice_details.setText(message);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notice_view_dialog.dismiss();
            }
        });

    }

    private void getUserNoticeData() {

        String url = "https://bdclean.winkytech.com/backend/api/getUserNoticeData.php?user_id="+user_id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Receiver Types :", response);
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

    private void submitNotice(String noticeBody, String noticeSubject, int receiverTypeRef) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                //Starting Write and Read data with URL
                //Creating array for parameters
                String[] field = new String[7];
                field[0] = "user_id";
                field[1] = "receiver_ref";
                field[2] = "body";
                field[3] = "subject";
                field[4] = "user_designation";
                field[5] = "org_level_pos";
                field[6] = "user_name";

                //Creating array for data
                String[] data = new String[7];
                data[0] = user_id;
                data[1] = String.valueOf(receiver_type_ref);
                data[2] = noticeBody;
                data[3] = noticeSubject;
                data[4] = designation;
                data[5] = String.valueOf(org_leveL_pos);
                data[6] = user_name;

                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/saveNoticeProfile.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();
                        if (result.equals("success")) {

                            getUserNoticeData();

                            Dialog dialog = new Dialog(NoticeActivity.this);
                            dialog.setContentView(R.layout.custom_submit_dialog);
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.setCancelable(true);
                            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                            TextView text = dialog.findViewById(R.id.submitTextDialog);
                            ImageView img = dialog.findViewById(R.id.submitImgDialog);
                            Button okBtn = dialog.findViewById(R.id.submitOkBtn);

                            text.setText("Notice Submitted to review");
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
                            toast_message.setText("Failed To Create Evaluation!!!");
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

    private void getReceiverTypeData() {

        String url = "https://bdclean.winkytech.com/backend/api/getNoticeReceiverData.php";
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Users Notices :", response);

                try {

                    JSONArray obj = new JSONArray(response);

                    receiver_type_result = obj;

                    for (int i=0;i<obj.length();i++){
                        JSONObject jsonObject = obj.getJSONObject(i);
                        String name=jsonObject.getString("name");
                        receiver_type.add(name);
                    }

                }
                catch (JSONException e){
                    Log.e("anyText",response);
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

    private class NoticeListAdapter extends BaseAdapter{

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}