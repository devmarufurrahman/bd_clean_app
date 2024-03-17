package com.winkytech.bdclean;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("SetTextI18n")
public class MemberListAdapter extends BaseAdapter implements Filterable {

    MemberFilter memberFilter;

    Context context;
    ArrayList<MemberList> memberList;
    ArrayList<MemberList> filteredList;

    int memberCount;

    public MemberListAdapter(@NonNull Context context, ArrayList<MemberList> memberList, int memberCount) {

        this.context=context;
        this.memberList=memberList;
        this.filteredList = memberList;
        this.memberCount = memberCount;
    }

    @Override
    public int getCount() {
        return memberList.size();
    }

    @Override
    public Object getItem(int position) {
        return memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return memberList.indexOf(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        @SuppressLint("ViewHolder") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_list_layout, null, true);

        TextView member_name = view.findViewById(R.id.member_name);
        TextView member_position = view.findViewById(R.id.member_position);
        ImageView photo = view.findViewById(R.id.member_img);
        TextView user_id = view.findViewById(R.id.user_id);
        TextView address = view.findViewById(R.id.user_address);


        member_name.setText(memberList.get(position).getName());
        member_position.setText(memberList.get(position).getPosition());

        if (memberList.get(position).getMember_code().equals("")){
            user_id.setText("BDC-M");
        } else {
            user_id.setText(memberList.get(position).getMember_code());
        }


        if (memberList.get(position).getVillage().equals("null") && !memberList.get(position).getUnion().equals("null") && !memberList.get(position).getUpazila().equals("null") && !memberList.get(position).getDistrict().equals("null") && !memberList.get(position).getDivision().equals("null")) {
            address.setText(memberList.get(position).getUnion()+ " , "+memberList.get(position).getUpazila()+ " , "+memberList.get(position).getDistrict()+ " , "+memberList.get(position).getDivision());
        } else if (memberList.get(position).getUnion().equals("null") && !memberList.get(position).getUpazila().equals("null") && !memberList.get(position).getDistrict().equals("null") && !memberList.get(position).getDivision().equals("null")) {
            address.setText(memberList.get(position).getUpazila()+ " , "+memberList.get(position).getDistrict()+ " , "+memberList.get(position).getDivision());
        } else if (memberList.get(position).getUpazila().equals("null") && !memberList.get(position).getDistrict().equals("null") && !memberList.get(position).getDivision().equals("null")) {
            address.setText(memberList.get(position).getDistrict()+ " , "+memberList.get(position).getDivision());
        } else if (memberList.get(position).getDistrict().equals("null") && !memberList.get(position).getDivision().equals("null")) {
            address.setText(memberList.get(position).getDivision());
        } else if (memberList.get(position).getVillage().equals("null") && memberList.get(position).getUnion().equals("null") && memberList.get(position).getUpazila().equals("null") && memberList.get(position).getDistrict().equals("null") && memberList.get(position).getDivision().equals("null")) {
            address.setVisibility(View.GONE);
        } else {
            address.setText(memberList.get(position).getVillage() + " , " +memberList.get(position).getUnion()+ " , "+memberList.get(position).getUpazila()+ " , "+memberList.get(position).getDistrict()+ " , "+memberList.get(position).getDivision());
        }


        int targetWidth = 100; // Specify your target width
        int targetHeight = 100; // Specify your target height
        Picasso.get()
                .load(memberList.get(position).getPhoto())
                .resize(targetWidth, targetHeight)
                .placeholder(R.drawable.error_image) // Set a placeholder image
                .error(R.drawable.error_image) // Set an error image if loading fails
                .into(photo);

        return view;
    }

    @Override
    public Filter getFilter() {

        if (memberFilter==null){

            memberFilter=new MemberFilter();

        }
        return memberFilter;
    }



    class MemberFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();


            if (constraint!=null && constraint.length()>0) {
                constraint = constraint.toString().toLowerCase();


                ArrayList<MemberList> filter = new ArrayList<>();
                for (int i=0;i<filteredList.size();i++) {

                    if (filteredList.get(i).getName().toLowerCase().contains(constraint)
                            || filteredList.get(i).getPosition().toLowerCase().contains(constraint)
                            || filteredList.get(i).getMember_code().toLowerCase().contains(constraint)
                            || filteredList.get(i).getDivision().toLowerCase().contains(constraint)
                            || filteredList.get(i).getDistrict().toLowerCase().contains(constraint)
                            || filteredList.get(i).getUpazila().toLowerCase().contains(constraint)
                            || filteredList.get(i).getUnion().toLowerCase().contains(constraint)
                            || filteredList.get(i).getVillage().toLowerCase().contains(constraint) ) {
                        MemberList memberList = new MemberList(filteredList.get(i).getId(),filteredList.get(i).getName(), filteredList.get(i).getPosition(), filteredList.get(i).getPhoto(),filteredList.get(i).getDivision(),filteredList.get(i).getDistrict(),filteredList.get(i).getUpazila(), filteredList.get(i).getUnion(), filteredList.get(i).getVillage(), filteredList.get(i).getMember_code());
                        filter.add(memberList);
                    }

                    filterResults.count = filter.size();
                    filterResults.values = filter;
                }
            } else {

                filterResults.count=filteredList.size();
                filterResults.values=filteredList;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            memberList=(ArrayList<MemberList>) results.values;
            notifyDataSetChanged();
            memberCount = memberList.size();

        }
    }

}
