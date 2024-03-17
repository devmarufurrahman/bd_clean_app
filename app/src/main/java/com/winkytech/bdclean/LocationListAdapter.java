package com.winkytech.bdclean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LocationListAdapter extends BaseAdapter {

    Context context;
    ArrayList<LocationList> locationLists = new ArrayList<>();

    public LocationListAdapter(Context context, ArrayList<LocationList> locationLists) {
        this.context = context;
        this.locationLists = locationLists;
    }

    @Override
    public int getCount() {
        return locationLists.size();
    }

    @Override
    public Object getItem(int position) {
        return locationLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return locationLists.indexOf(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        @SuppressLint("ViewHolder") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list_layout,null,true);

        TextView name = view.findViewById(R.id.location_name);

        name.setText(locationLists.get(position).getName());

        return view;
    }
}
