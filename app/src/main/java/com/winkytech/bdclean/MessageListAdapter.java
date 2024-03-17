package com.winkytech.bdclean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageListAdapter extends BaseAdapter {

    Context context;
    ArrayList<MessageList> messageLists;
    String user_id;

    public MessageListAdapter(Context context, ArrayList<MessageList> messageLists,String user_id) {
        this.context = context;
        this.messageLists = messageLists;
        this.user_id = user_id;
    }

    @Override
    public int getCount() {
        return messageLists.size();
    }

    @Override
    public Object getItem(int position) {
        return messageLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return messageLists.indexOf(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        @SuppressLint("ViewHolder") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_left.xml,null,true);

        View view;
        LayoutInflater vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if((messageLists.get(position).getId().equals(user_id))){
            // even display the image on the right.
            view = vi.inflate(R.layout.message_list_left, null);
        }else{
            // odd display the image on the left.
            view = vi.inflate(R.layout.message_list_right, null);

        }

        TextView message = view.findViewById(R.id.message_body);
        TextView receiver = view.findViewById(R.id.message_receiver);
        message.setText(messageLists.get(position).getMessage());
        receiver.setText(messageLists.get(position).getReceiver());

        return view;
    }
}
