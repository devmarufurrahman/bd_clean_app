package com.winkytech.bdclean;

import static com.google.android.material.color.utilities.MaterialDynamicColors.error;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

@SuppressLint("SetTextI18n")
public class NewMemberActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private  String position_id,user_id,dept_level;
    Toolbar toolbar;
    EditText member_name,member_email,member_contact,member_address,member_password,member_confirm_password;
    Button select_gender,select_religion,select_occupation,select_division,select_district,select_upazilla,select_union, select_ward, zone_spinner,select_village,select_parent, select_blood, select_shirt;
    EditText father_name, mother_name, father_contact, mother_contact,nationality, education, education_institute, current_institute, facebook_id, nid, refer_by;
    ImageView member_photo;
    Button create_member,cancel, date_birth;
    Bitmap bitmap;
    String encodedImage = "",pos_name,group, size, birth_date;
    public JSONArray division_result,district_result,upazila_result,union_result, wad_result,village_result,parent_result;
    TextView position_tv,toast_message, village_tv;
    ProgressBar progressBar;
    Dialog dialog;
    List<String> gender, religion,occupation,division, district,upazila,union, ward,village,parent, blood_group, shirt_size;
    int gender_ref,religion_ref,occupation_ref,district_ref=0,division_ref=0,upazila_ref=0,zone_ref=0, union_ref=0,village_ref=0,parent_ref=0;
    int day, month, year,myday, myMonth, myYear;
    int type_ref;
    LinearLayout location_type_layout, location_type2_layout, upazilaLayout, zoneLayout, wardLayout, unionLayout, villageLayout;

    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_member);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        Intent intent = getIntent();
        position_id = intent.getStringExtra("position_id");
        user_id=intent.getStringExtra("user_id");
        pos_name=intent.getStringExtra("pos_name");
        dept_level=intent.getStringExtra("dept_level");
        System.out.println("position_id = " + position_id);
        System.out.println("user_id = " + user_id);
        System.out.println("dept_level_pos = " + dept_level);

        toolbar=findViewById(R.id.custom_toolbar);
        member_name=findViewById(R.id.member_name);
        member_email=findViewById(R.id.member_email);
        member_contact=findViewById(R.id.member_contact);
        member_address=findViewById(R.id.member_address);
        member_password=findViewById(R.id.member_password);
        member_confirm_password=findViewById(R.id.member_con_password);
        select_gender=findViewById(R.id.gender_spinner);
        select_religion=findViewById(R.id.religion_spinner);
        select_occupation=findViewById(R.id.occupation_spinner);
        select_division=findViewById(R.id.division_spinner);
        village_tv = findViewById(R.id.village_tv);
        location_type_layout=findViewById(R.id.location_type_layout);
        location_type2_layout=findViewById(R.id.location_type2_layout);
        upazilaLayout=findViewById(R.id.upazilaLayout);
        zoneLayout=findViewById(R.id.zoneLayout);
        wardLayout=findViewById(R.id.wardLayout);
        unionLayout=findViewById(R.id.unionLayout);
        villageLayout=findViewById(R.id.villageLayout);
        select_district=findViewById(R.id.district_spinner);
        select_upazilla=findViewById(R.id.upazilla_spinner);
        select_union=findViewById(R.id.union_spinner);
        select_ward=findViewById(R.id.ward_spinner);
        zone_spinner=findViewById(R.id.zone_spinner);
        father_name = findViewById(R.id.father_name);
        mother_name = findViewById(R.id.mother_name);
        father_contact = findViewById(R.id.father_contact);
        mother_contact = findViewById(R.id.mother_contact);
        nationality = findViewById(R.id.nationality);
        education = findViewById(R.id.education);
        education_institute = findViewById(R.id.education_institute);
        current_institute = findViewById(R.id.current_institute);
        facebook_id = findViewById(R.id.fb_link);
        nid = findViewById(R.id.nid_number);
        refer_by = findViewById(R.id.ref_by);
        date_birth = findViewById(R.id.date_birth);
        select_village=findViewById(R.id.village_spinner);
        select_parent = findViewById(R.id.parent_spinner);
        member_photo=findViewById(R.id.member_photo);
        select_blood = findViewById(R.id.blood_group);
        select_shirt = findViewById(R.id.shirt_size);
        create_member=findViewById(R.id.create_member);
        cancel=findViewById(R.id.cancel_btn);
        position_tv = findViewById(R.id.position_tv);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        position_tv.setText(pos_name);

        division = new ArrayList<>();
        district = new ArrayList<>();
        upazila = new ArrayList<>();
        union = new ArrayList<>();
        ward = new ArrayList<>();
        village = new ArrayList<>();
        parent = new ArrayList<>();
        gender = new ArrayList<>();
        religion = new ArrayList<>();
        occupation = new ArrayList<>();
        blood_group = new ArrayList<>();
        shirt_size = new ArrayList<>();

        date_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewMemberActivity.this, NewMemberActivity.this,year, month,day);
                datePickerDialog.show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                member_name.setText("");
                member_email.setText("");
                member_contact.setText("");
                member_address.setText("");
                member_password.setText("");
                member_confirm_password.setText("");
                member_photo.setImageResource(R.drawable.ic_baseline_image_24);
                finish();
            }
        });

        gender=new ArrayList<>();
        gender.add("Male");
        gender.add("Female");
        select_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(NewMemberActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout_2);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(NewMemberActivity.this, android.R.layout.simple_list_item_1,gender);
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

        shirt_size = new ArrayList<>();
        shirt_size.add("S");
        shirt_size.add("M");
        shirt_size.add("L");
        shirt_size.add("XL");
        shirt_size.add("XXL");
        select_shirt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(NewMemberActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout_2);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(NewMemberActivity.this, android.R.layout.simple_list_item_1,shirt_size);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        size  = adapter.getItem(position);
                        select_shirt.setText(size);
                        dialog.dismiss();
                    }
                });
            }
        });

        blood_group = new ArrayList<String>();
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
                dialog = new Dialog(NewMemberActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout_2);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(NewMemberActivity.this, android.R.layout.simple_list_item_1,blood_group);
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
                dialog = new Dialog(NewMemberActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout_2);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(NewMemberActivity.this, android.R.layout.simple_list_item_1,religion);
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
                dialog = new Dialog(NewMemberActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout_2);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(NewMemberActivity.this, android.R.layout.simple_list_item_1,occupation);
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

        getDivisionData();

        select_division.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(NewMemberActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView= dialog.findViewById(R.id.list_view);
                EditText editText= dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(NewMemberActivity.this, android.R.layout.simple_list_item_1,division);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        String item  = adapter.getItem(position);
                        RadioButton cityBtn, districtBtn;
                        cityBtn = findViewById(R.id.city_dialog_btn);
                        districtBtn = findViewById(R.id.district_dialog_btn);

                        cityBtn.setChecked(false);
                        districtBtn.setChecked(false);
                        location_type_layout.setVisibility(View.GONE);
                        wardLayout.setVisibility(View.GONE);
                        upazilaLayout.setVisibility(View.GONE);
                        select_district.setText("");
                        select_upazilla.setText("");
                        select_ward.setText("");
                        select_union.setText("");
                        select_village.setText("");
                        location_type2_layout.setVisibility(View.GONE);
                        unionLayout.setVisibility(View.GONE);
                        villageLayout.setVisibility(View.GONE);
                        zoneLayout.setVisibility(View.GONE);

                        for (int i =0; i<division_result.length();i++){
                            try {
                                JSONObject jsonObject = division_result.getJSONObject(i);

                                if (jsonObject.getString("name").equals(item)){
                                    division_ref = Integer.parseInt(jsonObject.getString("id"));

                                    district.clear();
                                    select_division.setText(jsonObject.getString("name"));
                                    dialog.dismiss();
                                    select_division.setError(null);

                                    location_type_layout.setVisibility(View.VISIBLE);


                                    TextView district_tv = findViewById(R.id.district_tv);


                                    cityBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            district.clear();
                                            union.clear();
                                            type_ref = 2;

                                            getDistrictData(division_ref,type_ref);
//                                            location_type_layout.setVisibility(View.GONE);
                                            wardLayout.setVisibility(View.GONE);
                                            district_tv.setText("City Corporation");
                                            unionLayout.setVisibility(View.GONE);
                                            cityBtn.setChecked(true);
                                            districtBtn.setChecked(false);
                                            location_type2_layout.setVisibility(View.GONE);
                                            villageLayout.setVisibility(View.GONE);
                                            upazilaLayout.setVisibility(View.GONE);


                                            select_district.setText("");
                                            select_upazilla.setText("");
                                            select_ward.setText("");
                                            select_union.setText("");
                                            select_village.setText("");
                                        }
                                    });

                                    districtBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            district.clear();
                                            type_ref = 1;
                                            getDistrictData(division_ref,type_ref);
//                                            location_type_layout.setVisibility(View.GONE);
                                            wardLayout.setVisibility(View.GONE);
                                            district_tv.setText("District");
                                            unionLayout.setVisibility(View.GONE);
                                            districtBtn.setChecked(true);
                                            cityBtn.setChecked(false);
                                            zoneLayout.setVisibility(View.GONE);
                                            villageLayout.setVisibility(View.GONE);
                                            union.clear();

                                            select_district.setText("");
                                            select_upazilla.setText("");
                                            select_ward.setText("");
                                            select_union.setText("");
                                            select_village.setText("");
                                        }
                                    });
                                }

                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        // Dismiss dialog
                    }
                });
            }
        });


        select_district.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(NewMemberActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(NewMemberActivity.this, android.R.layout.simple_list_item_1,district);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        String item  = adapter.getItem(position);
                        wardLayout.setVisibility(View.GONE);
                        upazilaLayout.setVisibility(View.GONE);
                        select_upazilla.setText("");
                        select_ward.setText("");
                        select_union.setText("");
                        select_village.setText("");
                        location_type2_layout.setVisibility(View.GONE);
                        unionLayout.setVisibility(View.GONE);
                        villageLayout.setVisibility(View.GONE);
                        zoneLayout.setVisibility(View.GONE);

                        for (int i =0; i<district_result.length();i++){
                            try {
                                JSONObject jsonObject = district_result.getJSONObject(i);

                                if (jsonObject.getString("name").equals(item)){
                                    district_ref = Integer.parseInt(jsonObject.getString("id"));

                                    upazila.clear();
                                    select_district.setText(jsonObject.getString("name"));
                                    dialog.dismiss();
                                    select_district.setError(null);

                                    if (type_ref == 2){


                                        getUnionData(district_ref, type_ref);
                                        upazilaLayout.setVisibility(View.GONE);
                                        wardLayout.setVisibility(View.VISIBLE);
                                        type_ref = 2;
                                    } else {
                                        getUpaziladata(district_ref,1);
                                        upazilaLayout.setVisibility(View.VISIBLE);
                                        zoneLayout.setVisibility(View.GONE);
                                    }


                                }

                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        // Dismiss dialog
                    }
                });
            }
        });

        select_upazilla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(NewMemberActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(NewMemberActivity.this, android.R.layout.simple_list_item_1,upazila);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        String item  = adapter.getItem(position);
                        RadioButton cityBtn, districtBtn;
                        cityBtn = findViewById(R.id.municipal_btn);
                        districtBtn = findViewById(R.id.union_btn);
                        cityBtn.setChecked(false);
                        districtBtn.setChecked(false);
                        select_union.setText("");
                        select_village.setText("");
                        location_type2_layout.setVisibility(View.GONE);
                        unionLayout.setVisibility(View.GONE);
                        villageLayout.setVisibility(View.GONE);

                        for (int i =0; i<upazila_result.length();i++){
                            try {
                                JSONObject jsonObject = upazila_result.getJSONObject(i);

                                if (jsonObject.getString("name").equals(item)){
                                    upazila_ref = Integer.parseInt(jsonObject.getString("id"));
                                    union.clear();
                                    getUnionData(upazila_ref, type_ref);

                                    select_upazilla.setText(jsonObject.getString("name"));
                                    dialog.dismiss();
                                    select_upazilla.setError(null);



                                    location_type2_layout.setVisibility(View.VISIBLE);

                                    TextView union_tv = findViewById(R.id.union_tv);

                                    cityBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            union.clear();
                                            type_ref = 2;

                                            getUnionData(upazila_ref,type_ref);
//                                            location_type2_layout.setVisibility(View.GONE);
                                            unionLayout.setVisibility(View.VISIBLE);

                                            union_tv.setText("Municipal");
                                            cityBtn.setChecked(true);
                                            select_union.setText("");
                                            select_village.setText("");
                                            village_tv.setText("Ward");
                                        }
                                    });


                                    districtBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            union.clear();
                                            type_ref = 1;
                                            getUnionData(upazila_ref,type_ref);
//                                            location_type2_layout.setVisibility(View.GONE);
                                            unionLayout.setVisibility(View.VISIBLE);

                                            union_tv.setText("Union");
                                            districtBtn.setChecked(true);
                                            select_union.setText("");
                                            select_village.setText("");
                                            village_tv.setText("Village");
                                        }
                                    });
                                }

                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        // Dismiss dialog
                    }
                });
            }
        });

        select_ward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(NewMemberActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(NewMemberActivity.this, android.R.layout.simple_list_item_1,union);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        String item  = adapter.getItem(position);
                        select_village.setText("");


                        for (int i =0; i<union_result.length();i++){
                            try {
                                JSONObject jsonObject = union_result.getJSONObject(i);
                                String json = jsonObject.getString("name");

                                if (json.equals(item)) {
                                    zone_ref = Integer.parseInt(jsonObject.getString("zone_ref"));
                                    union_ref = Integer.parseInt(jsonObject.getString("id"));
                                    getParentData(dept_level, division_ref, district_ref, upazila_ref, village_ref, union_ref);
                                    dialog.dismiss();
                                    select_ward.setText(item);
                                    getZoneData(zone_ref);
                                    select_ward.setError(null);
                                }



                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        // Dismiss dialog
                    }
                });
            }
        });

        select_union.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(NewMemberActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(NewMemberActivity.this, android.R.layout.simple_list_item_1,union);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        String item  = adapter.getItem(position);
                        select_village.setText("");

                        for (int i =0; i<union_result.length();i++){
                            try {
                                JSONObject jsonObject = union_result.getJSONObject(i);

                                if (jsonObject.getString("name").equals(item)) {
                                    union_ref = Integer.parseInt(jsonObject.getString("id"));
                                    village.clear();
                                    getVillageData(union_ref);
                                    select_union.setText(jsonObject.getString("name"));
                                    dialog.dismiss();
                                    villageLayout.setVisibility(View.VISIBLE);
                                    select_union.setError(null);
                                }

                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        // Dismiss dialog
                    }
                });
            }
        });

        select_village.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(NewMemberActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(NewMemberActivity.this, android.R.layout.simple_list_item_1,village);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        String item  = adapter.getItem(position);

                        for (int i =0; i<village_result.length();i++){
                            try {
                                JSONObject jsonObject = village_result.getJSONObject(i);

                                if (jsonObject.getString("name").equals(item)){
                                    village_ref = Integer.parseInt(jsonObject.getString("id"));
                                    getParentData(dept_level, division_ref, district_ref, upazila_ref, village_ref, union_ref);
                                    select_village.setText(jsonObject.getString("name"));
                                    dialog.dismiss();
                                    select_village.setError(null);
                                }

                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        // Dismiss dialog
                    }
                });
            }
        });

        select_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(NewMemberActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(NewMemberActivity.this, android.R.layout.simple_list_item_1,parent);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        String item  = adapter.getItem(position);

                        try {
                            JSONObject jsonObject = parent_result.getJSONObject(position);

                            parent_ref = Integer.parseInt(jsonObject.getString("user_id"));
                            select_parent.setText(jsonObject.getString("full_name"));
                            dialog.dismiss();
                            System.out.println(parent_ref );


                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        // Dismiss dialog
                    }
                });
            }
        });

        member_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    Dexter.withActivity(NewMemberActivity.this)
