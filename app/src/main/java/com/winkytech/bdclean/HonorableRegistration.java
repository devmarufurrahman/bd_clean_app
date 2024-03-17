package com.winkytech.bdclean;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.winkytech.bdclean.databinding.ActivityHonorableRegistrationBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HonorableRegistration extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener{

    ActivityHonorableRegistrationBinding binding;
    int gender_ref=0, marital_ref=0, religion_ref=0, day, month, year, myday, myMonth, myYear, district_ref,division_ref,upazila_ref,union_ref,village_ref, zone_ref, type_ref = 0, honorableExperience = 0;
    String birth_date="";
    TextView toast_message;
    public JSONArray division_result,district_result,upazila_result, wad_result, union_result,village_result;
    List<String> gender, marital, religion, division, district,upazila, ward, union,village;
    Dialog dialog;
    Context context = HonorableRegistration.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHonorableRegistrationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

//        toolbar
        setSupportActionBar(binding.customToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        new array method call
        division = new ArrayList<>();
        district = new ArrayList<>();
        upazila = new ArrayList<>();
        union = new ArrayList<>();
        ward = new ArrayList<>();
        village = new ArrayList<>();


//        division data get
        getDivisionData();

//        gender selection start here
        gender = new ArrayList<>();
        gender.add("Male");
        gender.add("Female");
        gender.add("Others");
        binding.genderSpinner.setOnClickListener(v -> {
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_spinner_layout_2);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();

            ListView listView=dialog.findViewById(R.id.list_view);
            ArrayAdapter<String> adapter=new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,gender);
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
                            binding.genderSpinner.setText(item);
                            dialog.dismiss();
                            break;
                        case "Female" :
                            gender_ref = 2;
                            binding.genderSpinner.setText(item);
                            dialog.dismiss();
                            break;

                        case "Others" :
                            gender_ref = 3;
                            binding.genderSpinner.setText(item);
                            dialog.dismiss();
                            break;
                    }
                }
            });
        });

//        gender selection ends here


//        marital selection start here
        marital = new ArrayList<>();
        marital.add("Married");
        marital.add("Unmarried");
        binding.maritalSpinner.setOnClickListener(v -> {
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_spinner_layout_2);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();

            ListView listView=dialog.findViewById(R.id.list_view);
            ArrayAdapter<String> adapter=new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,marital);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // when item selected from list
                    // set selected item on textView
                    String item  = adapter.getItem(position);
                    switch (item){
                        case "Married" :
                            marital_ref = 1;
                            binding.maritalSpinner.setText(item);
                            binding.spouseName.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                            break;
                        case "Unmarried" :
                            marital_ref = 2;
                            binding.maritalSpinner.setText(item);
                            binding.spouseName.setVisibility(View.GONE);
                            dialog.dismiss();
                            break;
                    }
                }
            });
        });

//        marital selection ends here


//        religion selection start here
        religion = new ArrayList<>();
        religion.add("Islam");
        religion.add("Hindu");
        religion.add("Christian");
        religion.add("Buddha");
        binding.religionSpinner.setOnClickListener(v -> {
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_spinner_layout_2);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();

            ListView listView=dialog.findViewById(R.id.list_view);
            ArrayAdapter<String> adapter=new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,religion);
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
                            binding.religionSpinner.setText(item);
                            dialog.dismiss();
                            break;
                        case "Hindu" :
                            religion_ref = 2;
                            binding.religionSpinner.setText(item);
                            dialog.dismiss();
                            break;
                        case "Christian" :
                            religion_ref = 3;
                            binding.religionSpinner.setText(item);
                            dialog.dismiss();
                            break;
                        case "Buddha" :
                            religion_ref = 4;
                            binding.religionSpinner.setText(item);
                            dialog.dismiss();
                            break;
                    }
                }
            });
        });

//        religion selection ends here


//        birth day start here
        binding.dateBirth.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, HonorableRegistration.this,year, month,day);
            datePickerDialog.show();
        });
