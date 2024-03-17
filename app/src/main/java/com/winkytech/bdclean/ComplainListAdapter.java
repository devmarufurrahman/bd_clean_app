package com.winkytech.bdclean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ComplainListAdapter extends ArrayAdapter<ComplainList> {

    Context context;
    List<ComplainList> complainLists;

    public ComplainListAdapter(Context context, List<ComplainList> complainLists){
        super(context,R.layout.complain_list_layout,complainLists);
        this.complainLists=complainLists;
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        @SuppressLint("ViewHolder") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.complain_list_layout,null,true);

        String eva_status = complainLists.get(position).getStatus();

        TextView subject = view.findViewById(R.id.cm_subject);
        TextView send_from = view.findViewById(R.id.cm_from);
        TextView send_to = view.findViewById(R.id.cm_to);
        TextView send_for = view.findViewById(R.id.cm_for);
        TextView date = view.findViewById(R.id.cm_date);
        TextView status = view.findViewById(R.id.cm_status);

        switch (eva_status){
            case "Approved" :
                status.setTextColor(ColorStateList.valueOf(Color.parseColor("#009748")));

                break;
            case "Rejected" :
                status.setTextColor(ColorStateList.valueOf(Color.parseColor("#C63235")));

                break;

            case "Pending" :
                status.setTextColor(ColorStateList.valueOf(Color.parseColor("#E88655")));

                break;
        }

        subject.setText(complainLists.get(position).getSubject());
        send_from.setText(complainLists.get(position).getSend_from());
        send_to.setText(complainLists.get(position).getSend_to());
        send_for.setText(complainLists.get(position).getSend_for());
        date.setText(complainLists.get(position).getDate());
        status.setText(complainLists.get(position).getStatus());
        return view;
    }
}