//                        .withPermission(Manifest.permission.READ_MEDIA_IMAGES)
//                        .withListener(new PermissionListener() {
//                            @Override
//                            public void onPermissionGranted(PermissionGrantedResponse response) {
//
//                                Intent intent = new Intent(Intent.ACTION_PICK);
//                                intent.setType("image/*");
//                                startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
//                            }
//                            @Override
//                            public void onPermissionDenied(PermissionDeniedResponse response) {
//
//                                //Toast.makeText(NewMemberActivity.this, "Permission Denied!!", Toast.LENGTH_SHORT).show();
//                                Toast toast = new Toast(getApplicationContext());
//                                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
//                                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
//                                toast_message.setText("Permission Denied!!");
//                                toast.setView(toast_view);
//                                toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
//                                toast.setDuration(Toast.LENGTH_SHORT);
//                                toast.show();
//                            }
//
//                            @Override
//                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//
//                                token.continuePermissionRequest();
//                            }
//                        }).check();
//                } else {
//
//                    Dexter.withActivity(NewMemberActivity.this)
//                            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                            .withListener(new PermissionListener() {
//                                @Override
//                                public void onPermissionGranted(PermissionGrantedResponse response) {
//
//                                    Intent intent = new Intent(Intent.ACTION_PICK);
//                                    intent.setType("image/*");
//                                    startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
//
//                                }
//                                @Override
//                                public void onPermissionDenied(PermissionDeniedResponse response) {
//
//                                    //Toast.makeText(NewMemberActivity.this, "Permission Denied!!", Toast.LENGTH_SHORT).show();
//                                    Toast toast = new Toast(getApplicationContext());
//                                    View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
//                                    toast_message=toast_view.findViewById(R.id.custom_toast_tv);
//                                    toast_message.setText("Permission Denied!!");
//                                    toast.setView(toast_view);
//                                    toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
//                                    toast.setDuration(Toast.LENGTH_SHORT);
//                                    toast.show();
//                                }
//
//                                @Override
//                                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//
//                                    token.continuePermissionRequest();
//                                }
//                            }).check();
//                }

                float aspectRatio = 10.0f;

                ImagePicker.with(NewMemberActivity.this)

                    .crop(aspectRatio, 10.0f)
                    .compress(512)         //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(512, 512)  //Final image resolution will be less than 1080 x 1080(Optional)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            startActivityForResult(intent,1);
                            return null;
                        }
                    });
                
            }
        });

        create_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMember();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void buttonFocus(Button btn, String error) {
        btn.setError(error);
        btn.setFocusable(true);
        btn.setFocusableInTouchMode(true);
        btn.requestFocus();
    }


    private void getParentData(String dept_level, int division_ref, int district_ref, int upazila_ref, int village_ref, int union_ref) {

        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getParentData.php?dept_level="+dept_level+"&upazila_ref="+upazila_ref+"&division_ref="+division_ref+"&district_ref="+district_ref+"&village_ref="+village_ref+"&union_ref="+union_ref;
        System.out.println("Dept level = "+dept_level+" , upazila_ref="+upazila_ref+" , division_ref = "+division_ref+"&district_ref="+district_ref);
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String name="";
                String level = "";
                parent.clear();

                Log.d("Parent member data = ",response);

                try {
                    JSONArray obj = new JSONArray(response);
                    parent_result = obj;
                    for (int i=0;i<obj.length();i++){
                        JSONObject jsonObject = obj.getJSONObject(i);
                        name=jsonObject.getString("full_name");
                        String designation = jsonObject.getString("designation");


                        parent.add(name+" ("+designation+")");
                    }
                }
                catch (JSONException e){
                    Log.e("anyText",response);
                    e.printStackTrace();

                }
                System.out.println("RESPONSE = "+response);
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(NewMemberActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                Toast.makeText(NewMemberActivity.this, volleyError, Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                progressBar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(NewMemberActivity.this);
        requestQueue.add(stringRequest);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==1 && resultCode==RESULT_OK){

            assert data != null;
            Uri filepath=data.getData();
            try {

                InputStream inputStream= getContentResolver().openInputStream(filepath);
                bitmap= BitmapFactory.decodeStream(inputStream);
                member_photo.setImageBitmap(bitmap);
                encodeBitmapImage();

            } catch (Exception ex){
                ex.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void encodeBitmapImage() {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,60,byteArrayOutputStream);
        byte[] bytesOfImage=byteArrayOutputStream.toByteArray();
        int lengthbmp = bytesOfImage.length;
        lengthbmp=lengthbmp/1024;
        System.out.println("image length : " + lengthbmp);

        if (lengthbmp>2048){
            //Toast.makeText(getApplicationContext(), "Image Too Large...select a smaller one", Toast.LENGTH_SHORT).show();
            Toast toast = new Toast(getApplicationContext());
            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
            toast_message.setText("Image Too Large...select a smaller one");
            toast.setView(toast_view);
            toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        } else if (lengthbmp==0){

            encodedImage="";

        }else{

            encodedImage= Base64.encodeToString(bytesOfImage, Base64.DEFAULT);
        }
//        System.out.println("encoded_image = "+encodedImage);
    }



    private void getZoneData(int zoneRef) {

        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getZoneData.php?zone_ref="+zoneRef;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("zone ", "onResponse: "+ response);

                try {
                    String zone_name = "";
                    JSONArray obj = new JSONArray(response);
                    for (int i=0;i<obj.length();i++){
                        JSONObject jsonObject = obj.getJSONObject(i);

                        zone_name = jsonObject.getString("name");
                    }
                    zoneLayout.setVisibility(View.VISIBLE);
                    zone_spinner.setText(zone_name);
                    TextView village_tv = findViewById(R.id.village_tv);
                    villageLayout.setVisibility(View.VISIBLE);
                    village_tv.setText("Road/Area");
                }
                catch (JSONException e){
                    Log.e("anyText",response);
                    e.printStackTrace();

                }
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(NewMemberActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(NewMemberActivity.this, volleyError, Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                progressBar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(NewMemberActivity.this);
        requestQueue.add(stringRequest);

    }



    private void getVillageData(int union_ref) {

        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getVillageData.php?union_ref="+union_ref;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showVillageJSONS(response);
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(NewMemberActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(NewMemberActivity.this, volleyError, Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                progressBar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(NewMemberActivity.this);
        requestQueue.add(stringRequest);

    }

    private void showVillageJSONS(String response) {

        String name="";

        System.out.println(response);

        try {

            JSONArray obj = new JSONArray(response);

            village_result = obj;

            for (int i=0;i<obj.length();i++){
                JSONObject jsonObject = obj.getJSONObject(i);
                name=jsonObject.getString("name");
                village.add(name);
            }

        }
        catch (JSONException e){
            Log.e("anyText",response);
            e.printStackTrace();
        }
    }




    private void getUnionData(int upazila_ref, int type_ref) {

        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getUnionData.php?upazila_ref="+upazila_ref+"&type_ref="+type_ref;

        System.out.println(type_ref+"get union ");
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showUnionJSONS(response);
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(NewMemberActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(NewMemberActivity.this, volleyError, Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                progressBar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(NewMemberActivity.this);
        requestQueue.add(stringRequest);
    }

    private void showUnionJSONS(String response) {

        String name="";

        System.out.println(response);

        try {

            JSONArray obj = new JSONArray(response);

            union_result = obj;

            for (int i=0;i<obj.length();i++){
                JSONObject jsonObject = obj.getJSONObject(i);
                name=jsonObject.getString("name");
                union.add(name);
            }

        }
        catch (JSONException e){
            Log.e("anyText",response);
            e.printStackTrace();
        }

    }

    private void getWardData(int city_ref) {

        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getWardData.php?district_ref="+city_ref;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                System.out.println("Wards = "+response);

                try {

                    JSONArray obj = new JSONArray(response);

                    wad_result = obj;

                    for (int i=0;i<obj.length();i++){
                        JSONObject jsonObject = obj.getJSONObject(i);
                        String name=jsonObject.getString("ward");
                        ward.add(name+" No. Ward");
                    }

                }
                catch (JSONException e){
                    Log.e("anyText",response);
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(NewMemberActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(NewMemberActivity.this, volleyError, Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                progressBar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(NewMemberActivity.this);
        requestQueue.add(stringRequest);

    }


    private void getUpaziladata(int district_ref, int type_ref) {

        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getUpazilaProfile.php?district_ref="+district_ref+"&type_ref="+type_ref;
        System.out.println(type_ref+"upazilaData get");

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showUpazilaJSONS(response);
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(NewMemberActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(NewMemberActivity.this, volleyError, Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                progressBar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(NewMemberActivity.this);
        requestQueue.add(stringRequest);


    }

    private void showUpazilaJSONS(String response) {
        String name="";

        System.out.println(response);

        try {

            JSONArray obj = new JSONArray(response);

            upazila_result = obj;

            for (int i=0;i<obj.length();i++){
                JSONObject jsonObject = obj.getJSONObject(i);
                name=jsonObject.getString("name");
                upazila.add(name);
            }


        }
        catch (JSONException e){
            Log.e("anyText",response);
            e.printStackTrace();
        }

    }

    private void getDistrictData(int division_ref, int type_ref) {

        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getDistrictProfile.php?division_ref="+division_ref+"&type_ref="+type_ref;

        System.out.println(type_ref+"get district ");
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showDistrictJSONS(response);
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(NewMemberActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(NewMemberActivity.this, volleyError, Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                progressBar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(NewMemberActivity.this);
        requestQueue.add(stringRequest);

    }

    private void showDistrictJSONS(String response) {

        String name="";

        System.out.println(response);

        try {

            JSONArray obj = new JSONArray(response);

            district_result = obj;

            for (int i=0;i<obj.length();i++){
                JSONObject jsonObject = obj.getJSONObject(i);
                name=jsonObject.getString("name");
                district.add(name);
            }


        }
        catch (JSONException e){
            Log.e("anyText",response);
            e.printStackTrace();
        }
    }

    private void getDivisionData() {

        progressBar.setVisibility(View.VISIBLE);

        String url = "https://bdclean.winkytech.com/backend/api/getDivisionProfile.php";
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showDivisionJSONS(response);
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(NewMemberActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(NewMemberActivity.this, volleyError, Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                progressBar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(NewMemberActivity.this);
        requestQueue.add(stringRequest);


    }

    private void showDivisionJSONS(String response) {

        String name;

        System.out.println(response);

        try {

            JSONArray obj = new JSONArray(response);

            division_result = obj;

            for (int i=0;i<obj.length();i++){
                JSONObject jsonObject = obj.getJSONObject(i);
                name=jsonObject.getString("name");
                division.add(name);
            }

        }
        catch (JSONException e){
            Log.e("anyText",response);
            e.printStackTrace();
        }

    }

    private void createMember() {

        String name = member_name.getText().toString().trim();
        String email = member_email.getText().toString().trim();
        String contact = member_contact.getText().toString().trim();
        String address = member_address.getText().toString().trim();
        String password = member_password.getText().toString().trim();
        String con_pass = member_confirm_password.getText().toString().trim();
        String name_father = father_name.getText().toString().trim();
        String name_mother = mother_name.getText().toString().trim();
        String contact_father = father_contact.getText().toString().trim();
        String contact_mother = mother_contact.getText().toString().trim();
        String nation = nationality.getText().toString().trim();
        String high_education = education.getText().toString().trim();
        String edu_institute = education_institute.getText().toString().trim();
        String cur_institute = current_institute.getText().toString().trim();
        String fb_link = facebook_id.getText().toString().trim();
        String nid_num = nid.getText().toString().trim();
        String ref_by = refer_by.getText().toString().trim();

        boolean isValid = isValidEmail(email);

        if (name.equals("")
                || email.equals("")
                || contact.equals("")
                || address.equals("")
//                || password.equals("")
//                || name_father.equals("")
//                || name_mother.equals("")
//                || contact_father.equals("")
//                || contact_mother.equals("")
//                || nation.equals("")
//                || high_education.equals("")
//                || edu_institute.equals("")
//                || cur_institute.equals("")
//                || fb_link.equals("")
//                || nid_num.equals("")
        ){
            Toast toast = new Toast(getApplicationContext());
            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
            toast_message.setText("PLEASE FILL UP ALL FIELDS");
            toast.setView(toast_view);
            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        } else if (!password.equals(con_pass)){
            Toast toast = new Toast(getApplicationContext());
            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
            toast_message.setText("Password doesn't match..");
            toast.setView(toast_view);
            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
//        }else if (blood_ref==0 || shirt_ref==0 || religion_ref==0 || occupation_ref==0 || gender_ref==0 || division_ref==0 || district_ref==0 || upazila_ref==0 || union_ref==0|| village_ref==0){
//
//            Toast toast = new Toast(getApplicationContext());
//            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
//            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
//            toast_message.setText("All options are required");
//            toast.setView(toast_view);
//            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
//            toast.setDuration(Toast.LENGTH_SHORT);
//            toast.show();

//        } else if (parent_ref==0){
//
//            Toast toast = new Toast(getApplicationContext());
//            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
//            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
//            toast_message.setText("Parent member not found");
//            toast.setView(toast_view);
//            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
//            toast.setDuration(Toast.LENGTH_SHORT);
//            toast.show();

        } else if (encodedImage.equals("")){

            Toast toast = new Toast(getApplicationContext());
            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
            toast_message.setText("Please select a profile photo");
            toast.setView(toast_view);
            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();

        } else if (isValid){
            contact = "88"+contact;
            progressBar.setVisibility(View.VISIBLE);
            Handler handler = new Handler(Looper.getMainLooper());
            String finalContact = contact;
            handler.post(new Runnable() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void run() {
                    //Starting Write and Read data with URL
                    //Creating array for parameters
                    String[] field = new String[32];
                    field[0] = "name";
                    field[1] = "email";
                    field[2] = "contact";
                    field[3] = "address";
                    field[4] = "password";
                    field[5] = "gender_ref";
                    field[6] = "religion_ref";
                    field[7] = "occupation_ref";
                    field[8] = "division_ref";
                    field[9] = "district_ref";
                    field[10] = "upazila_ref";
                    field[11] = "position_ref";
                    field[12] = "user_id";
                    field[13] = "photo";
                    field[14] = "dept_level_pos";
                    field[15] = "parent_level_pos";
                    field[16] = "union_ref";
                    field[17] = "village_ref";
                    field[18] = "father_name";
                    field[19] = "mother_name";
                    field[20] = "father_contact";
                    field[21] = "mother_contact";
                    field[22] = "nationality";
                    field[23] = "education";
                    field[24] = "edu_institute";
                    field[25] = "cur_institute";
                    field[26] = "fb_link";
                    field[27] = "nid";
                    field[28] = "ref_by";
                    field[29] = "shirt_size";
                    field[30] = "blood_group";
                    field[31] = "date_birth";

                    //Creating array for data
                    String[] data = new String[32];
                    data[0] = name;
                    data[1] = email;
                    data[2] = finalContact;
                    data[3] = address;
                    data[4] = password;
                    data[5] = String.valueOf(gender_ref);
                    data[6] = String.valueOf(religion_ref);
                    data[7] = String.valueOf(occupation_ref);
                    data[8] = String.valueOf(division_ref);
                    data[9] = String.valueOf(district_ref);
                    data[10] = String.valueOf(upazila_ref);
                    data[11] = position_id;
                    data[12] = user_id;
                    data[13] = encodedImage;
                    data[14] = dept_level;
                    data[15] = String.valueOf(parent_ref);
                    data[16] = String.valueOf(union_ref);
                    data[17] = String.valueOf(village_ref);
                    data[18] = name_father;
                    data[19] = name_mother;
                    data[20] = contact_father;
                    data[21] = contact_mother;
                    data[22] = nation;
                    data[23] = high_education;
                    data[24] = edu_institute;
                    data[25] = cur_institute;
                    data[26] = fb_link;
                    data[27] = nid_num;
                    data[28] = ref_by;
                    data[29] = size;
                    data[30] = group;
                    data[31] = birth_date;

                    PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/create_new_member_2.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult().trim();
                            Log.d("NEW MEMBER API :",result);
                            progressBar.setVisibility(View.GONE);
                            if (result.equals("success")) {

                                // Toast.makeText(getActivity(), "Login Success", Toast.LENGTH_SHORT).show();
                                Toast toast = new Toast(NewMemberActivity.this);
                                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout,findViewById(R.id.custom_toast));
                                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                                toast_message.setText("Member Created Successfully.");
                                toast.setView(toast_view);
                                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.show();
                                clearform();

                            } else if(result.equals("Contact Already in use. Try with Another One")) {
                                progressBar.setVisibility(View.GONE);
                                Log.i("PutData", result);
                                Toast toast = new Toast(getApplicationContext());
                                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
                                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                                toast_message.setText("Contact Already in use. Try with Another One");
                                toast.setView(toast_view);
                                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.show();
                                progressBar.setVisibility(View.GONE);

                            }
                        }
                    }
                    //End Write and Read data with URL
                }
            });
        } else {
            Toast toast = new Toast(getApplicationContext());
            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
            toast_message.setText("email is not valid");
            toast.setView(toast_view);
            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
    }

    private void clearform() {
        father_name.setText("");
        member_name.setText("");
        mother_name.setText("");
        father_contact.setText("");
        mother_contact.setText("");
        member_contact.setText("");
        select_division.setText("Division");
        select_district.setText("District");
        select_upazilla.setText("Upazilla");
        select_union.setText("Union");
        select_village.setText("Village");
        nid.setText("");
        education.setText("");
        education_institute.setText("");
        current_institute.setText("");
        facebook_id.setText("");
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
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = dayOfMonth;
        myMonth = month;
        birth_date = (myYear + "-" + (myMonth+1) + "-" + myday);
        date_birth.setText(birth_date);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister broadcast receiver when activity is destroyed
        unregisterReceiver(networkChangeListener);
    }

    public boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}