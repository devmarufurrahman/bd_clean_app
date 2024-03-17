package com.winkytech.bdclean;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class RegPage_2Fragment extends Fragment {
    Button cancel,next,back;
    Spinner select_gender,select_religion,select_occupation;
    List<String> gender, religion,occupation;
    int gender_ref,religion_ref,occupation_ref;
    String genderType,religionType,occupationType;
    String name,email,address,contact;
    Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reg_page_2, container, false);

        Bundle bundle = getArguments();
        name= bundle.getString("name");
        email= bundle.getString("email");
        address= bundle.getString("address");
        contact= bundle.getString("contact");

        context = getContext();

        cancel=view.findViewById(R.id.reg_cancel_btn);
        next=view.findViewById(R.id.reg_next_btn);
        select_gender=view.findViewById(R.id.gender_spinner);
        select_religion=view.findViewById(R.id.religion_spinner);
        select_occupation=view.findViewById(R.id.occupation_spinner);
        back=view.findViewById(R.id.reg_back_btn);

        gender=new ArrayList<>();
        gender.add("Male");
        gender.add("Female");
        select_gender.setAdapter(new ArrayAdapter<>(context, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, gender));

        select_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                genderType=gender.get(position);
                if (genderType.equals("Male")){
                    gender_ref=1;
                }
                if (genderType.equals("Female")){
                    gender_ref=2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        religion=new ArrayList<>();
        religion.add("Muslim");
        religion.add("Hindu");
        religion.add("Christian");
        religion.add("Buddha");
        select_religion.setAdapter(new ArrayAdapter<>(getContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, religion));
        select_religion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                religionType=religion.get(position);
                if (religionType.equals("Muslim")){
                    religion_ref = 1;
                }
                if (religionType.equals("Hindu")){
                    religion_ref = 2;
                }
                if (religionType.equals("Christian")){
                    religion_ref = 3;
                }
                if (religionType.equals("Buddha")){
                    religion_ref = 4;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        select_occupation.setAdapter(new ArrayAdapter<>(getContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, occupation));
        select_occupation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                occupationType=occupation.get(position);
                if (occupationType.equals("Student")){
                    occupation_ref=1;
                }
                if (occupationType.equals("Farmer")){
                    occupation_ref=2;
                }
                if (occupationType.equals("Businessman")){
                    occupation_ref=3;
                }
                if (occupationType.equals("Service Holder (Govt.)")){
                    occupation_ref=4;
                }
                if (occupationType.equals("Service Holder (Private Company)")){
                    occupation_ref=5;
                }
                if (occupationType.equals("Enterpreneur")){
                    occupation_ref=6;
                }
                if (occupationType.equals("Home Maker")){
                    occupation_ref=7;
                }
                if (occupationType.equals("Social Worker")){
                    occupation_ref=8;
                }
                if (occupationType.equals("Technical Worker")){
                    occupation_ref=9;
                }
                if (occupationType.equals("Other")){
                    occupation_ref=10;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_right_out,R.anim.no_anim);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle1 = new Bundle();
                bundle1.putString("name",name);
                bundle1.putString("email",email);
                bundle1.putString("contact",contact);
                bundle1.putString("address",address);
                bundle1.putInt("gender_ref",gender_ref);
                bundle1.putInt("religion_ref",religion_ref);
                bundle1.putInt("occupation_ref",occupation_ref);

                RegPage_4Fragment regPage_4Fragment = new RegPage_4Fragment();
                regPage_4Fragment.setArguments(bundle1);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.push_left_in,R.anim.fade_out);
                transaction.replace(R.id.registration_container,regPage_4Fragment).commit();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RegPage_1Fragment regPage_1Fragment = new RegPage_1Fragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.push_right_out,R.anim.no_anim);
                transaction.replace(R.id.registration_container,regPage_1Fragment).commit();

            }
        });

        return view;
    }
}