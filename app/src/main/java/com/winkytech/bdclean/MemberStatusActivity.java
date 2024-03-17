package com.winkytech.bdclean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MemberStatusActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView event_joint,post_shared,status,toast_message, eventApprove, postApprove, days30_post, days30_event;
    String user_id;
    ProgressBar progressBar, status_bar;
//    CardView gradingCard;
    int status_ref;

    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_status);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        Intent intent = getIntent();
        user_id=intent.getStringExtra("user_id");

        toolbar=findViewById(R.id.custom_toolbar);
        event_joint=findViewById(R.id.event_joined);
        post_shared=findViewById(R.id.social_post);
        status=findViewById(R.id.status);
        progressBar=findViewById(R.id.progressbar);
        status_bar = findViewById(R.id.point_bar);
        eventApprove = findViewById(R.id.event_joined_approve);
        postApprove = findViewById(R.id.social_post_approve);
        days30_event = findViewById(R.id.days30_event);
        days30_post = findViewById(R.id.days30_post);
        progressBar.setVisibility(View.GONE);


        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
        params.height = 90;
        toolbar.setLayoutParams(params);
        toolbar.setMinimumHeight(90);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getStatusData();

    }

    private void getStatusData() {

        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getMemberStatus.php?user_id="+user_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(String response) {
                        System.out.println("response = " + response);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                int event_count = Integer.parseInt(object.getString("join_count"));
                                int post_count = Integer.parseInt(object.getString("post_count"));
                                String approve_event = object.getString("approve_event");
                                String approve_post = object.getString("post_approve");
                                String daysEvent = object.getString("last_30_days_event");
                                String daysPost = object.getString("last_30_days_post");

                                event_joint.setText(String.valueOf(event_count));
                                post_shared.setText(String.valueOf(post_count));
                                eventApprove.setText(approve_event);
                                postApprove.setText(approve_post);
                                days30_event.setText(daysEvent);
                                days30_post.setText(daysPost);

                                if (event_count>= 4 && post_count>=16){
                                    status_ref = 1;
                                    status.setText("Active Member");
                                    status.setTextColor(R.color.green_2);
                                    status_bar.setProgress(100);
                                    status_bar.setProgress((int) ((event_count*20)+(post_count*1.25)));
                                    status_bar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_2)));

                                } else if (event_count>=3 && post_count>=12){
                                    status_ref = 0;
                                    status.setText("Regular Member");
                                    status.setTextColor(Color.parseColor("#2686cf"));
                                    status_bar.setProgress((int) ((event_count*20)+(post_count*1.25)));
                                    status_bar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#2686cf")));

                                } else if (event_count>=2 && post_count>=8){
                                    status_ref = 0;
                                    status.setText("Irregular Member");
                                    status.setTextColor(Color.parseColor("#f1c232"));
                                    status_bar.setProgress((int) ((event_count*20)+(post_count*1.25)));
                                    status_bar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#f1c232")));

                                } else if (event_count>=1 && post_count>=4){
                                    status_ref = 0;
                                    status.setText("Infrequent Member");
                                    status.setTextColor(Color.parseColor("#e69138"));
                                    status_bar.setProgress((int) ((event_count*20)+(post_count*1.25)));
                                    status_bar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#e69138")));

                                } else if (event_count>=0 && post_count>=0){
                                    status_ref = 0;
                                    status.setText("Inactive Member");
                                    status.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                                    status_bar.setProgress((int) ((event_count*20)+(post_count*1.25)));
                                    status_bar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#e69138")));
                                }
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
                toast_message.setText("Failed to get member status");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                progressBar.setVisibility(View.GONE);

                Log.d("error", "onErrorResponse: " + error);
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