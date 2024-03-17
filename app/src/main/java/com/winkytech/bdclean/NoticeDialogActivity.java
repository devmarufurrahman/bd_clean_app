package com.winkytech.bdclean;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NoticeDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_dialog);

        Intent intent = getIntent();

        String message = intent.getStringExtra("message");
        String date = intent.getStringExtra("date");
        String subject = intent.getStringExtra("subject");
        String sender_designation = intent.getStringExtra("sender_designation");
        String sender_name = intent.getStringExtra("sender_name");

        Dialog notice_view_dialog = new Dialog(NoticeDialogActivity.this);
        notice_view_dialog.setContentView(R.layout.notice_view_dialog_layout);
        notice_view_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        notice_view_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        notice_view_dialog.setCancelable(false);
        notice_view_dialog.show();

        TextView close_btn = notice_view_dialog.findViewById(R.id.close_btn);
        TextView subject_tv = notice_view_dialog.findViewById(R.id.notice_subject);
        TextView sender_nametv = notice_view_dialog.findViewById(R.id.notice_send_by);
        TextView notice_date = notice_view_dialog.findViewById(R.id.notice_date);
        TextView notice_details = notice_view_dialog.findViewById(R.id.notice_details);

        subject_tv.setText(subject);
        sender_nametv.setText(sender_name + " ( " + sender_designation +" )");
        notice_date.setText(date);
        notice_details.setText(message);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notice_view_dialog.dismiss();
                finish();
            }
        });

    }
}