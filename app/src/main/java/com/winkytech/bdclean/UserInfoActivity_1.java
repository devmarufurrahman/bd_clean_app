package com.winkytech.bdclean;

import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.LayoutDirection;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class UserInfoActivity_1 extends AppCompatActivity {

    //    Button next_btn, back_btn;
    TextView event_tv, toast_message;
    Toolbar toolbar;
    String contact;
    EditText present_address, facebook_id_link, fathers_name, father_contact, mother_name, mother_contact;
    Button submit_btn;
    int gender_ref, counter;
    String ev_id, ev_name, ev_address ,ev_details, start_date, end_date, cover_pic, encodedImage = "";
    Dialog dialog;
    ImageView select_photo;
    NetworkChangeListener networkChangeListener;
    Bitmap bitmap;
    ProgressBar progressbar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info1);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        contact = shared.getString("contactKey","");
        System.out.println("Contact = " + contact);

        Intent intent = getIntent();
        ev_id = intent.getStringExtra("ev_id");
        ev_name = intent.getStringExtra("ev_name");
        ev_address = intent.getStringExtra("ev_address");
        ev_details = intent.getStringExtra("ev_details");
        start_date = intent.getStringExtra("date_start");
        end_date = intent.getStringExtra("end_date");
        cover_pic = intent.getStringExtra("cover_pic");
        counter = intent.getIntExtra("counter",0);

        present_address = findViewById(R.id.present_address);
        fathers_name = findViewById(R.id.father_name);
        father_contact = findViewById(R.id.father_contact);
        mother_name = findViewById(R.id.mother_name);
        mother_contact = findViewById(R.id.mother_contact);
        submit_btn = findViewById(R.id.next_btn);
        event_tv=findViewById(R.id.event_tv);
        toolbar = findViewById(R.id.custom_toolbar);
        select_photo = findViewById(R.id.member_photo);
        progressbar = findViewById(R.id.progressbar);
        facebook_id_link = findViewById(R.id.facebook_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        event_tv.setText("Completed events: "+ counter);

        if (counter==4){
            event_tv.setText(String.valueOf(counter));
        }

        select_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImagePicker.with(UserInfoActivity_1.this)
                        .crop(1f,1f)
                        .compress(512)         //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(512, 512)  //Final image resolution will be less than 1080 x 1080(Optional)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                startActivityForResult(intent,1);
                                return null;
                            }
                        });


            }
        });


        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });
//        back_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//                overridePendingTransition(R.anim.no_anim,R.anim.push_right_in);
//            }
//        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==1 && resultCode==RESULT_OK){

            assert data != null;
            Uri filepath=data.getData();
            try {

                InputStream inputStream= getContentResolver().openInputStream(filepath);
                bitmap= BitmapFactory.decodeStream(inputStream);
                select_photo.setImageBitmap(bitmap);
                encodeBitmapImage();

            } catch (Exception ex){
                ex.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void encodeBitmapImage() {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,60,byteArrayOutputStream);
        byte[] bytesOfImage=byteArrayOutputStream.toByteArray();
        int lengthbmp = bytesOfImage.length;
        lengthbmp=lengthbmp/1024;
        System.out.println("image length : " + lengthbmp);

        if (lengthbmp>2048){
            //Toast.makeText(getApplicationContext(), "Image Too Large...select a smaller one", Toast.LENGTH_SHORT).show();
            Toast toast = new Toast(getApplicationContext());
            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
            toast_message.setText("Image Too Large...select a smaller one");
            toast.setView(toast_view);
            toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        } else if (lengthbmp==0){

            encodedImage="";

        }else{

            encodedImage= Base64.encodeToString(bytesOfImage, Base64.DEFAULT);
        }
    }

    private void updateUserInfo() {
        String name_father = fathers_name.getText().toString().trim();
        String name_mother = mother_name.getText().toString().trim();
        String contact_father = father_contact.getText().toString().trim();
        String contact_mother = mother_contact.getText().toString().trim();
        String address = present_address.getText().toString().trim();
        String facebook_id = facebook_id_link.getText().toString().trim();

        if (facebook_id.equals("")){
            fieldFocus(facebook_id_link,"Facebook id required!");
        } else if (address.equals("")){
            fieldFocus(present_address, "Enter your address");
        } else if (name_father.equals("")) {
            fieldFocus(fathers_name, "Enter your father name");
        } else if (name_mother.equals("") ) {
            fieldFocus(fathers_name, "Enter your mother name");
        } else if (encodedImage.equals("")){
            Toast toast = new Toast(getApplicationContext());
            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
            toast_message.setText("Please select a profile photo");
            toast.setView(toast_view);
            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.FILL_HORIZONTAL,0,110);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        } else {
            progressbar.setVisibility(View.VISIBLE);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void run() {

                    //Starting Write and Read data with URL
                    //Creating array for parameters
                    String[] field = new String[8];
                    field[0] = "father_name";
                    field[1] = "mother_name";
                    field[2] = "father_contact";
                    field[3] = "mother_contact";
                    field[4] = "contact";
                    field[5] = "photo";
                    field[6] = "address";
                    field[7] = "facebook_id";

                    //Creating array for data
                    String[] data = new String[8];
                    data[0] = name_father;
                    data[1] = name_mother;
                    data[2] = contact_father;
                    data[3] = contact_mother;
                    data[4] = contact;
                    data[5] = encodedImage;
                    data[6] = address;
                    data[7] = facebook_id;

                    PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/updateUserProfile_1.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult().trim();
                            if (result.equals("success")) {

                                Toast toast = new Toast(UserInfoActivity_1.this);
                                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout,findViewById(R.id.custom_toast));
                                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                                toast_message.setText("Profile Updated");
                                toast.setView(toast_view);
                                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.show();

                                progressbar.setVisibility(View.GONE);
                                Intent intent = new Intent(getApplicationContext(), NewUsersEventActivity.class);
                                intent.putExtra("ev_id", ev_id);
                                intent.putExtra("ev_name", ev_name);
                                intent.putExtra("ev_address", ev_address);
                                intent.putExtra("start_date", start_date);
                                intent.putExtra("ev_details",ev_details);
                                intent.putExtra("end_date", end_date);
                                intent.putExtra("cover_pic", cover_pic);
                                intent.putExtra("counter", counter);
                                startActivity(intent);
                                overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
                                finish();

                            } else {
                                Log.i("PutData", result);
                                Toast toast = new Toast(getApplicationContext());
                                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
                                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                                toast_message.setText("Failed to update data");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister broadcast receiver when activity is destroyed
        unregisterReceiver(networkChangeListener);
    }

    public boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}