package com.winkytech.bdclean;

import static com.winkytech.bdclean.LoginActivity.MyPREFERENCES;
import static com.winkytech.bdclean.MainActivity.Contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressLint("SetTextI18n")
public class UserLocationActivity extends AppCompatActivity {

    Button next,cancel,back, log_out;
    Toolbar toolbar;
    Button select_division,select_district,select_upazilla, select_ward, zone_spinner,select_union,select_village;
    int district_ref,division_ref,upazila_ref,union_ref,village_ref, zone_ref;
    List<String> division, district,upazila, ward, union,village;
    String name,email,contact,address;
    int gender_ref,religion_ref,occupation_ref;
    ProgressBar progressBar;
    SharedPreferences sharedpreferences;
    Context context;
    TextView toast_message, village_tv;
    public JSONArray division_result,district_result,upazila_result, wad_result, union_result,village_result;
    NetworkChangeListener networkChangeListener;
    Dialog dialog;
    int type_ref;
    Switch language_switch;
    LinearLayout location_type_layout, location_type2_layout, upazilaLayout, zoneLayout, wardLayout, unionLayout, villageLayout;
    EditText area_details_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        sharedpreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences shared = getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE);
        contact = (shared.getString(Contact,""));

        select_division=findViewById(R.id.division_spinner);
        next=findViewById(R.id.reg_next_btn);
        cancel=findViewById(R.id.reg_cancel_btn);
        location_type_layout=findViewById(R.id.location_type_layout);
        location_type2_layout=findViewById(R.id.location_type2_layout);
        upazilaLayout=findViewById(R.id.upazilaLayout);
        zoneLayout=findViewById(R.id.zoneLayout);
        wardLayout=findViewById(R.id.wardLayout);
        unionLayout=findViewById(R.id.unionLayout);
        villageLayout=findViewById(R.id.villageLayout);
        village_tv = findViewById(R.id.village_tv);
        select_district=findViewById(R.id.district_spinner);
        select_upazilla=findViewById(R.id.upazilla_spinner);
        select_union=findViewById(R.id.union_spinner);
        select_ward=findViewById(R.id.ward_spinner);
        select_village = findViewById(R.id.village_spinner);
        zone_spinner=findViewById(R.id.zone_spinner);
        progressBar=findViewById(R.id.progressbar);
        log_out = findViewById(R.id.log_out);
        progressBar.setVisibility(View.GONE);
        toolbar = findViewById(R.id.custom_toolbar);
        language_switch = findViewById(R.id.language_toggle);
        area_details_et = findViewById(R.id.area_details_et);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        division = new ArrayList<>();
        district = new ArrayList<>();
        upazila = new ArrayList<>();
        union = new ArrayList<>();
        ward = new ArrayList<>();
        village = new ArrayList<>();

        String division_data = shared.getString("division_ref", "0");
//        String district_data = shared.getString("district_ref", "0");
//        String upazila_data = shared.getString("upazila_ref", "0");
//        String union_data = shared.getString("union_ref", "0");
//        String village_data = shared.getString("village_ref", "0");
        Log.d("Location data = ",division_data);

        if (division_data.equals("0") || division_data.equals("null")){

            getDivisionData();

        } else {
            Intent intent = new Intent(getApplicationContext(),UserEventListActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
            finish();
        }

//        if (!shared.getString("upazila_ref","").equals("null")
//                || !shared.getString("district_ref","").equals("null")
//                || !shared.getString("division_ref","").equals("null")
//                || !shared.getString("village_ref","").equals("null")
//                || !shared.getString("union_ref","").equals("null")) {
//
//            Intent intent = new Intent(getApplicationContext(),UserEventListActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
//            finish();
//
//        } else {
//
//            getDivisionData();
//        }


        language_switch.setChecked(false);

        language_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b){
                    setLocale("bn");
                } else {
                    setLocale("en");
                }

            }
        });


        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserLocationActivity.this);
                // Set the message show for the Alert time
                builder.setMessage("Do you want to logout ?");
                // Set Alert Title
                builder.setTitle("Logout!!");
                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);
                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
//                  log_out from account
                    SharedPreferences sharedpreferences =getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent=new Intent(UserLocationActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // If user click no then dialog box is canceled.
                    dialog.cancel();
                });
                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();

            }
        });

