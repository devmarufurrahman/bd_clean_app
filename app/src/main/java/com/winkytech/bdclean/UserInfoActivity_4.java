package com.winkytech.bdclean;

import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class UserInfoActivity_4 extends AppCompatActivity {

    Button next_btn, back_btn;
    TextView event_tv, toast_message;
    Toolbar toolbar;
    EditText present_institution, highest_education, education_institution, present_address, permanent_address;
    Button select_occupation;
    int occupation_ref;
    ArrayList<String> occupation = new ArrayList<>();
    String contact,ev_id,ev_name, ev_address, start_date , end_date, cover_pic;
    int counter;
    Dialog dialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info4);
        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        contact = shared.getString("contactKey","");
        System.out.println("Contact = " + contact);

        Intent intent = getIntent();
         ev_id = intent.getStringExtra("ev_id");
         ev_name = intent.getStringExtra("ev_name");
         ev_address = intent.getStringExtra("ev_address");
         start_date = intent.getStringExtra("date_start");
         end_date = intent.getStringExtra("end_date");
         cover_pic = intent.getStringExtra("cover_pic");
         counter = intent.getIntExtra("counter",0);

        next_btn = findViewById(R.id.next_btn);
        event_tv=findViewById(R.id.event_tv);
        back_btn = findViewById(R.id.back_btn);
        toolbar = findViewById(R.id.custom_toolbar);
        present_institution = findViewById(R.id.present_institution);
        highest_education= findViewById(R.id.education);
        education_institution = findViewById(R.id.education_institution);
        present_address = findViewById(R.id.present_address);
        permanent_address = findViewById(R.id.permanent_address);
        select_occupation = findViewById(R.id.select_occupation);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        event_tv.setText(String.valueOf(counter));
        if (counter==4){
            event_tv.setText(String.valueOf(counter));
        }



        occupation = new ArrayList<>();
        occupation.add("Student");
        occupation.add("Farmer");
        occupation.add("Businessman");
        occupation.add("Service Holder (Govt.)");
        occupation.add("Service Holder (Private Company)");
        occupation.add("Enterpreneur");
        occupation.add("Home Maker");
        occupation.add("Social Worker");
        occupation.add("Technical Worker");
        occupation.add("Other");

        select_occupation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(UserInfoActivity_4.this);
                dialog.setContentView(R.layout.custom_spinner_layout_2);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(UserInfoActivity_4.this, android.R.layout.simple_list_item_1,occupation);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        String name = adapter.getItem(position);
                        if (name.equals("Student")){
                            occupation_ref=1;
                            dialog.dismiss();
                            select_occupation.setText(name);
                        }
                        if (name.equals("Farmer")){
                            occupation_ref=2;
                            dialog.dismiss();
                            select_occupation.setText(name);
                        }
                        if (name.equals("Businessman")){
                            occupation_ref=3;
                            dialog.dismiss();
                            select_occupation.setText(name);
                        }
                        if (name.equals("Service Holder (Govt.)")){
                            occupation_ref=4;
                            dialog.dismiss();
                            select_occupation.setText(name);
                        }
                        if (name.equals("Service Holder (Private Company)")){
                            occupation_ref=5;
                            dialog.dismiss();
                            select_occupation.setText(name);
                        }
                        if (name.equals("Enterpreneur")){
                            occupation_ref=6;
                            dialog.dismiss();
                            select_occupation.setText(name);
                        }
                        if (name.equals("Home Maker")){
                            occupation_ref=7;
                            dialog.dismiss();
                            select_occupation.setText(name);
                        }
                        if (name.equals("Social Worker")){
                            occupation_ref=8;
                            dialog.dismiss();
                            select_occupation.setText(name);
                        }
                        if (name.equals("Technical Worker")){
                            occupation_ref=9;
                            dialog.dismiss();
                            select_occupation.setText(name);
                        }
                        if (name.equals("Other")){
                            occupation_ref=10;
                            dialog.dismiss();
                            select_occupation.setText(name);
                        }
                    }
                });
            }
        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.no_anim,R.anim.push_right_in);
            }
        });
    }

    private void saveData() {

        String current_institute = present_institution.getText().toString().trim();
        String higher_education = highest_education.getText().toString().trim();
        String edu_institute = education_institution.getText().toString().trim();
        String current_address = present_address.getText().toString().trim();
        String per_add = permanent_address.getText().toString().trim();

        if (current_address.equals("") || current_institute.equals("") || higher_education.equals("") || edu_institute.equals("") || per_add.equals("")
                || occupation_ref==0){
            Toast toast = new Toast(getApplicationContext());
            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
            toast_message.setText("All fields are required");
            toast.setView(toast_view);
            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        } else {

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void run() {

                    //Starting Write and Read data with URL
                    //Creating array for parameters
                    String[] field = new String[7];
                    field[0] = "contact";
                    field[1] = "occupation_ref";
                    field[2] = "pre_add";
                    field[3] = "per_add";
                    field[4] = "education";
                    field[5] = "edu_ins";
                    field[6] = "curr_institute";

                    //Creating array for data
                    String[] data = new String[7];
                    data[0] = contact;
                    data[1] = String.valueOf(occupation_ref);
                    data[2] = current_address;
                    data[3] = per_add;
                    data[4] = higher_education;
                    data[5] = edu_institute;
                    data[6] = current_institute;

                    PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/updateUserProfile_4.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult().trim();
                            if (result.equals("success")) {

                                Toast toast = new Toast(UserInfoActivity_4.this);
                                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout,findViewById(R.id.custom_toast));
                                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                                toast_message.setText("Profile Updated");
                                toast.setView(toast_view);
                                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.show();

                                Intent intent = new Intent(getApplicationContext(), NewUsersEventActivity.class);
                                intent.putExtra("ev_id", ev_id );
                                intent.putExtra("ev_name", ev_name);
                                intent.putExtra("ev_address", ev_address);
                                intent.putExtra("start_date", start_date);
                                intent.putExtra("end_date", end_date);
                                intent.putExtra("cover_pic", cover_pic);
                                intent.putExtra("counter", counter);
                                startActivity(intent);
                                overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);

                            } else {
                                Log.i("PutData", result);
                                Toast toast = new Toast(getApplicationContext());
                                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
                                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                                toast_message.setText("Failed To Create Evaluation!!!");
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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                overridePendingTransition(R.anim.no_anim,R.anim.push_right_in);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}