package com.winkytech.bdclean;

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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PersonalProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    String userContact,name,email,address,photo,occupation, division, district, upazila, union, village,designation, user_id;
    TextView full_name, user_email, user_address, user_contact,user_occupation, current_location, user_designation, member_id;
    String photoUrl = "https://bdclean.winkytech.com/resources/user/profile_pic/";
    ImageView profile_photo;

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
        user_occupation=findViewById(R.id.personal_occupation);
        member_id=findViewById(R.id.user_id);
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
        member_id.setText("Member ID : "+user_id);
        Picasso.get().load(photo).into(profile_photo);
        current_location.setText("Division: " + division+ " , " +"District: "+ district+ " , " + "Upazila: "+upazila+" , " +"Union: "+union+" , "+"Village: "+village);

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