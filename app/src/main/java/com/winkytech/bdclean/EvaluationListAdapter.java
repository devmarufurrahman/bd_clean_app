package com.winkytech.bdclean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EvaluationListAdapter extends BaseAdapter {

    Context context;
    ArrayList<EvaluationList> evaluationLists;

    public EvaluationListAdapter(Context context, ArrayList<EvaluationList> evaluationLists) {
        this.context = context;
        this.evaluationLists = evaluationLists;
    }

    @Override
    public int getCount() {
        return evaluationLists.size();
    }

    @Override
    public Object getItem(int position) {
        return evaluationLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return evaluationLists.indexOf(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.evaluation_list_layout,null,true);

        String eva_status = evaluationLists.get(position).getStatus();

        TextView subject = view.findViewById(R.id.ev_subject);
        TextView send_from = view.findViewById(R.id.ev_from);
        TextView send_to = view.findViewById(R.id.ev_to);
        TextView send_for = view.findViewById(R.id.ev_for);
        TextView date = view.findViewById(R.id.ev_date);
        TextView status = view.findViewById(R.id.ev_status);

        switch (eva_status){
            case "Approved" :
                status.setTextColor(ColorStateList.valueOf(Color.parseColor("#009748")));

                break;
            case "Rejected" :
                status.setTextColor(ColorStateList.valueOf(Color.parseColor("#C63235")));

                break;

            case "Pending Approval" :
                status.setTextColor(ColorStateList.valueOf(Color.parseColor("#E88655")));

                break;
        }

        subject.setText(evaluationLists.get(position).getSubject());
        send_from.setText(evaluationLists.get(position).getSend_from());
        send_to.setText(evaluationLists.get(position).getSend_to());
        send_for.setText(evaluationLists.get(position).getSend_for());
        date.setText(evaluationLists.get(position).getDate());
        status.setText(evaluationLists.get(position).getStatus());

        return view;
    }
}
