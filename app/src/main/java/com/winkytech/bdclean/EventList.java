package com.winkytech.bdclean;

public class EventList {

    String id, name,photo,start_date,end_date,location,type,district,upazilla, status, supervisor, union, village, division, comment, details;
    public EventList(String id, String name, String photo, String start_date, String end_date, String location, String type, String district, String upazilla, String approve_status, String supervisor, String union, String village, String division, String comment) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.start_date = start_date;
        this.end_date = end_date;
        this.location = location;
        this.type = type;
        this.district = district;
        this.upazilla = upazilla;
        this.status = approve_status;
        this.supervisor = supervisor;
        this.union = union;
        this.village = village;
        this.division = division;
        this.comment = comment;
    }

    public EventList(String id, String name, String photo, String start_date, String end_date, String location, String type, String district, String upazilla, String status, String details) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.start_date = start_date;
        this.end_date = end_date;
        this.location = location;
        this.type = type;
        this.district = district;
        this.upazilla = upazilla;
        this.status = status;
        this.details = details;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getUnion() {
        return union;
    }

    public void setUnion(String union) {
        this.union = union;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getUpazilla() {
        return upazilla;
    }

    public void setUpazilla(String upazilla) {
        this.upazilla = upazilla;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
