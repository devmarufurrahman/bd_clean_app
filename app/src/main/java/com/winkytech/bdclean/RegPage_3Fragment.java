package com.winkytech.bdclean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vishnusivadas.advanced_httpurlconnection.PutData;


public class RegPage_3Fragment extends Fragment {
    Button cancel,next,back;
    EditText reg_password,reg_re_password;
    String name,email,address,contact;
    int gender_ref,religion_ref,occupation_ref,district_ref,division_ref,upazila_ref,union_ref,village_ref;
    ProgressBar progressBar;
    TextView toast_message;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Contact = "contactKey";
    SharedPreferences sharedpreferences;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reg_page_3, container, false);

        Bundle bundle = getArguments();
        name=bundle.getString("name");
        email=bundle.getString("email");
        contact=bundle.getString("contact");
        address=bundle.getString("address");
        gender_ref=bundle.getInt("gender_ref");
        religion_ref=bundle.getInt("religion_ref");
        occupation_ref=bundle.getInt("occupation_ref");
        district_ref=bundle.getInt("district_ref");
        division_ref= bundle.getInt("division_ref");
        upazila_ref= bundle.getInt("upazila_ref");
        union_ref= bundle.getInt("union_ref");
        village_ref= bundle.getInt("village_ref");

        cancel=view.findViewById(R.id.reg_cancel_btn);
        next=view.findViewById(R.id.reg_complete_btn);
        reg_password=view.findViewById(R.id.reg_et_userName);
        reg_re_password=view.findViewById(R.id.reg_et_password);
        back=view.findViewById(R.id.reg_back_btn);
        progressBar=view.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        sharedpreferences=getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                getActivity().overridePendingTransition(R.anim.push_right_out,R.anim.no_anim);
                startActivity(intent);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("name",name);
                bundle.putString("email",email);
                bundle.putString("contact",contact);
                bundle.putString("address",address);

                RegPage_4Fragment regPage_4Fragment = new RegPage_4Fragment();
                regPage_4Fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.push_right_out,R.anim.no_anim);
                transaction.replace(R.id.registration_container,regPage_4Fragment).commit();

            }
        });

        return view;
    }

    private void registerUser() {

        String password = reg_password.getText().toString().trim();
        String re_password = reg_re_password.getText().toString().trim();

        if (!password.equals("") && !re_password.equals("") && password.equals(re_password)){

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                    //Starting Write and Read data with URL
                    //Creating array for parameters
                    String[] field = new String[13];
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
                    field[11] = "union_ref";
                    field[12] = "village_ref";
                    //Creating array for data
                    String[] data = new String[13];
                    data[0] = name;
                    data[1] = email;
                    data[2] = contact;
                    data[3] = address;
                    data[4] = password;
                    data[5] = String.valueOf(gender_ref);
                    data[6] = String.valueOf(religion_ref);
                    data[7] = String.valueOf(occupation_ref);
                    data[8] = String.valueOf(division_ref);
                    data[9] = String.valueOf(district_ref);
                    data[10] = String.valueOf(upazila_ref);
                    data[11] = String.valueOf(union_ref);
                    data[12] = String.valueOf(village_ref);

                    PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/registration.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult().trim();
                            if (result.equals("Registration Successful. You Can now Login")) {
                                progressBar.setVisibility(View.GONE);

                            Toast toast = new Toast(getContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout,getView().findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Registration Successful. You Can now Login");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();

                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(Contact, contact);
                                editor.commit();
//                                Toast.makeText(getContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), AboutBDCleanActivity.class);
                                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.fade_out);
                                startActivity(intent);

                            } else {
                                progressBar.setVisibility(View.GONE);
                                Log.i("PutData", result);
                                //Toast.makeText(getContext(), "Failed To Create User", Toast.LENGTH_SHORT).show();
                                //Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                            Toast toast = new Toast(getContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, getView().findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Failed To Create User");
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

            Toast.makeText(getContext(), "lease Fill Up Requirements", Toast.LENGTH_SHORT).show();
        }

    }
}