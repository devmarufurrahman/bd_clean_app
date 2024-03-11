package com.winkytech.bdclean;

public class ComplainList {

    String subject, send_from,send_to,send_for,date,body,id, status, comment, complain_by;

    public ComplainList(String subject, String send_from, String send_to, String send_for, String date, String body,String id, String status,String comment, String complain_by) {
        this.subject = subject;
        this.send_from = send_from;
        this.send_to = send_to;
        this.send_for = send_for;
        this.date = date;
        this.body = body;
        this.id = id;
        this.status = status;
        this.comment = comment;
        this.complain_by = complain_by;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSend_from() {
        return send_from;
    }

    public void setSend_from(String send_from) {
        this.send_from = send_from;
    }

    public String getSend_to() {
        return send_to;
    }

    public void setSend_to(String send_to) {
        this.send_to = send_to;
    }

    public String getSend_for() {
        return send_for;
    }

    public void setSend_for(String send_for) {
        this.send_for = send_for;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComplain_by() {
        return complain_by;
    }

    public void setComplain_by(String complain_by) {
        this.complain_by = complain_by;
    }
}
