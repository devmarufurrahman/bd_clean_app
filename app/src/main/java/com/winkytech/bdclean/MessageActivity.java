package com.winkytech.bdclean;

import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressLint({"ResourceAsColor", "SetTextI18n"})
public class MessageActivity extends AppCompatActivity {

    String user_id, user_name, designation, name, sender_id;
    int org_level_pos,member_ref = 0, member_id=0, intent_flag;
    ListView message_list_view;
    ImageView backBtn;
    Button send_btn;
    EditText message_body;
    MessageList message_list_class;
    MessageListAdapter messageListAdapter;
    ArrayList<MessageList> messageLists = new ArrayList<>();
    Toolbar toolbar;
    ProgressBar progressBar;
    TextView toast_message, sendTo;
    ListView listView;
    EditText searchText;
    Dialog dialog;

    public JSONArray member_result;
    List<String> member;
    ArrayAdapter<String> adapter;

    NetworkChangeListener networkChangeListener;
    LinearLayout message_layout, sendToLayout, allLayout;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        listView=findViewById(R.id.list_view);
        searchText=findViewById(R.id.edit_text);
        sendTo=findViewById(R.id.sendTo);
        sendToLayout=findViewById(R.id.sendToLayout);
        allLayout=findViewById(R.id.allLayout);
        backBtn=findViewById(R.id.backBtn);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = shared.getString("user_id","");
        org_level_pos = Integer.parseInt(shared.getString("org_level_ref","0"));
        user_name = shared.getString("name", "0");
        designation = shared.getString("designation", "0");


        Intent intent = getIntent();
        intent_flag = intent.getIntExtra("intent_flag",0);

        if (intent_flag == 0){

            member_id = intent.getIntExtra("member_id",0);
            name = intent.getStringExtra("member_name");

        } else if (intent_flag == 10){

            sender_id = intent.getStringExtra("sender_id");
            name = intent.getStringExtra("sender_name");
        }

        Log.d("notificationRes", "intentflag "+ intent_flag +" senderid "+sender_id+" senderName "+name);


        toolbar = findViewById(R.id.custom_toolbar);
        message_list_view = findViewById(R.id.message_list_view);
        send_btn = findViewById(R.id.send_btn);
        message_body = findViewById(R.id.message_et);
        progressBar = findViewById(R.id.progressbar);
        message_layout = findViewById(R.id.message_layout);
        progressBar.setVisibility(View.GONE);
        member = new ArrayList<String>();




