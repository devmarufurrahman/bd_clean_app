package com.winkytech.bdclean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UpdateEventDetails extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    Button coordinate_button, update_start_date, update_end_date, update_btn;
    Dialog dialog;
    SupportMapFragment mapFragment;
    ProgressBar progressBar;
    TextView toast_message;
    int date_flag1 = 0, date_flag2 = 0, location_flag = 0, date_flag;
    int day, month, year, hour, minute, user_id,  myday, myMonth, myYear, myHour, myMinute, eventId;
    double user_latitude, user_longitude;
    String event_longitude="", event_latitude="", date_start="", date_end="";
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event_details);

        coordinate_button = findViewById(R.id.coordinate_button);
        update_start_date = findViewById(R.id.update_start_date);
        update_end_date = findViewById(R.id.update_end_date);
        update_btn = findViewById(R.id.update_btn);
        progressBar = findViewById(R.id.progressbar);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Intent intent = getIntent();
        eventId = intent.getIntExtra("event_id",0);

        getCurrentLocation();

        coordinate_button.setOnClickListener(v -> {
            final Marker[] mMarker = {null};
            dialog = new Dialog(UpdateEventDetails.this);
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
                                Geocoder geocoder = new Geocoder(UpdateEventDetails.this);
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

                                        
                                        location_flag = 1;
                                        coordinate_button.setText("Location Selected");
                                        removeMapFragment();
                                        dialog.dismiss();


                                        Toast.makeText(UpdateEventDetails.this, "Event Loaction Selected From Search Result", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(UpdateEventDetails.this, "No results found for the specified place", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(UpdateEventDetails.this, "Error occurred while searching for the place", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(UpdateEventDetails.this, "Please enter a place name", Toast.LENGTH_SHORT).show();
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

                            
                            location_flag = 1;
                            coordinate_button.setText("Location Selected");
                            removeMapFragment();
                            dialog.dismiss();

                            Toast.makeText(UpdateEventDetails.this, "Event Loaction Selected ", Toast.LENGTH_SHORT).show();

                            MarkerOptions clickedMarkerOptions = new MarkerOptions();
                            clickedMarkerOptions.position(latLng);
                            clickedMarkerOptions.title("Event Location");
                            previousMarker[0] = googleMap.addMarker(clickedMarkerOptions);
                        }
                    });

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(UpdateEventDetails.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
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

        });


        update_start_date.setOnClickListener(v -> {
            date_flag = 1;
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateEventDetails.this, UpdateEventDetails.this,year, month,day);
            datePickerDialog.show();
        });

        update_end_date.setOnClickListener(v -> {
            date_flag = 2;
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateEventDetails.this, UpdateEventDetails.this,year, month,day);
            datePickerDialog.show();
        });

        update_btn.setOnClickListener(v -> {
            updateEvent();
        });

    }

    private void updateEvent() {
        progressBar.setVisibility(View.VISIBLE);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {
                //Starting Write and Read data with URL
                //Creating array for parameters
                String[] field = new String[5];
                field[0] = "latitude";
                field[1] = "longitude";
                field[2] = "start_date";
                field[3] = "end_date";
                field[4] = "e_id";

                String[] data = new String[5];
                data[0] = event_latitude;
                data[1] = event_longitude;
                data[2] = date_start;
                data[3] = date_end;
                data[4] = String.valueOf(eventId);


                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/updateEventProfile.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();
                        if (result.equals("success")) {
                            progressBar.setVisibility(View.GONE);
                            // showNotification();

                            event_latitude = "";
                            event_longitude = "";
                            date_start = "";
                            date_end = "";
                            update_start_date.setText(getString(R.string.start_date));
                            update_end_date.setText(getString(R.string.end_date));

                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Success");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                            Log.d("eventUpdateResponse", "res: " + result);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.i("PutData", result);
                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Failed to update event details");
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


    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {

            @Override
            public void onComplete(@NonNull Task<Location> task) {

                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(UpdateEventDetails.this, Locale.getDefault());
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


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = dayOfMonth;
        myMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateEventDetails.this, UpdateEventDetails.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        myHour = hourOfDay;
        myMinute = minute;

        if (date_flag==1){
            date_start = (myYear + "-" + (myMonth+1) + "-" + myday + " " + myHour + ":" + myMinute);
            update_start_date.setText(date_start);

        } else if (date_flag==2){
            date_end = (myYear + "-" + (myMonth+1) + "-" + myday + " " + myHour + ":" + myMinute);
            update_end_date.setText(date_end);
        }
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