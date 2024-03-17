package com.winkytech.bdclean;

import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.ArrayList;

public class UserInfoActivity_3 extends AppCompatActivity {

    Button next_btn;
    TextView event_tv, toast_message;
    Toolbar toolbar;
    EditText  refer_to;
    Button select_blood, select_t_shirt, select_occupation, select_gender, select_religion;
    ArrayList<String> shirt_size, blood_group, religion, occupation, gender;
    String size="", group="", contact,ev_name, ev_address,start_date,end_date, cover_pic;
    int counter, occupation_ref = 0, religion_ref= 0, gender_ref= 0, ev_id;
    Dialog dialog;
    ProgressBar progressbar;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info3);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        contact = shared.getString("contactKey","");
        System.out.println("Contact = " + contact);

        Intent intent = getIntent();
        ev_id = intent.getIntExtra("ev_id",0);
        ev_name = intent.getStringExtra("ev_name");
        ev_address = intent.getStringExtra("ev_address");
        start_date = intent.getStringExtra("date_start");
        end_date = intent.getStringExtra("end_date");
        cover_pic = intent.getStringExtra("cover_pic");
        counter = intent.getIntExtra("counter",0);

        next_btn = findViewById(R.id.next_btn);
        event_tv=findViewById(R.id.event_tv);
        toolbar = findViewById(R.id.custom_toolbar);
        refer_to = findViewById(R.id.ref_by);
        select_blood = findViewById(R.id.select_blood);
        select_t_shirt = findViewById(R.id.select_shirt_size);
        select_occupation = findViewById(R.id.select_occupation);
        select_gender = findViewById(R.id.select_gender);
        select_religion = findViewById(R.id.religion_spinner);
        progressbar = findViewById(R.id.progressbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        event_tv.setText(String.valueOf(counter));
        if (counter==4){
            event_tv.setText(String.valueOf(counter));
        }

        shirt_size = new ArrayList<>();
        shirt_size.add("S");
        shirt_size.add("M");
        shirt_size.add("L");
        shirt_size.add("XL");
        shirt_size.add("XXL");

        select_t_shirt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(UserInfoActivity_3.this);
                dialog.setContentView(R.layout.custom_spinner_layout_2);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(UserInfoActivity_3.this, android.R.layout.simple_list_item_1,shirt_size);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        size  = adapter.getItem(position);
                        select_t_shirt.setText(size);
                        dialog.dismiss();
                    }
                });
            }
        });

        blood_group = new ArrayList<>();
        blood_group.add("A+");
        blood_group.add("A-");
        blood_group.add("B+");
        blood_group.add("B-");
        blood_group.add("AB+");
        blood_group.add("AB-");
        blood_group.add("O+");
        blood_group.add("O-");

        select_blood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(UserInfoActivity_3.this);
                dialog.setContentView(R.layout.custom_spinner_layout_2);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(UserInfoActivity_3.this, android.R.layout.simple_list_item_1,blood_group);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        group = blood_group.get(position);
                        select_blood.setText(group);
                        dialog.dismiss();
                    }
                });
            }
        });

        religion=new ArrayList<>();
        religion.add("Islam");
        religion.add("Hindu");
        religion.add("Christian");
        religion.add("Buddha");
        select_religion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(UserInfoActivity_3.this);
                dialog.setContentView(R.layout.custom_spinner_layout_2);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(UserInfoActivity_3.this, android.R.layout.simple_list_item_1,religion);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        String item  = adapter.getItem(position);
                        switch (item){
                            case "Islam" :
                                religion_ref = 1;
                                select_religion.setText(item);
                                dialog.dismiss();
                                break;
                            case "Hindu" :
                                religion_ref = 2;
                                select_religion.setText(item);
                                dialog.dismiss();
                                break;
                            case "Christian" :
                                religion_ref = 3;
                                select_religion.setText(item);
                                dialog.dismiss();
                                break;
                            case "Buddha" :
                                religion_ref = 4;
                                select_religion.setText(item);
                                dialog.dismiss();
                                break;
                        }
                    }
                });
            }
        });

        gender=new ArrayList<>();
        gender.add("Male");
        gender.add("Female");
        select_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(UserInfoActivity_3.this);
                dialog.setContentView(R.layout.custom_spinner_layout_2);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(UserInfoActivity_3.this, android.R.layout.simple_list_item_1,gender);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        String item  = adapter.getItem(position);
                        switch (item){
                            case "Male" :
                                gender_ref = 1;
                                select_gender.setText(item);
                                dialog.dismiss();
                                break;
                            case "Female" :
                                gender_ref = 2;
                                select_gender.setText(item);
                                dialog.dismiss();
                                break;
                        }
                    }
                });
            }
        });

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
                dialog = new Dialog(UserInfoActivity_3.this);
                dialog.setContentView(R.layout.custom_spinner_layout_2);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(UserInfoActivity_3.this, android.R.layout.simple_list_item_1,occupation);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        String item  = adapter.getItem(position);
                        switch (item){
                            case "Farmer" :
                                occupation_ref = 2;
                                select_occupation.setText(item);
                                dialog.dismiss();
                                break;
                            case "Businessman" :
                                occupation_ref = 3;
                                select_occupation.setText(item);
                                dialog.dismiss();
                                break;
                            case "Service Holder (Govt.)" :
                                occupation_ref = 4;
                                select_occupation.setText(item);
                                dialog.dismiss();
                                break;
                            case "Service Holder (Private Company)" :
                                occupation_ref = 5;
                                select_occupation.setText(item);
                                dialog.dismiss();
                                break;
                            case "Enterpreneur" :
                                occupation_ref = 6;
                                select_occupation.setText(item);
                                dialog.dismiss();
                                break;
                            case "Home Maker" :
                                occupation_ref = 7;
                                select_occupation.setText(item);
                                dialog.dismiss();
                                break;
                            case "Social Worker" :
                                occupation_ref = 8;
                                select_occupation.setText(item);
                                dialog.dismiss();
                                break;
                            case "Technical Worker" :
                                occupation_ref = 9;
                                select_occupation.setText(item);
                                dialog.dismiss();
                                break;
                            case "Other" :
                                occupation_ref = 10;
                                select_occupation.setText(item);
                                dialog.dismiss();
                                break;
                            case "Student" :
                                occupation_ref = 1;
                                select_occupation.setText(item);
                                dialog.dismiss();
                                break;
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


    }

    private void saveData() {
        progressbar.setVisibility(View.VISIBLE);
        String ref = refer_to.getText().toString().trim();

        if (gender_ref == 0) {
            buttonFocus(select_gender, "select gender");
        } else if (group.equals("")) {
            buttonFocus(select_blood, "select blood group");
        } else if ( size.equals("")) {
            buttonFocus(select_t_shirt, "select t-shirt size");
        } else if (occupation_ref == 0) {
            buttonFocus(select_occupation, "select occupation");
        } else if (religion_ref ==0) {
            buttonFocus(select_religion, "select religion");
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
                    field[1] = "ref";
                    field[2] = "shirt_size";
                    field[3] = "blood_group";
                    field[4] = "gender_ref";
                    field[5] = "occupation_ref";
                    field[6] = "religion_ref";

                    //Creating array for data
                    String[] data = new String[7];
                    data[0] = contact;
                    data[1] = ref;
                    data[2] = size;
                    data[3] = group;
                    data[4] = String.valueOf(gender_ref);
                    data[5] = String.valueOf(occupation_ref);
                    data[6] = String.valueOf(religion_ref);

                    PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/updateUserProfile_3.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult().trim();
                            if (result.equals("success")) {

                                Toast toast = new Toast(UserInfoActivity_3.this);
                                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout,findViewById(R.id.custom_toast));
                                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                                toast_message.setText("Profile Updated");
                                toast.setView(toast_view);
                                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.show();

                                sharedpreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("occupation", String.valueOf(occupation_ref));
                                editor.apply();

                                progressbar.setVisibility(View.GONE);
                                Intent intent = new Intent(getApplicationContext(), NewUsersEventActivity.class);
                                intent.putExtra("ev_id", ev_id );
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
                this.finish();
                overridePendingTransition(R.anim.no_anim,R.anim.push_right_in);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}