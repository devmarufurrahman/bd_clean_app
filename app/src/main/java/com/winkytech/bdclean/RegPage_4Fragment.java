package com.winkytech.bdclean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RegPage_4Fragment extends Fragment {

    FusedLocationProviderClient fusedLocationProviderClient;
    Button next,cancel,back;
    Spinner select_division,select_district,select_upazila,select_union,select_village;
    int district_ref,division_ref,upazila_ref,union_ref,village_ref;
    List<String> division, district,upazila,union,village;
    String name,email,contact,address;
    int gender_ref,religion_ref,occupation_ref;
    ProgressBar progressBar;

    Context context;

    public JSONArray division_result,district_result,upazila_result,union_result,village_result;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reg_page_4, container, false);

        Bundle bundle = getArguments();
        name= bundle.getString("name");
        email= bundle.getString("email");
        contact= bundle.getString("contact");
        address= bundle.getString("address");
        gender_ref= bundle.getInt("gender_ref");
        religion_ref= bundle.getInt("religion_ref");
        occupation_ref= bundle.getInt("occupation_ref");

        context = getContext();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        select_division=view.findViewById(R.id.division_spinner);
        next=view.findViewById(R.id.reg_next_btn);
        cancel=view.findViewById(R.id.reg_cancel_btn);
        back=view.findViewById(R.id.reg_back_btn);
        select_district=view.findViewById(R.id.district_spinner);
        select_upazila=view.findViewById(R.id.upazilla_spinner);
        select_union=view.findViewById(R.id.union_spinner);
        select_village=view.findViewById(R.id.village_spinner);
        progressBar=view.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        division=new ArrayList<String>();
        district=new ArrayList<String>();
        upazila=new ArrayList<String>();
        union=new ArrayList<String>();
        village=new ArrayList<String>();

        getDivisionData();
        select_division.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                getDivisionRef(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        select_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getDistrictRef(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        select_upazila.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                getUpazilaRef(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        select_union.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                getUnionRef(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        select_village.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                getVillageRef(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goToFinalFragment();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),LoginActivity.class);
                getActivity().overridePendingTransition(R.anim.push_right_out,R.anim.no_anim);
                startActivity(intent);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("name",name);
                bundle.putString("email",email);
                bundle.putString("contact",contact);
                bundle.putString("address",address);

                RegPage_2Fragment regPage_2Fragment = new RegPage_2Fragment();
                regPage_2Fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.push_right_out,R.anim.no_anim);
                transaction.replace(R.id.registration_container,regPage_2Fragment).commit();

            }
        });

        return view;
    }

    private void getVillageRef(int position) {

        try {
            JSONObject jsonObject = village_result.getJSONObject(position);
            village_ref = Integer.parseInt(jsonObject.getString("id"));
            System.out.println(village_ref);

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getUnionRef(int position) {

        try {
            JSONObject jsonObject = union_result.getJSONObject(position);
            union_ref = Integer.parseInt(jsonObject.getString("id"));
            System.out.println(union_ref);
            village.clear();
            getVillageData(union_ref);

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getVillageData(int union_ref) {

        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getVillageData.php?union_ref="+union_ref;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showVillageJSONS(response);
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                Toast.makeText(getContext(), volleyError, Toast.LENGTH_LONG).show();
//                Toast toast = new Toast(getApplicationContext());
//                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_layout_2,findViewById(R.id.custom_toast));
//                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
//                toast_message.setText(volleyError +",  Failed To Get User information");
//                toast.setView(toast_view);
//                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
//                toast.setDuration(Toast.LENGTH_SHORT);
//                toast.show();
//                progressBar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    private void showVillageJSONS(String response) {
        String name="";

        System.out.println(response);

        try {

            JSONArray obj = new JSONArray(response);

            village_result = obj;

            for (int i=0;i<obj.length();i++){
                JSONObject jsonObject = obj.getJSONObject(i);
                name=jsonObject.getString("name");
                village.add(name);
            }

        }
        catch (JSONException e){
            Log.e("anyText",response);
            e.printStackTrace();
        }

        select_village.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,village));

    }

    private void getUpazilaRef(int position) {

        try {
            JSONObject jsonObject = upazila_result.getJSONObject(position);
            upazila_ref = Integer.parseInt(jsonObject.getString("id"));
            System.out.println(upazila_ref);
            union.clear();
            getUnionData(upazila_ref);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getUnionData(int upazila_ref) {

        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getUnionData.php?upazila_ref="+upazila_ref;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showUnionJSONS(response);
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                Toast.makeText(getContext(), volleyError, Toast.LENGTH_LONG).show();
//                Toast toast = new Toast(getApplicationContext());
//                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_layout_2,findViewById(R.id.custom_toast));
//                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
//                toast_message.setText(volleyError +",  Failed To Get User information");
//                toast.setView(toast_view);
//                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
//                toast.setDuration(Toast.LENGTH_SHORT);
//                toast.show();
//                progressBar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void showUnionJSONS(String response) {
        String name="";

        System.out.println(response);

        try {

            JSONArray obj = new JSONArray(response);

            union_result = obj;

            for (int i=0;i<obj.length();i++){
                JSONObject jsonObject = obj.getJSONObject(i);
                name=jsonObject.getString("name");
                union.add(name);
            }

        }
        catch (JSONException e){
            Log.e("anyText",response);
            e.printStackTrace();
        }

        select_union.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,union));

    }

    private void getUpaziladata(int district_ref) {
        progressBar.setVisibility(View.GONE);

        String url = "https://bdclean.winkytech.com/backend/api/getUpazilaProfile.php?district_ref="+district_ref;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showUpazilaJSONS(response);
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                Toast.makeText(getContext(), volleyError, Toast.LENGTH_LONG).show();
//                Toast toast = new Toast(getApplicationContext());
//                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_layout_2,findViewById(R.id.custom_toast));
//                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
//                toast_message.setText(volleyError +",  Failed To Get User information");
//                toast.setView(toast_view);
//                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
//                toast.setDuration(Toast.LENGTH_SHORT);
//                toast.show();
//                progressBar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);


    }

    private void showUpazilaJSONS(String response) {
        String name="";

        System.out.println(response);

        try {

            JSONArray obj = new JSONArray(response);

            upazila_result = obj;

            for (int i=0;i<obj.length();i++){
                JSONObject jsonObject = obj.getJSONObject(i);
                name=jsonObject.getString("name");
                upazila.add(name);
            }


        }
        catch (JSONException e){
            Log.e("anyText",response);
            e.printStackTrace();
        }

        select_upazila.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item,upazila));

    }

    private void getDistrictRef(int position) {

        try {
            JSONObject jsonObject = district_result.getJSONObject(position);
            district_ref = Integer.parseInt(jsonObject.getString("id"));
            upazila.clear();
            getUpaziladata(district_ref);
            //System.out.println(district_ref);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getDistrictData(int division_ref) {

        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bdclean.winkytech.com/backend/api/getDistrictProfile.php?division_ref="+division_ref;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showDistrictJSONS(response);
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.i("VolleyError",error.getMessage());
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                String volleyError = "";

                if (error instanceof NetworkError){
                    volleyError="Network Error";
                } else if (error instanceof ServerError){

                    volleyError="Server Connection error";
                }

                Toast.makeText(getContext(), volleyError, Toast.LENGTH_LONG).show();
//                Toast toast = new Toast(getApplicationContext());
//                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_layout_2,findViewById(R.id.custom_toast));
//                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
//                toast_message.setText(volleyError +",  Failed To Get User information");
//                toast.setView(toast_view);
//                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
//                toast.setDuration(Toast.LENGTH_SHORT);
//                toast.show();
//                progressBar.setVisibility(View.GONE);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    private void showDistrictJSONS(String response) {

        String name="";

        System.out.println(response);

        try {

            JSONArray obj = new JSONArray(response);

            district_result = obj;

            for (int i=0;i<obj.length();i++){
                JSONObject jsonObject = obj.getJSONObject(i);
                name=jsonObject.getString("name");
                district.add(name);
            }
        }
        catch (JSONException e){
            Log.e("anyText",response);
            e.printStackTrace();
        }

        select_district.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item,district));

    }

    private void getDivisionRef(int position) {

        try {
            JSONObject jsonObject = division_result.getJSONObject(position);
            division_ref = Integer.parseInt(jsonObject.getString("id"));
            //System.out.println(division_ref);
            district.clear();
            getDistrictData(division_ref);

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private void goToFinalFragment() {

        Bundle bundle = new Bundle();
        bundle.putString("name",name);
        bundle.putString("email",email);
        bundle.putString("contact",contact);
        bundle.putString("address",address);
        bundle.putInt("gender_ref",gender_ref);
        bundle.putInt("occupation_ref",occupation_ref);
        bundle.putInt("religion_ref",religion_ref);
        bundle.putInt("district_ref", district_ref);
        bundle.putInt("division_ref", division_ref);
        bundle.putInt("upazila_ref", upazila_ref);
        bundle.putInt("union_ref", union_ref);
        bundle.putInt("village_ref", village_ref);

        RegPage_3Fragment regPage_3Fragment = new RegPage_3Fragment();
        regPage_3Fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.push_left_in,R.anim.fade_out);
        transaction.replace(R.id.registration_container,regPage_3Fragment).commit();

    }


    private void getDivisionData() {
        progressBar.setVisibility(View.VISIBLE);
            String url = "https://bdclean.winkytech.com/backend/api/getDivisionProfile.php";
            StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    showDivisionJSONS(response);
                    progressBar.setVisibility(View.GONE);

                }
            }, new Response.ErrorListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    Log.i("VolleyError",error.getMessage());
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    String volleyError = "";

                    if (error instanceof NetworkError){
                        volleyError="Network Error";
                    } else if (error instanceof ServerError){

                        volleyError="Server Connection error";
                    }

                    Toast.makeText(getContext(), volleyError, Toast.LENGTH_LONG).show();
//                Toast toast = new Toast(getApplicationContext());
//                View toast_view = getLayoutInflater().inflate(R.layout.custom_toast_layout_2,findViewById(R.id.custom_toast));
//                toast_message=toast_view.findViewById(R.id.custom_toast_tv);
//                toast_message.setText(volleyError +",  Failed To Get User information");
//                toast.setView(toast_view);
//                toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,110);
//                toast.setDuration(Toast.LENGTH_SHORT);
//                toast.show();
//                progressBar.setVisibility(View.GONE);
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);


    }

    private void showDivisionJSONS(String response) {

        String name="";

        System.out.println(response);

        try {

            JSONArray obj = new JSONArray(response);
            division_result = obj;
            for (int i=0;i<obj.length();i++){
                JSONObject jsonObject = obj.getJSONObject(i);
                name=jsonObject.getString("name");
                division.add(name);
            }
        }
        catch (JSONException e){
            Log.e("anyText",response);
            e.printStackTrace();
        }
        select_division.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item,division));
    }

}