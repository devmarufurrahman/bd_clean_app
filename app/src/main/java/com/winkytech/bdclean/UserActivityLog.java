package com.winkytech.bdclean;

import static com.winkytech.bdclean.NewEventActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserActivityLog extends AppCompatActivity {
    Toolbar toolbar;
    ProgressBar progressBar;
    String user_id;
    TextView toast_message, log_tv;
    ListView log_list_view;
    LogList log_list_class;
    LogListAdapter logListAdapter;
    ArrayList<LogList> LogLists = new ArrayList<>();
    Button load_more;
    int limit=15, offset=0;
    ExecutorService executorService;

    NetworkChangeListener networkChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_log);


        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = shared.getString("user_id", "");

        toolbar=findViewById(R.id.custom_toolbar);
        log_list_view = findViewById(R.id.log_list);
        progressBar=findViewById(R.id.progressbar);
        log_tv = findViewById(R.id.log_tv);
        load_more = findViewById(R.id.load_more);
        progressBar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        getLogActivity(limit,offset);
        logListAdapter = new LogListAdapter(UserActivityLog.this,LogLists);
        log_list_view.setAdapter(logListAdapter);

        load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offset = offset+10;

                getLogActivity(limit, offset);
            }
        });
    }

    private void getLogActivity(int limit, int offset) {
        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getLogActivity.php?user_id="+user_id+"&limit="+limit+"&offset="+offset;
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                StringRequest request = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
//                        LogLists.clear();
                                System.out.println("log response = " + response);

                                try {

                                    JSONArray jsonArray = new JSONArray(response);
                                    for (int i =0; i<jsonArray.length();i++){
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        String id = object.getString("id");
                                        String activity_type_ref = object.getString("activity_type_ref");
                                        String creator_name = object.getString("creator_name");
                                        String type = object.getString("activity_type");
                                        String date = object.getString("create_date");
                                        String read_flag = object.getString("read_flag");
                                        String receiver_name = object.getString("full_name");
                                        String remain_time = "";


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


                                        if (!activity_type_ref.equals("15")) {
                                            log_list_class = new LogList(id, receiver_name, final_date, type, activity_type_ref, creator_name, remain_time);
                                            LogLists.add(log_list_class);
                                        }
                                        progressBar.setVisibility(View.GONE);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    logListAdapter.notifyDataSetChanged();
                                                }
                                            });




                                    }

                                } catch (JSONException e){
                                    e.printStackTrace();
                                    progressBar.setVisibility(View.GONE);
                                    log_tv.setText("You have no activity");
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = new Toast(getApplicationContext());
                        View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                        toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                        toast_message.setText("Failed to get Log List");
                        toast.setView(toast_view);
                        toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(request);
            }
        });

    }


    private class LogList{
        String id, name, date, type, type_code, creator_name, remain_time;

        public LogList() {
        }

        public LogList(String id, String name, String date, String type, String type_code, String creator_name, String remain_time) {
            this.id = id;
            this.name = name;
            this.date = date;
            this.type = type;
            this.type_code = type_code;
            this.creator_name = creator_name;
            this.remain_time = remain_time;
        }

        public String getId() {
            return id;
        }

        public String getRemain_time() {
            return remain_time;
        }

        public void setRemain_time(String remain_time) {
            this.remain_time = remain_time;
        }

        public String getCreator_name() {
            return creator_name;
        }

        public void setCreator_name(String creator_name) {
            this.creator_name = creator_name;
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

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getType_code() {
            return type_code;
        }

        public void setType_code(String type_code) {
            this.type_code = type_code;
        }
    }


    private class LogListAdapter extends BaseAdapter {

        Context context;
        ArrayList<LogList> logLists;

        public LogListAdapter(Context context, ArrayList<LogList> logLists) {
            this.context = context;
            this.logLists = logLists;
        }

        @Override
        public int getCount() {
            return logLists.size();
        }

        @Override
        public Object getItem(int position) {
            return logLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return logLists.indexOf(position);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_log_layout,null,true);



            TextView log_name = view.findViewById(R.id.log_name);
            TextView date = view.findViewById(R.id.log_date);
            TextView log_type = view.findViewById(R.id.log_type);
            CardView logLayout = view.findViewById(R.id.logLayout);

            String mStatus = logLists.get(position).getType_code();
            String name = logLists.get(position).getCreator_name();
            date.setText(logLists.get(position).getDate()+ " , " + logLists.get(position).getRemain_time());

            log_type.setText(logLists.get(position).getType());


                switch (mStatus){
                    case "1" :
                    case "13" :
                        log_name.setText(name + " created an event");
                        break;
                    case "2" :
                        log_name.setText(name + " reviewed an event approval");

                        break;
                    case "3" :
                        log_name.setText(name + " join an event");

                        break;
                    case "4" :
                        log_name.setText(name + " reviewed an event joining approval");

                        break;
                    case "5" :
                        log_name.setText(name + " created a complaint");

                        break;
                    case "6" :
                        log_name.setText(name + " reviewed a complaint");

                        break;
                    case "7" :
                        log_name.setText(name + " created an evaluation");

                        break;
                    case "8" :
                        log_name.setText(name + " reviewed an evaluation");

                        break;
                    case "9" :
                        log_name.setText(name + " created a post");

                        break;
                    case "10" :
                        log_name.setText(name + " reviewed a post approval");

                        break;
                    case "11" :
                        log_name.setText(name + " created a new user");

                        break;
                    case "12" :
                        log_name.setText(name + " reviewed an user approval");

                        break;
                    case "14" :
                            log_name.setText(name + " sent a message to " + logLists.get(position).getName());

                        break;


            }



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

}