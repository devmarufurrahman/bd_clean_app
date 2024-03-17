package com.winkytech.bdclean;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Contact = "contactKey";
    public static final String UserType = "userType";
    NetworkChangeListener networkChangeListener;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String user_data = (shared.getString(Contact, ""));
        String designation = (shared.getString("designation", ""));
        String user_type = (shared.getString("user_type", ""));
        System.out.println(user_data);
        System.out.println("USER TYPE = "+designation);

        VideoView videoView = findViewById(R.id.videoView);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.splash_animation;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.setVolume(0f, 0f); // Mute the audio
        mediaPlayer.setLooping(false); // Loop the video

        videoView.start();

//        printHashKey(context);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!user_data.equals("")){

                    if (user_type.equals("user") || designation.equals("User")){
                        Intent intent = new Intent(getApplicationContext(),AboutBDCleanActivity.class);
                        intent.putExtra("contact",user_data);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
                    } else {
                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                        intent.putExtra("contact",user_data);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
                    }

                } else {
                    Intent intent1 = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent1);
                    finish();
                    overridePendingTransition(R.anim.push_left_in,R.anim.no_anim);
                }
            }
        },6500);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister broadcast receiver when activity is destroyed
        unregisterReceiver(networkChangeListener);

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

//    private void printHashKey(Context context) {
//        try {
//            PackageInfo info = context.getPackageManager().getPackageInfo(
//                    context.getPackageName(),
//                    PackageManager.GET_SIGNATURES
//            );
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String hashKey = new String(Base64.encode(md.digest(), 0));
//                Log.i("AppLog", "key:" + hashKey + "=");
//                System.out.println("hashkey = " + hashKey+"=");
//            }
//        } catch (Exception e) {
//            Log.e("AppLog", "error:", e);
//        }
//    }
}