//        if (division_data.equals("0") || district_data.equals("0") || upazila_data.equals("0") || union_data.equals("0") || village_data.equals("0")){
//
//            getDivisionData();
//
//        } else {
//            Intent intent = new Intent(getApplicationContext(),UserEventListActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
//            finish();
//        }

        select_division.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(UserLocationActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView= dialog.findViewById(R.id.list_view);
                EditText editText= dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(UserLocationActivity.this, android.R.layout.simple_list_item_1,division);
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
                        area_details_et.setVisibility(View.GONE);
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
                                            area_details_et.setVisibility(View.GONE);


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
                                            area_details_et.setVisibility(View.GONE);
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
                dialog = new Dialog(UserLocationActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(UserLocationActivity.this, android.R.layout.simple_list_item_1,district);
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
                        area_details_et.setVisibility(View.GONE);
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
                dialog = new Dialog(UserLocationActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(UserLocationActivity.this, android.R.layout.simple_list_item_1,upazila);
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
                        area_details_et.setVisibility(View.GONE);

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
                dialog = new Dialog(UserLocationActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(UserLocationActivity.this, android.R.layout.simple_list_item_1,union);
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
                dialog = new Dialog(UserLocationActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(UserLocationActivity.this, android.R.layout.simple_list_item_1,union);
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
                                    area_details_et.setVisibility(View.VISIBLE);
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
                dialog = new Dialog(UserLocationActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(UserLocationActivity.this, android.R.layout.simple_list_item_1,village);
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


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                Intent intent = new Intent(getApplicationContext(), UserEventListActivity.class);
//                intent.putExtra("upazilla_ref",upazila_ref);
//                startActivity(intent);
                saveLocation();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.no_anim,R.anim.push_right_in);
            }
        });
    }

    

    private void saveLocation() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                //Starting Write and Read data with URL
                //Creating array for parameters
                String[] field = new String[6];
                field[0] = "division_ref";
                field[1] = "district_ref";
                field[2] = "upazila_ref";
                field[3] = "union_ref";
                field[4] = "village_ref";
                field[5] = "contact";
                //Creating array for data
                String[] data = new String[6];
                data[0] = String.valueOf(division_ref);
                data[1] = String.valueOf(district_ref);
                data[2] = String.valueOf(upazila_ref);
                data[3] = String.valueOf(union_ref);
                data[4] = String.valueOf(village_ref);
                data[5] = String.valueOf(contact);

                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/saveUserLocation.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();
                        if (result.equals("USER LOCATION SAVED")) {
                            progressBar.setVisibility(View.GONE);

                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout,findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("USER LOCATION SAVED");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();

                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("upazila_ref", String.valueOf(upazila_ref));
                            editor.putString("district_ref", String.valueOf(district_ref));
                            editor.putString("division_ref", String.valueOf(division_ref));
                            editor.putString("village_ref", String.valueOf(village_ref));
                            editor.putString("union_ref", String.valueOf(union_ref));
                            editor.apply();

                            Intent intent = new Intent(getApplicationContext(),UserEventListActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
                            finish();

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.i("PutData", result);
                            //Toast.makeText(getContext(), "Failed To Create User", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Failed To save location");
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
//                    TextView village_tv = findViewById(R.id.village_tv);
//                    villageLayout.setVisibility(View.VISIBLE);
                    area_details_et.setVisibility(View.VISIBLE);
//                    village_tv.setText("Road/Area");
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
                Toast.makeText(UserLocationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(UserLocationActivity.this, volleyError, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(UserLocationActivity.this);
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
                Toast.makeText(UserLocationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(UserLocationActivity.this, volleyError, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(UserLocationActivity.this);
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
                Toast.makeText(UserLocationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(UserLocationActivity.this, volleyError, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(UserLocationActivity.this);
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
                ward.clear();

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
                Toast.makeText(UserLocationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(UserLocationActivity.this, volleyError, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(UserLocationActivity.this);
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
                Toast.makeText(UserLocationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(UserLocationActivity.this, volleyError, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(UserLocationActivity.this);
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
                Toast.makeText(UserLocationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(UserLocationActivity.this, volleyError, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(UserLocationActivity.this);
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
                Toast.makeText(UserLocationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(UserLocationActivity.this, volleyError, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(UserLocationActivity.this);
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

    public void setLocale(String languageCode) {
        // Check if the selected language is different from the current language
        if (!getCurrentLanguage().equals(languageCode)) {
            // Change the app's locale based on the selected language code
            Locale locale = new Locale(languageCode);
            Configuration configuration = getResources().getConfiguration();
            configuration.setLocale(locale);
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

            // Recreate the activity
            recreate();
        }
    }

    // Helper method to get the current language code
    public  String getCurrentLanguage() {
        return getResources().getConfiguration().locale.getLanguage();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(UserLocationActivity.this, AboutBDCleanActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.no_anim,R.anim.push_right_in);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}