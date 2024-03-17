package com.winkytech.bdclean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.winkytech.bdclean.databinding.ActivityForgotPasswordBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class ForgotPasswordActivity extends AppCompatActivity {
    ActivityForgotPasswordBinding binding;
    ProgressBar  progressBar;
    OtpTextView otpTextView;
    TextView toast_message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        View view =binding.getRoot();
        setContentView(view);
        progressBar = binding.progressbar;
        progressBar.setVisibility(View.GONE);

//        toolbar setup
        setSupportActionBar(binding.customToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        binding.findBtn.setOnClickListener(view1 -> {
            findNumber();
        });

        binding.submitBtn.setOnClickListener(view1 -> {
            setPassword();
        });

    }

    private void setPassword() {
        String password = binding.passwordForgot.getText().toString().trim();
        String passwordC = binding.confirmPasswordForgot.getText().toString().trim();

        if (password.equals("") || password.length() < 4){
            fieldFocus(binding.passwordForgot, "Password min char 4");
        } else if (!passwordC.equals(password)) {
            fieldFocus(binding.confirmPasswordForgot, "Password not match");
        } else {
            Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
        }
    }

    private void findNumber() {
        String contact = binding.forgotContact.getText().toString().trim();
        String final_contact = "88" + contact;

        if (contact.equals("") || contact.length() != 11){

            fieldFocus(binding.forgotContact, "Please give a valid phone number");

        } else {

            checkContact(final_contact);
           
        }
    }

    private void fieldFocus(EditText field, String error) {
        field.setError(error);
        field.requestFocus();
    }

    private void checkContact(String finalContact) {

        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/checkContact.php?contact="+finalContact;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("check contact = " + response);
                        progressBar.setVisibility(View.GONE);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id_count = object.getString("id_count");

                                if (id_count.equals("0")){

                                    fieldFocus(binding.forgotContact, "Contact not registered");
                                    Toast toast = new Toast(getApplicationContext());
                                    View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                                    toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                                    toast_message.setText("Contact not registered. Please sign up your account.");
                                    toast.setView(toast_view);
                                    toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                                    toast.setDuration(Toast.LENGTH_SHORT);
                                    toast.show();

                                } else {
                                    sendSMS(finalContact);
                                    Toast.makeText(ForgotPasswordActivity.this, "Account Searching...", Toast.LENGTH_LONG).show();
                                }

                            }

                        } catch (JSONException e){
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "Failed to get Patient List", Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText("Failed to get profile info");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

    }



    // SENDING OTP start
    private void sendSMS(String contact) {
        progressBar.setVisibility(View.VISIBLE);
        //String final_contact = "88"+contact;
        String otp = generateOTP();
        Log.d("OTP", "sendSMS: "+otp);

        // String authorizationHeader = "Bearer 184|pt5PZv1ROwn03DADlrpaNbRER229eSQIVGyJ0IP6";
        String recipient = contact;
        String sender_id = "8809601003721";
        String type = "plain";
        String message = "Your BD Clean verification OTP code is : "+otp;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.esms.com.bd/smsapi",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            Log.d("SMS API RESPONSE", response);
                            displayOTPDialog(otp, contact);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ForgotPasswordActivity.this, "Account found successfully", Toast.LENGTH_SHORT).show();
                        } catch (Exception e){
                            Log.d("OTP Response", String.valueOf(e));
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            Toast.makeText(ForgotPasswordActivity.this, "OTP Problem", Toast.LENGTH_SHORT).show();
                            Log.d("SMS API RESPONSE", error.getMessage());
                            binding.findBtn.setVisibility(View.VISIBLE);
                            binding.submitBtn.setVisibility(View.GONE);
                        } catch (Exception e){
                            Log.d("OTP Response", String.valueOf(e));
                        }

                    }
                }) {

//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Authorization: ", authorizationHeader);
//                return headers;
//            }

//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("contacts", recipient);
//                params.put("senderid", sender_id);
//                params.put("type", type);
////                params.put("schedule_time", scheduleTime);
//                params.put("msg", message);
//                params.put("api_key","184|pt5PZv1ROwn03DADlrpaNbRER229eSQIVGyJ0IP6");
//                return params;
//            }
        };

        requestQueue.add(stringRequest);
    }


    private void displayOTPDialog(String OTP, String contact) {

        Dialog dialog = new Dialog(ForgotPasswordActivity.this);
        dialog.setContentView(R.layout.otp_verification_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();


        otpTextView = dialog.findViewById(R.id.otp_view);
        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {

                if (OTP.equals(otp)) {

                    Toast.makeText(ForgotPasswordActivity.this, "OTP matched, Now you can set your Password.", Toast.LENGTH_LONG).show();

                    dialog.dismiss();
                    binding.submitBtn.setVisibility(View.VISIBLE);
                    binding.findBtn.setVisibility(View.GONE);
                    binding.passwordForgot.setVisibility(View.VISIBLE);
                    binding.confirmPasswordForgot.setVisibility(View.VISIBLE);
                    binding.forgotContact.setKeyListener(null);
                    binding.forgotContact.setText(contact);
//                    fieldFocus(binding.passwordForgot,"Set your password");
                    binding.header.setText(R.string.choose_a_new_password);
                    binding.numberTv.setText(R.string.create_a_new_password_that_is_at_least_4_characters_long);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "OTP not matching", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    public static String generateOTP () {
        int otpLength = 6;
        StringBuilder otp = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10)); // Generates random digits from 0 to 9
        }

        return otp.toString();
    }

    // SENDING OTP end


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}