package com.igenesys.model;

public class MemberInfoDetailsModel {

    String member_name, marital_status, gender, relationship, aadhar_no, pan_no,
            religion, education, occupation, type_of_work, place_of_work, from_state,
            mother_tongue, rent_transit;
    Object rel_globalid;
    long hoh_living_from;
    short age;

    public MemberInfoDetailsModel(String member_name, String relationship, String aadhar_no, String pan_no, String gender, String religion, String from_state,
                                  String mother_tongue, String marital_status, String education, String occupation, String type_of_work, String place_of_work,
                                  String rent_transit, Object rel_globalid, long hoh_living_from, short age) {
        this.member_name = member_name;
        this.marital_status = marital_status;
        this.gender = gender;
        this.relationship = relationship;
        this.aadhar_no = aadhar_no;
        this.pan_no = pan_no;
        this.religion = religion;
        this.education = education;
        this.occupation = occupation;
        this.type_of_work = type_of_work;
        this.place_of_work = place_of_work;
        this.from_state = from_state;
        this.mother_tongue = mother_tongue;
        this.rent_transit = rent_transit;
        this.rel_globalid = rel_globalid;
        this.hoh_living_from = hoh_living_from;
        this.age = age;
    }

    public String getPlace_of_work() {
        return place_of_work;
    }

    public void setPlace_of_work(String place_of_work) {
        this.place_of_work = place_of_work;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String getMarital_status() {
        return marital_status;
    }

    public void setMarital_status(String marital_status) {
        this.marital_status = marital_status;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getAadhar_no() {
        return aadhar_no;
    }

    public void setAadhar_no(String aadhar_no) {
        this.aadhar_no = aadhar_no;
    }

    public String getPan_no() {
        return pan_no;
    }

    public void setPan_no(String pan_no) {
        this.pan_no = pan_no;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getType_of_work() {
        return type_of_work;
    }

    public void setType_of_work(String type_of_work) {
        this.type_of_work = type_of_work;
    }

    public String getFrom_state() {
        return from_state;
    }

    public void setFrom_state(String from_state) {
        this.from_state = from_state;
    }

    public String getMother_tongue() {
        return mother_tongue;
    }

    public void setMother_tongue(String mother_tongue) {
        this.mother_tongue = mother_tongue;
    }

    public String getRent_transit() {
        return rent_transit;
    }

    public void setRent_transit(String rent_transit) {
        this.rent_transit = rent_transit;
    }

    public Object getRel_globalid() {
        return rel_globalid;
    }

    public void setRel_globalid(Object rel_globalid) {
        this.rel_globalid = rel_globalid;
    }

    public long getHoh_living_from() {
        return hoh_living_from;
    }

    public void setHoh_living_from(long hoh_living_from) {
        this.hoh_living_from = hoh_living_from;
    }

    public short getAge() {
        return age;
    }

    public void setAge(short age) {
        this.age = age;
    }
}
