package com.winkytech.bdclean;

import static com.winkytech.bdclean.HomeActivity.Contact;
import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.winkytech.bdclean.PollingPanel.ElectionControllerFragment;
import com.winkytech.bdclean.PollingPanel.ElectionDetailsActivity;
import com.winkytech.bdclean.PollingPanel.ElectionManageActivity;
import com.winkytech.bdclean.PollingPanel.VoterFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PollingActivity extends AppCompatActivity {

    String user_contact, user_id, profile_photo, user_name, designation;
    ImageView election_banner;
    TextView election_title, election_start_date, election_end_date, toast_message;
    int org_level_pos,upazila_ref, position_ref;
    private int selected_election_ref = 0;
    private final String photoUrl = "https://bdclean.winkytech.com/resources/user/profile_pic/";
    String banner_url = "https://bdclean.winkytech.com/resources/election_media/";
    Toolbar toolbar;
    ProgressBar progressBar;
    NetworkChangeListener networkChangeListener;

    Button proceed_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_contact = (shared.getString(Contact, ""));
        user_id = shared.getString("user_id","");
        profile_photo = photoUrl+(shared.getString("profile_photo",""));
        org_level_pos = Integer.parseInt(shared.getString("org_level_ref","0"));
        user_name = shared.getString("name","");
        upazila_ref = Integer.parseInt(shared.getString("upazila_ref","0"));
        designation = shared.getString("designation","");
        position_ref = Integer.parseInt(shared.getString("user_position","0"));
        System.out.println("designation = "+designation);

        election_banner = findViewById(R.id.election_banner);
        election_title = findViewById(R.id.election_title);
//        election_session_year = findViewById(R.id.election_session_year);
        election_start_date = findViewById(R.id.election_start_date);
        election_end_date = findViewById(R.id.election_end_date);
        toolbar = findViewById(R.id.custom_toolbar);
        progressBar = findViewById(R.id.progressbar);
        proceed_btn = findViewById(R.id.proceed_btn);
        progressBar.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getElectionData();

        proceed_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(selected_election_ref != 0 ){

                    if (position_ref == 26 || position_ref == 34){

                        Intent intent = new Intent(PollingActivity.this, ElectionManageActivity.class);
                        intent.putExtra("election_ref", selected_election_ref);
                        startActivity(intent);
                    } else {

                        checkVoterStatus();
                    }

                } else {

                    Toast.makeText(PollingActivity.this, "Failed to load Election System !!", Toast.LENGTH_SHORT).show();
                    Log.e("Election System", "Election ID not found");
                }

            }
        });

    }
    private void getElectionData() {

        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getElectionData.php";
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                System.out.println(response);
                try {
                    JSONArray obj = new JSONArray(response);
                    for (int i=0;i<obj.length();i++){
                        JSONObject jsonObject = obj.getJSONObject(i);
                        String title=jsonObject.getString("title");
                        String banner= banner_url+ jsonObject.getString("banner");
                        String session_year=jsonObject.getString("session_year");
                        String start_date=jsonObject.getString("start_date");
                        String end_date=jsonObject.getString("end_date");
                        selected_election_ref = Integer.parseInt(jsonObject.getString("id"));

                        @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(start_date);
                        @SuppressLint("SimpleDateFormat") Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(end_date);
                        String pattern = "MMMM dd, yyyy";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                        String from_date = simpleDateFormat.format(date1);
                        String to_date = simpleDateFormat.format(date2);

                        int targetImageViewWidth = election_banner.getWidth();
                        int targetImageViewHeight = election_banner.getHeight();

                        election_title.setText(title);
                        //election_session_year.setText("নির্বাচনের বছর : "+session_year);
                        election_start_date.setText("Election start time: "+from_date+"  16:00");
                        election_end_date.setText("Election ending time : "+to_date+ "  16:15");
                        election_title.setText(title);
                        Picasso.get().load(banner).resize(targetImageViewWidth, targetImageViewHeight)
                                .onlyScaleDown().into(election_banner);

                    }
                }
                catch (JSONException e){
                    Log.e("anyText",response);
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                } catch (ParseException e) {
                    progressBar.setVisibility(View.GONE);
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                String volleyError = "";
                if (!error.getMessage().equals("null") || error.getMessage() != null ){

                    Log.e("VolleyError",error.getMessage());

                    if (error instanceof NetworkError){
                        volleyError="Network Error";
                    } else if (error instanceof ServerError){
                        volleyError="Server Connection error";
                    }
                }
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(PollingActivity.this);
        requestQueue.add(stringRequest);

    }

    private void checkVoterStatus() {

        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/checkVoterStatus.php?election_ref="+selected_election_ref+"&user_ref="+user_id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                System.out.println("Voter Checker = "+response);
                progressBar.setVisibility(View.GONE);

                try {

                    JSONArray obj = new JSONArray(response);
                    for (int i=0;i<obj.length();i++){

                        JSONObject jsonObject = obj.getJSONObject(i);
                        int vote_status = Integer.parseInt(jsonObject.getString("voter_check"));

                        if (vote_status == 1) {

                            Intent intent = new Intent(PollingActivity.this, ElectionManageActivity.class);
                            intent.putExtra("election_ref", selected_election_ref);
                            startActivity(intent);

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast toast = new Toast(PollingActivity.this);
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_warning_dialog_layout,findViewById(R.id.custom_toast));
                            TextView toast_message;
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            Button ok_btn = toast_view.findViewById(R.id.ok_btn);
                            ok_btn.setVisibility(View.GONE);
                            toast_message.setText("Your vote is complete.\n");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();

                        }

                    }
                }
                catch (JSONException e){
                    progressBar.setVisibility(View.GONE);
                    Log.e("getAllChiefCoordinatorCandidate",response);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast toast = new Toast(PollingActivity.this);
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                TextView toast_message;
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText("Failed To get info");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(PollingActivity.this);
        requestQueue.add(stringRequest);

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