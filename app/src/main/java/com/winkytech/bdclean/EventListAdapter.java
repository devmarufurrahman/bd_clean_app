package com.winkytech.bdclean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;

public class EventListAdapter extends ArrayAdapter<EventList> {

    Context context;
    List<EventList> eventLists;

    public  EventListAdapter(Context context, List<EventList> eventLists){
        super(context, R.layout.event_list_layout,eventLists);
        this.context = context;
        this.eventLists = eventLists;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        @SuppressLint({"ViewHolder", "InflateParams"}) View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_layout,null,true);

        String status = eventLists.get(position).getStatus();

        TextView event_name = view.findViewById(R.id.event_name);
        TextView start_date = view.findViewById(R.id.start_date);
//        TextView end_date = view.findViewById(R.id.end_date);
        TextView event_location = view.findViewById(R.id.location);
        TextView type = view.findViewById(R.id.event_type);
        ImageView cover_photo = view.findViewById(R.id.cover_photo);
        TextView event_status = view.findViewById(R.id.status);


//        switch (status){
//            case "Approved" :
//
//            case "ONGOING" :
//                event_status.setTextColor(ColorStateList.valueOf(Color.parseColor("#00A651")));
//                event_status.setBackgroundResource(R.drawable.layout_background);
//                break;
//            case "Rejected" :
//                event_status.setTextColor(ColorStateList.valueOf(Color.parseColor("#ff5252")));
//                event_status.setBackgroundResource(R.drawable.layout_background_2);
//                break;
//
//            case "Pending Approval" :
//
//            case "UPCOMING" :
//                event_status.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffa700")));
//                event_status.setBackgroundResource(R.drawable.layout_background_3);
//                break;
//        }

        event_name.setText(eventLists.get(position).getName());
        start_date.setText(eventLists.get(position).getStart_date());
        //+ " To "+eventLists.get(position).getEnd_date());
//        end_date.setText(": "+eventLists.get(position).getEnd_date());
        event_location.setText(eventLists.get(position).getUpazilla() + ", " + eventLists.get(position).getDistrict() + ", " + eventLists.get(position).getLocation());
        type.setText(eventLists.get(position).getType());
        Picasso.get().load(eventLists.get(position).getPhoto()).into(cover_photo);
        event_status.setText(eventLists.get(position).getStatus());

        return view;
    }

}
