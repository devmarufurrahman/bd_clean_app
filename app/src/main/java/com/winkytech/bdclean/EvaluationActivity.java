package com.winkytech.bdclean;

import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EvaluationActivity extends AppCompatActivity {

    Toolbar toolbar;
    Button self_history,other_history,newEvaluation, refresh_btn;
    ListView evaluation_list_view_1,evaluation_list_view_2;
    EvaluationListAdapter evaluationListAdapter;
    EvaluationList evaluation_list_class;
    ProgressBar progressBar;
    TextView toast_message, record_status;

    List<EvaluationList> evaluationLists = new ArrayList<>();

    String user_id;
    int org_leveL_pos;

    SharedPreferences sharedpreferences;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10000;

    NetworkChangeListener networkChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = shared.getString("user_id","");
        org_leveL_pos = Integer.parseInt(shared.getString("org_level_ref","0"));
        System.out.println("user_id = " +user_id + " , ORG_LEVEL_POS = " + org_leveL_pos);

        self_history=findViewById(R.id.self_ev_history);
        other_history=findViewById(R.id.other_ev_history);
        newEvaluation=findViewById(R.id.new_evaluation);
        evaluation_list_view_1=findViewById(R.id.evaluation_list);
        evaluation_list_view_2=findViewById(R.id.evaluation_list_2);
        toolbar=findViewById(R.id.custom_toolbar);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        record_status = findViewById(R.id.record_status);
        record_status.setVisibility(View.GONE);
        refresh_btn = findViewById(R.id.reload_btn);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(org_leveL_pos==1){
            newEvaluation.setVisibility(View.GONE);
            self_history.setVisibility(View.GONE);
            other_history.setVisibility(View.GONE);
            evaluation_list_view_2.setVisibility(View.VISIBLE);
            getEvaluationToList();
            evaluationListAdapter=new EvaluationListAdapter(getApplicationContext(), (ArrayList<EvaluationList>) evaluationLists);
            evaluation_list_view_2.setAdapter(evaluationListAdapter);

        } else {
            newEvaluation.setVisibility(View.VISIBLE);
            self_history.setVisibility(View.VISIBLE);
            other_history.setVisibility(View.VISIBLE);
            evaluation_list_view_2.setVisibility(View.GONE);
            evaluation_list_view_1.setVisibility(View.VISIBLE);
            getEvaluationByList();
            evaluationListAdapter=new EvaluationListAdapter(getApplicationContext(), (ArrayList<EvaluationList>) evaluationLists);
            evaluation_list_view_1.setAdapter(evaluationListAdapter);
//            self_history.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0D235")));
            self_history.setTextColor(Color.BLACK);
//            other_history.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
            other_history.setTextColor(getResources().getColor(R.color.black));
        }

//        handler.postDelayed(runnable = new Runnable() {
//            public void run() {
//
//                handler.postDelayed(runnable, delay);
//                if(org_leveL_pos==1){
//                    newEvaluation.setVisibility(View.GONE);
//                    self_history.setVisibility(View.GONE);
//                    other_history.setVisibility(View.GONE);
//                    evaluation_list_view_2.setVisibility(View.VISIBLE);
//                    getEvaluationToList();
//                    evaluationListAdapter=new EvaluationListAdapter(getApplicationContext(), (ArrayList<EvaluationList>) evaluationLists);
//                    evaluation_list_view_2.setAdapter(evaluationListAdapter);
//
//                } else {
//                    newEvaluation.setVisibility(View.VISIBLE);
//                    self_history.setVisibility(View.VISIBLE);
//                    other_history.setVisibility(View.VISIBLE);
//                    evaluation_list_view_2.setVisibility(View.GONE);
//                    evaluation_list_view_1.setVisibility(View.VISIBLE);
//                    getEvaluationByList();
//                    evaluationListAdapter=new EvaluationListAdapter(getApplicationContext(), (ArrayList<EvaluationList>) evaluationLists);
//                    evaluation_list_view_1.setAdapter(evaluationListAdapter);
//                    self_history.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#cc0000")));
//                    self_history.setTextColor(Color.WHITE);
//                    other_history.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F8D7DA")));
//                    other_history.setTextColor(getResources().getColor(R.color.red));
//                }
//
//                progressBar.setVisibility(View.GONE);
//            }
//
//        }, delay);

        newEvaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),NewEvaluationActivity.class);
                intent.putExtra("user_id",user_id);
                intent.putExtra("org_level_pos",org_leveL_pos);
                startActivity(intent);

            }
        });


        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(org_leveL_pos==1){
                    newEvaluation.setVisibility(View.GONE);
                    self_history.setVisibility(View.GONE);
                    other_history.setVisibility(View.GONE);
                    evaluation_list_view_2.setVisibility(View.VISIBLE);
                    getEvaluationToList();
                    evaluationListAdapter=new EvaluationListAdapter(getApplicationContext(), (ArrayList<EvaluationList>) evaluationLists);
                    evaluation_list_view_2.setAdapter(evaluationListAdapter);

                } else {
                    newEvaluation.setVisibility(View.VISIBLE);
                    self_history.setVisibility(View.VISIBLE);
                    other_history.setVisibility(View.VISIBLE);
                    evaluation_list_view_2.setVisibility(View.GONE);
                    evaluation_list_view_1.setVisibility(View.VISIBLE);
                    getEvaluationByList();
                    evaluationListAdapter=new EvaluationListAdapter(getApplicationContext(), (ArrayList<EvaluationList>) evaluationLists);
                    evaluation_list_view_1.setAdapter(evaluationListAdapter);
//            self_history.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0D235")));
                    self_history.setTextColor(Color.BLACK);
//            other_history.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
                    other_history.setTextColor(getResources().getColor(R.color.black));
                }

            }
        });


        self_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluation_list_view_2.setVisibility(View.GONE);
                evaluation_list_view_1.setVisibility(View.VISIBLE);
                record_status.setVisibility(View.GONE);
                getEvaluationByList();
                evaluationListAdapter=new EvaluationListAdapter(getApplicationContext(), (ArrayList<EvaluationList>) evaluationLists);
                evaluation_list_view_1.setAdapter(evaluationListAdapter);
                self_history.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0D235")));
                self_history.setTextColor(Color.BLACK);
                other_history.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
                other_history.setTextColor(getResources().getColor(R.color.black));


            }
        });

        other_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                evaluation_list_view_1.setVisibility(View.GONE);
                evaluation_list_view_2.setVisibility(View.VISIBLE);
                record_status.setVisibility(View.GONE);
                getEvaluationToList();
                evaluationListAdapter=new EvaluationListAdapter(getApplicationContext(), (ArrayList<EvaluationList>) evaluationLists);
                evaluation_list_view_2.setAdapter(evaluationListAdapter);
                other_history.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0D235")));
                other_history.setTextColor(Color.BLACK);
                self_history.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D0EB9A")));
                self_history.setTextColor(getResources().getColor(R.color.black));

            }
        });

        evaluation_list_view_1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String ev_body = evaluationLists.get(position).getBody();
                String subject = evaluationLists.get(position).getSubject();
                String from = evaluationLists.get(position).getSend_from();
                String to = evaluationLists.get(position).getSend_to();
                String ev_for = evaluationLists.get(position).getSend_for();
                String date = evaluationLists.get(position).getDate();
                String status = evaluationLists.get(position).getStatus();
                String comment = evaluationLists.get(position).getComment();
                String evaluation_by_ref = evaluationLists.get(position).getEvaluation_by();

                Intent intent1 = new Intent(getApplicationContext(),EvaluationDetailsActivity.class);
                intent1.putExtra("ev_body",ev_body);
                intent1.putExtra("subject",subject);
                intent1.putExtra("from",from);
                intent1.putExtra("to",to);
                intent1.putExtra("ev_for",ev_for);
                intent1.putExtra("date",date);
                intent1.putExtra("intent_flag",1);
                intent1.putExtra("status",status);
                intent1.putExtra("comment",comment);
                intent1.putExtra("evaluation_by_ref",evaluation_by_ref);
                startActivity(intent1);

            }
        });

        evaluation_list_view_2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String ev_body = evaluationLists.get(position).getBody();
                String subject = evaluationLists.get(position).getSubject();
                String from = evaluationLists.get(position).getSend_from();
                String to = evaluationLists.get(position).getSend_to();
                String ev_for = evaluationLists.get(position).getSend_for();
                String date = evaluationLists.get(position).getDate();
                String comment = evaluationLists.get(position).getComment();
                String evaluation_by_ref = evaluationLists.get(position).getEvaluation_by();
                int eva_id = Integer.parseInt(evaluationLists.get(position).getId());

                Intent intent1 = new Intent(getApplicationContext(),EvaluationDetailsActivity.class);
                intent1.putExtra("ev_body",ev_body);
                intent1.putExtra("subject",subject);
                intent1.putExtra("from",from);
                intent1.putExtra("to",to);
                intent1.putExtra("ev_for",ev_for);
                intent1.putExtra("date",date);
                intent1.putExtra("intent_flag",2);
                intent1.putExtra("id",eva_id);
                intent1.putExtra("comment",comment);
                intent1.putExtra("evaluation_by_ref",evaluation_by_ref);
                startActivity(intent1);

            }
        });

    }

    private void getEvaluationToList() {

        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getEvaluationToList.php?user_id="+user_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        evaluationLists.clear();
                        System.out.println("EVALUATION TO DATA = " + response);
                        String data = response.toString().trim();
                        if (data.equals("null") || data.equals("")){
                            record_status.setVisibility(View.VISIBLE);
                        } else {
                            record_status.setVisibility(View.GONE);
                        }

                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("id");
                                String subject = object.getString("evaluation_subject");
                                String send_by = object.getString("evaluation_by_name");
                                String send_to = object.getString("evaluation_to_name");
                                String send_for = object.getString("evaluation_for_name");
                                String date = object.getString("evaluation_report_date");
                                String body = object.getString("evaluation_body");
                                String approve_flag = object.getString("approve_flag");
                                String evaluation_by_ref = object.getString("evaluation_by");
                                String status = "";
                                switch (approve_flag){
                                    case "0" :
                                        status = "Pending Approval";
                                        break;
                                    case "1" :
                                        status = "Approved";
                                        break;
                                    case "2" :
                                        status = "Rejected";
                                        break;
                                }

                                @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);

                                String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String from_date = simpleDateFormat.format(date1);

                                evaluation_list_class=new EvaluationList(subject,send_by,send_to,send_for,from_date,body,id,status, "", evaluation_by_ref);
                                evaluationLists.add(evaluation_list_class);
                                evaluationListAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);

                            }

                        } catch (JSONException e){
                            progressBar.setVisibility(View.GONE);
                            record_status.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                record_status.setVisibility(View.VISIBLE);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    private void getEvaluationByList() {
        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getEvaluationByList.php?user_id="+user_id;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        evaluationLists.clear();
                        System.out.println("EVALUATION BY DATA = " + response);
                        String data = response.toString().trim();
                        if (data.equals("null") || data.equals("")){
                            record_status.setVisibility(View.VISIBLE);
                        }
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("id");
                                String subject = object.getString("evaluation_subject");
                                String send_by = object.getString("evaluation_by_name");
                                String send_to = object.getString("evaluation_to_name");
                                String send_for = object.getString("evaluation_for_name");
                                String date = object.getString("evaluation_report_date");
                                String body = object.getString("evaluation_body");
                                String approve_flag = object.getString("approve_flag");
                                String comment = object.getString("comment");
                                String status = "";
                                switch (approve_flag){
                                    case "0" :
                                        status = "Pending Approval";
                                        break;
                                    case "1" :
                                        status = "Approved";
                                        break;
                                    case "2" :
                                        status = "Rejected";
                                        break;
                                }

                                @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);

                                String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String from_date = simpleDateFormat.format(date1);

                                evaluation_list_class=new EvaluationList(subject,send_by,send_to,send_for,from_date,body,id,status, comment, user_id);
                                evaluationLists.add(evaluation_list_class);
                                evaluationListAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);

                            }

                        } catch (JSONException e){
                            progressBar.setVisibility(View.GONE);
                            record_status.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                record_status.setVisibility(View.VISIBLE);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
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