package com.winkytech.bdclean;

import static com.winkytech.bdclean.NewEventActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class ComplainDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView com_body,com_subject,com_from,com_to,com_for,com_date,toast_message, com_status, comment_tv;
    Button approve_btn, disapprove_btn;
    String body,subject,from,to,for_com,date,status, comment,complain_from_ref;
    ProgressBar progressBar;
    ImageView status_view;
    int user_id, index, com_id;
    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_details);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = Integer.parseInt((shared.getString("user_id", "")));

        toolbar=findViewById(R.id.custom_toolbar);
        com_body = findViewById(R.id.cm_body);
        com_subject = findViewById(R.id.cm_subject);
        com_from = findViewById(R.id.cm_from);
        com_to = findViewById(R.id.cm_to);
        com_for = findViewById(R.id.cm_for);
        com_date = findViewById(R.id.cm_date);
        approve_btn=findViewById(R.id.approve_btn);
        progressBar = findViewById(R.id.progressbar);
        comment_tv = findViewById(R.id.comment);
        com_status = findViewById(R.id.status);
        status_view = findViewById(R.id.status_iv);
        progressBar.setVisibility(View.GONE);
        disapprove_btn = findViewById(R.id.disApprove_btn);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        index = intent.getIntExtra("intent_flag",0);

        if (index == 1 || index ==2 ){
            body = intent.getStringExtra("cm_body");
            subject = intent.getStringExtra("subject");
            from = intent.getStringExtra("from");
            to = intent.getStringExtra("to");
            for_com = intent.getStringExtra("cm_for");
            date = intent.getStringExtra("date");
            com_id = intent.getIntExtra("id",0);
            status = intent.getStringExtra("status");
            comment = intent.getStringExtra("comment");
            complain_from_ref = intent.getStringExtra("complain_by_ref");
            System.out.println("complain_from_ref = "+complain_from_ref);

            if (index==1){
                com_status.setText(status);
                if (com_status.equals("Approved")){
                    status_view.setImageResource(R.drawable.ic_baseline_check_circle_24);
                    status_view.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ff5252")));
                } else if (com_status.equals("Rejected")){
                    status_view.setImageResource(R.drawable.icon_cancel);
                    status_view.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ff5252")));
                } else if (com_status.equals("Pending Approval")){
                    status_view.setImageResource(R.drawable.icon_sand_clock);
                }
                approve_btn.setVisibility(View.GONE);
                disapprove_btn.setVisibility(View.GONE);
            } else {
                approve_btn.setVisibility(View.VISIBLE);
                disapprove_btn.setVisibility(View.VISIBLE);
            }

            com_body.setText(body);
            com_subject.setText(subject);
            com_from.setText(from);
            com_to.setText(to);
            com_for.setText(for_com);
            com_date.setText(date);
            if (!comment.equals("null")){
                comment_tv.setText(comment);
            }
            getComplainStatus();

        } else if (index == 10){
            com_id = intent.getIntExtra("function_ref",0);
            approve_btn.setVisibility(View.VISIBLE);
            disapprove_btn.setVisibility(View.VISIBLE);
            getComplainData(com_id);
            getComplainStatus();
        }else if (index == 11) {
            com_id = intent.getIntExtra("function_ref", 0);
            approve_btn.setVisibility(View.GONE);
            disapprove_btn.setVisibility(View.GONE);
            getComplainData(com_id);
            getComplainStatus();

        }

        approve_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flag_1 = "1";
                String comment = "";
                approveComplain(flag_1, comment);

            }
        });

        disapprove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flag_2 = "2";
                displayCommentDialog(flag_2);
            }
        });

    }

    private void displayCommentDialog(String flag) {
        Dialog dialog = new Dialog(ComplainDetailsActivity.this);
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
                approveComplain(flag, comment);
                dialog.dismiss();

            }
        });

    }

    private void getComplainData(int function_ref) {
        String url= "https://bdclean.winkytech.com/backend/api/getComplainReportData.php?com_id="+function_ref;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("response = " + response);
                        progressBar.setVisibility(View.GONE);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String subject = object.getString("complain_subject");
                                String body = object.getString("complain_body");
                                String date = object.getString("complain_date");
                                String member = object.getString("complain_by");
                                String approval_body = object.getString("complain_to_name");
                                String for_complain = object.getString("complain_for_name");
                                String approve_status = object.getString("approve_flag");
                                String comment = object.getString("comment");
                                complain_from_ref = object.getString("complain_from_ref");

                                com_subject.setText(subject);
                                com_body.setText(body);
                                com_date.setText(date);
                                com_from.setText(member);
                                com_to.setText(approval_body);
                                com_for.setText(for_complain);
                                if (!comment.equals("null")){
                                    comment_tv.setText(comment);
                                }

                                String status;

                                switch (approve_status){
                                    case "0" :
                                        status = "PENDING";
                                        com_status.setText(status);
                                        break;
                                    case "1" :
                                        status = "APPROVED";
                                        com_status.setText(status);
                                        break;
                                    case "2" :
                                        status = "REJECTED";
                                        com_status.setText(status);
                                        break;
                                }


                            }

                        } catch (JSONException e){
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText("Failed to get status");
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

    private void getComplainStatus() {
        String url= "https://bdclean.winkytech.com/backend/api/getComplainStatus.php?com_id="+com_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("response = " + response);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String status = object.getString("approve_flag");

                                if (status.equals("0")){
                                    com_status.setText("Pending");
                                    com_status.setTextColor(Color.RED);
                                    status_view.setImageResource(R.drawable.icon_sand_clock);

                                } else if (status.equals("1")){
                                    com_status.setText("Approved");
                                    com_status.setTextColor(Color.GREEN);
                                    approve_btn.setEnabled(false);
                                    disapprove_btn.setEnabled(false);
                                    approve_btn.setVisibility(View.GONE);
                                    disapprove_btn.setVisibility(View.GONE);
                                    approve_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4EDDA")));
                                    approve_btn.setTextColor(Color.BLACK);
                                    disapprove_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F8D7DA")));
                                    disapprove_btn.setTextColor(Color.BLACK);
                                    status_view.setImageResource(R.drawable.ic_baseline_check_circle_24);
                                    status_view.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                } else if (status.equals("2")){
                                    com_status.setText("Rejected");
                                    com_status.setTextColor(Color.RED);
                                    approve_btn.setEnabled(false);
                                    disapprove_btn.setEnabled(false);
                                    approve_btn.setVisibility(View.GONE);
                                    disapprove_btn.setVisibility(View.GONE);
                                    approve_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4EDDA")));
                                    approve_btn.setTextColor(Color.BLACK);
                                    disapprove_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F8D7DA")));
                                    disapprove_btn.setTextColor(Color.BLACK);
                                    status_view.setImageResource(R.drawable.icon_cancel);
                                    status_view.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ff5252")));
                                }
                                progressBar.setVisibility(View.GONE);

                            }

                        } catch (JSONException e){
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText("Failed to get status");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    private void approveComplain(String Flag, String comment) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                String[] field = new String[5];
                field[0] = "com_id";
                field[1] = "flag";
                field[2] = "user_id";
                field[3] = "com_from";
                field[4] = "comment";

                //Creating array for data
                String[] data = new String[5];
                data[0] = String.valueOf(com_id);
                data[1] = Flag;
                data[2] = String.valueOf(user_id);
                data[3] = String.valueOf(complain_from_ref);
                data[4] = comment;

                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/updateComplainStatus.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();
                        if (result.equals("Status Updated")) {
                            progressBar.setVisibility(View.GONE);
                            getComplainStatus();
                            approve_btn.setEnabled(false);
                            disapprove_btn.setEnabled(false);
                            approve_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                            approve_btn.setTextColor(Color.BLACK);
                            disapprove_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                            disapprove_btn.setTextColor(Color.BLACK);
                            // Toast.makeText(getActivity(), "Login Success", Toast.LENGTH_SHORT).show();


                            if (Flag.equals("1")){
                                submitDialog("Successfully approve complain", R.drawable.success_image,R.color.bdclean_green);
                            } else if (Flag.equals("2")){
                                submitDialog("complain rejected", R.drawable.success_image,R.color.red);
                            }

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.i("PutData", result);
                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Failed To Update Status!!!");
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


    private void submitDialog(String notice, int imgGet, int noticeColor) {
        Dialog dialog = new Dialog(ComplainDetailsActivity.this);
        dialog.setContentView(R.layout.custom_submit_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView text = dialog.findViewById(R.id.submitTextDialog);
        ImageView img = dialog.findViewById(R.id.submitImgDialog);
        Button okBtn = dialog.findViewById(R.id.submitOkBtn);

        text.setText(notice);
        img.setImageResource(imgGet);
        text.setTextColor(getResources().getColor(noticeColor));
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
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
}