//        birth day ends here

        binding.submitBtn.setOnClickListener(v -> {
            saveData();

        });



        binding.divisionSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView= dialog.findViewById(R.id.list_view);
                EditText editText= dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,division);
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


                        binding.cityDialog.setChecked(false);
                        binding.districtDialog.setChecked(false);
                        binding.wardLayout.setVisibility(View.GONE);
                        binding.upazilaLayout.setVisibility(View.GONE);
                        binding.districtSpinner.setText("");
                        binding.upazillaSpinner.setText("");
                        binding.wardSpinner.setText("");
                        binding.unionSpinner.setText("");
                        binding.villageSpinner.setText("");
                        binding.locationType2Layout.setVisibility(View.GONE);
                        binding.unionLayout.setVisibility(View.GONE);
                        binding.villageLayout.setVisibility(View.GONE);
                        binding.areaDetailsEt.setVisibility(View.GONE);
                        binding.zoneLayout.setVisibility(View.GONE);

                        for (int i =0; i<division_result.length();i++){
                            try {
                                JSONObject jsonObject = division_result.getJSONObject(i);

                                if (jsonObject.getString("name").equals(item)){
                                    division_ref = Integer.parseInt(jsonObject.getString("id"));

                                    district.clear();
                                    binding.divisionSpinner.setText(jsonObject.getString("name"));
                                    dialog.dismiss();
                                    binding.divisionSpinner.setError(null);

                                    binding.locationType.setVisibility(View.VISIBLE);


                                    TextView district_tv = findViewById(R.id.district_tv);


                                    binding.cityDialog.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            district.clear();
                                            union.clear();
                                            type_ref = 2;

                                            getDistrictData(division_ref,type_ref);
//                                            binding.locationTypeLayout.setVisibility(View.GONE);
                                            binding.wardLayout.setVisibility(View.GONE);
                                            district_tv.setText("City Corporation");
                                            binding.unionLayout.setVisibility(View.GONE);
                                            binding.cityDialog.setChecked(true);
                                            binding.districtDialog.setChecked(false);
                                            binding.locationType2Layout.setVisibility(View.GONE);
                                            binding.villageLayout.setVisibility(View.GONE);
                                            binding.upazilaLayout.setVisibility(View.GONE);
                                            binding.areaDetailsEt.setVisibility(View.GONE);


                                            binding.districtSpinner.setText("");
                                            binding.upazillaSpinner.setText("");
                                            binding.wardSpinner.setText("");
                                            binding.unionSpinner.setText("");
                                            binding.villageSpinner.setText("");
                                        }
                                    });

                                    binding.districtDialog.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            district.clear();
                                            type_ref = 1;
                                            getDistrictData(division_ref,type_ref);
