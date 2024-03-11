package com.winkytech.bdclean;

import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MemberDetailsActivity extends AppCompatActivity {

    int member_id,  event_count = 0;
    TextView user_id;
    TextView member_name,member_email,member_contact, member_address, member_designation, member_location, toast_message, member_gender, member_occupation, member_religion, member_status;
    TextView completed_event, completed_post, complain_created, evaluation_create;
    ImageView member_photo, message_icon;
    Toolbar toolbar;
    String photoUrl = "https://bdclean.winkytech.com/resources/user/profile_pic/", photo = "", name;
    Button manage_member;
    LinearLayout locationLayout;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Contact = "contactKey";

    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_details);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        Intent intent = getIntent();
        member_id = intent.getIntExtra("member_id",0);

        member_name=findViewById(R.id.member_name);
        member_email=findViewById(R.id.member_email);
        member_contact=findViewById(R.id.member_contact);
        member_address=findViewById(R.id.member_address);
        member_designation=findViewById(R.id.member_designation);
        member_location=findViewById(R.id.member_location);
        member_photo=findViewById(R.id.member_photo);
        toolbar = findViewById(R.id.custom_toolbar);
        member_gender = findViewById(R.id.member_gender);
        completed_event = findViewById(R.id.completed_event);
        completed_post = findViewById(R.id.completed_post);
        complain_created = findViewById(R.id.complain_created);
        evaluation_create = findViewById(R.id.evaluation_create);
        member_occupation = findViewById(R.id.member_occupation);
        member_religion = findViewById(R.id.member_religion);
        member_status = findViewById(R.id.status);
        manage_member = findViewById(R.id.manage_member_btn);
        user_id = findViewById(R.id.user_id);
        locationLayout = findViewById(R.id.locationLayout);
        message_icon = findViewById(R.id.message_icon);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getMemberDetails();

        manage_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(MemberDetailsActivity.this, ManageMemberActivity.class);
                intent1.putExtra("member_id", member_id);
                intent1.putExtra("event_count",event_count);
                intent1.putExtra("photo",photo);

                intent1.putExtra("name",event_count);
                intent1.putExtra("email",event_count);
                intent1.putExtra("contact",event_count);
                intent1.putExtra("gender",event_count);
                intent1.putExtra("occupation",event_count);
                intent1.putExtra("religion",event_count);
                intent1.putExtra("address",event_count);
                startActivity(intent1);

            }
        });

        message_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(MemberDetailsActivity.this, MessageActivity.class);
                intent1.putExtra("intent_flag", 0);
                intent1.putExtra("member_id",member_id);
                intent1.putExtra("member_name", name);
                startActivity(intent1);
            }
        });


    }

    private void getMemberDetails() {
        String url= "https://bdclean.winkytech.com/backend/api/getMemberDetails.php?user_ref=" + member_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("member_details = " , response);

                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                name = object.getString("full_name");
                                String designation = object.getString("designaiton");
                                photo = photoUrl+ (object.getString("profile_photo"));
                                String email = object.getString("email");
                                String contact = object.getString("contact");
                                String address = object.getString("address");
                                String division = object.getString("division");
                                String district = object.getString("district");
                                String upazilla = object.getString("upazilla");
                                String union = object.getString("union_name");
                                String village = object.getString("village");
                                String gender = object.getString("gender");
                                String occupation = object.getString("occupation");
                                String religion = object.getString("religion");
                                String member_code = object.getString("user_code");
                                event_count = Integer.parseInt(object.getString("join_count"));
                                int post_count = Integer.parseInt(object.getString("post_count"));
                                int complain_count = Integer.parseInt(object.getString("complain_count"));
                                int evaluation_count = Integer.parseInt(object.getString("evaluation_count"));



                                if (member_code.equals("")){
                                    user_id.setText("BDC-M");
                                } else {
                                    user_id.setText(member_code);
                                }


                                if (village.equals("null") && !union.equals("null") && !upazilla.equals("null") && !district.equals("null") && !division.equals("null")) {
                                    member_location.setText(union+ " , "+upazilla+ " , "+district+ " , "+division);
                                } else if (union.equals("null") && !upazilla.equals("null") && !district.equals("null") && !division.equals("null")) {
                                    member_location.setText(upazilla+ " , "+district+ " , "+division);
                                } else if (upazilla.equals("null") && !district.equals("null") && !division.equals("null")) {
                                    member_location.setText(district+ " , "+division);
                                } else if (district.equals("null") && !division.equals("null")) {
                                    member_location.setText(division);
                                } else if (village.equals("null") && union.equals("null") && upazilla.equals("null") && district.equals("null") && division.equals("null")) {
                                    locationLayout.setVisibility(View.GONE);
                                } else {
                                    member_location.setText(village + " , " +union+ " , "+upazilla+ " , "+district+ " , "+division);
                                }




                                member_name.setText(name);
                                member_designation.setText(designation);
                                member_email.setText(email);
                                member_contact.setText(contact);
                                member_address.setText(address);
                                member_gender.setText(gender);
                                member_occupation.setText(occupation);
                                member_religion.setText(religion);
                                completed_event.setText(String.valueOf(event_count));
                                completed_post.setText(String.valueOf(post_count));
                                complain_created.setText(String.valueOf(complain_count));
                                evaluation_create.setText(String.valueOf(evaluation_count));
                                Picasso.get().load(photo).into(member_photo);

                                if (event_count >= 4 && post_count>=16){
                                    member_status.setText("Active Member");
                                    member_status.setTextColor(getResources().getColor(R.color.green_2));
                                } else if (event_count>=3 && post_count>=12){
                                    member_status.setText("Regular Member");
                                    member_status.setTextColor(Color.parseColor("#2686cf"));
                                } else if (event_count>=2 && post_count>=8){
                                    member_status.setText("Irregular Member");
                                    member_status.setTextColor(Color.parseColor("#f1c232"));
                                } else if (event_count>=1 && post_count>=4){
                                    member_status.setText("Infrequent Member");
                                    member_status.setTextColor(Color.parseColor("#e69138"));
                                } else if (event_count>=0 && post_count>=0){
                                    member_status.setText("Inactive Member");
                                    member_status.setTextColor(getResources().getColor(R.color.red));
                                }
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
                toast_message.setText("Please check your internet connection or try again later");
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