package com.winkytech.bdclean;

public class MemberList {

    String id,name,position,photo, division, district, upazila, union, village, member_code;

    public MemberList(String id,String name, String position,String photo, String division, String district, String upazila, String union, String village, String member_code) {
        this.id= id;
        this.name = name;
        this.position = position;
        this.photo = photo;
        this.division = division;
        this.district = district;
        this.upazila = upazila;
        this.union = union;
        this.village = village;
        this.member_code = member_code;

    }

//    public MemberList(String id ,String name, String photo, String position) {
//        this.id = id;
//        this.name = name;
//        this.photo = photo;
//        this.position = position;
//
//    }


    public String getMember_code() {
        return member_code;
    }

    public void setMember_code(String member_code) {
        this.member_code = member_code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getUpazila() {
        return upazila;
    }

    public void setUpazila(String upazila) {
        this.upazila = upazila;
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
}
