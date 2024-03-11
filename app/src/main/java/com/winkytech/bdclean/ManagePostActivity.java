package com.winkytech.bdclean;

import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ManagePostActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView post_list_view;
    String user_id;
    PostList post_list_class;
    PostListAdapter postListAdapter;
    TextView toast_message, record_status;
    ArrayList<PostList> postLists = new ArrayList<>();
    String photoUrl = "https://bdclean.winkytech.com/resources/post_image/";
    ProgressBar progressbar;
    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_post);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = shared.getString("user_id","");

        toolbar = findViewById(R.id.custom_toolbar);
        record_status = findViewById(R.id.record_status);
        progressbar = findViewById(R.id.progressbar);
        progressbar.setVisibility(View.GONE);
        record_status.setVisibility(View.GONE);
        post_list_view = findViewById(R.id.post_list);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getPostList();
        postListAdapter = new PostListAdapter(getApplicationContext(),postLists);
        post_list_view.setAdapter(postListAdapter);

        post_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String link = postLists.get(position).getLink();
                String post_id = postLists.get(position).getId();
                String status = postLists.get(position).getStatus().trim();
//                Toast.makeText(ManagePostActivity.this, status, Toast.LENGTH_SHORT).show();

                Dialog dialog = new Dialog(ManagePostActivity.this);
                dialog.setContentView(R.layout.post_approve_dialog_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);
                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();
                WebView post_view = dialog.findViewById(R.id.post_view);
                Button statusBtn = dialog.findViewById(R.id.statusBtn);
                Button approve = dialog.findViewById(R.id.approve_btn);
                Button reject = dialog.findViewById(R.id.reject_btn);
                TextView close_btn = dialog.findViewById(R.id.close_btn);
//                ProgressBar webProgress = dialog.findViewById(R.id.progressBar_web);
//                webProgress.setVisibility(View.GONE);

                switch (status){
                    case "STATUS : PENDING APPROVAL":
                        approve.setEnabled(true);
                        reject.setEnabled(true);
                        break;

                    default:
                        approve.setEnabled(false);
                        approve.setVisibility(View.GONE);
                        reject.setEnabled(false);
                        reject.setVisibility(View.GONE);
                        statusBtn.setVisibility(View.VISIBLE);
                        statusBtn.setText(status);
                        approve.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                        reject.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                        approve.setTextColor(getResources().getColor(R.color.black));
                        reject.setTextColor(getResources().getColor(R.color.black));
                        break;
                }

                post_view.loadUrl(link);
                WebSettings settings= post_view.getSettings();
                settings.setJavaScriptEnabled(true);
                post_view.setWebViewClient(new WebViewClient());
                settings.setUseWideViewPort(true);
                settings.setLoadWithOverviewMode(true);

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
//                        webProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        // This method is called when the page finishes loading
//                        webProgress.setVisibility(View.GONE);
                    }
                });

                approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String status = "1";
                        dialog.dismiss();
                        updatePostStatus(post_id, status, "");
                    }
                });

                reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String status = "2";
                        dialog.dismiss();
                        displayCommentDialog(status, post_id);
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

    private void displayCommentDialog(String status, String post_id) {
        Dialog dialog = new Dialog(ManagePostActivity.this);
        dialog.setContentView(R.layout.comment_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.show();

        EditText comment_et = dialog.findViewById(R.id.comment_et);
        Button submit_btn = dialog.findViewById(R.id.submit_btn);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment = comment_et.getText().toString().trim();
                updatePostStatus(post_id, status, comment);
                dialog.dismiss();

            }
        });

    }

    private void updatePostStatus(String post_id, String status, String comment) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {
                String[] field = new String[4];
                field[0] = "post_id";
                field[1] = "user_id";
                field[2] = "status";
                field[3] = "comment";

                //Creating array for data
                String[] data = new String[4];
                data[0] = post_id;
                data[1] = user_id;
                data[2] = status;
                data[3] = comment;

                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/updatePostStatus.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();
                        if (result.equals("updated")){
                            getPostList();
                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_success_layout,findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Post reviewed");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Failed to review post. Please try again");
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

    private void getPostList() {
        progressbar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getManagePostList.php?user_id="+user_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        postLists.clear();
                        System.out.println("response = " + response);

                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("id");
                                String link = object.getString("post_url");
                                String caption = object.getString("caption");
                                String date = object.getString("create_date");
                                String name = object.getString("member");
                                String status_ref = object.getString("approve_status");
                                String photo = photoUrl + (object.getString("photo"));
                                String status = "";

                                @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);

                                String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String post_date = simpleDateFormat.format(date1);

                                switch (status_ref){
                                    case "0":
                                        status = "PENDING APPROVAL";
                                        break;

                                    case "1":
                                        status = "APPROVED";
                                        break;

                                    case "2":
                                        status = "REJECTED";
                                        break;

                                }

                                progressbar.setVisibility(View.GONE);
                                post_list_class=new PostList(id, link, caption, name, post_date, photo,status);
                                postLists.add(post_list_class);
                                postListAdapter.notifyDataSetChanged();

                            }

                        } catch (JSONException e){
                            record_status.setVisibility(View.VISIBLE);
                            record_status.setText("No Record Found");
                            progressbar.setVisibility(View.GONE);
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                record_status.setVisibility(View.VISIBLE);
                record_status.setText("No Record Found");
                progressbar.setVisibility(View.GONE);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    private class PostList{
        String id, link, caption, member_name, date, photo, status;

        public PostList(String id, String link, String caption, String member_name, String date, String photo, String status) {
            this.id = id;
            this.link = link;
            this.caption = caption;
            this.member_name = member_name;
            this.date = date;
            this.photo = photo;
            this.status = status;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
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

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public String getMember_name() {
            return member_name;
        }

        public void setMember_name(String member_name) {
            this.member_name = member_name;
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_post_list_layout,null,true);

            TextView member_name = view.findViewById(R.id.member_name);
            TextView date = view.findViewById(R.id.post_date);
            TextView status = view.findViewById(R.id.status);

            member_name.setText(postLists.get(position).getMember_name());
            date.setText(postLists.get(position).getDate());
            status.setText(postLists.get(position).getStatus());

            return view;
        }
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister broadcast receiver when activity is destroyed
        unregisterReceiver(networkChangeListener);
    }
}