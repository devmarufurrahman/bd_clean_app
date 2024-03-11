package com.winkytech.bdclean;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class LoginActivity extends AppCompatActivity {

    Button login_btn ;
    TextView signup_tbn, forgot_password_btn;
    EditText login_contact, login_password;
    ProgressBar progressBar;
    TextView toast_message;
    OtpTextView otpTextView;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Contact = "contactKey";
    public static final String org_level_ref = "org_level_ref";
    SharedPreferences sharedpreferences;

    NetworkChangeListener networkChangeListener;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        sharedpreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    }, 1);
        } else {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,}, 1);
        }


        login_btn=findViewById(R.id.login_btn);
        login_contact=findViewById(R.id.login_et_contact);
        login_password=findViewById(R.id.login_et_password);
        progressBar = findViewById(R.id.progressbar);
        signup_tbn = findViewById(R.id.signup_btn);
        forgot_password_btn = findViewById(R.id.forgot_password_btn);
        progressBar.setVisibility(View.GONE);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userLogin();

            }
        });

        forgot_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });


        signup_tbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(LoginActivity.this);
                dialog.setContentView(R.layout.member_registration_info_dialog_1);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);
                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                EditText user_name = dialog.findViewById(R.id.user_name);
                EditText user_email = dialog.findViewById(R.id.user_email);
                EditText user_contact = dialog.findViewById(R.id.user_contact);
                EditText password = dialog.findViewById(R.id.password);
                EditText confirm_password = dialog.findViewById(R.id.confirm_password);
                Button submit_btn = dialog.findViewById(R.id.submit_btn);

                submit_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String name = user_name.getText().toString().trim();
                        String email = user_email.getText().toString().trim();
                        String contact = user_contact.getText().toString().trim();
                        String uPassword = password.getText().toString().trim();
                        String cPassword = confirm_password.getText().toString().trim();

                        boolean isValid = isValidEmail(email);

                        if (name.equals("")){
                            fieldFocus(user_name,"Enter Your Name");
                        } else if (email.equals("") || !isValid){
                            fieldFocus(user_email,"Enter Your Valid Email");
                        } else if (contact.equals("") || contact.length() != 11){

                            fieldFocus(user_contact,"Please Enter a valid number");
                        } else if (uPassword.equals("") || uPassword.length() < 4 ) {
                            fieldFocus(password,"Password min char 4");
                        } else if (!uPassword.equals(cPassword)) {

                            fieldFocus(confirm_password,"Password not match");
                        } else {
                            createUser(name, email, contact, cPassword);
                            dialog.dismiss();
                        }

                    }
                });

            }
        });

    }

    private void userLogin(){

        String contact =login_contact.getText().toString().trim();
        String password =login_password.getText().toString().trim();

        if (!contact.equals("") && contact.length() == 11 && !password.equals("") ){

            contact = "88"+contact;
            String finalContact = contact.trim();
            progressBar.setVisibility(View.VISIBLE);
            String url = "https://bdclean.winkytech.com/backend/api/userLogin.php?contact="+finalContact+"&password="+password;

            StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String response_data = response.toString().trim();
                    Log.d("LOGIN DATA = ", response_data);
                    if (response_data.equals("no user")){

                        Dialog dialog = new Dialog(LoginActivity.this);
                        dialog.setContentView(R.layout.login_error_dialog_layout);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.setCancelable(true);
                        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                        dialog.show();

                        Button ok_btn = dialog.findViewById(R.id.ok_btn);

                        ok_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                            }
                        });
                        progressBar.setVisibility(View.GONE);

