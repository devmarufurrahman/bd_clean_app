package com.winkytech.bdclean;

public class MessageList {

    String  message,receiver,id;

    public MessageList( String message, String receiver,String id) {
        this.message = message;
        this.receiver = receiver;
        this.id = id;
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
