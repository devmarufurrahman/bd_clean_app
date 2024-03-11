package com.winkytech.bdclean;

import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    ProgressBar progressBar;
    ListView chat_list_view;
    ChatList chat_list_class;
    ChatListAdapter chatListAdapter;
    ArrayList<ChatList> chatLists = new ArrayList<>();
    EditText chat_edit;
    Button send_btn, reload_btn;
    String user_id, team_ref, user_name, designation;
    TextView toast_message;
//
//    String photoUrl = "https://bdclean.winkytech.com/resources/user/profile_pic/";

    private Handler handler;
    private Runnable runnable;
    int delay = 10000, response_length = 0;
    NetworkChangeListener networkChangeListener;

    Uri photo_uri;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
//    ArrayList<String> user_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        toolbar = findViewById(R.id.custom_toolbar);
        progressBar = findViewById(R.id.progressbar);
        chat_list_view = findViewById(R.id.chat_list_view);
        chat_edit = findViewById(R.id.chat_edit);
        send_btn = findViewById(R.id.send_msg);
        reload_btn = findViewById(R.id.reload_btn);
        progressBar.setVisibility(View.GONE);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = shared.getString("user_id", "");
        team_ref = shared.getString("team_ref", "0");
        user_name = shared.getString("name", "0");
        designation = shared.getString("designation", "0");

        reference = database.getReference("messages/team_ref/"+team_ref);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChatData();
            }
        });

        handler = new Handler();
//        startGettingChatData();

        getChatData();
        chatListAdapter = new ChatListAdapter(getApplicationContext(), chatLists, user_id);
        chat_list_view.setAdapter(chatListAdapter);


        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChatProfile();
            }
        });
    }

//    private void startGettingChatData() {
//        runnable = new Runnable() {
//            public void run() {
//                progressBar.setVisibility(View.GONE);
//                getChatData();
//                handler.postDelayed(runnable, delay);
//            }
//        };
//
//        handler.postDelayed(runnable, delay);
//    }

    private void stopGettingChatData() {
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopGettingChatData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopGettingChatData();
    }

    private void saveChatProfile() {

        String body = chat_edit.getText().toString().trim();

        if (body.equals("")) {
            Toast.makeText(this, "Please type some message", Toast.LENGTH_SHORT).show();
        } else {

            String uniqueId = reference.push().getKey();

            MessageModel message = new MessageModel();
            message.setUser_name(user_name);
            message.setMessage(body);
            message.setDate_time("2024-01-27 12:34:56");
            message.setId(user_id);
            message.setDesignation(designation);

            // Use the generated unique ID to push the message
            DatabaseReference uniqueMessageReference = reference.child(uniqueId);
            uniqueMessageReference.setValue(message);

//            progressBar.setVisibility(View.VISIBLE);
//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.post(new Runnable() {
//                @SuppressLint("ResourceAsColor")
//                @Override
//                public void run() {
//
//
//                    String[] field = new String[3];
//                    field[0] = "user_id";
//                    field[1] = "body";
//                    field[2] = "team_ref";
//
//                    //Creating array for data
//                    String[] data = new String[3];
//                    data[0] = user_id;
//                    data[1] = body;
//                    data[2] = team_ref;
//
//                    PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/saveChatData.php", "POST", field, data);
//                    if (putData.startPut()) {
//                        if (putData.onComplete()) {
//                            String result = putData.getResult().trim();
//                            if (result.equals("success")) {
//
//                                progressBar.setVisibility(View.GONE);
//                                //saveInFirebase(chatTeamRef, body);
//                                getChatData();
//                                chat_edit.setText("");
//
//                            } else {
//                                progressBar.setVisibility(View.GONE);
//                                Log.i("PutData", result);
//                                //Toast.makeText(MessageActivity.this, "Failed To send message!!!", Toast.LENGTH_SHORT).show();
//                                Toast toast = new Toast(getApplicationContext());
//                                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
//                                toast_message = toast_view.findViewById(R.id.custom_toast_tv);
//                                toast_message.setText("Failed To send message!!!");
//                                toast.setView(toast_view);
//                                toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 110);
//                                toast.setDuration(Toast.LENGTH_SHORT);
//                                toast.show();
//                                progressBar.setVisibility(View.GONE);
//
//                            }
//                        }
//                    }
//                    //End Write and Read data with URL
//                }
//            });
        }
    }

    private void getChatData() {

//        DatabaseReference teamReferenceRef = reference.child(teamReference);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Loop through the messages
                chatLists.clear();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    MessageModel message = messageSnapshot.getValue(MessageModel.class);
                    // Do something with the message

                    String name = message.getUser_name();
                    String message_body = message.getMessage();
                    String date = message.getDate_time();
                    String id = message.getId();
                    String designation = message.getDesignation();

                    String fileName = id+".jpg";
                    //String photoUrl ;

                    // Get a reference to the storage root directory
                    //FirebaseStorage storage = FirebaseStorage.getInstance();
                    //StorageReference storageRef = storage.getReference();

                    // Create a reference to the image file
                    //StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("user_photos/"+fileName);
                    StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("/user_photos/"+fileName);
                    Log.d("Image_reference", String.valueOf(imageRef));

                    // Get the download URL for the image

                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            photo_uri = uri;
                            Log.d("User photo", String.valueOf(photo_uri));

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            e.printStackTrace();

                        }
                    });

                    chat_list_class = new ChatList( id, message_body, name, date, photo_uri, designation);
                    chatLists.add(chat_list_class);
                }

                chatListAdapter.notifyDataSetChanged();
                chat_list_view.setSelection(chatListAdapter.getCount() - 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });

