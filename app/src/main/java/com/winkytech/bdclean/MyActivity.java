package com.winkytech.bdclean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.List;

public class MyActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView event_list_view,post_list_view;
    Button joined_event, shared_post, load_more;
    CompletedEventAdapter completedEventAdapter;
    ArrayList<CompletedEventList> completedEventLists = new ArrayList<>();
    CompletedEventList completed_event_list_class;
    String photoUrl = "https://bdclean.winkytech.com/resources/event/";

    PostList post_list_class;
    PostListAdapter postListAdapter;
    ArrayList<PostList> postLists = new ArrayList<>();
    TextView toast_message, activity_tv;
    ProgressBar progressbar;

    public static final String MyPREFERENCES = "MyPrefs" ;
    String user_id, post;
    int org_level_pos, upazila_ref,  limit=15, offset=0;

    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = shared.getString("user_id","");
        org_level_pos = Integer.parseInt(shared.getString("org_level_ref",""));
        upazila_ref = Integer.parseInt(shared.getString("upazila_ref",""));

        toolbar = findViewById(R.id.custom_toolbar);
        event_list_view = findViewById(R.id.event_list_view);
        post_list_view = findViewById(R.id.post_list_view);
        joined_event = findViewById(R.id.joined_event);
        shared_post = findViewById(R.id.post_shared);
        activity_tv = findViewById(R.id.activity_tv);
        progressbar = findViewById(R.id.progressbar);
        load_more = findViewById(R.id.load_more);
        activity_tv.setVisibility(View.GONE);
        progressbar.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getCompletedEvent(limit,offset);
        completedEventAdapter = new CompletedEventAdapter(getApplicationContext(),completedEventLists);
        event_list_view.setAdapter(completedEventAdapter);
        post_list_view.setVisibility(View.GONE);
        joined_event.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0D235")));
        joined_event.setTextColor(getResources().getColor(R.color.black));
        shared_post.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
        shared_post.setTextColor(getResources().getColor(R.color.black));

        joined_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCompletedEvent(limit,offset);
                event_list_view.setVisibility(View.VISIBLE);
                completedEventAdapter = new CompletedEventAdapter(getApplicationContext(),completedEventLists);
                event_list_view.setAdapter(completedEventAdapter);
                post_list_view.setVisibility(View.GONE);

                joined_event.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0D235")));
                joined_event.setTextColor(getResources().getColor(R.color.black));
                shared_post.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
                shared_post.setTextColor(getResources().getColor(R.color.black));
            }
        });

        shared_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPostList();
                post_list_view.setVisibility(View.VISIBLE);
                postListAdapter = new PostListAdapter(getApplicationContext(),postLists);
                post_list_view.setAdapter(postListAdapter);
                event_list_view.setVisibility(View.GONE);

                shared_post.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0D235")));
                shared_post.setTextColor(getResources().getColor(R.color.black));
                joined_event.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
                joined_event.setTextColor(getResources().getColor(R.color.black));

            }
        });

        load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offset = offset+10;

                getCompletedEvent(limit, offset);
            }
        });

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

    private static class PostList{
        String link, status, date;

        public PostList(String link, String status, String date) {
            this.link = link;
            this.status = status;
            this.date = date;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

    private void getPostList() {
        progressbar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getPostList.php?user_id="+user_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        postLists.clear();
                        String response_data = response.toString().trim();
                        if (response_data.equals("null")){
                            activity_tv.setVisibility(View.VISIBLE);
                            activity_tv.setText("No Activity Found");
                            progressbar.setVisibility(View.GONE);
                        } else {
                            try {

                                JSONArray jsonArray = new JSONArray(response);
                                for (int i =0; i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String id = object.getString("id");
                                    String name = object.getString("post_url");
                                    String approve_status = object.getString("approve_status");
                                    String date = object.getString("create_date");

                                    String status = "", remain_time = "";
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

                                    progressbar.setVisibility(View.GONE);
                                    post_list_class=new PostList(name, status, remain_time);
                                    postLists.add(post_list_class);
                                    postListAdapter.notifyDataSetChanged();

                                }

                            } catch (JSONException e){
                                e.printStackTrace();
                                activity_tv.setVisibility(View.VISIBLE);
                                activity_tv.setText("No Activity Found");
                                progressbar.setVisibility(View.GONE);
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley Error", error.getMessage());
                activity_tv.setVisibility(View.VISIBLE);
                activity_tv.setText("No Activity Found");
                progressbar.setVisibility(View.GONE);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    private void getCompletedEvent(int limit, int offset) {
        progressbar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getCompletedEventList.php?user_id="+user_id+"&limit="+limit+"&offset="+offset;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onResponse(String response) {
                        completedEventLists.clear();
                        System.out.println("COMPLETED EVENT RESPONSE = " + response);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("event_ref");
                                String name = object.getString("name");
                                String start_date = object.getString("start_date");
                                String end_date = object.getString("end_date");
                                String event_location = object.getString("event_location");
                                String photo = photoUrl + (object.getString("event_cover"));
                                String status = object.getString("approve_status");
                                String comment = object.getString("comment");

                                @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_date);
                                @SuppressLint("SimpleDateFormat") Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_date);

                                String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String from_date = simpleDateFormat.format(date1);
                                String to_date = simpleDateFormat.format(date2);

                                progressbar.setVisibility(View.GONE);
                                completed_event_list_class = new CompletedEventList(id, name,from_date, to_date, photo,status, event_location, comment);
                                completedEventLists.add(completed_event_list_class);
                                completedEventAdapter.notifyDataSetChanged();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            activity_tv.setVisibility(View.VISIBLE);
                            activity_tv.setText("No Activity Found");
                            progressbar.setVisibility(View.GONE);
                        } catch (ParseException e) {
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("Volley Error", error.getMessage());
                activity_tv.setVisibility(View.VISIBLE);
                activity_tv.setText("No Activity Found");
                progressbar.setVisibility(View.GONE);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

    }

    private class PostListAdapter extends BaseAdapter {

        Context context;
        List<PostList> postLists;

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

            TextView post_link = view.findViewById(R.id.post_caption);
            TextView date = view.findViewById(R.id.post_date);
            TextView status = view.findViewById(R.id.post_status);
            TextView comment = view.findViewById(R.id.comment);
            comment.setVisibility(View.GONE);
            String approve_status = postLists.get(position).getStatus();

            post_link.setText("Facebook Post");
            date.setText(postLists.get(position).getDate());
            status.setText(postLists.get(position).getStatus());
            if (approve_status.equals("REJECTED")){
                status.setTextColor(getResources().getColor(R.color.red));
                comment.setVisibility(View.VISIBLE);
            } else if (approve_status.equals("PENDING APPROVAL")) {
                status.setTextColor(getResources().getColor(R.color.pending));
            }  else if (approve_status.equals("APPROVED")) {
                status.setTextColor(getResources().getColor(R.color.green_1));
            } else {

                comment.setVisibility(View.GONE);
            }


            return view;
        }
    }

    private class CompletedEventList{
        String id, name, start_date, end_date, photo, status, location, comment;

        public CompletedEventList(String id, String name, String start_date, String end_date, String photo, String status, String location, String comment) {
            this.id = id;
            this.name = name;
            this.start_date = start_date;
            this.end_date = end_date;
            this.photo = photo;
            this.status = status;
            this.location = location;
            this.comment = comment;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStart_date() {
            return start_date;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public String getEnd_date() {
            return end_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
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

    private class CompletedEventAdapter extends BaseAdapter{

        Context context;
        ArrayList<CompletedEventList> completedEventLists;

        public CompletedEventAdapter(Context context, ArrayList<CompletedEventList> completedEventLists) {
            this.context = context;
            this.completedEventLists = completedEventLists;
        }

        @Override
        public int getCount() {
            return completedEventLists.size();
        }

        @Override
        public Object getItem(int position) {
            return completedEventLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return completedEventLists.indexOf(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.completed_event_list_layout,null,true);

//            ImageView cover_photo = view.findViewById(R.id.cover_photo);
            TextView from_date = view.findViewById(R.id.start_date);
//            TextView to_date = view.findViewById(R.id.end_date);
            TextView location = view.findViewById(R.id.location);
            TextView status = view.findViewById(R.id.status);
            TextView name = view.findViewById(R.id.event_name);
            TextView comment = view.findViewById(R.id.comment);
            LinearLayout comment_layout = view.findViewById(R.id.comment_layout);

            String approve_status = completedEventLists.get(position).getStatus();
            switch (approve_status){
                case "0" :
                    status.setText("Pending");
                    status.setTextColor(getResources().getColor(R.color.pending));
//                    status.setBackgroundResource(R.drawable.layout_background_3);
                    comment_layout.setVisibility(View.GONE);
                    break;
                case "1" :
                    status.setText("Approved");
                    status.setTextColor(getResources().getColor(R.color.green_1));
                    comment_layout.setVisibility(View.GONE);
                    break;
                case "2" :
                    status.setText("Rejected");
                    status.setTextColor(getResources().getColor(R.color.red));
//                    status.setBackgroundResource(R.drawable.layout_background_2);
                    comment_layout.setVisibility(View.VISIBLE);
                    comment.setText( " " +completedEventLists.get(position).getComment());
                    break;
            }

//            Picasso.get().load(completedEventLists.get(position).getPhoto()).into(cover_photo);
            from_date.setText(completedEventLists.get(position).getStart_date());
//            to_date.setText(completedEventLists.get(position).getEnd_date());
            location.setText(completedEventLists.get(position).getLocation());
            name.setText(completedEventLists.get(position).getName());

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