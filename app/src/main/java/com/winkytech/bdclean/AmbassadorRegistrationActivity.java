package com.winkytech.bdclean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;

import com.winkytech.bdclean.databinding.ActivityAmbassadorRegistrationBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AmbassadorRegistrationActivity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener{

    ActivityAmbassadorRegistrationBinding binding;
    List<String> occupation, gender;
    Dialog dialog;
    int occupation_ref=0,  gender_ref=0, day, month, year,myday, myMonth, myYear;
    String birth_date;
    Context context = AmbassadorRegistrationActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAmbassadorRegistrationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

//        toolbar
        setSupportActionBar(binding.customToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        occupation selection start here
        binding.conditionCardView3.setVisibility(View.GONE);
        occupation = new ArrayList<>();
        occupation.add("Student");
        occupation.add("Medical/Pharmaceuticals industry");
        occupation.add("Legal Profession");
        occupation.add("Civil Service");
        occupation.add("Defence / Military");
        occupation.add("Accounting / Finance");
        occupation.add("Education / Training");
        occupation.add("Garments / Textile");
        occupation.add("Engineering");
        occupation.add("Architecture / Construction");
        occupation.add("Manufacturing");
        occupation.add("Arts, Culture and Entertainment");
        occupation.add("Author");
        occupation.add("Computer Science / Information Technology");
        occupation.add("Consultant");
        occupation.add("Dentist");
        occupation.add("Law Enforcement");
        occupation.add("Marketing / Media / Ad / Event Mgt.");
        occupation.add("Sports / Athletes");
        occupation.add("Social Media Influencer / Motivational Speaker");
        occupation.add("Politics");
        occupation.add("Travel / Tourism / Hospitality");
        occupation.add("Others");

        binding.occupationSpinner.setOnClickListener(view1 -> {
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_spinner_layout_2);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();

            ListView listView=dialog.findViewById(R.id.list_view);
            ArrayAdapter<String> adapter=new ArrayAdapter<>(AmbassadorRegistrationActivity.this, android.R.layout.simple_list_item_1,occupation);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // when item selected from list
                    // set selected item on textView
                    String item  = adapter.getItem(position);
                    switch (item){
                        case "Student" :
                            occupation_ref = 1;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                        case "Medical/Pharmaceuticals industry" :
                            occupation_ref = 2;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                        case "Legal Profession" :
                            occupation_ref = 3;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                        case "Civil Service" :
                            occupation_ref = 4;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                        case "Defence / Military" :
                            occupation_ref = 5;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                        case "Accounting / Finance" :
                            occupation_ref = 6;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                        case "Education / Training" :
                            occupation_ref = 7;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                        case "Garments / Textile" :
                            occupation_ref = 8;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                        case "Engineering" :
                            occupation_ref = 9;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                        case "Architecture / Construction" :
                            occupation_ref = 10;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                        case "Manufacturing" :
                            occupation_ref = 11;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                        case "Arts, Culture and Entertainment" :
                            occupation_ref = 12;
                            binding.occupationSpinner.setText(item);
                            inspiration();
                            dialog.dismiss();
                            break;
                        case "Author" :
                            occupation_ref = 13;
                            binding.occupationSpinner.setText(item);
                            inspiration();
                            dialog.dismiss();
                            break;
                        case "Computer Science / Information Technology" :
                            occupation_ref = 14;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                        case "Consultant" :
                            occupation_ref = 15;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                        case "Dentist" :
                            occupation_ref = 16;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                        case "Law Enforcement" :
                            occupation_ref = 17;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                        case "Marketing / Media / Ad / Event Mgt." :
                            occupation_ref = 18;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                        case "Sports / Athletes" :
                            occupation_ref = 19;
                            binding.occupationSpinner.setText(item);
                            inspiration();
                            dialog.dismiss();
                            break;
                        case "Social Media Influencer / Motivational Speaker" :
                            occupation_ref = 20;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                        case "Politics" :
                            occupation_ref = 21;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                        case "Travel / Tourism / Hospitality" :
                            occupation_ref = 22;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                        case "Others" :
                            occupation_ref = 23;
                            binding.occupationSpinner.setText(item);
                            goodwill();
                            dialog.dismiss();
                            break;
                    }
                }
            });

        });
//        occupation selection ends here

//        birth day start here

        binding.dateBirth.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, AmbassadorRegistrationActivity.this,year, month,day);
            datePickerDialog.show();
        });
//        birth day ends here

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

    }


    //    goodwill_inspiration selection function start here
    private void goodwill() {
        binding.conditionCardView3.setVisibility(View.VISIBLE);
        binding.goodwillInspirationTv.setText("You are eligible for Goodwill Ambassador");
    }
    private void inspiration() {
        binding.conditionCardView3.setVisibility(View.VISIBLE);
        binding.goodwillInspirationTv.setText("You are eligible for Inspiration Ambassador");
    }
//    goodwill_inspiration selection function end here



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

}