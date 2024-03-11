package com.winkytech.bdclean;

import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserInfoActivity_2 extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    Button next_btn, birth_date;
    TextView event_tv, toast_message;
    Toolbar toolbar;
    EditText nationality, highest_education,education_institute, nid_no;
    String ev_id,ev_name, ev_address,start_date,end_date, cover_pic, contact ;
    int day, month, year;
    int myday, myMonth, myYear;
    int counter;
    String date_birth = "";
    ProgressBar progressbar;

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info2);

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

        next_btn = findViewById(R.id.submit_btn);
        //back_btn = findViewById(R.id.back_btn);
        event_tv=findViewById(R.id.event_tv);
        toolbar  = findViewById(R.id.custom_toolbar);

        highest_education = findViewById(R.id.highest_education);
        education_institute = findViewById(R.id.education_institute);
        nid_no = findViewById(R.id.nid_number);
        nationality = findViewById(R.id.nationality);
        progressbar = findViewById(R.id.progressbar);

        birth_date = findViewById(R.id.date_birth);

        birth_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(UserInfoActivity_2.this, UserInfoActivity_2.this,year, month,day);
                datePickerDialog.show();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        event_tv.setText(String.valueOf("Completed events: "+ counter));
        if (counter==4){
            event_tv.setText(String.valueOf(counter));
        }
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

//        back_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//                overridePendingTransition(R.anim.no_anim,R.anim.push_right_in);
//            }
//        });
    }

    private void saveData() {
        progressbar.setVisibility(View.VISIBLE);
        String user_nationality = nationality.getText().toString().trim();
        String education = highest_education.getText().toString().trim();
        String institute = education_institute.getText().toString().trim();
        String nid = nid_no.getText().toString().trim();

        if (user_nationality.equals("")){
            progressbar.setVisibility(View.GONE);
            fieldFocus(nationality, "Enter your nationality");
        } else if (education.equals("")) {
            progressbar.setVisibility(View.GONE);
            fieldFocus(highest_education, "Enter your Higher Education");
        } else if (institute.equals("")) {
            progressbar.setVisibility(View.GONE);
            fieldFocus(education_institute, "Enter your Higher Education");
        } else if (nid.equals("")) {
            progressbar.setVisibility(View.GONE);
            fieldFocus(nid_no, "Enter your NID NO.");
        } else if (date_birth.equals("")) {
            progressbar.setVisibility(View.GONE);
            buttonFocus(birth_date,"Birthday Selection");
        } else {

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void run() {

                    //Starting Write and Read data with URL
                    //Creating array for parameters
                    String[] field = new String[6];
                    field[0] = "nationality";
                    field[1] = "education";
                    field[2] = "institute";
                    field[3] = "nid";
                    field[4] = "date_birth";
                    field[5] = "contact";

                    //Creating array for data
                    String[] data = new String[6];
                    data[0] = user_nationality;
                    data[1] = education;
                    data[2] = institute;
                    data[3] = nid;
                    data[4] = date_birth;
                    data[5] = contact;

                    PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/updateUserProfile_2.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult().trim();
                            if (result.equals("success")) {

                                Toast toast = new Toast(UserInfoActivity_2.this);
                                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout,findViewById(R.id.custom_toast));
                                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                                toast_message.setText("Profile Updated");
                                toast.setView(toast_view);
                                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.show();

                                progressbar.setVisibility(View.VISIBLE);
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
                                finish();

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


    // field validation
    private void buttonFocus(Button btn, String error) {
        btn.setError(error);
        btn.setFocusable(true);
        btn.setFocusableInTouchMode(true);
        btn.requestFocus();
    }
    private void fieldFocus(EditText field, String error) {
        field.setError(error);
        field.requestFocus();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                overridePendingTransition(R.anim.no_anim,R.anim.push_right_in);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = dayOfMonth;
        myMonth = month;
        date_birth = (myYear + "-" + (myMonth+1) + "-" + myday);
        birth_date.setText(date_birth);
    }
}