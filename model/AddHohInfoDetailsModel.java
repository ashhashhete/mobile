package com.igenesys.model;

import java.util.GregorianCalendar;

public class AddHohInfoDetailsModel {

    String hoh_name, staying_year, aadhar_no, consumer_no, gender, religion, from_state, mother_tongue, marital_status, education, occupation, place_of_work, rent_transit;
    Object rel_globalid;
    GregorianCalendar hoh_living_from;
    short age;

    public AddHohInfoDetailsModel(String hoh_name, String staying_year, String aadhar_no, String consumer_no, String gender, String religion, String from_state,
                                  String mother_tongue, String marital_status, String education, String occupation, String place_of_work, String rent_transit,
                                  Object rel_globalid, GregorianCalendar hoh_living_from, short age) {
        this.hoh_name = hoh_name;
        this.staying_year = staying_year;
        this.aadhar_no = aadhar_no;
        this.consumer_no = consumer_no;
        this.gender = gender;
        this.religion = religion;
        this.from_state = from_state;
        this.mother_tongue = mother_tongue;
        this.marital_status = marital_status;
        this.education = education;
        this.occupation = occupation;
        this.place_of_work = place_of_work;
        this.rent_transit = rent_transit;
        this.rel_globalid = rel_globalid;
        this.hoh_living_from = hoh_living_from;
        this.age = age;
    }

    public String getHoh_name() {
        return hoh_name;
    }

    public void setHoh_name(String hoh_name) {
        this.hoh_name = hoh_name;
    }

    public String getStaying_year() {
        return staying_year;
    }

    public void setStaying_year(String staying_year) {
        this.staying_year = staying_year;
    }

    public String getAadhar_no() {
        return aadhar_no;
    }

    public void setAadhar_no(String aadhar_no) {
        this.aadhar_no = aadhar_no;
    }

    public String getConsumer_no() {
        return consumer_no;
    }

    public void setConsumer_no(String consumer_no) {
        this.consumer_no = consumer_no;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
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

    public String getMarital_status() {
        return marital_status;
    }

    public void setMarital_status(String marital_status) {
        this.marital_status = marital_status;
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

    public String getPlace_of_work() {
        return place_of_work;
    }

    public void setPlace_of_work(String place_of_work) {
        this.place_of_work = place_of_work;
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

    public GregorianCalendar getHoh_living_from() {
        return hoh_living_from;
    }

    public void setHoh_living_from(GregorianCalendar hoh_living_from) {
        this.hoh_living_from = hoh_living_from;
    }

    public short getAge() {
        return age;
    }

    public void setAge(short age) {
        this.age = age;
    }
}