        if (member_id != 0 && !name.equals("")) {
            member_ref = member_id;
            setMessages(name);
            getMessageList(member_ref);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMemberList();
            }
        });

        searchText.requestFocus();
        getMemberData();


        adapter=new ArrayAdapter<>(MessageActivity.this, R.layout.custom_member_spinner_layout,member);
        listView.setAdapter(adapter);

        searchText.addTextChangedListener(new TextWatcher() {
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // when item selected from list
                // set selected item on textView
                String item  = adapter.getItem(position);

                for (int i =0; i<member_result.length();i++){
                    try {
                        JSONObject jsonObject = member_result.getJSONObject(i);

                        if ((jsonObject.getString("full_name")+ " ( "+jsonObject.getString("designation")+" )").equals(item)){
                            member_ref = Integer.parseInt(jsonObject.getString("id"));
                            String name = jsonObject.getString("full_name");
                            setMessages(name);
                            getMessageList(member_ref);
                            System.out.println("member_ref = "+member_ref);
                            if (org_level_pos == 1){

                                message_layout.setVisibility(View.VISIBLE);
                                getMemberData();

                            } else {
                                message_layout.setVisibility(View.VISIBLE);
                                getMemberMessageList();
                                getMemberData();
                            }
                        }

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });



        messageListAdapter = new MessageListAdapter(getApplicationContext(),messageLists,user_id);
        message_list_view.setAdapter(messageListAdapter);



        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (member_ref == 0){
                    Toast.makeText(MessageActivity.this, "Please Select a Member", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage();
                }


            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setMessages(String name) {
        sendTo.setText(name);
        searchText.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        allLayout.setPadding(0,0,0,0);
        sendToLayout.setGravity(Gravity.START);
        sendToLayout.setPadding(10,20,8,20);
        sendToLayout.setBackgroundColor(Color.parseColor("#02682c"));
        backBtn.setVisibility(View.VISIBLE);
        message_list_view.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        message_layout.setVisibility(View.VISIBLE);
//        message_body.requestFocus();
    }

    private void setMemberList() {

        getMemberData();
        searchText.setVisibility(View.VISIBLE);
        message_list_view.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        message_layout.setVisibility(View.GONE);
        toolbar.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.GONE);
        sendToLayout.setBackgroundColor(Color.TRANSPARENT);
        sendTo.setText("To: ");
        allLayout.setPadding(10,10,10,10);
    }

    private void sendMessage() {

        String body = message_body.getText().toString().trim();

        if (body.equals("")){
            Toast.makeText(this, "Please type some message", Toast.LENGTH_SHORT).show();
        } else {

            progressBar.setVisibility(View.VISIBLE);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {

                    //Starting Write and Read data with URL
                    //Creating array for parameters
                    String[] field = new String[3];
                    field[0] = "user_id";
                    field[1] = "body";
                    field[2] = "send_to";

                    //Creating array for data
                    String[] data = new String[3];
                    data[0] = user_id;
                    data[1] = body;
                    data[2] = String.valueOf(member_ref);

                    PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/saveMessage.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult().trim();
                            if (result.equals("Message Send")) {
                                progressBar.setVisibility(View.GONE);
                                getMessageList(member_ref);


                                reference = database.getReference("notification/reciever/user_ref/"+member_ref);
                                String uniqueId = reference.push().getKey();

                                Date date = new Date();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                String current_date = dateFormat.format(date);


                                NotificationModel notificationModel = new NotificationModel();
                                notificationModel.setSender_name(user_name);
                                notificationModel.setMessage(body);
                                notificationModel.setDate(current_date);
                                notificationModel.setSender_id(user_id);
                                notificationModel.setDesignation(designation);

                                // Use the generated unique ID to push the message
                                DatabaseReference uniqueMessageReference = reference.child(uniqueId);
                                uniqueMessageReference.setValue(notificationModel);

                                message_body.setText("");

                            } else {
                                progressBar.setVisibility(View.GONE);
                                Log.i("PutData", result);
                                //Toast.makeText(MessageActivity.this, "Failed To send message!!!", Toast.LENGTH_SHORT).show();
                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Failed To send message!!!");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                            progressBar.setVisibility(View.GONE);

                            }
                        }
                    }
                    //End Write and Read data with URL
                }
            });
        }
    }


    private void getMemberData() {
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getAllDescendingMember.php?org_level_pos="+org_level_pos;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                progressBar.setVisibility(View.GONE);

                try {

                    JSONArray obj = new JSONArray(response);

                    member_result = obj;

                    for (int i=0;i<obj.length();i++){
                        JSONObject jsonObject = obj.getJSONObject(i);
                        String name=jsonObject.getString("full_name");
                        String designation=jsonObject.getString("designation");
                        member.add(name+" ( "+designation+" )");
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
                progressBar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(MessageActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(MessageActivity.this, volleyError, Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                progressBar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(MessageActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getMessageList(int member_ref) {
        progressBar.setVisibility(View.VISIBLE);

        String url= "https://bdclean.winkytech.com/backend/api/getOutgoingMessageList.php?user_id="+user_id+"&send_to="+ member_ref;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        messageLists.clear();
                        System.out.println("response = " + response);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("id");
                                String body = object.getString("message_from_body");
                                String receiver = object.getString("full_name");

                                message_list_class= new MessageList(body,receiver,id);
                                messageLists.add(message_list_class);
                                messageListAdapter.notifyDataSetChanged();
                                message_list_view.setSelection(messageListAdapter.getCount() - 1);
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
                //Toast.makeText(getApplicationContext(), "Failed to get Event List", Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText("Failed to get Message List");
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


    private void getMemberMessageList() {
        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getMemberMessageList.php?user_id="+user_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        messageLists.clear();
                        System.out.println("response = " + response);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("id");
                                String body = object.getString("message_from_body");
                                String receiver = object.getString("full_name");

                                message_list_class= new MessageList(body,receiver,id);
                                messageLists.add(message_list_class);
                                messageListAdapter.notifyDataSetChanged();
                                message_list_view.setSelection(messageListAdapter.getCount() - 1);
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
                //Toast.makeText(getApplicationContext(), "Failed to get Event List", Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText("Failed to get Message List");
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
    }

    public class NotificationModel {

        String sender_id, sender_name, message, date, designation;

        public NotificationModel() {
        }

        public NotificationModel(String sender_id, String sender_name, String message, String date, String designation) {
            this.sender_id = sender_id;
            this.sender_name = sender_name;
            this.message = message;
            this.date = date;
            this.designation = designation;
        }

        public String getSender_id() {
            return sender_id;
        }

        public void setSender_id(String sender_id) {
            this.sender_id = sender_id;
        }

        public String getSender_name() {
            return sender_name;
        }

        public void setSender_name(String sender_name) {
            this.sender_name = sender_name;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }
    }
}