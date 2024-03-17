package com.winkytech.bdclean.PollingPanel;

import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.winkytech.bdclean.NetworkChangeListener;
import com.winkytech.bdclean.R;
import com.winkytech.bdclean.RegPage_2Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class ElectionManageActivity extends AppCompatActivity {

    String election_position_ref;
    int user_id, org_level_pos, position_ref, election_id;
    Bundle bundle = new Bundle();

    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_manage);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = Integer.parseInt(shared.getString("user_id",""));
        org_level_pos = Integer.parseInt(shared.getString("org_level_ref","0"));
        position_ref = Integer.parseInt(shared.getString("user_position","0"));

        System.out.println("User Position in Election Manage Activity = "+position_ref);

        Intent intent = getIntent();
        election_id = intent.getIntExtra("election_ref",0);
        //election_position_ref = intent.getStringExtra("election_position_ref");
       // bundle.putString("election_position_ref", election_position_ref);

        bundle.putInt("election_ref", election_id);

        if (position_ref == 26 || position_ref == 34){

            ElectionControllerFragment electionControllerFragment = new ElectionControllerFragment();
            electionControllerFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.push_left_in,R.anim.fade_out);
            transaction.replace(R.id.election_manage_container,electionControllerFragment).commit();

        } else {

            
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime startTime = LocalDateTime.of(2023, 12, 15, 10, 0);
            LocalDateTime endTime = LocalDateTime.of(2023, 12, 15, 16, 30);

            if (currentDateTime.isAfter(startTime) && currentDateTime.isBefore(endTime)){

//                checkVoterStatus();
                VoterFragment voterFragment = new VoterFragment();
                voterFragment.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.push_left_in,R.anim.fade_out);
                transaction.replace(R.id.election_manage_container,voterFragment).commit();


            } else {

                Toast.makeText(this, "Sorry, voting time is up", Toast.LENGTH_SHORT).show();
                finish();
            }

        }

    }

//    private void checkVoterStatus() {
//
//        String url = "https://bdclean.winkytech.com/backend/api/checkVoterStatus.php?election_ref="+election_id+"&user_ref="+user_id;
//        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                System.out.println("Voter Checker = "+response);
//
//                try {
//
//                    JSONArray obj = new JSONArray(response);
//                    for (int i=0;i<obj.length();i++){
//
//                        JSONObject jsonObject = obj.getJSONObject(i);
//                        int vote_status = Integer.parseInt(jsonObject.getString("voter_check"));
//
//                        if (vote_status == 1) {
//
//                            VoterFragment voterFragment = new VoterFragment();
//                            voterFragment.setArguments(bundle);
//                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                            transaction.setCustomAnimations(R.anim.push_left_in,R.anim.fade_out);
//                            transaction.replace(R.id.election_manage_container,voterFragment).commit();
//
//                        } else {
//
//                            Toast toast = new Toast(ElectionManageActivity.this);
//                            View toast_view = getLayoutInflater().inflate(R.layout.custom_warning_dialog_layout,findViewById(R.id.custom_toast));
//                            TextView toast_message;
//                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
//                            Button ok_btn = toast_view.findViewById(R.id.ok_btn);
//                            ok_btn.setVisibility(View.GONE);
//                            toast_message.setText("Your vote is complete.\n");
//                            toast.setView(toast_view);
//                            toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
//                            toast.setDuration(Toast.LENGTH_SHORT);
//                            toast.show();
//                            finish();
//
//                        }
//
//                    }
//                }
//                catch (JSONException e){
//                    Log.e("getAllChiefCoordinatorCandidate",response);
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast toast = new Toast(ElectionManageActivity.this);
//                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
//                TextView toast_message;
//                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
//                toast_message.setText("Failed To get info");
//                toast.setView(toast_view);
//                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
//                toast.setDuration(Toast.LENGTH_SHORT);
//                toast.show();
//            }
//        });
//
//        RequestQueue requestQueue = Volley.newRequestQueue(ElectionManageActivity.this);
//        requestQueue.add(stringRequest);
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister broadcast receiver when activity is destroyed
        unregisterReceiver(networkChangeListener);
    }

}