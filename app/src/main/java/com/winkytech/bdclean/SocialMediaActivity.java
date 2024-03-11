package com.winkytech.bdclean;

import static com.winkytech.bdclean.NewEventActivity.MyPREFERENCES;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

@SuppressLint({"ResourceAsColor", "SetTextI18n"})
public class SocialMediaActivity extends AppCompatActivity  {

    Toolbar toolbar;
    Button save_btn, load_more;
    ProgressBar progressBar;
    String user_id;
    TextView toast_message, post_tv;
    ListView post_list_view;
    PostList post_list_class;
    PostListAdapter postListAdapter;
    ArrayList<PostList> postLists = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    int limit=10, offset=0;

    String photoUrl = "https://bdclean.winkytech.com/resources/post_image/", fb_link;

    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = shared.getString("user_id", "");
        fb_link = shared.getString("fb_link", "www.facebook.com");
        System.out.println("fb_ink = " + fb_link);

        toolbar=findViewById(R.id.custom_toolbar);
        save_btn = findViewById(R.id.save_btn);
        post_list_view = findViewById(R.id.social_list);
        progressBar=findViewById(R.id.progressbar);
        post_tv = findViewById(R.id.post_tv);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        load_more = findViewById(R.id.load_more);
        progressBar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getOpenFacebookIntent()!=null){
                    startActivity(getOpenFacebookIntent());
                } else {
                    // Facebook app not found, show warning message

                    AlertDialog.Builder builder = new AlertDialog.Builder(SocialMediaActivity.this);
                    builder.setTitle("WARNING")
                            .setMessage("Facebook app not installed. please install facebook app and try again");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPostLinks(limit, offset);
            }
        });


        load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offset = offset+10;

                getPostLinks(limit, offset);
            }
        });


        getPostLinks(limit, offset);
        postListAdapter = new PostListAdapter(SocialMediaActivity.this,postLists);
        post_list_view.setAdapter(postListAdapter);

        post_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String link = postLists.get(position).getLink();

