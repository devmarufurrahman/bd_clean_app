package com.winkytech.bdclean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

import java.util.ArrayList;

public class PositionActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView position_list_view;
    PositionList position_list_class;
    PositionListAdapter positionListAdapter ;
    String position_id,user_id;
    int org_level_pos;
    ProgressBar progressBar;
    TextView toast_message;

    public static ArrayList<PositionList> positionLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        Intent intent = getIntent();
        user_id=intent.getStringExtra("user_id");
        org_level_pos = intent.getIntExtra("org_level_pos",0);

        toolbar = findViewById(R.id.custom_toolbar);
        position_list_view = findViewById(R.id.position_list_view);
        progressBar=findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getPositionData();
        positionListAdapter = new PositionListAdapter(getApplicationContext(),positionLists);
        position_list_view.setAdapter(positionListAdapter);
        position_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                position_id = positionLists.get(position).getId();
                String pos_name = positionLists.get(position).getName();
                Intent intent = new Intent(getApplicationContext(),NewMemberActivity.class);
                intent.putExtra("position_id",position_id);
                intent.putExtra("user_id",user_id);
                intent.putExtra("pos_name",pos_name);
                intent.putExtra("dept_level",positionLists.get(position).getDept_level());
                startActivity(intent);

            }
        });

    }

    private void getPositionData() {
        progressBar.setVisibility(View.VISIBLE);
        String url= "https://bdclean.winkytech.com/backend/api/getPositionData.php";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        positionLists.clear();
                        System.out.println("response = " + response);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i =0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String id = object.getString("id");
                                String name = object.getString("name");
                                String dept_level = object.getString("org_level_pos");

                                position_list_class=new PositionList(id,name,dept_level);
                                positionLists.add(position_list_class);
                                positionListAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
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
                //Toast.makeText(getApplicationContext(), "Failed to get Patient List", Toast.LENGTH_LONG).show();
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText("Failed to get position data");
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}