package com.winkytech.bdclean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends BaseAdapter {

    Context context;
    ArrayList<ChatList> chatLists;
    String user_id;

    String photoUrl = "https://bdclean.winkytech.com/resources/user/profile_pic/";

    public ChatListAdapter(Context context, ArrayList<ChatList> chatLists, String user_id) {
        this.context = context;
        this.chatLists = chatLists;
        this.user_id = user_id;
    }

    @Override
    public int getCount() {
        return chatLists.size();
    }

    @Override
    public Object getItem(int position) {
        return chatLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return chatLists.indexOf(position);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        LayoutInflater vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if((chatLists.get(position).getId().equals(user_id))){
            // even display the image on the right.
            view = vi.inflate(R.layout.message_row_left, null);
        }else{
            // odd display the image on the left.
            view = vi.inflate(R.layout.message_row_right, null);
        }

        TextView message = view.findViewById(R.id.message);
        TextView receiver = view.findViewById(R.id.receiver);
        TextView designation = view.findViewById(R.id.designation);
        TextView date_view = view.findViewById(R.id.date);
        String date = chatLists.get(position).getDate();
        ImageView imgView = view.findViewById(R.id.chat_profile_img);

        @SuppressLint("SimpleDateFormat") Date date_format = null;
        try {
            date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String pattern = "E : dd MMM yyyy, ( hh:mm: a)";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String final_date = simpleDateFormat.format(date_format);
        //String profileImgUrl = photoUrl+ chatLists.get(position).getProfileImg();
        String profileImgUrl = String.valueOf(chatLists.get(position).getProfileImg());

        message.setText(chatLists.get(position).getMessage());
        receiver.setText(chatLists.get(position).getReceiver());
        designation.setText(chatLists.get(position).getDesignation());
        date_view.setText(final_date);

        int targetWidth = 200; // Specify your target width
        int targetHeight = 200; // Specify your target height

//        Picasso.get().load(memberList.get(position).getPhoto()).resize(targetWidth, targetHeight).into(photo);

        if (profileImgUrl.equals(null)){
            imgView.setImageResource(R.drawable.icon_bdclean);
        } else {
            Picasso.get()
                    .load(profileImgUrl)
                    .resize(targetWidth, targetHeight)
                    .placeholder(R.drawable.error_image) // Set a placeholder image
                    .error(R.drawable.error_image) // Set an error image if loading fails
                    .into(imgView);
            //Picasso.get().load(profileImgUrl).into(imgView);
        }



        return view;
    }
}
