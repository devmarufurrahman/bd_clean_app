package com.winkytech.bdclean;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.winkytech.bdclean.databinding.ActivityHonorableRegistrationBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HonorableRegistration extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener{

    ActivityHonorableRegistrationBinding binding;
    int gender_ref=0, marital_ref=0, religion_ref=0, day, month, year, myday, myMonth, myYear;
    String birth_date;
    List<String> gender, marital, religion;
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

}