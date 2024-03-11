package com.winkytech.bdclean;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class NetworkChangeListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!NetworkCheckingClass.isInternetEnabled(context)) {
            // Internet or location is disabled, show warning dialog
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_warning_dialog_layout);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(true);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
            dialog.show();
            Button ok = dialog.findViewById(R.id.ok_btn);
            TextView warning_message = dialog.findViewById(R.id.custom_toast_tv);
            warning_message.setText("Internet connection is turned off. Please turn on internet connection");
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        } else if (!NetworkCheckingClass.isLocationEnabled(context)){

            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_warning_dialog_layout);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(true);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
            dialog.show();
            Button ok = dialog.findViewById(R.id.ok_btn);
            TextView warning_message = dialog.findViewById(R.id.custom_toast_tv);
            warning_message.setText("Location service is turned off. Please turn on device location service and try again");
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }
}
