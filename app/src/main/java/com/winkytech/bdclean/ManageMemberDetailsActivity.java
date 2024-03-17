package com.winkytech.bdclean;

import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class ManageMemberDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView user_name, user_address, user_email, user_contact, user_occupation, user_location, toast_message, user_gender;
    Button approve_btn, reject_btn;
    ImageView user_photo;
    String member_id, user_id;
    String photoUrl = "https://bdclean.winkytech.com/resources/user/profile_pic/";

    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_member_details);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = shared.getString("user_id","");
        Intent intent = getIntent();
        member_id = intent.getStringExtra("member_id");

        toolbar = findViewById(R.id.custom_toolbar);
        user_name = findViewById(R.id.user_name);
        user_address = findViewById(R.id.user_address);
        user_email = findViewById(R.id.user_email);
        user_contact = findViewById(R.id.user_contact);
        user_occupation = findViewById(R.id.user_occupation);
        user_location = findViewById(R.id.user_location);
        approve_btn = findViewById(R.id.approve_btn);
        reject_btn = findViewById(R.id.reject_btn);
        user_photo = findViewById(R.id.user_photo);
        user_gender = findViewById(R.id.user_gender);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getUserData();
        approve_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 1;
                approveUser(flag);
            }
        });

        reject_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 2;
                approveUser(flag);
            }
        });

    }

    private void approveUser(int flag) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {

                //Starting Write and Read data with URL
                //Creating array for parameters
                String[] field = new String[3];
                field[0] = "user_id";
                field[1] = "member_id";
                field[2] = "flag";

                //Creating array for data
                String[] data = new String[3];
                data[0] = user_id;
                data[1] = member_id;
                data[2] = String.valueOf(flag);
                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/updateMemberApproval.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();
                        if (result.equals("success")) {

                            Toast toast = new Toast(ManageMemberDetailsActivity.this);
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout,findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("REQUEST REVIEWED");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();

                        } else {
                            Log.i("PutData", result);
                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("FAILED TO REVIEW REQUEST");
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

    private void getUserData() {
        String url= "https://bdclean.winkytech.com/backend/api/getUserData.php?member_ref="+member_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        System.out.println("response = " + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String name = object.getString("full_name");
                                String email = object.getString("email");
                                String contact = object.getString("contact");
                                String address = object.getString("address");
                                String occupation = object.getString("occupation");
                                String photo = photoUrl+ (object.getString("profile_photo"));
                                String division = object.getString("division_name");
                                String district = object.getString("district");
                                String upazilla = object.getString("upazilla");
                                String union = object.getString("union_name");
                                String village = object.getString("village");
                                String gender = object.getString("gender");

                                user_name.setText(name);
                                user_email.setText(email);
                                user_contact.setText(contact);
                                user_address.setText(address);
                                user_occupation.setText(occupation);
                                user_gender.setText(gender);
                                user_location.setText(division + " , " + district + " , " + upazilla + " , "+ union + " , " + village);
                                Picasso.get().load(photo).into(user_photo);

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
                toast_message.setText("Failed to get user data");
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