//                                            binding.locationTypeLayout.setVisibility(View.GONE);
                                            binding.wardLayout.setVisibility(View.GONE);
                                            district_tv.setText("District");
                                            binding.unionLayout.setVisibility(View.GONE);
                                            binding.districtDialog.setChecked(true);
                                            binding.cityDialog.setChecked(false);
                                            binding.zoneLayout.setVisibility(View.GONE);
                                            binding.villageLayout.setVisibility(View.GONE);
                                            binding.areaDetailsEt.setVisibility(View.GONE);
                                            union.clear();

                                            binding.districtSpinner.setText("");
                                            binding.upazillaSpinner.setText("");
                                            binding.wardSpinner.setText("");
                                            binding.unionSpinner.setText("");
                                            binding.villageSpinner.setText("");
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


        binding.districtSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,district);
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
                        binding.wardLayout.setVisibility(View.GONE);
                        binding.upazilaLayout.setVisibility(View.GONE);
                        binding.upazillaSpinner.setText("");
                        binding.wardSpinner.setText("");
                        binding.unionSpinner.setText("");
                        binding.villageSpinner.setText("");
                        binding.locationType2Layout.setVisibility(View.GONE);
                        binding.unionLayout.setVisibility(View.GONE);
                        binding.villageLayout.setVisibility(View.GONE);
                        binding.areaDetailsEt.setVisibility(View.GONE);
                        binding.zoneLayout.setVisibility(View.GONE);

                        for (int i =0; i<district_result.length();i++){
                            try {
                                JSONObject jsonObject = district_result.getJSONObject(i);

                                if (jsonObject.getString("name").equals(item)){
                                    district_ref = Integer.parseInt(jsonObject.getString("id"));

                                    upazila.clear();
                                    binding.districtSpinner.setText(jsonObject.getString("name"));
                                    dialog.dismiss();
                                    binding.districtSpinner.setError(null);

                                    if (type_ref == 2){

                                        getUnionData(district_ref, type_ref);
                                        binding.upazilaLayout.setVisibility(View.GONE);
                                        binding.wardLayout.setVisibility(View.VISIBLE);
                                        type_ref = 2;
                                    } else {
                                        getUpaziladata(district_ref,1);
                                        binding.upazilaLayout.setVisibility(View.VISIBLE);
                                        binding.zoneLayout.setVisibility(View.GONE);
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

        binding.upazillaSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,upazila);
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
                        binding.unionSpinner.setText("");
                        binding.villageSpinner.setText("");
                        binding.locationType2Layout.setVisibility(View.GONE);
                        binding.unionLayout.setVisibility(View.GONE);
                        binding.villageLayout.setVisibility(View.GONE);
                        binding.areaDetailsEt.setVisibility(View.GONE);

                        for (int i =0; i<upazila_result.length();i++){
                            try {
                                JSONObject jsonObject = upazila_result.getJSONObject(i);

                                if (jsonObject.getString("name").equals(item)){
                                    upazila_ref = Integer.parseInt(jsonObject.getString("id"));
                                    union.clear();
                                    getUnionData(upazila_ref, type_ref);

                                    binding.upazillaSpinner.setText(jsonObject.getString("name"));
                                    dialog.dismiss();
                                    binding.upazillaSpinner.setError(null);



                                    binding.locationType2Layout.setVisibility(View.VISIBLE);

                                    TextView union_tv = findViewById(R.id.union_tv);

                                    cityBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            union.clear();
                                            type_ref = 2;

                                            getUnionData(upazila_ref,type_ref);
//                                            binding.locationType2Layout.setVisibility(View.GONE);
                                            binding.unionLayout.setVisibility(View.VISIBLE);

                                            union_tv.setText("Municipal");
                                            cityBtn.setChecked(true);
                                            binding.unionSpinner.setText("");
                                            binding.villageSpinner.setText("");
                                            binding.villageTv.setText("Ward");
                                        }
                                    });


                                    districtBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            union.clear();
                                            type_ref = 1;
                                            getUnionData(upazila_ref,type_ref);
//                                            binding.locationType2Layout.setVisibility(View.GONE);
                                            binding.unionLayout.setVisibility(View.VISIBLE);

                                            union_tv.setText("Union");
                                            districtBtn.setChecked(true);
                                            binding.unionSpinner.setText("");
                                            binding.villageSpinner.setText("");
                                            binding.villageTv.setText("Village");
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

        binding.wardSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,union);
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
                        binding.villageSpinner.setText("");


                        for (int i =0; i<union_result.length();i++){
                            try {
                                JSONObject jsonObject = union_result.getJSONObject(i);
                                String json = jsonObject.getString("name");

                                if (json.equals(item)) {
                                    zone_ref = Integer.parseInt(jsonObject.getString("zone_ref"));
                                    union_ref = Integer.parseInt(jsonObject.getString("id"));
                                    dialog.dismiss();
                                    binding.wardSpinner.setText(item);
                                    getZoneData(zone_ref);
                                    binding.wardSpinner.setError(null);
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

        binding.unionSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,union);
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
                        binding.villageSpinner.setText("");

                        for (int i =0; i<union_result.length();i++){
                            try {
                                JSONObject jsonObject = union_result.getJSONObject(i);


                                if (jsonObject.getString("name").equals(item)) {
                                    union_ref = Integer.parseInt(jsonObject.getString("id"));
                                    village.clear();
                                    getVillageData(union_ref);
                                    binding.unionSpinner.setText(jsonObject.getString("name"));
                                    dialog.dismiss();
                                    binding.villageLayout.setVisibility(View.VISIBLE);
                                    binding.areaDetailsEt.setVisibility(View.VISIBLE);
                                    binding.unionSpinner.setError(null);
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

        binding.villageSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ListView listView=dialog.findViewById(R.id.list_view);
                EditText editText=dialog.findViewById(R.id.edit_text);
                ArrayAdapter<String> adapter=new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,village);
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
                                    binding.villageSpinner.setText(jsonObject.getString("name"));
                                    dialog.dismiss();
                                    binding.villageSpinner.setError(null);
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


        binding.yesBtn.setOnClickListener(v -> {
            honorableExperience = 1;
            binding.experienceDetails.setVisibility(View.VISIBLE);
            binding.experienceDetails1.setVisibility(View.VISIBLE);
            binding.experienceDetails2.setVisibility(View.VISIBLE);
            binding.experienceDetails3.setVisibility(View.VISIBLE);
            binding.detailsTv.setVisibility(View.VISIBLE);
        });
        binding.noBtn.setOnClickListener(v -> {
            honorableExperience = 2;
            binding.experienceDetails.setVisibility(View.GONE);
            binding.experienceDetails1.setVisibility(View.GONE);
            binding.experienceDetails2.setVisibility(View.GONE);
            binding.experienceDetails3.setVisibility(View.GONE);
            binding.detailsTv.setVisibility(View.GONE);

        });


    }

    private void saveData() {
        String address = binding.memberAddress.getText().toString().trim();
        String fatherName = binding.fatherName.getText().toString().trim();
        String motherName = binding.motherName.getText().toString().trim();
        String nationality = binding.nationality.getText().toString().trim();
        String nidNo = binding.nidNumber.getText().toString().trim();
        String facebookId = binding.fbLink.getText().toString().trim();
        String examTitle = binding.exam.getText().toString().trim();
        String examDegree = binding.major.getText().toString().trim();
        String instituteName = binding.educationInstitute.getText().toString().trim();
        String passingYear = binding.passYear.getText().toString().trim();
        String currentOccupation = binding.currentOccupation.getText().toString().trim();
        String employeeInstituteName = binding.employerInstitution.getText().toString().trim();
        String employeeInstituteAddress = binding.employerInstitution.getText().toString().trim();
        String employeeDetails = binding.employerDetails.getText().toString().trim();
        String motivationInterest = binding.motivationInterest.getText().toString().trim();
        String experienceDetails = binding.experienceDetails.getText().toString().trim();
        String experienceDetails1 = binding.experienceDetails1.getText().toString().trim();
        String experienceDetails2 = binding.experienceDetails2.getText().toString().trim();
        String experienceDetails3 = binding.experienceDetails3.getText().toString().trim();
        if (address.equals("")){
            fieldFocus(binding.memberAddress, "Input present address");
        } else if (fatherName.equals("")) {
            fieldFocus(binding.fatherName, "input father name");
        } else if (motherName.equals("")) {
            fieldFocus(binding.motherName, "input mother name");
        } else if (nationality.equals("")) {
            fieldFocus(binding.nationality, "input nationality");
        } else if (nidNo.equals("") && (nidNo.length() <= 9 || nidNo.length() >= 13)) {
            fieldFocus(binding.nidNumber, "input valid nid no");
        } else if (facebookId.equals("") ) {
            fieldFocus(binding.fbLink, "input valid facebook id");
        } else if (birth_date.equals("")) {
            buttonFocus(binding.dateBirth, "select birthday");
        } else if (marital_ref == 0) {
            buttonFocus(binding.maritalSpinner, "select marital");
        } else if (religion_ref == 0) {
            buttonFocus(binding.religionSpinner, "select religion");
        } else if (gender_ref == 0) {
            buttonFocus(binding.genderSpinner, "select gender");
        }  else if (district_ref == 0) {
            buttonFocus(binding.districtSpinner, "select district");
        } else if (examTitle.equals("")) {
            fieldFocus(binding.exam, "input exam title");
        } else if (examDegree.equals("")) {
            fieldFocus(binding.major, "input exam major");
        } else if (instituteName.equals("")) {
            fieldFocus(binding.educationInstitute, "input institute name");
        } else if ( passingYear.length() == 4) {
            fieldFocus(binding.passYear, "input valid passing year");
        } else if (currentOccupation.equals("")) {
            fieldFocus(binding.currentOccupation, "input occupation");
        } else if (employeeInstituteName.equals("")) {
            fieldFocus(binding.employerInstitution, "input institute");
        } else if (employeeDetails.equals("")) {
            fieldFocus(binding.employerDetails, "input details");
        } else if (experienceDetails.equals("") && honorableExperience == 1) {
            fieldFocus(binding.experienceDetails, "input details");
            Toast.makeText(context, "experience details?", Toast.LENGTH_SHORT).show();
        } else if (honorableExperience == 0) {
            customToast("experience details yes/no?");
        } else if (!binding.termsCheckBox.isChecked()) {
            customToast("accept terms and condition");
        } else{
            binding.progressbar.setVisibility(View.VISIBLE);
//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//
//                    //Starting Write and Read data with URL
//                    //Creating array for parameters
//                    String[] field = new String[5];
//                    field[0] = "user_id";
//                    field[1] = "send_to";
//                    field[2] = "send_for";
//                    field[3] = "subject";
//                    field[4] = "body";
//
//
//                    //Creating array for data
//                    String[] data = new String[5];
//                    data[0] = user_id;
//                    data[1] = String.valueOf(parent_ref);
//                    data[2] = String.valueOf(complain_for_ref);
//                    data[3] = subject;
//                    data[4] = body;
//
//                    PutData putData = new PutData("", "POST", field, data);
//                    if (putData.startPut()) {
//                        if (putData.onComplete()) {
//                            String result = putData.getResult().trim();
//                            if (result.equals("success")) {
//                                binding.progressbar.setVisibility(View.GONE);
//                                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
//
//                            } else {
//                                binding.progressbar.setVisibility(View.GONE);
//                                Log.i("PutData", result);
//                                Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
//
//                            }
//                        }
//                    }
//                    //End Write and Read data with URL
//                }
//            });

//            fake api response bellow
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String msg = "Thank you for your interest in becoming an Honorable Member of BD Clean. Your application will be evaluated within the next 15 working days, and further communication regarding the status of your application will be provided accordingly.";
                    binding.progressbar.setVisibility(View.GONE);
                    submitDialog(msg, R.drawable.success_image, R.color.bdclean_green);
                }
            }, 2000);
        }
    }





    //    custom toast method
    private void customToast(String msg){
        TextView toast_message;
        Toast toast = new Toast(getApplicationContext());
        View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout,findViewById(R.id.custom_toast));
        toast_message=toast_view.findViewById(R.id.custom_toast_tv);
        toast_message.setText(msg);
        toast.setView(toast_view);
        toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,80);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();

    }


    //    custom dialog method
    private void submitDialog(String notice, int imgGet, int noticeColor) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_submit_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView text = dialog.findViewById(R.id.submitTextDialog);
        ImageView img = dialog.findViewById(R.id.submitImgDialog);
        Button okBtn = dialog.findViewById(R.id.submitOkBtn);

        text.setText(notice);
        img.setImageResource(imgGet);
        text.setTextColor(getResources().getColor(noticeColor));
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onBackPressed();
            }
        });
        dialog.show();
    }




    // form validation
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

    //    date of birth set
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = dayOfMonth;
        myMonth = month;
        birth_date = (myYear + "-" + (myMonth+1) + "-" + myday);
        binding.dateBirth.setText(birth_date);
    }



    private void getZoneData(int zoneRef) {

        binding.progressbar.setVisibility(View.VISIBLE);
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
                    binding.zoneLayout.setVisibility(View.VISIBLE);
                    binding.zoneSpinner.setText(zone_name);
//                    TextView binding.villageTv = findViewById(R.id.binding.villageTv);
//                    binding.villageLayout.setVisibility(View.VISIBLE);
                    binding.areaDetailsEt.setVisibility(View.VISIBLE);
//                    binding.villageTv.setText("Road/Area");
                }
                catch (JSONException e){
                    Log.e("anyText",response);
                    e.printStackTrace();

                }
                binding.progressbar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.progressbar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(context, volleyError, Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                binding.progressbar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }



    private void getVillageData(int union_ref) {

        binding.progressbar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getVillageData.php?union_ref="+union_ref;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showVillageJSONS(response);
                binding.progressbar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.progressbar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(context, volleyError, Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                binding.progressbar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
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

        binding.progressbar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getUnionData.php?upazila_ref="+upazila_ref+"&type_ref="+type_ref;

        System.out.println(type_ref+"get union ");
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showUnionJSONS(response);
                binding.progressbar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.progressbar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(context, volleyError, Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                binding.progressbar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
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

        binding.progressbar.setVisibility(View.VISIBLE);
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
                binding.progressbar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.progressbar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(context, volleyError, Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                binding.progressbar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }


    private void getUpaziladata(int district_ref, int type_ref) {

        binding.progressbar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getUpazilaProfile.php?district_ref="+district_ref+"&type_ref="+type_ref;
        System.out.println(type_ref+"upazilaData get");

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showUpazilaJSONS(response);
                binding.progressbar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.progressbar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(context, volleyError, Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                binding.progressbar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
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

        binding.progressbar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getDistrictProfile.php?division_ref="+division_ref+"&type_ref="+type_ref;

        System.out.println(type_ref+"get district ");
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showDistrictJSONS(response);
                binding.progressbar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.progressbar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(context, volleyError, Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                binding.progressbar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
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

        binding.progressbar.setVisibility(View.VISIBLE);

        String url = "https://bdclean.winkytech.com/backend/api/getDivisionProfile.php";
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showDivisionJSONS(response);
                binding.progressbar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.progressbar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                //Toast.makeText(context, volleyError, Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To Get information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                binding.progressbar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
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




}