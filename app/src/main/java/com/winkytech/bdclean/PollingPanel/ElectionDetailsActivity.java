package com.winkytech.bdclean.PollingPanel;

import static com.winkytech.bdclean.HomeActivity.Contact;
import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.winkytech.bdclean.PollingActivity;
import com.winkytech.bdclean.PositionList;
import com.winkytech.bdclean.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ElectionDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView position_list_view;

    PositionList position_list_class;
    PositionListAdapter positionListAdapter;
    ArrayList<PositionList> positionLists = new ArrayList<>();
    TextView toast_message;
    int user_id, org_level_pos, position_ref, election_id;
    private static final String banner_url = "https://bdclean.winkytech.com/resources/election_media/";
    ProgressBar progressBar;
    String user_designation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_details);

        progressBar = findViewById(R.id.progressbar);
        position_list_view = findViewById(R.id.election_position_list);
        toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar.setVisibility(View.GONE);

        Intent intent = getIntent();
        election_id = intent.getIntExtra("election_ref",0);

        SharedPreferences shared = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = Integer.parseInt(shared.getString("user_id",""));
        org_level_pos = Integer.parseInt(shared.getString("org_level_ref","0"));
        position_ref = Integer.parseInt(shared.getString("user_position","0"));
        user_designation = shared.getString("designation","");

        System.out.println("org_level_pos = "+org_level_pos);
        System.out.println("position_ref = "+position_ref);
        System.out.println("Designation = "+user_designation);
        System.out.println("election ref = "+election_id);

        getElectionPositionLists();
        positionListAdapter = new PositionListAdapter(ElectionDetailsActivity.this, positionLists);
        position_list_view.setAdapter(positionListAdapter);

        position_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String election_position_ref = positionLists.get(i).getId();

                Intent election_manage = new Intent(ElectionDetailsActivity.this, ElectionManageActivity.class);
                election_manage.putExtra("election_position_ref", election_position_ref);
                election_manage.putExtra("election_id",election_id);
                startActivity(election_manage);

            }
        });

    }

    private void getElectionPositionLists() {
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getElectionPositionData.php?election_ref="+election_id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                System.out.println(response);
                try {
                    JSONArray obj = new JSONArray(response);
                    for (int i=0;i<obj.length();i++){

                        JSONObject jsonObject = obj.getJSONObject(i);
                        String election_position_ref = jsonObject.getString("id");
                        String election_id = jsonObject.getString("election_id");
                        String title=jsonObject.getString("title");
                        String banner= banner_url+ jsonObject.getString("banner");
                        String position_ref = jsonObject.getString("position_ref");
                        String org_level_pos = jsonObject.getString("org_level_pos");
                        String end_date=jsonObject.getString("end_date");
                        String start_date = jsonObject.getString("start_date");
                        String election_year = jsonObject.getString("election_year");

                        @SuppressLint("SimpleDateFormat") Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_date);
                        @SuppressLint("SimpleDateFormat") Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_date);
                        String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                        String from_date = simpleDateFormat.format(date1);
                        String to_date = simpleDateFormat.format(date2);

                        position_list_class = new ElectionDetailsActivity.PositionList(election_position_ref, title, election_id,banner, position_ref, org_level_pos, election_year, from_date, to_date);
                        positionLists.add(position_list_class);
                        positionListAdapter.notifyDataSetChanged();

                    }
                }
                catch (JSONException e){
                    Log.e("anyText",response);
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                } catch (ParseException e) {
                    progressBar.setVisibility(View.GONE);
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                String volleyError = "";
                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){
                    volleyError="Server Connection error";
                }
                Toast toast = new Toast(getApplicationContext());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,findViewById(R.id.custom_toast));
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText(volleyError +",  Failed To information");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(ElectionDetailsActivity.this);
        requestQueue.add(stringRequest);
    }

    private class PositionList{

        String id, title,election_id, banner, position_ref, org_level_pos, election_year, start_date, end_date;

        public PositionList(String id, String title,String election_id, String banner, String position_ref, String org_level_pos, String election_year, String start_date, String end_date) {
            this.id = id;
            this.title = title;
            this.election_id = election_id;
            this.banner = banner;
            this.position_ref = position_ref;
            this.org_level_pos = org_level_pos;
            this.election_year = election_year;
            this.start_date = start_date;
            this.end_date = end_date;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getElection_id() {
            return election_id;
        }

        public void setElection_id(String election_id) {
            this.election_id = election_id;
        }

        public String getBanner() {
            return banner;
        }

        public void setBanner(String banner) {
            this.banner = banner;
        }

        public String getPosition_ref() {
            return position_ref;
        }

        public void setPosition_ref(String position_ref) {
            this.position_ref = position_ref;
        }

        public String getOrg_level_pos() {
            return org_level_pos;
        }

        public void setOrg_level_pos(String org_level_pos) {
            this.org_level_pos = org_level_pos;
        }

        public String getElection_year() {
            return election_year;
        }

        public void setElection_year(String election_year) {
            this.election_year = election_year;
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
    }

    private class PositionListAdapter extends BaseAdapter{

        Context context;
        ArrayList<ElectionDetailsActivity.PositionList> positionLists;

        public PositionListAdapter(Context context, ArrayList<ElectionDetailsActivity.PositionList> positionLists) {
            this.context = context;
            this.positionLists = positionLists;
        }

        @Override
        public int getCount() {
            return positionLists.size();
        }

        @Override
        public Object getItem(int i) {
            return positionLists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return positionLists.indexOf(i);
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {

            @SuppressLint("ViewHolder") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.election_position_list_layout,null,true);

            ImageView position_banner = view.findViewById(R.id.position_banner);
            TextView position_title = view.findViewById(R.id.position_title);
            TextView election_start_date = view.findViewById(R.id.election_start_date);
            TextView election_end_date = view.findViewById(R.id.election_end_date);

            Picasso.get().load(positionLists.get(i).getBanner()).into(position_banner);
            position_title.setText(positionLists.get(i).getTitle());
            election_start_date.setText("Stats from :" + positionLists.get(i).getStart_date());
            election_end_date.setText("Ends on :" + positionLists.get(i).getEnd_date());

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