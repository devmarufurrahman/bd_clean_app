package com.winkytech.bdclean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MemberActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView memberListView;
    MemberListAdapter memberListAdapter;
    ArrayList<MemberList> memberLists = new ArrayList<>();
    Button create_member, load_more;
    String user_id;
    ProgressBar progressBar;
    MemberList member_list_class;
    TextView toast_message, memberCounter;
    int org_level_pos , api_offset = 0, totalMemberCount = 0, filteredMemberCount = 0;
    SearchView search_member;

    String photoUrl = "https://bdclean.winkytech.com/resources/user/profile_pic/";

    NetworkChangeListener networkChangeListener;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        registerReceiver(networkChangeListener, filter);

        Intent intent = getIntent();
        user_id=intent.getStringExtra("user_id");
        org_level_pos=intent.getIntExtra("org_level_pos",0);
        progressBar=findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        memberListView = findViewById(R.id.member_listView);
        search_member=findViewById(R.id.search_member);
        toolbar=findViewById(R.id.custom_toolbar);
        load_more = findViewById(R.id.load_more);
        memberCounter = findViewById(R.id.memberCounter);
        load_more.setVisibility(View.GONE);
//        manage_member = findViewById(R.id.manage_member);
        create_member=findViewById(R.id.create_member);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getMemberList(10, api_offset);
        memberListAdapter=new MemberListAdapter(getApplicationContext(),memberLists, totalMemberCount);
        memberListView.setAdapter(memberListAdapter);

//        manage_member.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent1 = new Intent(getApplicationContext(), ManageMemberActivity.class);
//                startActivity(intent1);
//            }
//        });



        load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                api_offset = api_offset+10;

                getMemberList(10, api_offset);
            }
        });

        create_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),PositionActivity.class);
                intent.putExtra("org_level_pos",org_level_pos);
                intent.putExtra("user_id",user_id);
                startActivity(intent);

            }
        });

        search_member.setMaxWidth(Integer.MAX_VALUE);
        search_member.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                memberListAdapter.getFilter().filter(s);
                filteredMemberCount = memberListAdapter.memberCount;
                memberCounter.setText("Total Members (" + filteredMemberCount + ")");

                if (s.equals("")){
                    memberCounter.setText("Total Members (" + filteredMemberCount + ")");
                }

                return false;

            }
        });

        memberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Dialog dialog = new Dialog(MemberActivity.this);
                dialog.setContentView(R.layout.member_message_dialog_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);
                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                EditText edit_message = dialog.findViewById(R.id.edit_message);
                Button member_details = dialog.findViewById(R.id.member_details);
                Button send_message = dialog.findViewById(R.id.send_message);

                member_details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        MemberList clickedMember = (MemberList) memberListAdapter.getItem(position);
                        int member_id = Integer.parseInt(clickedMember.getId());
                        Intent intent1 = new Intent(getApplicationContext(), MemberDetailsActivity.class);
                        intent1.putExtra("member_id", member_id);
                        startActivity(intent1);
                        dialog.dismiss();

                    }
                });

                send_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String message = edit_message.getText().toString().trim();
                        MemberList clickedMember = (MemberList) memberListAdapter.getItem(position);
                        String member_id = clickedMember.getId();

                        if (message.equals("")){
                            edit_message.setError("Write message");
                        } else if (member_id.equals("")) {
                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Member ID not found!!");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            sendMessage(message, member_id);
                            dialog.dismiss();
                        }

                    }
                });
            }
        });
    }

    private void getMemberList(int limit, int api_offset) {

        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getAllMemberData.php?limit="+limit+"&offset="+api_offset;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // memberLists.clear();
                        //System.out.println("response = " + response);
                        load_more.setVisibility(View.VISIBLE);
                        Log.d("All members", response);
                        try {
                            String name;
                            String position;
                            String division;
                            String district;
                            String upazila;
                            String union;
                            String village;
                            String member_code;
                            JSONArray jsonArray = new JSONArray(response);

                            totalMemberCount = jsonArray.length();

                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("id");

                                    name = object.getString("full_name");

                                    position = object.getString("position");

                                    division = object.getString("division");

                                    district = object.getString("district");

                                    upazila = object.getString("upazila");

                                    union = object.getString("union_name");

                                    village = object.getString("village");

                                    member_code = object.getString("user_code");


                                    String photo = photoUrl+ (object.getString("profile_photo"));

                                    member_list_class=new MemberList(id,name,position,photo, division, district, upazila, union, village, member_code);
                                    memberLists.add(member_list_class);
                                    memberListAdapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                    Log.d("number of list",String.valueOf(i) );


                                memberCounter.setText("Total Members (" + totalMemberCount+")");
                            }

                        } catch (JSONException e){
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);

                //Toast.makeText(getApplicationContext(), "Failed to get Event List", Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText("Please check your internet connection or try again later");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP| Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    private void sendMessage(String message, String member_ref) {

        progressBar.setVisibility(View.VISIBLE);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                //Starting Write and Read data with URL
                //Creating array for parameters
                String[] field = new String[3];
                field[0] = "user_id";
                field[1] = "body";
                field[2] = "send_to";

                //Creating array for data
                String[] data = new String[3];
                data[0] = user_id;
                data[1] = message;
                data[2] = member_ref;

                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/saveMessage.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();
                        if (result.equals("Message Send")) {
                            progressBar.setVisibility(View.GONE);

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.i("PutData", result);
                            //Toast.makeText(MessageActivity.this, "Failed To send message!!!", Toast.LENGTH_SHORT).show();
                            Toast toast = new Toast(getApplicationContext());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Failed To send message!!!");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                            progressBar.setVisibility(View.GONE);

                        }
                    }
                }
                //End Write and Read data with URL
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister broadcast receiver when activity is destroyed
        unregisterReceiver(networkChangeListener);
    }

    private  int getTotalMemberCount() {

        return memberListAdapter.getCount();

    }
}