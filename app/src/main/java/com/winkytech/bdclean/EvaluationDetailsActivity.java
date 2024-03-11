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
@SuppressLint("SetTextI18n")
public class EvaluationDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView ev_subject,ev_body,ev_from,ev_to,ev_for,ev_date, toast_message, status, comment_tv;
    Button approve_btn,disapprove_btn;
    String body,subject,from,to,for_ev,date,eva_status, comment;
    ProgressBar progressBar;
    ImageView status_view;
    int user_id,eva_id,index, evaluation_from_ref;
    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_details);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = Integer.parseInt((shared.getString("user_id", "")));

        toolbar=findViewById(R.id.custom_toolbar);
        ev_subject=findViewById(R.id.ev_subject);
        ev_body=findViewById(R.id.ev_body);
        ev_from=findViewById(R.id.ev_from);
        ev_to=findViewById(R.id.ev_to);
        ev_for=findViewById(R.id.ev_for);
        ev_date=findViewById(R.id.ev_date);
        progressBar = findViewById(R.id.progressbar);
        status = findViewById(R.id.ev_details_status);
        comment_tv = findViewById(R.id.comment);
        progressBar.setVisibility(View.GONE);
        approve_btn=findViewById(R.id.approve_btn);
        status_view = findViewById(R.id.approval_details_iv);
        disapprove_btn=findViewById(R.id.disApprove_btn);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        index=intent.getIntExtra("intent_flag",0);

        if (index == 1 || index ==2 ){
            body=intent.getStringExtra("ev_body");
            subject=intent.getStringExtra("subject");
            from=intent.getStringExtra("from");
            to=intent.getStringExtra("to");
            for_ev=intent.getStringExtra("ev_for");
            date=intent.getStringExtra("date");
            eva_id = intent.getIntExtra("id",0);
            eva_status = intent.getStringExtra("status");
            comment = intent.getStringExtra("comment");
            evaluation_from_ref = Integer.parseInt(intent.getStringExtra("evaluation_by_ref"));

            if (index==1){
                if (eva_status.equals("Approved")){

                    status.setText("Approved");
                    status.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                    approve_btn.setEnabled(false);
                    approve_btn.setVisibility(View.GONE);
                    disapprove_btn.setEnabled(false);
                    disapprove_btn.setVisibility(View.GONE);

                    approve_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4EDDA")));
                    approve_btn.setTextColor(getResources().getColor(R.color.green_1));
                    disapprove_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F8D7DA")));
                    disapprove_btn.setTextColor(getResources().getColor(R.color.red));
                    status_view.setImageResource(R.drawable.ic_baseline_check_circle_24);
                    status_view.setImageTintList(ColorStateList.valueOf(Color.parseColor("#00974A")));
                    comment_tv.setText("");

                } else if (eva_status.equals("Rejected")){

                    status.setText("Rejected");
                    status.setTextColor(Color.RED);
                    approve_btn.setEnabled(false);
                    approve_btn.setVisibility(View.GONE);
                    disapprove_btn.setEnabled(false);
                    disapprove_btn.setVisibility(View.GONE);
                    approve_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4EDDA")));
                    approve_btn.setTextColor(getResources().getColor(R.color.green_1));
                    disapprove_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F8D7DA")));
                    disapprove_btn.setTextColor(Color.RED);
                    status_view.setImageResource(R.drawable.icon_cancel);
                    status_view.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ff5252")));
                    comment_tv.setText(comment);

                } else if (eva_status.equals("Pending Approval")){


                    status_view.setImageResource(R.drawable.icon_sand_clock);
                    status.setText("Pending");
                    status.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffa700")));
                    approve_btn.setEnabled(true);
                    disapprove_btn.setEnabled(true);
                    approve_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00974A")));
                    approve_btn.setTextColor(getResources().getColor(R.color.white));
                    disapprove_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#91040A")));
                    disapprove_btn.setTextColor(getResources().getColor(R.color.white));

                }
                approve_btn.setVisibility(View.GONE);
                disapprove_btn.setVisibility(View.GONE);
            }else if (index==2){

                approve_btn.setVisibility(View.VISIBLE);
                disapprove_btn.setVisibility(View.VISIBLE);
                getEvaluationStatus();
            }
            ev_body.setText(body);
            ev_subject.setText(subject);
            ev_from.setText(from);
            ev_to.setText(to);
            ev_for.setText(for_ev);
            ev_date.setText(date);
            getEvaluationStatus();
        } else if (index == 10){
            eva_id = intent.getIntExtra("function_ref",0);
            approve_btn.setVisibility(View.VISIBLE);
            disapprove_btn.setVisibility(View.VISIBLE);
            getEvaluationData(eva_id);
            getEvaluationStatus();
        } else if (index == 11){
            eva_id = intent.getIntExtra("function_ref", 0);
            approve_btn.setVisibility(View.GONE);
            disapprove_btn.setVisibility(View.GONE);
            getEvaluationData(eva_id);
            getEvaluationStatus();
        }

        approve_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flag = "1";
                String comment = "";
                approveEvaluation(flag, comment);


            }
        });

        disapprove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flag = "2";
                displayCommentDialog(flag);

            }
        });

    }

    private void displayCommentDialog(String flag) {
        Dialog dialog = new Dialog(EvaluationDetailsActivity.this);
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
                approveEvaluation(flag, comment);
                dialog.dismiss();

            }
        });

    }

    private void getEvaluationData(int function_ref) {
        String url= "https://bdclean.winkytech.com/backend/api/getEvaluationReportData.php?ev_id="+function_ref;
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
                                String subject = object.getString("evaluation_subject");
                                String body = object.getString("evaluation_body");
                                String date = object.getString("evaluation_report_date");
                                String member = object.getString("evaluation_for_name");
                                String approval_body = object.getString("evaluation_to_name");
                                String evaluation_from = (object.getString("evaluation_by"));
                                String approve_status = object.getString("approve_flag");
                                String comment = object.getString("comment");
                                evaluation_from_ref = Integer.parseInt(object.getString("evaluation_by_ref"));

                                ev_subject.setText(subject);
                                ev_body.setText(body);
                                ev_date.setText(date);
                                ev_for.setText(member);
                                ev_to.setText(approval_body);
                                ev_from.setText(evaluation_from);
                                if (!comment.equals("null")){
                                    comment_tv.setText(comment);
                                }

                                String app_status;

                                switch (approve_status){
                                    case "0" :
                                        app_status = "PENDING";
                                        status.setText(app_status);
                                        status.setTextColor(Color.RED);
                                        break;
                                    case "1" :
                                        app_status = "APPROVED";
                                        status.setText(app_status);
                                        status.setTextColor(Color.GREEN);
                                        approve_btn.setVisibility(View.GONE);
                                        disapprove_btn.setVisibility(View.GONE);
                                        break;
                                    case "2" :
                                        app_status = "REJECTED";
                                        status.setText(app_status);
                                        status.setTextColor(Color.RED);
                                        approve_btn.setVisibility(View.GONE);
                                        disapprove_btn.setVisibility(View.GONE);
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
                progressBar.setVisibility(View.GONE);
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

    private void approveEvaluation(String flag, String comment) {
        progressBar.setVisibility(View.VISIBLE);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {

                //Starting Write and Read data with URL
                //Creating array for parameters
                String[] field = new String[5];
                field[0] = "eva_id";
                field[1] = "flag";
                field[2] = "user_id";
                field[3] = "eva_from";
                field[4] = "comment";

                //Creating array for data
                String[] data = new String[5];
                data[0] = String.valueOf(eva_id);
                data[1] = flag;
                data[2] = String.valueOf(user_id);
                data[3] = String.valueOf(evaluation_from_ref);
                data[4] = comment;

                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/updateEvaluationStatus.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();
                        if (result.equals("Status Updated")) {
                            progressBar.setVisibility(View.GONE);
                            getEvaluationStatus();
                            approve_btn.setEnabled(false);
                            approve_btn.setVisibility(View.GONE);
                            disapprove_btn.setEnabled(false);
                            disapprove_btn.setVisibility(View.GONE);

                            approve_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                            approve_btn.setTextColor(Color.BLACK);
                            disapprove_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                            disapprove_btn.setTextColor(Color.BLACK);

                            if (flag.equals("1")){
                                submitDialog("Successfully approve evaluation", R.drawable.success_image,R.color.bdclean_green);
                            } else if (flag.equals("2")){
                                submitDialog("evaluation rejected", R.drawable.success_image,R.color.red);
                            }


                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.i("PutData", result);

                            submitDialog("failed to approve evaluation", R.drawable.warning_image
                                    ,R.color.red);
                        }
                    }
                }
                //End Write and Read data with URL
            }
        });
    }



    private void submitDialog(String notice, int imgGet, int noticeColor) {
        Dialog dialog = new Dialog(EvaluationDetailsActivity.this);
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

    private void getEvaluationStatus() {
        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getEvaluationStatus.php?eva_id="+eva_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("response = " + response);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String com_status = object.getString("approve_flag");

                                if (com_status.equals("0")){
                                    status.setText("Pending");
                                    status.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFF00")));
                                    approve_btn.setEnabled(true);
                                    disapprove_btn.setEnabled(true);
                                    status_view.setImageResource(R.drawable.icon_sand_clock);

                                } else if (com_status.equals("1")){
                                    status.setText("Approved");
                                    status.setTextColor(getResources().getColor(R.color.green_2));
                                    approve_btn.setEnabled(false);
                                    approve_btn.setVisibility(View.GONE);
                                    disapprove_btn.setEnabled(false);
                                    disapprove_btn.setVisibility(View.GONE);
                                    approve_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4EDDA")));
                                    approve_btn.setTextColor(getResources().getColor(R.color.green_1));
                                    disapprove_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F8D7DA")));
                                    disapprove_btn.setTextColor(getResources().getColor(R.color.red));
                                    status_view.setImageResource(R.drawable.ic_baseline_check_circle_24);
                                    status_view.setImageTintList(ColorStateList.valueOf(Color.parseColor("#00974A")));
                                } else if (com_status.equals("2")){
                                    status.setText("Rejected");
                                    status.setTextColor(Color.RED);
                                    approve_btn.setEnabled(false);
                                    approve_btn.setVisibility(View.GONE);
                                    disapprove_btn.setEnabled(false);
                                    disapprove_btn.setVisibility(View.GONE);
                                    approve_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4EDDA")));
                                    approve_btn.setTextColor(getResources().getColor(R.color.green_1));
                                    disapprove_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F8D7DA")));
                                    disapprove_btn.setTextColor(Color.RED);
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
}