//        progressBar.setVisibility(View.VISIBLE);
//        String url = "https://bdclean.winkytech.com/backend/api/getChatData.php?team_ref=" + team_ref;
//        StringRequest request = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        chatLists.clear();
//                        Log.d("chat data = ", response);
//                        System.out.println("response_length = " + response.length());
//                        int i;
//                        try {
//                            JSONArray jsonArray = new JSONArray(response);
//                            for (i = 0; i < jsonArray.length(); i++) {
//                                JSONObject object = jsonArray.getJSONObject(i);
//                                String id = object.getString("send_by");
//                                String body = object.getString("message");
//                                String receiver = object.getString("name");
//                                String designation = object.getString("designation");
//                                String create_date = object.getString("create_date");
////                                String photo = photoUrl +(object.getString("profile_photo"));
//                                String photo = object.getString("profile_photo");
//
//                                chat_list_class = new ChatList(id, body, receiver, create_date, photo, designation);
//                                chatLists.add(chat_list_class);
//                                chatListAdapter.notifyDataSetChanged();
//                                progressBar.setVisibility(View.GONE);
//                            }
//
//                        } catch (JSONException e) {
//                            progressBar.setVisibility(View.GONE);
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast toast = new Toast(getApplicationContext());
//                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
//                toast_message = toast_view.findViewById(R.id.custom_toast_tv);
//                toast_message.setText("Failed to get chat List");
//                toast.setView(toast_view);
//                toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 110);
//                toast.setDuration(Toast.LENGTH_SHORT);
//                toast.show();
//                progressBar.setVisibility(View.GONE);
//            }
//        });
//
//        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        requestQueue.add(request);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Unregister broadcast receiver when activity is destroyed
//        unregisterReceiver(networkChangeListener);
//    }

    static class MessageModel {

        private String id;
        private String designation;
        private String user_name;
        private String message;
        private String date_time;

        public MessageModel() {

        }

        public MessageModel(String id, String designation ,String user_name, String message, String date_time) {
            this.user_name = user_name;
            this.message = message;
            this.date_time = date_time;
            this.id = id;
            this.designation = designation;
        }

        // Getters and setters


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDate_time() {
            return date_time;
        }

        public void setDate_time(String date_time) {
            this.date_time = date_time;
        }
    }

}

