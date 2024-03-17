package com.winkytech.bdclean.PollingPanel;

import static android.content.Context.MODE_PRIVATE;
import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import com.vishnusivadas.advanced_httpurlconnection.PutData;
import com.winkytech.bdclean.EventDetailsActivity;
import com.winkytech.bdclean.NetworkChangeListener;
import com.winkytech.bdclean.PollingActivity;
import com.winkytech.bdclean.R;
import com.winkytech.bdclean.SocialMediaActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class VoterFragment extends Fragment {

    String election_position_ref, chief_name = "", it_name = "", logistics_name = "", chief_photo = "", it_photo = "", logistics_photo="", chief_symbol = "", it_symbol= "", logistics_symbol= "";
    int user_id, org_level_pos, position_ref, election_id, voter_step = 1;
    String selected_chief_coordinator_candidate_ref = "", selected_head_it_candidate_ref = "", selected_head_logistics_candidate_ref = "";
    Toolbar toolbar;
    ProgressBar progressBar;
    ListView candidate_list_view;
    CandidateList candidate_list_class;
    CandidateListAdapter candidateListAdapter;
    ArrayList<CandidateList> candidateLists = new ArrayList<>();
    Button submit_btn;
    private final String photoUrl = "https://bdclean.winkytech.com/resources/user/profile_pic/";
    TextView position_tv, toast_message, election_position_tv3;
    NetworkChangeListener networkChangeListener;

    private static final int activity_type = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_voter, container, false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        requireActivity().registerReceiver(networkChangeListener, filter);

        toolbar = view.findViewById(R.id.custom_toolbar);
        candidate_list_view = view.findViewById(R.id.candidate_list);
        submit_btn = view.findViewById(R.id.submit_btn);
        progressBar = view.findViewById(R.id.progressbar);
        position_tv = view.findViewById(R.id.election_position_tv);
        election_position_tv3 = view.findViewById(R.id.election_position_tv3);
        position_tv.setText("চীফ কর্ডিনেটর");
        progressBar.setVisibility(View.GONE);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the home button click
                // For example, you might want to pop the current fragment from the back stack
                requireActivity().onBackPressed();
            }
        });

        SharedPreferences shared = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = Integer.parseInt(shared.getString("user_id",""));
        org_level_pos = Integer.parseInt(shared.getString("org_level_ref","0"));
        position_ref = Integer.parseInt(shared.getString("user_position","0"));

        Bundle bundle = getArguments();
        election_id = bundle.getInt("election_ref");

        System.out.println("election_position_ref = " + election_position_ref + " , election_ref = "+election_id);

        getCandidateList(1);
        candidateListAdapter = new CandidateListAdapter(getActivity(), candidateLists, activity_type);
        candidate_list_view.setAdapter(candidateListAdapter);

        candidate_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                candidateListAdapter.setSelectedPosition(i);

                if (voter_step == 1){

                    selected_chief_coordinator_candidate_ref = candidateLists.get(i).getId();
                    chief_name = candidateLists.get(i).getFull_name();
                    chief_photo = candidateLists.get(i).getProfile_photo();
                    chief_symbol = candidateLists.get(i).getSymbol();
                    //Toast.makeText(getContext(), candidate_name, Toast.LENGTH_SHORT).show();
                    Log.d("Selected_chief_coordinator = ", selected_chief_coordinator_candidate_ref + " , name = "+chief_name);

                } else if (voter_step == 2){

                    selected_head_it_candidate_ref = candidateLists.get(i).getId();
                    it_name = candidateLists.get(i).getFull_name();
                    it_photo = candidateLists.get(i).getProfile_photo();
                    it_symbol = candidateLists.get(i).getSymbol();
                    //Toast.makeText(getContext(), candidate_name, Toast.LENGTH_SHORT).show();
                    Log.d("Selected_chief_coordinator = ", selected_head_it_candidate_ref + " , name = "+it_name);

                } else if (voter_step == 3){

                    selected_head_logistics_candidate_ref = candidateLists.get(i).getId();
                    logistics_name = candidateLists.get(i).getFull_name();
                    logistics_photo = candidateLists.get(i).getProfile_photo();
                    logistics_symbol = candidateLists.get(i).getSymbol();
                    //Toast.makeText(getContext(), candidate_name, Toast.LENGTH_SHORT).show();
                    Log.d("Selected_chief_coordinator = ", selected_head_logistics_candidate_ref + " , name = "+logistics_name);

                }
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (voter_step == 1){

                    if (selected_chief_coordinator_candidate_ref.equals("")){
                        Toast toast = new Toast(getActivity());
                        View toast_view = getLayoutInflater().inflate(R.layout.custom_warning_dialog_layout,getView().findViewById(R.id.custom_toast));
                        toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                        toast_message.setText("Please select a candidate for Chief Coordinator");
                        toast.setView(toast_view);
                        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.FILL_HORIZONTAL,0,110);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();
                    } else {

                        voter_step = voter_step+1;
                        //Toast.makeText(getActivity(), "Chief coordinator election complete", Toast.LENGTH_SHORT).show();
                        position_tv.setText("হেড অফ আইটি অ্যান্ড মিডিয়া");
                        getCandidateList(2);
                    }

                } else if (voter_step == 2){

                    if (selected_head_it_candidate_ref.equals("")){
                        Toast toast = new Toast(getActivity());
                        View toast_view = getLayoutInflater().inflate(R.layout.custom_warning_dialog_layout,getView().findViewById(R.id.custom_toast));
                        toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                        toast_message.setText("Please select a candidate  Head of IT & Media");
                        toast.setView(toast_view);
                        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.FILL_HORIZONTAL,0,110);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();
                    } else {

                        voter_step = voter_step+1;
                        //Toast.makeText(getActivity(), "Head of IT & Media election complete", Toast.LENGTH_SHORT).show();
                        position_tv.setText("হেড অফ লজিস্টিকস");
                        getCandidateList(3);
                        election_position_tv3.setText("প্রার্থীর নাম সিলেক্ট করুন ও রিভিউ বাটন চাপুন ");
                        submit_btn.setText("Review");
                    }

                } else if (voter_step == 3){

                    if (selected_head_logistics_candidate_ref.equals("")){

                        //Toast.makeText(getContext(), "Please select a candidate for Head of Logistics", Toast.LENGTH_SHORT).show();
                        Toast toast = new Toast(getActivity());
                        View toast_view = getLayoutInflater().inflate(R.layout.custom_warning_dialog_layout,getView().findViewById(R.id.custom_toast));
                        toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                        toast_message.setText("Please select a candidate for Head of Logistics");
                        toast.setView(toast_view);
                        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.FILL_HORIZONTAL,0,110);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();

                    } else {

                        loadDialog();

                        //Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();

                    }

                }

            }
        });

        return view;
    }

    private void loadDialog() {

        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.vote_view_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        TextView chief_name_tv = dialog.findViewById(R.id.chief_name);
        TextView head_it_name_tv = dialog.findViewById(R.id.head_it_name);
        TextView head_logistics_name_tv = dialog.findViewById(R.id.head_logistics_name);

        ImageView chief_photo_iv = dialog.findViewById(R.id.chief_photo);
        ImageView head_it_photo_iv = dialog.findViewById(R.id.head_it_photo);
        ImageView head_logistics_photo_iv = dialog.findViewById(R.id.head_logistics_photo);

        ImageView chiefSymbol = dialog.findViewById(R.id.chief_symbol);
        ImageView itSymbol = dialog.findViewById(R.id.head_it_symble);
        ImageView logisticsSymbol = dialog.findViewById(R.id.head_logistics_symble);
//
        Button reset_btn = dialog.findViewById(R.id.reset_btn);
        Button submit_btn = dialog.findViewById(R.id.submit_btn);

        chief_name_tv.setText(chief_name);
        head_it_name_tv.setText(it_name);
        head_logistics_name_tv.setText(logistics_name);

        Picasso.get().load(chief_photo).into(chief_photo_iv);
        Picasso.get().load(it_photo).into(head_it_photo_iv);
        Picasso.get().load(logistics_photo).into(head_logistics_photo_iv);

        Picasso.get().load(chief_symbol).into(chiefSymbol);
        Picasso.get().load(it_symbol).into(itSymbol);
        Picasso.get().load(logistics_symbol).into(logisticsSymbol);

        reset_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent  = new Intent(getActivity(), ElectionManageActivity.class);
            intent.putExtra("election_ref", election_id);
            startActivity(intent);
            dialog.dismiss();

            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            saveVoteProfile();
            dialog.dismiss();
            }
        });

    }

    private void saveVoteProfile() {

        progressBar.setVisibility(View.VISIBLE);

        System.out.println("Candidates = "+selected_chief_coordinator_candidate_ref+ ", "+selected_head_it_candidate_ref+" , "+selected_head_logistics_candidate_ref);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void run() {

                String[] field = new String[5];
                field[0] = "user_ref";
                field[1] = "election_ref";
                field[2] = "chief_coordinator";
                field[3] = "head_id";
                field[4] = "head_logistics";

                //Creating array for data
                String[] data = new String[5];
                data[0] = String.valueOf(user_id);
                data[1] = String.valueOf(election_id);
                data[2] = selected_chief_coordinator_candidate_ref;
                data[3] = selected_head_it_candidate_ref;
                data[4] = selected_head_logistics_candidate_ref;

                PutData putData = new PutData("https://bdclean.winkytech.com/backend/api/saveVoteProfile.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult().trim();

                        Log.d("Save Vote ", result);

                        if (result.equals("success")) {
                            progressBar.setVisibility(View.GONE);

                            Toast toast = new Toast(getActivity());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_warning_dialog_layout,getView().findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Your Vote is Complete.\n");
                            Button ok_btn = toast_view.findViewById(R.id.ok_btn);
                            ok_btn.setVisibility(View.GONE);
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                            Intent intent = new Intent(getActivity(), PollingActivity.class);
                            startActivity(intent);
                            //Toast.makeText(EventDetailsActivity.this, "Left Event", Toast.LENGTH_SHORT).show();

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.i("PutData", result);
                            // Toast.makeText(EventDetailsActivity.this, "Failed To leave event!!!", Toast.LENGTH_SHORT).show();
                            Toast toast = new Toast(getActivity());
                            View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout, getView().findViewById(R.id.custom_toast));
                            toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                            toast_message.setText("Failed To submit vote. please try again");
                            toast.setView(toast_view);
                            toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();

                        }
                    }
                }
                //End Write and Read data with URL
            }
        });

    }

    private void getCandidateList(int election_position_ref) {

        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getElectionCandidateList.php?election_ref="+election_id+"&election_position_ref="+election_position_ref;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);
                System.out.println(response);
                candidateLists.clear();

                try {
                    JSONArray obj = new JSONArray(response);
                    for (int i=0;i<obj.length();i++){

                        JSONObject jsonObject = obj.getJSONObject(i);
                        String id = jsonObject.getString("id");
                        String election_ref = jsonObject.getString("election_ref");
                        String election_position_ref = jsonObject.getString("election_position_ref");
                        String user_ref = jsonObject.getString("user_ref");
                        String division_ref = jsonObject.getString("division_ref");
                        String district_ref = jsonObject.getString("district_ref");
                        String upazila_ref = jsonObject.getString("upazila_ref");
                        String position_ref = jsonObject.getString("position_ref");
                        String full_name = jsonObject.getString("full_name");
                        String profile_photo = photoUrl + jsonObject.getString("profile_photo");
                        String symbol = photoUrl + jsonObject.getString("symbol");

                        candidate_list_class = new CandidateList(id, election_ref, election_position_ref, user_ref, division_ref, district_ref, upazila_ref, position_ref, full_name, profile_photo, symbol);
                        candidateLists.add(candidate_list_class);
                        candidateListAdapter.notifyDataSetChanged();
                        candidateListAdapter.clearSelection();

                    }
                }
                catch (JSONException e){
                    Log.e("anyText",response);
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast toast = new Toast(getActivity());
                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_error_layout,getView().findViewById(R.id.custom_toast));
                TextView toast_message;
                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
                toast_message.setText("Failed To get info");
                toast.setView(toast_view);
                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    private static class CandidateList{

        String id, election_ref, election_position_ref, user_ref, division_ref, district_ref, upazila_ref, position_ref,full_name, profile_photo, symbol;

        public CandidateList(String id, String election_ref, String election_position_ref, String user_ref, String division_ref, String district_ref, String upazila_ref, String position_ref, String full_name, String profile_photo, String symbol) {
            this.id = id;
            this.election_ref = election_ref;
            this.election_position_ref = election_position_ref;
            this.user_ref = user_ref;
            this.division_ref = division_ref;
            this.district_ref = district_ref;
            this.upazila_ref = upazila_ref;
            this.position_ref = position_ref;
            this.full_name = full_name;
            this.profile_photo = profile_photo;
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getElection_ref() {
            return election_ref;
        }

        public void setElection_ref(String election_ref) {
            this.election_ref = election_ref;
        }

        public String getElection_position_ref() {
            return election_position_ref;
        }

        public void setElection_position_ref(String election_position_ref) {
            this.election_position_ref = election_position_ref;
        }

        public String getUser_ref() {
            return user_ref;
        }

        public void setUser_ref(String user_ref) {
            this.user_ref = user_ref;
        }

        public String getDivision_ref() {
            return division_ref;
        }

        public void setDivision_ref(String division_ref) {
            this.division_ref = division_ref;
        }

        public String getDistrict_ref() {
            return district_ref;
        }

        public void setDistrict_ref(String district_ref) {
            this.district_ref = district_ref;
        }

        public String getUpazila_ref() {
            return upazila_ref;
        }

        public void setUpazila_ref(String upazila_ref) {
            this.upazila_ref = upazila_ref;
        }

        public String getPosition_ref() {
            return position_ref;
        }

        public void setPosition_ref(String position_ref) {
            this.position_ref = position_ref;
        }

        public String getFull_name() {
            return full_name;
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        public String getProfile_photo() {
            return profile_photo;
        }

        public void setProfile_photo(String profile_photo) {
            this.profile_photo = profile_photo;
        }
    }

    private class CandidateListAdapter extends BaseAdapter{

        Context context;
        ArrayList<CandidateList> candidateLists;

        private int selectedPosition = -1;

        private final int activity_type;
        public CandidateListAdapter(Context context, ArrayList<CandidateList> candidateLists, int activity_type) {
            this.context = context;
            this.candidateLists = candidateLists;
            this.activity_type = activity_type;
        }

        @Override
        public int getCount() {
            return candidateLists.size();
        }

        @Override
        public Object getItem(int i) {
            return candidateLists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return candidateLists.indexOf(i);
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {


            @SuppressLint("ViewHolder") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.candidate_list_layout,null,true);
            ShapeableImageView candidate_photo = view.findViewById(R.id.candidate_photo);
            ImageView candidate_symbol = view.findViewById(R.id.candidate_symbol);
            TextView candidate_name = view.findViewById(R.id.candidate_name);
            TextView total_vote = view.findViewById(R.id.total_vote);
            total_vote.setVisibility(View.GONE);

            Picasso.get().load(candidateLists.get(i).getProfile_photo()).into(candidate_photo);
            Picasso.get().load(candidateLists.get(i).getSymbol()).into(candidate_symbol);
            candidate_name.setText(candidateLists.get(i).getFull_name());

            if (i == selectedPosition) {
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bdclean_greenDark));
            } else {
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bdclean_green));
            }

            return view;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
            notifyDataSetChanged();
        }

        public void clearSelection() {
            selectedPosition = -1; // Set the selected position to an invalid value
            notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister the BroadcastReceiver when the Fragment is destroyed
        requireActivity().unregisterReceiver(networkChangeListener);
    }
}