package com.winkytech.bdclean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vishnusivadas.advanced_httpurlconnection.PutData;
import com.winkytech.bdclean.databinding.ActivityAmbassadorRegistrationBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AmbassadorRegistrationActivity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener{

    ActivityAmbassadorRegistrationBinding binding;
    List<String> occupation, gender;
    Dialog dialog;
    int occupation_ref=0,  gender_ref=0, day, month, year,myday, myMonth, myYear, ambassador_ref = 0, experience_ref = 0, invite_ref = 0;
    String birth_date="", experienceDetails;
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
                            buttonFocus(binding.genderSpinner, null);
                            dialog.dismiss();
                            break;
                        case "Female" :
                            gender_ref = 2;
                            binding.genderSpinner.setText(item);
                            buttonFocus(binding.genderSpinner, null);
                            dialog.dismiss();
                            break;

                        case "Others" :
                            gender_ref = 3;
                            binding.genderSpinner.setText(item);
                            buttonFocus(binding.genderSpinner, null);
                            dialog.dismiss();
                            break;
                    }
                }
            });
        });

//        gender selection ends here


//        experienceDetails radio button method start
        binding.noExperience.setOnClickListener(v -> {
            binding.experienceDetails.setVisibility(View.GONE);
            experienceDetails = "No experience";
            experience_ref = 2;
            binding.yesExperience.setChecked(false);
        });

        binding.yesExperience.setOnClickListener(v -> {
            binding.experienceDetails.setVisibility(View.VISIBLE);
            experience_ref = 1;
            binding.noExperience.setChecked(false);

        });
//        experienceDetails radio button method end

//        invite radio button method start
        binding.yes1.setOnClickListener(v -> {
            invite_ref = 1;
            binding.no1.setChecked(false);
        });

        binding.no1.setOnClickListener(v -> {
            invite_ref = 2;
            binding.yes1.setChecked(false);

        });
//        invite radio button method end



//        data save on click method
        binding.submitBtn.setOnClickListener(v -> {
            saveData();
        });

    }

//    save method
    private void saveData() {
        String address = binding.memberAddress.getText().toString().trim();
        String fatherName = binding.fatherName.getText().toString().trim();
        String motherName = binding.motherName.getText().toString().trim();
        String nationality = binding.nationality.getText().toString().trim();
        String nidNo = binding.nidNumber.getText().toString().trim();
        String examTitle = binding.exam.getText().toString().trim();
        String examMajor = binding.major.getText().toString().trim();
        experienceDetails = binding.experienceDetails.getText().toString().trim();
        String instituteName = binding.educationInstitute.getText().toString().trim();
        String passingYear = binding.passYear.getText().toString().trim();
        String currentOccupation = binding.currentOccupation.getText().toString().trim();
        String employeeInstituteName = binding.employerInstitution.getText().toString().trim();
        String employeeDetails = binding.employerDetails.getText().toString().trim();
        String interestOfAmbassador = binding.ambassadorInterest.getText().toString().trim();
        String skillOfAmbassador = binding.skillOfExperience.getText().toString().trim();
        String timeOfAmbassador = binding.timeOfAmbassador.getText().toString().trim();
        String othersInfo = binding.othersInfo.getText().toString().trim();

        if (address.equals("")){
            fieldFocus(binding.memberAddress, "Input present address");
            
        } else if (occupation_ref == 0) {
            buttonFocus(binding.occupationSpinner, "Select occupation");
        } else if (fatherName.equals("")) {
            fieldFocus(binding.fatherName, "input father name");
        } else if (motherName.equals("")) {
            fieldFocus(binding.motherName, "input mother name");
        } else if (nationality.equals("")) {
            fieldFocus(binding.nationality, "input nationality");
        } else if (nidNo.equals("") && (nidNo.length() <= 8 || nidNo.length() >= 13)) {
            fieldFocus(binding.nidNumber, "input valid nid no");
        } else if (birth_date.equals("")) {
            buttonFocus(binding.dateBirth, "select birthday");
        } else if (gender_ref == 0) {
            buttonFocus(binding.genderSpinner, "select gender");
        } else if (examTitle.equals("")) {
            fieldFocus(binding.exam, "input exam title");
        } else if (examMajor.equals("")) {
            fieldFocus(binding.major, "input exam major");
        } else if (instituteName.equals("")) {
            fieldFocus(binding.educationInstitute, "input institute name");
        } else if (passingYear.equals("") &&  passingYear.length() == 4) {
            fieldFocus(binding.passYear, "input valid passing year");
        } else if (currentOccupation.equals("")) {
            fieldFocus(binding.currentOccupation, "input occupation");
        } else if (employeeInstituteName.equals("")) {
            fieldFocus(binding.employerInstitution, "input institute");
        } else if (employeeDetails.equals("")) {
            fieldFocus(binding.employerDetails, "input details");
        } else if (experienceDetails.equals("") && experience_ref == 1) {
            fieldFocus(binding.experienceDetails, "input details");
            Toast.makeText(context, "experience details?", Toast.LENGTH_SHORT).show();
        } else if (experience_ref == 0) {
            customToast("experience details yes/no?");
        } else if (interestOfAmbassador.equals("")) {
            fieldFocus(binding.ambassadorInterest, "input interest");
        } else if (skillOfAmbassador.equals("")) {
            fieldFocus(binding.skillOfExperience, "input skill");
        } else if (timeOfAmbassador.equals("")) {
            fieldFocus(binding.timeOfAmbassador, "input field");
        } else if (invite_ref == 0) {
            customToast("willing to travel event or invites yes/no?");
        } else if (othersInfo.equals("")) {
            fieldFocus(binding.othersInfo, "input field");
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
                    String msg = "Thank you for your interest in becoming an Ambassador Member of BD Clean. Your application will be evaluated within the next 15 working days, and further communication regarding the status of your application will be provided accordingly.";
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



    //    goodwill_inspiration selection function start here
    private void goodwill() {
        binding.conditionCardView3.setVisibility(View.VISIBLE);
        binding.goodwillInspirationTv.setText("You are eligible for Goodwill Ambassador");
        ambassador_ref = 1;
    }
    private void inspiration() {
        binding.conditionCardView3.setVisibility(View.VISIBLE);
        binding.goodwillInspirationTv.setText("You are eligible for Inspiration Ambassador");
        ambassador_ref = 2;
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
        buttonFocus(binding.dateBirth, null);
        binding.dateBirth.setText(birth_date);
    }

}