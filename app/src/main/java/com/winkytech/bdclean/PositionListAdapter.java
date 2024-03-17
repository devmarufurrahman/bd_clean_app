package com.winkytech.bdclean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PositionListAdapter  extends BaseAdapter {

    Context context;
    ArrayList<PositionList> positionLists = new ArrayList<>();

    public PositionListAdapter(Context context, ArrayList<PositionList> positionLists) {
        this.context = context;
        this.positionLists = positionLists;
    }

    @Override
    public int getCount() {
        return positionLists.size();
    }

    @Override
    public Object getItem(int position) {

        return positionLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return positionLists.indexOf(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.position_list_layout,null);

        TextView position_name = view.findViewById(R.id.position_name);

        position_name.setText(positionLists.get(position).getName());

        return view;
    }
}