//                        submit_btn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                String full_name = user_name.getText().toString().trim();
//                                String user_password = password.getText().toString().trim();
//                                String con_pass = confirm_password.getText().toString().trim();
//
//                                if (full_name.equals("") || user_password.equals("") || con_pass.equals("")){
//                                    Toast toast = new Toast(getApplicationContext());
//                                    View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
//                                    toast_message=toast_view.findViewById(R.id.custom_toast_tv);
//                                    toast_message.setText("Please fill up all fields");
//                                    toast.setView(toast_view);
//                                    toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
//                                    toast.setDuration(Toast.LENGTH_SHORT);
//                                    toast.show();
//                                } else if (!user_password.equals(con_pass)){
//
//                                    Toast toast = new Toast(getApplicationContext());
//                                    View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
//                                    toast_message=toast_view.findViewById(R.id.custom_toast_tv);
//                                    toast_message.setText("Password doesn't match");
//                                    toast.setView(toast_view);
//                                    toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
//                                    toast.setDuration(Toast.LENGTH_SHORT);
//                                    toast.show();
//                                } else {
//
//                                    createUser(full_name, con_pass, finalContact);
//                                    dialog.dismiss();
//                                }
//
//                            }
//                        });

                    } else {

                        try {
                            JSONArray obj = new JSONArray(response);
                            JSONObject jsonObject = obj.getJSONObject(0);
                            String id = jsonObject.getString("id");
                            String name = jsonObject.getString("full_name");
                            String designation = jsonObject.getString("designation");
                            String email = jsonObject.getString("email");
                            String upazila_ref = jsonObject.getString("upazila_ref");
                            String address = jsonObject.getString("address");
                            String profile_photo = jsonObject.getString("profile_photo");
                            String org_level_pos = jsonObject.getString("org_level_pos");
                            String division = jsonObject.getString("division");
                            if (division.equals("null")){
                                division = "N/A";
                            }
                            String district = jsonObject.getString("district");
                            if (district.equals("null")){
                                district = "N/A";
                            }
                            String upazila = jsonObject.getString("upazila");
                            if (upazila.equals("null")){
                                upazila = "N/A";
                            }
                            String union = jsonObject.getString("union_name");
                            if (union.equals("null")){
                                union = "N/A";
                            }
                            String village = jsonObject.getString("village");
                            if (village.equals("null")){
                                village = "N/A";
                            }

                            String occupation = jsonObject.getString("occupation");
                            String fb_link = jsonObject.getString("fb_link");
                            String district_ref = jsonObject.getString("district_ref");
                            String division_ref = jsonObject.getString("division_ref");
                            String village_ref = jsonObject.getString("village_ref");
                            String union_ref = jsonObject.getString("union_ref");
                            String team_ref = jsonObject.getString("team_ref");
                            String user_position = jsonObject.getString("user_position");
                            String member_code = jsonObject.getString("user_code");
                            if (member_code.equals("null")){
                                member_code = "N/A";
                            }

                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(org_level_ref, org_level_pos);
                            editor.putString("email",email);
                            editor.putString("address",address);
                            editor.putString("profile_photo",profile_photo);
                            editor.putString("upazila_ref",upazila_ref);
                            editor.putString("name",name);
                            editor.putString("designation", designation);
                            editor.putString("user_id",id);
                            editor.putString("division",division);
                            editor.putString("district",district);
                            editor.putString("upazila",upazila);
                            editor.putString("union",union);
                            editor.putString("village",village);
                            editor.putString("occupation",occupation);
                            editor.putString("fb_link", fb_link);
                            editor.putString("district_ref", district_ref);
                            editor.putString("division_ref", division_ref);
                            editor.putString("village_ref", village_ref);
                            editor.putString("union_ref", union_ref);
                            editor.putString("team_ref", team_ref);
                            editor.putString("user_position", user_position);
                            editor.putString("member_code", member_code);
                            editor.putString(Contact, finalContact);  // You must comment out this line, if you don't need to bypass otp
                            editor.apply();


                            // You must comment out this code, if you don't need to bypass otp
                            if (designation.equals("User")){

                                Intent intent = new Intent(getApplicationContext(),AboutBDCleanActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
                                finish();

                            } else {
                                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
                                finish();
                            }
                            // You must comment out this code, if you don't need to bypass otp


//                            sendSMS(finalContact , designation); // this method working on OTP



                        }
                        catch (JSONException e){
                            Log.e("anyText",response);
                            progressBar.setVisibility(View.GONE);
                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("ERROR: FAILED TO LOGIN");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    Log.i("VolleyError",error.getMessage());
                    Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    String volleyError = "";

                    if (error instanceof NetworkError){
                        volleyError="Network Error";
                    } else if (error instanceof ServerError){

                        volleyError="Server Connection error";
                    }

                    Toast toast = new Toast(getApplicationContext());
                    View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                    toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                    toast_message.setText(error.getMessage());
                    toast.setView(toast_view);
                    toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                    //progressBar.setVisibility(View.GONE);
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        } else if (contact.length() != 11){
            progressBar.setVisibility(View.GONE);
            fieldFocus(login_contact,"Invalid Number");
        } else if (password.equals("")){
            progressBar.setVisibility(View.GONE);
            fieldFocus(login_password,"Please Fill Password");
        } else if (contact.equals("")) {
            fieldFocus(login_contact,"Please Fill Contact");
        }
    }

    private void createUser(String full_name, String email, String contact, String password) {
        progressBar.setVisibility(View.VISIBLE);
        contact = "88"+contact;
        String finalContact = contact;

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                //Starting Write and Read data with URL
                //Creating array for parameters
                String[] field = new String[4];
                field[0] = "name";
                field[1] = "email";
                field[2] = "contact";
                field[3] = "password";

                //Creating array for data
                String[] data = new String[4];
                data[0] = full_name;
                data[1] = email;
                data[2] = finalContact;
                data[3] = password;

                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/createNewUser.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();
                        System.out.println("New user = " + result);

                        if (result.equals("success")) {




                            // You must comment this code, if you want to bypass otp
                            Intent intent = new Intent(getApplicationContext(),AboutBDCleanActivity.class);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(Contact, finalContact);
                            editor.putString("user_type","user");
                            editor.apply();
                            startActivity(intent);
                            finish();
                            // You must comment this code, if you want to bypass otp

//                            sendSMS(finalContact,"new_user"); // this method working on OTP

                        } else if (result.equals("contact duplicate")){
                            progressBar.setVisibility(View.GONE);
                            Log.i("PutData", result);
                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Contact already in use. Please try with different one");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.i("PutData", result);
                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Failed, please try again");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();

                        }
                    }
                }
                //End Write and Read data with URL
            }
        });

    }

    // field validation
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


    // SENDING OTP start
    private void sendSMS(String contact, String designation) {
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
                            displayOTPDialog(otp, designation, contact);
                            progressBar.setVisibility(View.GONE);
                        } catch (Exception e){
                            Log.d("OTP Response", String.valueOf(e));
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            Toast.makeText(LoginActivity.this, "OTP Problem", Toast.LENGTH_SHORT).show();
                            Log.d("SMS API RESPONSE", error.getMessage());
                            login_btn.setEnabled(true);
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

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("contacts", recipient);
                params.put("senderid", sender_id);
                params.put("type", type);
//                params.put("schedule_time", scheduleTime);
                params.put("msg", message);
                params.put("api_key","184|pt5PZv1ROwn03DADlrpaNbRER229eSQIVGyJ0IP6");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }


    private void displayOTPDialog(String OTP, String designation, String contact) {

        Dialog dialog = new Dialog(LoginActivity.this);
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

                    Toast.makeText(LoginActivity.this, "OTP matched", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Contact, contact);
                    editor.apply();

                    if (designation.equals("User")){

                        Intent intent = new Intent(getApplicationContext(),UserLocationActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
                        finish();

                    } else if (designation.equals("new_user")) {

                        Toast toast = new Toast(LoginActivity.this);
                        View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout,findViewById(R.id.custom_toast));
                        toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                        toast_message.setText("Your account has been created");
                        toast.setView(toast_view);
                        toast.setGravity(Gravity.BOTTOM|Gravity.FILL_HORIZONTAL,0,110);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();

                        Intent intent = new Intent(getApplicationContext(),AboutBDCleanActivity.class);
                        editor.putString("user_type","user");
                        editor.apply();
                        startActivity(intent);
                        finish();

                    } else {
                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
                        finish();
                    }


                } else {
                    Toast.makeText(LoginActivity.this, "OTP not matching", Toast.LENGTH_SHORT).show();
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
    protected void onDestroy() {
        super.onDestroy();
        // Unregister broadcast receiver when activity is destroyed
        unregisterReceiver(networkChangeListener);
    }

//    private void sendSMS() {
//        //String url = "https://login.esms.com.bd/api/v3/sms/send";
//        String authorizationHeader = "Bearer 184|pt5PZv1ROwn03DADlrpaNbRER229eSQIVGyJ0IP6";
//        String recipient = "8801792921068";
//        String sender_id = "8809601003721";
//        String type = "plain";
//        String message = "This is a test message";
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.esms.com.bd/smsapi",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d("SMS API RESPONSE", response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("SMS API RESPONSE", error.getMessage());
//                    }
//                }) {
//
////            @Override
////            public Map<String, String> getHeaders() {
////                Map<String, String> headers = new HashMap<>();
////                headers.put("Authorization: ", authorizationHeader);
////                return headers;
////            }
//
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
//        };
//
//        requestQueue.add(stringRequest);
//    }

    public boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}