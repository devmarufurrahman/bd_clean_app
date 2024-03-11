package com.winkytech.bdclean.PollingPanel;

import static android.content.Context.MODE_PRIVATE;
import static com.winkytech.bdclean.HomeActivity.MyPREFERENCES;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.winkytech.bdclean.NetworkChangeListener;
import com.winkytech.bdclean.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
public class ElectionControllerFragment extends Fragment {

   int user_id=0, org_level_pos=0, position_ref=0, election_ref = 0, total_voter = 0, rest_voter = 0, vote_cast = 0;
   private final String photoUrl = "https://bdclean.winkytech.com/resources/user/profile_pic/";
   ProgressBar progressBar;
   ChiefCoordinatorCandidateList chiefCoordinatorCandidate_list_class;
   ChiefCoordinatorCandidateAdapter chiefCoordinatorCandidateAdapter;
   ArrayList<ChiefCoordinatorCandidateList> chiefCoordinatorCandidateLists = new ArrayList<>();
   ListView chief_coordinator_listview, head_it_listview, head_logistics_listview;
   TextView chief_pos, head_it_pos, head_logistics_pos;
   Toolbar toolbar;
   NetworkChangeListener networkChangeListener;
   TextView total_voter_chief, vote_cast_chief, rest_voter_chief;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_election_controller, container, false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeListener = new NetworkChangeListener();
        requireActivity().registerReceiver(networkChangeListener, filter);

        SharedPreferences shared = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        user_id = Integer.parseInt(shared.getString("user_id",""));
        org_level_pos = Integer.parseInt(shared.getString("org_level_ref","0"));
        position_ref = Integer.parseInt(shared.getString("user_position","0"));

        Bundle bundle = getArguments();
        election_ref = bundle.getInt("election_ref",0);
        System.out.println("election_ref = "+election_ref);

        chief_coordinator_listview = view.findViewById(R.id.chief_cordinator_list);
//        head_logistics_listview = view.findViewById(R.id.head_logistics_list);
//        head_it_listview = view.findViewById(R.id.head_it_list);
        progressBar = view.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        total_voter_chief = view.findViewById(R.id.total_voter_chief);
        vote_cast_chief = view.findViewById(R.id.vote_cast_chief);
        rest_voter_chief = view.findViewById(R.id.rest_voter_chief);
        chief_pos = view.findViewById(R.id.chief_coordinator_pos);
        head_it_pos = view.findViewById(R.id.head_it_pos);
        head_logistics_pos = view.findViewById(R.id.head_logistics_pos);
        chief_pos.setBackgroundColor(Color.RED);

        if (position_ref != 26){

            chief_coordinator_listview.setVisibility(View.GONE);

        }

        getTotalVoter();
        getAllCandidateList(1);
        chiefCoordinatorCandidateAdapter = new ChiefCoordinatorCandidateAdapter(getActivity(), chiefCoordinatorCandidateLists);
        chief_coordinator_listview.setAdapter(chiefCoordinatorCandidateAdapter);

        chief_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chief_pos.setBackgroundColor(Color.RED);
                head_it_pos.setBackgroundColor(Color.TRANSPARENT);
                head_logistics_pos.setBackgroundColor(Color.TRANSPARENT);

