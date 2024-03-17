package com.winkytech.bdclean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    String userContact,name,email,address,photo,occupation, division, district, upazila, union, village,designation, user_id, memberCode;
    TextView full_name, user_email, user_address, user_contact,user_occupation, current_location, user_designation, member_id, postTv, eventTv;
    String photoUrl = "https://bdclean.winkytech.com/resources/user/profile_pic/";
    ImageView profile_photo, status_iv;
    FloatingActionButton editProfile;
    int totalEvent, totalPost;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Contact = "contactKey";

    NetworkChangeListener networkChangeListener;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        Intent intent = getIntent();
        totalPost = intent.getIntExtra("total_post",0);
        totalEvent = intent.getIntExtra("total_event",0);
        memberCode = intent.getStringExtra("code");

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        userContact = (shared.getString(Contact, ""));
        name = shared.getString("name", "");
        email =  shared.getString("email", "");
        user_id =  shared.getString("user_id", "");
        address = shared.getString("address", "");
        photo = photoUrl+ shared.getString("profile_photo", "");
        occupation  = shared.getString("occupation", "");
        division= shared.getString("division", "");
        district= shared.getString("district", "");
        upazila =  shared.getString("upazila", "");
        union =  shared.getString("union", "");
        village =  shared.getString("village", "");
        designation = shared.getString("designation", "");

        System.out.println("occupation = " + occupation);


        full_name=findViewById(R.id.personal_name);
        user_email=findViewById(R.id.personal_email);
        user_address=findViewById(R.id.personal_address);
        user_contact=findViewById(R.id.personal_contact);
        editProfile = findViewById(R.id.edit_profile);
        user_occupation=findViewById(R.id.personal_occupation);
        eventTv=findViewById(R.id.total_event);
        postTv=findViewById(R.id.total_post);
        member_id=findViewById(R.id.user_id);
        status_iv=findViewById(R.id.status_iv);
        profile_photo=findViewById(R.id.personal_iv);
        user_designation= findViewById(R.id.user_designation);
        toolbar=findViewById(R.id.custom_toolbar);
        current_location = findViewById(R.id.current_location);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        full_name.setText(name);
        user_email.setText(email);
        user_address.setText(address);
        user_contact.setText(userContact);
        user_occupation.setText(occupation);
        user_designation.setText(designation);
        member_id.setText("Member ID : "+memberCode);
        Picasso.get().load(photo).into(profile_photo);
        current_location.setText("Division: " + division+ " , " +"District: "+ district+ " , " + "Upazila: "+upazila+" , " +"Union: "+union+" , "+"Village: "+village);
        postTv.setText("Total post: "+String.valueOf(totalPost));
        eventTv.setText("Total event: "+String.valueOf(totalEvent));
        if (totalEvent>= 4 && totalPost>=16){
            status_iv.setImageDrawable(getResources().getDrawable(R.drawable.active_status));
        } else if (totalEvent>=3 && totalPost>=12){
            status_iv.setImageDrawable(getResources().getDrawable(R.drawable.regular_status));

        } else if (totalEvent>=2 && totalPost>=8){
            status_iv.setImageDrawable(getResources().getDrawable(R.drawable.irregular_status));


        } else if (totalEvent>=1 && totalPost>=4){

            status_iv.setImageDrawable(getResources().getDrawable(R.drawable.infrequent_status));

        } else if (totalEvent>=0 && totalPost>=0){

            status_iv.setImageDrawable(getResources().getDrawable(R.drawable.inactive_status));

        }



        status_iv.setOnClickListener(v -> {

            Dialog dialog = new Dialog(PersonalProfileActivity.this);
            dialog.setContentView(R.layout.member_status_dialog_layout);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(true);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            Button close_btn = dialog.findViewById(R.id.close_btn);
            TextView event_count_tv = dialog.findViewById(R.id.event_count);
            TextView totalPost_tv = dialog.findViewById(R.id.post_count);
            TextView member_status = dialog.findViewById(R.id.member_status);

            event_count_tv.setText(String.valueOf(totalEvent));
            totalPost_tv.setText(String.valueOf(totalPost));

            if (totalEvent>= 4 && totalPost>=16){
                member_status.setText("Active Member");
                member_status.setTextColor(getResources().getColor(R.color.green_1));
            } else if (totalEvent>=3 && totalPost>=12){
                member_status.setText("Regular Member");
                member_status.setTextColor(Color.parseColor("#0000FF"));

            } else if (totalEvent>=2 && totalPost>=8){
                member_status.setText("Irregular Member");
                member_status.setTextColor(Color.parseColor("#FFFF00"));

            } else if (totalEvent>=1 && totalPost>=4){

                member_status.setText("Infrequent Member");
                member_status.setTextColor(Color.parseColor("#FFA500"));

            } else if (totalEvent>=0 && totalPost>=0){

                member_status.setText("Inactive Member");
                member_status.setTextColor(Color.parseColor("#FF0000"));
            }

            close_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        });




        editProfile.setOnClickListener(v -> {
            editProfile();
        });

    }

    private void editProfile() {

        Dialog dialog = new Dialog(PersonalProfileActivity.this);
        dialog.setContentView(R.layout.profile_edit_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        EditText user_name = dialog.findViewById(R.id.update_name);
        EditText user_email = dialog.findViewById(R.id.update_email);
        EditText user_contact = dialog.findViewById(R.id.update_contact);
        Button occupation_spinner = dialog.findViewById(R.id.occupation_spinner);
        CircleImageView profileImage = dialog.findViewById(R.id.update_photo);
        Button update_btn = dialog.findViewById(R.id.update_btn);
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