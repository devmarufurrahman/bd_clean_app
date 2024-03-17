package com.winkytech.bdclean;

import static com.winkytech.bdclean.HomeActivity.Contact;
import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyForegroundService extends Service {

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 20000;
    RequestQueue requestQueue;
    String user_id  ="", last_notice_key;
    int  user_position;
    DatabaseReference notification_reference, notice_reference_all_member, notice_reference_designated_member;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String last_notice_pref = "last_notice_ky";
    SharedPreferences sharedpreferences;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        if (intent != null && intent.hasExtra("user_id")) {
//            user_id = intent.getStringExtra("user_id");
//
//
//        } else {
//
//            Toast.makeText(this, "INTENT NOT FOUND", Toast.LENGTH_SHORT).show();
//        }

        sharedpreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = shared.getString("user_id","");
        user_position = Integer.parseInt(shared.getString("user_position","0"));
        last_notice_key= shared.getString(last_notice_pref,"");
        Log.d("org_leveL_pos in foreground:", String.valueOf(user_position));
        Log.d("last notice key :", last_notice_key);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        notification_reference = database.getReference("notification/reciever/user_ref/"+user_id);
        notice_reference_all_member = database.getReference("notice/receiver_type/0");
        notice_reference_designated_member = database.getReference("notice/receiver_type/"+user_position);
        Query query_1 = notice_reference_all_member.limitToLast(1);
        Query query_2 = notice_reference_designated_member.limitToLast(1);

        notification_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Handle new data, trigger notification, etc.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    NotificationData notificationModel = snapshot.getValue(NotificationData.class);
                    String sender_name = notificationModel.getSender_name();
                    String message = notificationModel.getMessage();
                    String sender_designation = notificationModel.getDesignation();

                    Intent intent = new Intent(getApplicationContext(), Messenger.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 3, intent, PendingIntent.FLAG_IMMUTABLE);
                    NotificationChannel channel = new NotificationChannel("act_notification_realtime", "act_notification_realtime", NotificationManager.IMPORTANCE_HIGH);
                    NotificationManager manager = getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "act_notification_realtime");
                    builder.setContentTitle("Notification From  "+sender_name+ " ( "+sender_designation+" ) ");
                    builder.setContentText(message);
                    builder.setSmallIcon(R.drawable.bd_clean_logo);
                    builder.setContentIntent(pendingIntent);
                    builder.setAutoCancel(true);
                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
                    managerCompat.notify(3, builder.build());

                    // Display the notification using notification manager
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

        query_1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NoticeModelForeground noticeModel = dataSnapshot.getValue(NoticeModelForeground.class);
                    if (noticeModel != null) {

                        String uniqueKey = dataSnapshot.getKey();

                        String message = noticeModel.getMessage();
                        String sender_name = noticeModel.getSenderName();
                        String sender_designation = noticeModel.getSenderDesignation();
                        String subject = noticeModel.getSubject();
                        String date = noticeModel.getDate();

                        if (!uniqueKey.equals(last_notice_key)){

                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(last_notice_pref, uniqueKey);
                            editor.apply();
                            displayDialog(message, sender_name, sender_designation, subject, date);
                        }

                        // Your notification building logic goes here
//                            Intent intent = new Intent(getApplicationContext(), Messenger.class);
//                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 3, intent, PendingIntent.FLAG_IMMUTABLE);
//                            NotificationChannel channel = new NotificationChannel("notice", "notice", NotificationManager.IMPORTANCE_HIGH);
//                            NotificationManager manager = getSystemService(NotificationManager.class);
//                            manager.createNotificationChannel(channel);
//                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "notice");
//                            builder.setContentTitle("Notice From  "+sender_name+ " ( "+sender_designation+" ) ");
//                            builder.setContentText(message);
//                            builder.setSmallIcon(R.drawable.bd_clean_logo);
//                            builder.setContentIntent(pendingIntent);
//                            builder.setAutoCancel(true);
//                            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
//                            managerCompat.notify(4, builder.build());

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        query_2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NoticeModelForeground noticeModel = dataSnapshot.getValue(NoticeModelForeground.class);
                    if (noticeModel != null) {

                        String uniqueKey = dataSnapshot.getKey();
                        String message = noticeModel.getMessage();
                        String sender_name = noticeModel.getSenderName();
                        String sender_designation = noticeModel.getSenderDesignation();
                        String subject = noticeModel.getSubject();
                        String date = noticeModel.getDate();

                        if (!uniqueKey.equals(last_notice_key)){

                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(last_notice_pref, uniqueKey);
                            editor.apply();
                            displayDialog(message, sender_name, sender_designation, subject, date);
                        }

                        // Your notification building logic goes here
//                            Intent intent = new Intent(getApplicationContext(), Messenger.class);
//                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 3, intent, PendingIntent.FLAG_IMMUTABLE);
//                            NotificationChannel channel = new NotificationChannel("notice_2", "notice_2", NotificationManager.IMPORTANCE_HIGH);
//                            NotificationManager manager = getSystemService(NotificationManager.class);
//                            manager.createNotificationChannel(channel);
//                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "notice_2");
//                            builder.setContentTitle("Notice From  "+sender_name+ " ( "+sender_designation+" ) ");
//                            builder.setContentText(message);
//                            builder.setSmallIcon(R.drawable.bd_clean_logo);
//                            builder.setContentIntent(pendingIntent);
//                            builder.setAutoCancel(true);
//                            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
//                            managerCompat.notify(5, builder.build());

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });


        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true){
                            Log.d("TAG", "Foreground Service is running...");
                            try {
                                Thread.sleep(60000);
                            }
                            catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).start();

        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                getNotificationData();
                Log.d("USER ID", user_id);
                handler.postDelayed(runnable, delay);
            }
        }, delay);

        final String CHANNEL_ID= "Foreground Service";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this,CHANNEL_ID)
                .setContentText("Foreground Service App")
                .setContentTitle("APP IS RUNNING");

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            startForeground(1001, notification.build());
        } else {
            startForeground(1001, notification.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void displayDialog(String message, String sender_name, String sender_designation, String subject, String date) {

        Intent intent = new Intent(getApplicationContext(), NoticeDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("message", message);
        intent.putExtra("sender_name", sender_name);
        intent.putExtra("sender_designation", sender_designation);
        intent.putExtra("subject", subject);
        intent.putExtra("date", date);
        startActivity(intent);

//        Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
//        intent.putExtra("message", message);
//        intent.putExtra("sender_name", sender_name);
//        intent.putExtra("sender_designation", sender_designation);
//        intent.putExtra("subject", subject);
//        intent.putExtra("date", date);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 2, intent, PendingIntent.FLAG_IMMUTABLE);

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void getNotificationData() {

        String url= "https://bdclean.winkytech.com/backend/api/getNotificationCount.php?user_ref="+user_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Notification_counter = " + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject object = jsonArray.getJSONObject(0);
                            int notification_count = Integer.parseInt(object.getString("notifi_count"));
                            int read_count = Integer.parseInt(object.getString("read_count"));
                            int activity_type = 0;

                            if (!object.getString("activity_type_ref").equals("null")){

                                activity_type = Integer.parseInt(object.getString("activity_type_ref"));

                            }

                            Intent  notification_intent = new Intent(HomeActivity.NotificationUpdateReceiver.ACTION_UPDATE_NOTIFICATION_COUNT);
                                notification_intent.putExtra("read_count", read_count);

                                notification_intent.putExtra("activity_type", activity_type);
                                sendBroadcast(notification_intent);


                            if (notification_count !=0){

                                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 2, intent, PendingIntent.FLAG_IMMUTABLE);
                                NotificationChannel channel = new NotificationChannel("act_notification", "act_notification", NotificationManager.IMPORTANCE_HIGH);
                                NotificationManager manager = getSystemService(NotificationManager.class);
                                manager.createNotificationChannel(channel);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "act_notification");
                                builder.setContentTitle("New Notification");
                                builder.setContentText("Tap to see what's new");
                                builder.setSmallIcon(R.drawable.bd_clean_logo);
                                builder.setContentIntent(pendingIntent);
                                builder.setAutoCancel(true);
                                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
                                managerCompat.notify(2, builder.build());

//                                Dialog dialog = new Dialog(HomeActivity.this);
//                                dialog.setContentView(R.layout.event_warning_dialog_layout);
//                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                dialog.setCancelable(true);
//                                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
//                                Button close_btn = dialog.findViewById(R.id.close_btn);
//                                TextView dialog_tv = dialog.findViewById(R.id.dialog_tv);
//
//                                dialog.show();

                                updateNotificationCount();
                            }

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Notification error", "Failed to get Notification");

            }
        });

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    private void updateNotificationCount() {
        // Your existing code for updating notification count
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {
                String[] field = new String[1];
                field[0] = "user_id";
                //Creating array for data
                String[] data = new String[1];
                data[0] = String.valueOf(user_id);
                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/updateNotificationFlag.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();
                        if (result.equals("updated")) {
                            System.out.println("NOTIFICATION FLAG = UPDATED" );
                        } else {
                            Log.i("PutData", result);

                        }
                    }
                }
            }
        });
    }

    static class NotificationData {

        String sender_id, sender_name, message, date, designation;

        public NotificationData(){
        }

        public NotificationData(String sender_id, String sender_name, String message, String date, String designation) {
            this.sender_id = sender_id;
            this.sender_name = sender_name;
            this.message = message;
            this.date = date;
            this.designation = designation;
        }

        public String getSender_id() {
            return sender_id;
        }

        public void setSender_id(String sender_id) {
            this.sender_id = sender_id;
        }

        public String getSender_name() {
            return sender_name;
        }

        public void setSender_name(String sender_name) {
            this.sender_name = sender_name;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }
    }

    static class NoticeModelForeground{

        String noticeID, subject, message, senderName, senderDesignation, date;

        public NoticeModelForeground() {
        }

        public NoticeModelForeground(String noticeID, String subject, String message, String senderName, String senderDesignation, String date) {
            this.noticeID = noticeID;
            this.subject = subject;
            this.message = message;
            this.senderName = senderName;
            this.senderDesignation = senderDesignation;
            this.date = date;
        }

        public String getNoticeID() {
            return noticeID;
        }

        public void setNoticeID(String noticeID) {
            this.noticeID = noticeID;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public String getSenderDesignation() {
            return senderDesignation;
        }

        public void setSenderDesignation(String senderDesignation) {
            this.senderDesignation = senderDesignation;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

}
