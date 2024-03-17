package com.winkytech.bdclean;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
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
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

@SuppressLint("SetTextI18n")
public class NewEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    Toolbar toolbar;
    EditText title, details,location;
    Button select_type,select_division,select_district,select_upazila, select_ward, zone_spinner, select_union,select_village;
    Button start_date,end_date,cancel,create, coordinate_button;
    int event_type_ref,division_ref,district_ref,upazila_ref,village_ref,union_ref,supervisor_ref,zone_ref=0;
    String date_start="",date_end="",encodedImage="",event_longitude="", event_latitude="";
    ImageView cover_photo;
    Bitmap bitmap;
    ProgressBar progressBar;
    TextView toast_message, latitude, longitude, village_tv, monitor_tv;
    List<String> event_type,division,district,upazila,village,union, ward,supervisor;
    private  JSONArray type_result,division_result,district_result,upazila_result, wad_result, union_result,village_result, supervisor_result;
    public static final String MyPREFERENCES = "MyPrefs" ;
    int day, month, year, hour, minute, date_flag, org_level_pos,user_id;
    int myday, myMonth, myYear, myHour, myMinute;
    Dialog dialog;
    double user_latitude, user_longitude;
    int type_ref;

    FusedLocationProviderClient fusedLocationProviderClient;
    NetworkChangeListener networkChangeListener;
    SupportMapFragment mapFragment;
    LinearLayout location_type_layout, location_type2_layout, upazilaLayout, zoneLayout, wardLayout, unionLayout, villageLayout;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        org_level_pos = Integer.parseInt((shared.getString("org_level_ref", "")));
        user_id = Integer.parseInt((shared.getString("user_id", "")));
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();

        toolbar=findViewById(R.id.custom_toolbar);
        title=findViewById(R.id.event_name);
        details=findViewById(R.id.event_details);
        location=findViewById(R.id.event_location);
        select_type=findViewById(R.id.select_event_type);
        select_division=findViewById(R.id.division_spinner);
        location_type_layout=findViewById(R.id.location_type_layout);
        location_type2_layout=findViewById(R.id.location_type2_layout);
        upazilaLayout=findViewById(R.id.upazilaLayout);
        zoneLayout=findViewById(R.id.zoneLayout);
        wardLayout=findViewById(R.id.wardLayout);
        unionLayout=findViewById(R.id.unionLayout);
        village_tv = findViewById(R.id.village_tv);
        villageLayout=findViewById(R.id.villageLayout);
        select_district=findViewById(R.id.district_spinner);
        select_upazila=findViewById(R.id.upazilla_spinner);
        select_union=findViewById(R.id.union_spinner);
        select_ward=findViewById(R.id.ward_spinner);
        select_village = findViewById(R.id.village_spinner);
        monitor_tv = findViewById(R.id.monitor_name);
        zone_spinner=findViewById(R.id.zone_spinner);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        start_date=findViewById(R.id.start_date);
        end_date=findViewById(R.id.end_date);
        cancel=findViewById(R.id.cancel_btn);
        create=findViewById(R.id.create_btn);
        coordinate_button = findViewById(R.id.coordinate_button);
        cover_photo=findViewById(R.id.select_photo);
        progressBar=findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        event_type= new ArrayList<>();
        division=new ArrayList<>();
        district=new ArrayList<>();
        upazila=new ArrayList<>();
        union = new ArrayList<>();
        ward = new ArrayList<>();
        village = new ArrayList<>();
        supervisor = new ArrayList<>();

        getEventTypeData();
        select_type.setOnClickListener(v -> {
            dialog = new Dialog(NewEventActivity.this);
            dialog.setContentView(R.layout.custom_spinner_layout);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();

            ListView listView=dialog.findViewById(R.id.list_view);
            EditText editText=dialog.findViewById(R.id.edit_text);
            ArrayAdapter<String> adapter=new ArrayAdapter<>(NewEventActivity.this, android.R.layout.simple_list_item_1,event_type);
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

            listView.setOnItemClickListener((parent, view, position, id) -> {
                String item  = adapter.getItem(position);

                for (int i =0; i<type_result.length();i++){
                    try {
                        JSONObject jsonObject = type_result.getJSONObject(i);

                        if (jsonObject.getString("name").equals(item)){
                            event_type_ref = Integer.parseInt(jsonObject.getString("id"));
                            select_type.setText(jsonObject.getString("name"));
                            dialog.dismiss();
                            select_type.setError(null);
                        }

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                // Dismiss dialog
            });
        });

        coordinate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Marker[] mMarker = {null};
                dialog = new Dialog(NewEventActivity.this);
                dialog.setContentView(R.layout.google_map_dialog_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                dialog.show();

                EditText map_search = dialog.findViewById(R.id.map_search);
                Button search_btn = dialog.findViewById(R.id.search_map_btn);
                Button close_map = dialog.findViewById(R.id.close_map);

                mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapContainer);

                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        // Customize the map settings
                        // You can add markers, set the camera position, enable/disable gestures, etc.
                        LatLng latLng = new LatLng(user_latitude, user_longitude);
                        //LatLng latLng = new LatLng(23.64184558104228, 88.63826319419519);
                        MarkerOptions markerOptions = new MarkerOptions();
                        // Set position of marker
                        markerOptions.position(latLng);
                        googleMap.isBuildingsEnabled();
                        // Set title of marker
                        markerOptions.title("areaName");

                        final Marker[] previousMarker = {null};

                        search_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String placeName = map_search.getText().toString().trim();

                                if (!placeName.isEmpty()) {
                                    Geocoder geocoder = new Geocoder(NewEventActivity.this);
                                    try {
                                        List<Address> addressList = geocoder.getFromLocationName(placeName, 1);

                                        if (!addressList.isEmpty()) {
                                            Address address = addressList.get(0);
                                            double place_latitude = address.getLatitude();
                                            double place_longitude = address.getLongitude();

                                            LatLng latLng = new LatLng(place_latitude, place_longitude);
                                            googleMap.clear(); // Clear previous markers
                                            googleMap.addMarker(new MarkerOptions().position(latLng).title(placeName));
                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

                                            event_latitude = String.valueOf(place_latitude);
                                            event_longitude = String.valueOf(place_longitude);

                                            latitude.setText("latitude = " + event_latitude);
                                            longitude.setText("Longitude = " + event_longitude);
                                            coordinate_button.setText("Location Selected");
                                            removeMapFragment();
                                            dialog.dismiss();


                                            Toast.makeText(NewEventActivity.this, "Event Loaction Selected From Search Result", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(NewEventActivity.this, "No results found for the specified place", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Toast.makeText(NewEventActivity.this, "Error occurred while searching for the place", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(NewEventActivity.this, "Please enter a place name", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                // Remove the previous marker if it exists
                                if (previousMarker[0] != null) {
                                    previousMarker[0].remove();
                                }

                                event_latitude = String.valueOf(latLng.latitude);
                                event_longitude = String.valueOf(latLng.longitude);

                                latitude.setText("latitude = " + event_latitude);
                                longitude.setText("Longitude = " + event_longitude);
                                coordinate_button.setText("Location Selected");
                                removeMapFragment();
                                dialog.dismiss();

                                Toast.makeText(NewEventActivity.this, "Event Loaction Selected ", Toast.LENGTH_SHORT).show();

                                MarkerOptions clickedMarkerOptions = new MarkerOptions();
                                clickedMarkerOptions.position(latLng);
                                clickedMarkerOptions.title("Event Location");
                                previousMarker[0] = googleMap.addMarker(clickedMarkerOptions);
                            }
                        });

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(NewEventActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                        }
                        googleMap.setMyLocationEnabled(false);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17.0f));
                        googleMap.addMarker(markerOptions);

                    }
                });

                close_map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeMapFragment();
                        dialog.dismiss();
                    }
                });

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        removeMapFragment();
                    }
                });

                dialog.show();

            }
        });


        getDivisionData();
        select_division.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(NewEventActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView= dialog.findViewById(R.id.list_view);
                EditText editText= dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(NewEventActivity.this, android.R.layout.simple_list_item_1,division);
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
                        select_upazila.setText("");
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
                                            select_upazila.setText("");
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
                                            select_upazila.setText("");
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
                dialog = new Dialog(NewEventActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(NewEventActivity.this, android.R.layout.simple_list_item_1,district);
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
                        select_upazila.setText("");
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

        select_upazila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(NewEventActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(NewEventActivity.this, android.R.layout.simple_list_item_1,upazila);
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

                                    select_upazila.setText(jsonObject.getString("name"));
                                    dialog.dismiss();
                                    select_upazila.setError(null);

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
                dialog = new Dialog(NewEventActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(NewEventActivity.this, android.R.layout.simple_list_item_1,union);
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
                                    getMonitorData(union_ref);
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
                dialog = new Dialog(NewEventActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(NewEventActivity.this, android.R.layout.simple_list_item_1,union);
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
                dialog = new Dialog(NewEventActivity.this);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(NewEventActivity.this, android.R.layout.simple_list_item_1,village);
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

        cover_photo.setOnClickListener(v -> {

            float aspectRatio = 16f / 9f;

            ImagePicker.with(NewEventActivity.this)
                .crop(aspectRatio, 1)
                .compress(512)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(512, 512)  //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent(new Function1<Intent, Unit>() {
                    @Override
                    public Unit invoke(Intent intent) {
                        startActivityForResult(intent,1);
                        return null;
                    }
                });

        });

        cancel.setOnClickListener(v -> {

            title.setText("");
            details.setText("");
            location.setText("");
            cover_photo.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_image_24));
            start_date.setText("");
            end_date.setText("");
            latitude.setText("");
            longitude.setText("");
            division_ref = 0;
            district_ref = 0;
            upazila_ref = 0;
            union_ref = 0;
            village_ref = 0;
            supervisor_ref = 0;
            event_type_ref = 0;
            select_type.setText("select type");
            select_district.setText("district");
            select_division.setText("division");
            select_upazila.setText("upazila");
            select_union.setText("union");
            select_village.setText("village");
            monitor_tv.setText("Monitor");

        });

        create.setOnClickListener(v -> createEventProfile());

        start_date.setOnClickListener(v -> {
            date_flag = 1;
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(NewEventActivity.this, NewEventActivity.this,year, month,day);
            datePickerDialog.show();

        });

        end_date.setOnClickListener(v -> {
            date_flag = 2;
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(NewEventActivity.this, NewEventActivity.this,year, month,day);
            datePickerDialog.show();

        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }



    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {

            @Override
            public void onComplete(@NonNull Task<Location> task) {

                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(NewEventActivity.this, Locale.getDefault());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        //List<Address> addressList = geocoder.getFromLocation(23.944918, 90.382778, 1);

                        Log.d("User Current location String", addressList.toString());

                        user_latitude = addressList.get(0).getLatitude();
                        user_longitude = addressList.get(0).getLongitude();

                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getMonitorData(int union_id) {
        String url = "https://bdclean.winkytech.com/backend/api/getSupervisorData.php?union_ref="+union_id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Monitor Name = " + response);
                try {

                    JSONArray obj = new JSONArray(response);
                    supervisor_result = obj;
                    for (int i=0;i<obj.length();i++){
                        JSONObject jsonObject = obj.getJSONObject(i);
                        String name=jsonObject.getString("full_name");
                        monitor_tv.setText("Monitor : "+ name);

                    }

                }
                catch (JSONException e){
                    Log.e("anyText",response);
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(NewEventActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(NewEvaluationActivity.this, volleyError, Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                progressBar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(NewEventActivity.this);
        requestQueue.add(stringRequest);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = dayOfMonth;
        myMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(NewEventActivity.this, NewEventActivity.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        myHour = hourOfDay;
        myMinute = minute;

        if (date_flag==1){
            date_start = (myYear + "-" + (myMonth+1) + "-" + myday + " " + myHour + ":" + myMinute);
            start_date.setText(date_start);

        } else if (date_flag==2){
            date_end = (myYear + "-" + (myMonth+1) + "-" + myday + " " + myHour + ":" + myMinute);
            end_date.setText(date_end);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==1 && resultCode==RESULT_OK){

            assert data != null;
            Uri filepath=data.getData();
            try {

                InputStream inputStream= getContentResolver().openInputStream(filepath);
                bitmap= BitmapFactory.decodeStream(inputStream);
                cover_photo.setImageBitmap(bitmap);
                encodeBitmapImage();

            } catch (Exception ex){
                ex.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void encodeBitmapImage() {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytesOfImage=byteArrayOutputStream.toByteArray();
        int lengthbmp = bytesOfImage.length;
        lengthbmp=lengthbmp/1024;
        System.out.println("image length" + lengthbmp);

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
                Toast.makeText(NewEventActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(NewEventActivity.this, volleyError, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(NewEventActivity.this);
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
                Toast.makeText(NewEventActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(NewEventActivity.this, volleyError, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(NewEventActivity.this);
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

                union.clear();
                Log.d("Union Data :", response);
                showUnionJSONS(response);
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(NewEventActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(NewEventActivity.this, volleyError, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(NewEventActivity.this);
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
                Toast.makeText(NewEventActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(NewEventActivity.this, volleyError, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(NewEventActivity.this);
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
                Toast.makeText(NewEventActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(NewEventActivity.this, volleyError, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(NewEventActivity.this);
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
                Toast.makeText(NewEventActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(NewEventActivity.this, volleyError, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(NewEventActivity.this);
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
                Toast.makeText(NewEventActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(NewEventActivity.this, volleyError, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(NewEventActivity.this);
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

    private void getEventTypeData() {

        String url = "https://bdclean.winkytech.com/backend/api/getEventType.php";
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showTypeJSONS(response);

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(NewEventActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showTypeJSONS(String response) {

        String name="";

        System.out.println(response);

        try {

            JSONArray obj = new JSONArray(response);

            type_result = obj;

            for (int i=0;i<obj.length();i++){
                JSONObject jsonObject = obj.getJSONObject(i);
                name=jsonObject.getString("name");
                event_type.add(name);
            }


        }
        catch (JSONException e){
            Log.e("anyText",response);
            e.printStackTrace();
        }

    }

    @SuppressLint("SetTextI18n")
    private void createEventProfile() {
        String event_title = title.getText().toString().trim();
        String event_details = details.getText().toString().trim();
        String event_location = location.getText().toString().trim();
        String border = "50";
        String join_area = "50";

        if (event_type_ref == 0){
            select_type.setError("select a event type");
            select_type.setFocusable(true);
            select_type.setFocusableInTouchMode(true);
            select_type.requestFocus();

        } else if(event_title.equals("")){
            title.setError("Input Event title");
            title.requestFocus();
        } else if (event_details.equals("") ){
            details.setError("Input Event Details");
            details.requestFocus();
        } else if (event_location.equals("")){
            location.setError("Input Location");
            location.requestFocus();
        } else if (division_ref==0){

            select_division.setError("Input division");
            select_division.setFocusableInTouchMode(true);
            select_division.setFocusable(true);
            select_division.requestFocus();

        } else if (district_ref==0 ){

            select_district.setError("Input District");
            select_district.setFocusableInTouchMode(true);
            select_district.setFocusable(true);
            select_district.requestFocus();

        }

//        else if (upazila_ref ==0){
//
//            select_upazila.setError("Input Upazila");
//            select_upazila.setFocusableInTouchMode(true);
//            select_upazila.setFocusable(true);
//            select_upazila.requestFocus();

         else if (union_ref ==0){

            select_union.setError("Input union");
            select_union.setFocusableInTouchMode(true);
            select_union.setFocusable(true);
            select_union.requestFocus();


        }

//         else if (village_ref == 0){
//
//            select_village.setError("Input village");
//            select_village.setFocusableInTouchMode(true);
//            select_village.setFocusable(true);
//            select_village.requestFocus();

//         else if (supervisor_ref==0){
//            select_supervisor.setError("Input supervisor");
//            select_supervisor.setFocusableInTouchMode(true);
//            select_supervisor.setFocusable(true);
//            select_supervisor.requestFocus();

         else if (event_latitude.equals("") || event_longitude.equals("")){
            coordinate_button.setError("Select your event location");
            coordinate_button.setFocusableInTouchMode(true);
            coordinate_button.setFocusable(true);
            coordinate_button.requestFocus();
        } else if (date_start.equals("") ){

            start_date.setError("Input Start date");
            start_date.setFocusableInTouchMode(true);
            start_date.setFocusable(true);
            start_date.requestFocus();
        } else if (date_end.equals("")){
            end_date.setError("Input End date");
            end_date.setFocusableInTouchMode(true);
            end_date.setFocusable(true);
            end_date.requestFocus();
        } else if (encodedImage.equals("")){
            Toast toast = new Toast(getApplicationContext());
            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
            toast_message.setText("Please select a cover photo");
            toast.setView(toast_view);
            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void run() {
                    //Starting Write and Read data with URL
                    //Creating array for parameters
                    String[] field = new String[18];
                    field[0] = "title";
                    field[1] = "spec";
                    field[2] = "location";
                    field[3] = "start_date";
                    field[4] = "end_date";
                    field[5] = "event_type";
                    field[6] = "division_ref";
                    field[7] = "district_ref";
                    field[8] = "upazila_ref";
                    field[9] = "cover_photo";
                    field[10] = "union_ref";
                    field[11] = "village_ref";
                    field[12] = "supervisor_ref";
                    field[13] = "latitude";
                    field[14] = "longitude";
                    field[15] = "border";
                    field[16] = "join_area";
                    field[17] = "user_id";

                    String[] data = new String[18];
                    data[0] = event_title;
                    data[1] = event_details;
                    data[2] = event_location;
                    data[3] = date_start;
                    data[4] = date_end;
                    data[5] = String.valueOf(event_type_ref);
                    data[6] = String.valueOf(division_ref);
                    data[7] = String.valueOf(district_ref);
                    data[8] = String.valueOf(upazila_ref);
                    data[9] = encodedImage;
                    data[10] = String.valueOf(union_ref);
                    data[11] = String.valueOf(village_ref);
                    data[12] = String.valueOf(supervisor_ref);
                    data[13] = event_latitude;
                    data[14] = event_longitude;
                    data[15] = border;
                    data[16] = join_area;
                    data[17] = String.valueOf(user_id);

                    PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/createEventProfile.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult().trim();
                            if (result.equals("success")) {
                                progressBar.setVisibility(View.GONE);
                                // showNotification();
                                showSuccessDialog();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Log.i("PutData", result);
                                Toast toast = new Toast(getApplicationContext());
                                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
                                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                                toast_message.setText("SOME ERROR OCCURRED, PLEASE TRY AGAIN");
                                toast.setView(toast_view);
                                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }
                }
            });
        }
    }

    private void showSuccessDialog() {
        Dialog dialog = new Dialog(NewEventActivity.this);
        dialog.setContentView(R.layout.event_create_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        Button yes_btn = dialog.findViewById(R.id.yes_btn);
        Button close_btn = dialog.findViewById(R.id.no_btn);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("");
                details.setText("");
                location.setText("");
                cover_photo.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_image_24));
                start_date.setText("Start Date");
                end_date.setText("End Date");
                latitude.setText("Latitude");
                longitude.setText("Longitude");
                division_ref = 0;
                district_ref = 0;
                upazila_ref = 0;
                union_ref = 0;
                village_ref = 0;
                supervisor_ref = 0;
                event_type_ref = 0;
                select_type.setText("select type");
                select_district.setText("district");
                select_division.setText("division");
                select_upazila.setText("upazila");
                select_union.setText("union");
                select_village.setText("village");
                monitor_tv.setText("Monitor");
                dialog.dismiss();
            }
        });
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

    private void removeMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapContainer);
        if (mapFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(mapFragment)
                    .commitAllowingStateLoss();
        }
    }

}