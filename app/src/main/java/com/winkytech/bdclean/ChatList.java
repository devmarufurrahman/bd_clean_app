package com.winkytech.bdclean;

import android.net.Uri;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatList {

    String id, message, receiver, date, designation;
    Uri profileImg;



    public ChatList(String id, String message, String receiver, String date, Uri profileImg, String designation) {
        this.id = id;
        this.message = message;
        this.receiver = receiver;
        this.date = date;
        this.profileImg = profileImg;
        this.designation = designation;
    }

    public Uri getProfileImg() {
        return profileImg;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void setProfileImg(Uri profileImg) {
        this.profileImg = profileImg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