//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(link));
//                intent.setPackage("com.android.chrome"); // Specify the package name of Chrome
//
//                try {
//                    startActivity(intent);
//                } catch (ActivityNotFoundException e) {
//                    // If Chrome is not installed, open the default browser
//                    intent.setPackage(null);
//                    startActivity(intent);
//                }

                Dialog dialog = new Dialog(SocialMediaActivity.this);
                dialog.setContentView(R.layout.post_view_dialog_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.setCancelable(false);
                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                WebView post_view = dialog.findViewById(R.id.post_view);
                TextView close_btn = dialog.findViewById(R.id.close_btn);
                ProgressBar webProgress = dialog.findViewById(R.id.progressBar_web);
                webProgress.setVisibility(View.GONE);

                post_view.loadUrl(link);
                WebSettings settings= post_view.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setUseWideViewPort(true);
                settings.setLoadWithOverviewMode(true);
                post_view.setWebViewClient(new WebViewClient());

                post_view.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        super.onReceivedError(view, request, error);
                        // Handle the error here
                        post_view.loadUrl("https://www.facebook.com/"); // Load default URL
                        Log.d("POST VIEW ERROR",error.toString());
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        // This method is called when a new page starts loading
                        webProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        // This method is called when the page finishes loading
                        webProgress.setVisibility(View.GONE);
                    }
                });

                post_view.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);

                        if (newProgress < 100) {
                            webProgress.setVisibility(View.VISIBLE);
                            webProgress.setProgress(newProgress);
                        } else {
                            webProgress.setVisibility(View.GONE);
                        }
                    }
                });

                close_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });

    }

    private Intent getOpenFacebookIntent() {
        try {
            PackageManager packageManager = getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage("com.facebook.katana");

            if (intent != null) {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(fb_link));
                startActivityForResult(intent, 0);
                return intent;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void displayFacebookDialog() {
        Dialog dialog = new Dialog(SocialMediaActivity.this);
        dialog.setContentView(R.layout.install_facebook_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        Button save = dialog.findViewById(R.id.post_btn);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            displayLinkDialog();
        }
    }

    private void getPostLinks(int limit, int offset) {
        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getPostList.php?user_id="+user_id+"&limit="+limit+"&offset="+offset;
        @SuppressLint("SetTextI18n") StringRequest request = new StringRequest(Request.Method.GET, url,
            response -> {
                progressBar.setVisibility(View.GONE);
//                postLists.clear();
                System.out.println("Response = " + response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i=0; i< jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);

                        String id = object.getString("id");
                        String link = object.getString("post_url");
                        String date = object.getString("create_date");
                        String photo = photoUrl + (object.getString("photo"));
                        String caption = object.getString("caption");
                        String approve_status = object.getString("approve_status");
                        String comment = object.getString("comment");
                        String status = "", remain_time;
                        switch (approve_status){
                            case "0" :
                                status = "PENDING APPROVAL";
                                break;
                            case "1" :
                                status = "APPROVED";
                                break;
                            case "2" :
                                status = "REJECTED";
                                break;
                        }

                        Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
                        String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                        String final_date = simpleDateFormat.format(date1);

                        // Replace with your start date
                        LocalDateTime now = LocalDateTime.now();
                        // Parse the given date string
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime createDate = LocalDateTime.parse(date, formatter);

                        // Calculate the time difference
                        long minutesDiff = ChronoUnit.MINUTES.between(createDate, now);
                        long hoursDiff = ChronoUnit.HOURS.between(createDate, now);
                        long daysDiff = ChronoUnit.DAYS.between(createDate, now);

                        // Print the time difference

                        if (daysDiff == 0){
                            remain_time = hoursDiff + "  hours ago";
                        } else if (hoursDiff == 0){
                            remain_time =minutesDiff + " minutes ago";
                        } else {
                            remain_time =daysDiff + " days ago";
                        }
                        int post = postListAdapter.getCount() + 1;

                        post_tv.setText("My Facebook Posts ("+ post +")");
                        post_list_class = new PostList(id,link,final_date, photo, caption, status, remain_time, comment);
                        postLists.add(post_list_class);
                        postListAdapter.notifyDataSetChanged();

                    }
                } catch (JSONException e){
                    progressBar.setVisibility(View.GONE);
                    post_tv.setText("No Post Found");
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }, error -> {
                Log.d("Volley Error", error.getMessage());
                progressBar.setVisibility(View.GONE);
                post_tv.setText("FAILED TO GET POST LIST");

            });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

        // Signal SwipeRefreshLayout to start the progress indicator.
        swipeRefreshLayout.setRefreshing(false);
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

    private void displayLinkDialog() {
        Dialog dialog = new Dialog(SocialMediaActivity.this);
        dialog.setContentView(R.layout.post_link_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.show();

        EditText post_link = dialog.findViewById(R.id.post_link);
        Button save = dialog.findViewById(R.id.post_btn);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = post_link.getText().toString().trim();
                if (link.equals("")){
                    Toast toast = new Toast(getApplicationContext());
                    View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                    toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                    toast_message.setText("PLEASE INSERT A VALID LINK");
                    toast.setView(toast_view);
                    toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    updatePost(link);
                }
            }
        });
    }

    private void updatePost(String link) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = new String[2];
                field[0] = "link";
                field[1] = "user_id";

                //Creating array for data
                String[] data = new String[2];
                data[0] = link;
                data[1] = user_id;

                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/updateSocialPost.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();
                        if (result.equals("success")){
                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout,findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("POST SAVED");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                            getPostLinks(limit,offset);
                        }else {
                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("FAILED. PLEASE TRY AGAIN");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
                //End Write and Read data with URL
            }
        });
    }

    private class PostList{
        String id, link, date, photo, caption , status, remain_time,comment;

        public PostList(String id, String link, String date, String photo, String caption, String status, String remain_time, String comment) {
            this.id = id;
            this.link = link;
            this.date = date;
            this.photo = photo;
            this.caption = caption;
            this.status = status;
            this.remain_time = remain_time;
            this.comment = comment;
        }

        public String getRemain_time() {
            return remain_time;
        }

        public void setRemain_time(String remain_time) {
            this.remain_time = remain_time;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    private class PostListAdapter extends BaseAdapter{

        Context context;
        ArrayList<PostList> postLists;

        public PostListAdapter(Context context, ArrayList<PostList> postLists) {
            this.context = context;
            this.postLists = postLists;
        }

        @Override
        public int getCount() {
            return postLists.size();
        }

        @Override
        public Object getItem(int position) {
            return postLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return postLists.indexOf(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_layout,null,true);

            String mStatus = postLists.get(position).getStatus().trim();

            TextView caption = view.findViewById(R.id.post_caption);
            TextView date = view.findViewById(R.id.post_date);
            TextView status = view.findViewById(R.id.post_status);
            TextView comment = view.findViewById(R.id.comment);

            caption.setText("FACEBOOK POST");
            date.setText(postLists.get(position).getDate() + " , " + postLists.get(position).getRemain_time());
            status.setText(postLists.get(position).getStatus());

            String post_comment = postLists.get(position).getComment().trim();
            if (post_comment.equals("") || post_comment.equals("null")){
                comment.setVisibility(View.GONE);
            } else {
                comment.setText("Comment : "+postLists.get(position).getComment());
            }

            switch (mStatus){
                case "PENDING APPROVAL" :
                    status.setTextColor(ColorStateList.valueOf(Color.parseColor("#ff5252")));
                    break;
                case "APPROVED" :
                    status.setTextColor(ColorStateList.valueOf(Color.parseColor("#009748")));

                    break;
                case "REJECTED" :
                    status.setTextColor(ColorStateList.valueOf(Color.parseColor("#C63235")));

                    break;
            }

            return view;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister broadcast receiver when activity is destroyed
        unregisterReceiver(networkChangeListener);
    }

}