                getAllCandidateList(1);
                getVoteCast(1);
                chiefCoordinatorCandidateLists.clear();
                chiefCoordinatorCandidateAdapter = new ChiefCoordinatorCandidateAdapter(getActivity(), chiefCoordinatorCandidateLists);
                chief_coordinator_listview.setAdapter(chiefCoordinatorCandidateAdapter);

            }
        });

        head_it_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chief_pos.setBackgroundColor(Color.TRANSPARENT);
                head_it_pos.setBackgroundColor(Color.RED);
                head_logistics_pos.setBackgroundColor(Color.TRANSPARENT);

                getAllCandidateList(2);
                getVoteCast(2);
                chiefCoordinatorCandidateLists.clear();
                chiefCoordinatorCandidateAdapter = new ChiefCoordinatorCandidateAdapter(getActivity(), chiefCoordinatorCandidateLists);
                chief_coordinator_listview.setAdapter(chiefCoordinatorCandidateAdapter);

            }
        });

        head_logistics_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chief_pos.setBackgroundColor(Color.TRANSPARENT);
                head_it_pos.setBackgroundColor(Color.TRANSPARENT);
                head_logistics_pos.setBackgroundColor(Color.RED);

                getAllCandidateList(3);
                getVoteCast(3);
                chiefCoordinatorCandidateLists.clear();
                chiefCoordinatorCandidateAdapter = new ChiefCoordinatorCandidateAdapter(getActivity(), chiefCoordinatorCandidateLists);
                chief_coordinator_listview.setAdapter(chiefCoordinatorCandidateAdapter);

            }
        });

        return view;
    }

    private void getTotalVoter() {

        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getTotalVoter.php?election_ref="+election_ref;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                System.out.println("Total Voter = "+response);

                try {

                    JSONArray obj = new JSONArray(response);
                    for (int i=0;i<obj.length();i++){

                        JSONObject jsonObject = obj.getJSONObject(i);
                        total_voter = Integer.parseInt(jsonObject.getString("total_voter"));
                        total_voter_chief.setText(String.valueOf(total_voter));
                        getVoteCast(1);

                    }
                }
                catch (JSONException e){
                    Log.e("getAllChiefCoordinatorCandidate",response);
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

    private void getVoteCast(int election_position_ref) {

        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getVoteCast.php?election_ref="+election_ref+"&election_position_ref="+election_position_ref;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                System.out.println("Vote Cast = "+response);

                try {

                    JSONArray obj = new JSONArray(response);
                    for (int i=0;i<obj.length();i++){

                        JSONObject jsonObject = obj.getJSONObject(i);
                        vote_cast = Integer.parseInt(jsonObject.getString("vote_cast"));
                        vote_cast_chief.setText(String.valueOf(vote_cast));

                        rest_voter = total_voter - vote_cast;
                        rest_voter_chief.setText(String.valueOf(rest_voter));

                    }
                }
                catch (JSONException e){
                    Log.e("getAllChiefCoordinatorCandidate",response);
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


    private void getAllCandidateList(int election_position_ref) {
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getAllCandidateList.php?election_ref="+election_ref+"&election_position_ref="+election_position_ref;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                System.out.println(response);

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
                        String vote_count = jsonObject.getString("vote_count");
                        String symbol = photoUrl + jsonObject.getString("symbol");

                        chiefCoordinatorCandidate_list_class = new ChiefCoordinatorCandidateList(id, election_ref, election_position_ref, user_ref, division_ref, district_ref, upazila_ref, position_ref,full_name, profile_photo, vote_count, symbol);
                        chiefCoordinatorCandidateLists.add(chiefCoordinatorCandidate_list_class);
                        chiefCoordinatorCandidateAdapter.notifyDataSetChanged();

                    }
                }
                catch (JSONException e){
                    Log.e("getAllChiefCoordinatorCandidate",response);
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

    private class ChiefCoordinatorCandidateList{

        String id, election_ref, election_position_ref, user_ref, division_ref, district_ref, upazila_ref, position_ref,full_name, profile_photo, vote_count, symbol;

        public ChiefCoordinatorCandidateList(String id, String election_ref, String election_position_ref, String user_ref, String division_ref, String district_ref, String upazila_ref, String position_ref, String full_name, String profile_photo, String vote_count, String symbol) {
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
            this.vote_count = vote_count;
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

        public String getVote_count() {
            return vote_count;
        }

        public void setVote_count(String vote_count) {
            this.vote_count = vote_count;
        }
    }

    private class ChiefCoordinatorCandidateAdapter extends BaseAdapter{

        Context context;
        ArrayList<ChiefCoordinatorCandidateList> chiefCoordinatorCandidateLists;

        public ChiefCoordinatorCandidateAdapter(Context context, ArrayList<ChiefCoordinatorCandidateList> chiefCoordinatorCandidateLists) {
            this.context = context;
            this.chiefCoordinatorCandidateLists = chiefCoordinatorCandidateLists;
        }

        @Override
        public int getCount() {
            return chiefCoordinatorCandidateLists.size();
        }

        @Override
        public Object getItem(int i) {
            return chiefCoordinatorCandidateLists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return chiefCoordinatorCandidateLists.indexOf(i);
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {

            @SuppressLint("ViewHolder") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.candidate_list_layout,null,true);
            ImageView candidate_photo = view.findViewById(R.id.candidate_photo);
            ImageView candidate_symbol = view.findViewById(R.id.candidate_symbol);
            TextView candidate_name = view.findViewById(R.id.candidate_name);
            TextView total_vote = view.findViewById(R.id.total_vote);

            Picasso.get().load(chiefCoordinatorCandidateLists.get(i).getProfile_photo()).into(candidate_photo);
            Picasso.get().load(chiefCoordinatorCandidateLists.get(i).getSymbol()).into(candidate_symbol);
            candidate_name.setText(chiefCoordinatorCandidateLists.get(i).getFull_name());
            total_vote.setText(chiefCoordinatorCandidateLists.get(i).getVote_count());

//            if (i == selectedPosition) {
//                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
//            } else {
//                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bdclean_green));
//            }

            return view;
        }

//        public void setSelectedPosition(int position) {
//            selectedPosition = position;
//            notifyDataSetChanged();
//        }
//
//        public void clearSelection() {
//            selectedPosition = -1; // Set the selected position to an invalid value
//            notifyDataSetChanged();
//        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister the BroadcastReceiver when the Fragment is destroyed
        requireActivity().unregisterReceiver(networkChangeListener);
    }

}