package com.winkytech.bdclean;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.telephony.mbms.MbmsErrors;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class RegPage_1Fragment extends Fragment {

    Button cancel,next;
    EditText reg_name,reg_email,reg_contact,reg_address;
    TextView toast_message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reg_page_1, container, false);

        cancel=view.findViewById(R.id.reg_cancel_btn);
        next=view.findViewById(R.id.reg_next_btn);
        reg_name = view.findViewById(R.id.reg_et_name);
        reg_email = view.findViewById(R.id.reg_et_email);
        reg_contact = view.findViewById(R.id.reg_et_contact);
        reg_address = view.findViewById(R.id.reg_et_address);

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

                String name = reg_name.getText().toString().trim();
                String email = reg_email.getText().toString().trim();
                String contact = reg_contact.getText().toString().trim();
                String address = reg_address.getText().toString().trim();

                if (!name.equals("") && !email.equals("") && !contact.equals("") && !address.equals("")){

                    Bundle bundle = new Bundle();
                    bundle.putString("name",name);
                    bundle.putString("email",email);
                    bundle.putString("contact",contact);
                    bundle.putString("address",address);

                    RegPage_2Fragment regPage_2Fragment = new RegPage_2Fragment();
                    regPage_2Fragment.setArguments(bundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.push_left_in,R.anim.fade_out);
                    transaction.replace(R.id.registration_container,regPage_2Fragment).commit();
                } else {
                    Toast toast = new Toast(getActivity());
                    View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,getView().findViewById(R.id.custom_toast));
                    toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                    toast_message.setText("Please insert all information");
                    toast.setView(toast_view);
                    toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                    toast.setDuration(Toast.LENGTH_SHORT);
                }
            }
        });

        return view;
    }
}