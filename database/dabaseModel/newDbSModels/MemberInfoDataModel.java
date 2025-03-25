package com.igenesys.database.dabaseModel.newDbSModels;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "memberInfoDataTable")
public class MemberInfoDataModel implements Serializable {


    @NonNull
    @PrimaryKey(autoGenerate = false)
    // variable for our id.
    private String member_id;
    private String memberSampleGlobalid;
    private String rel_globalid;
    private String relative_path;
    private String member_name;
    private String member_floor_no;
    private String member_hoh;
    // private String relationship;
    private String marital_status;
    private String marital_status_other;
    private String member_spouse_name;
    private String member_contact_no;
    private int age;
    private String gender;
    private String aadhar_no;
    private String pan_no;
    private String religion;
    private String religion_other;
    private String from_state;
    private String from_state_other;
    private String mother_tongue;
    private String mother_tongue_other;
    private String education;
    private String education_other;
    private String occupation;
    private String occupation_other;
    private String place_of_work;
    private String type_of_work;
    private String type_of_work_other;
    private String member_occupancy_type;
    private boolean aadhar_card;
    private boolean pan_card;
    private boolean photograph;
    private String surveyor_name;
    private String remarks;
    private int media_captured_cnt;
    private int media_uploaded_cnt;
    private String obejctId;
    private String globalId;
    private boolean isUploaded;
    private Date date;
    private Date lastEditedDate;
    //new
    private String relationship_with_hoh;
    private String relationship_with_hoh_other;
    private int member_spouse_count;
    private String member_dob;
    private int staying_since_year;
    private String ration_card_colour;
    private String ration_card_no;
    private String monthly_income;
    private String mode_of_transport;
    private String mode_of_transport_other;
    private String school_college_name_location;
    private String handicap_or_critical_disease;
    private String staying_with;
    private String vehicle_owned_driven;
    private String vehicle_owned_driven_other;
    private int death_certificate;

//HoH SampleGlobalid => member rel_globalid   <uppercase & { }>   change here new fields

    public MemberInfoDataModel(@NonNull String member_id, String memberSampleGlobalid,
                               String rel_globalid, String relative_path, String member_name,
                               String relationship_with_hoh, String relationship_with_hoh_other,
                               String marital_status, String marital_status_other,
                               int member_spouse_count, String member_spouse_name, String member_contact_no,
                               String member_dob, int age, String gender, int staying_since_year, String aadhar_no, String pan_no,
                               String ration_card_colour, String ration_card_no, String religion, String religion_other,
                               String from_state, String from_state_other, String mother_tongue, String mother_tongue_other,
                               String education, String education_other, String occupation, String occupation_other,
                               String place_of_work, String type_of_work, String type_of_work_other, String monthly_income,
                               String mode_of_transport, String mode_of_transport_other, String school_college_name_location,
                               String handicap_or_critical_disease, String staying_with,
                               String vehicle_owned_driven, String vehicle_owned_driven_other, String obejctId,
                               String globalId, boolean isUploaded, Date date, Date lastEditedDate, int death_certificate) {

        this.member_id = member_id;
        this.memberSampleGlobalid = memberSampleGlobalid;
        this.rel_globalid = rel_globalid;
        this.relative_path = relative_path;
        this.member_name = member_name;
        this.relationship_with_hoh = relationship_with_hoh;
        this.relationship_with_hoh_other = relationship_with_hoh_other;
        this.marital_status = marital_status;
        this.marital_status_other = marital_status_other;
        this.member_spouse_name = member_spouse_name;
        this.member_contact_no = member_contact_no;
        this.age = age;
        this.gender = gender;
        this.aadhar_no = aadhar_no;
        this.pan_no = pan_no;
        this.religion = religion;
        this.religion_other = religion_other;
        this.from_state = from_state;
        this.from_state_other = from_state_other;
        this.mother_tongue = mother_tongue;
        this.mother_tongue_other = mother_tongue_other;
        this.education = education;
        this.education_other = education_other;
        this.occupation = occupation;
        this.occupation_other = occupation_other;
        this.place_of_work = place_of_work;
        this.type_of_work = type_of_work;
        this.type_of_work_other = type_of_work_other;
        this.member_dob = member_dob;
        this.member_spouse_count = member_spouse_count;
        this.staying_since_year = staying_since_year;
        this.ration_card_colour = ration_card_colour;
        this.ration_card_no = ration_card_no;
        this.monthly_income = monthly_income;
        this.mode_of_transport = mode_of_transport;
        this.mode_of_transport_other = mode_of_transport_other;
        this.school_college_name_location = school_college_name_location;
        this.handicap_or_critical_disease = handicap_or_critical_disease;
        this.staying_with = staying_with;
        this.vehicle_owned_driven = vehicle_owned_driven;
        this.vehicle_owned_driven_other = vehicle_owned_driven_other;
        this.obejctId = obejctId;
        this.globalId = globalId;
        this.isUploaded = isUploaded;
        this.date = date;
        this.lastEditedDate = lastEditedDate;
        this.death_certificate = death_certificate;
    }

    //original

    /***
     @Ignore public MemberInfoDataModel(@NonNull String member_id, String memberSampleGlobalid, String rel_globalid, String relative_path, String member_name,
     String member_floor_no, String member_hoh, String relationship, String marital_status, String member_spouse_name,
     String member_contact_no, int age, String gender, String aadhar_no, String pan_no, String religion, String from_state,
     String mother_tongue, String education, String occupation, String place_of_work, String type_of_work, String member_occupancy_type,
     boolean aadhar_card, boolean pan_card, boolean photograph, String surveyor_name, String remarks,
     String obejctId, String globalId, boolean isUploaded, Date date, Date lastEditedDate) {
     this.member_id = member_id;
     this.memberSampleGlobalid = memberSampleGlobalid;
     this.rel_globalid = rel_globalid;
     this.relative_path = relative_path;
     this.member_name = member_name;
     this.member_floor_no = member_floor_no;
     this.member_hoh = member_hoh;
     this.relationship = relationship;
     this.marital_status = marital_status;
     this.member_spouse_name = member_spouse_name;
     this.member_contact_no = member_contact_no;
     this.age = age;
     this.gender = gender;
     this.aadhar_no = aadhar_no;
     this.pan_no = pan_no;
     this.religion = religion;
     this.from_state = from_state;
     this.mother_tongue = mother_tongue;
     this.education = education;
     this.occupation = occupation;
     this.place_of_work = place_of_work;
     this.type_of_work = type_of_work;
     this.member_occupancy_type = member_occupancy_type;
     this.aadhar_card = aadhar_card;
     this.pan_card = pan_card;
     this.photograph = photograph;
     this.surveyor_name = surveyor_name;
     this.remarks = remarks;
     this.obejctId = obejctId;
     this.globalId = globalId;
     this.isUploaded = isUploaded;
     this.date = date;
     this.lastEditedDate = lastEditedDate;
     }
     ***/
    public String getObejctId() {
        return obejctId;
    }

    public void setObejctId(String obejctId) {
        this.obejctId = obejctId;
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public Date getLastEditedDate() {
        return lastEditedDate;
    }

    public void setLastEditedDate(Date lastEditedDate) {
        this.lastEditedDate = lastEditedDate;
    }

    @NonNull
    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getMemberSampleGlobalid() {
        return memberSampleGlobalid;
    }

    public void setMemberSampleGlobalid(String memberSampleGlobalid) {
        this.memberSampleGlobalid = memberSampleGlobalid;
    }

    public String getRel_globalid() {
        return rel_globalid;
    }

    public void setRel_globalid(String rel_globalid) {
        this.rel_globalid = rel_globalid;
    }

    public String getRelative_path() {
        return relative_path;
    }

    public void setRelative_path(String relative_path) {
        this.relative_path = relative_path;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String getMember_floor_no() {
        return member_floor_no;
    }

    public void setMember_floor_no(String member_floor_no) {
        this.member_floor_no = member_floor_no;
    }

    public String getMember_hoh() {
        return member_hoh;
    }

    public void setMember_hoh(String member_hoh) {
        this.member_hoh = member_hoh;
    }

    /*public String getRelationship() {
        return relationship_with_hoh;
    }

    public void setRelationship(String relationship) {
        this.relationship_with_hoh = relationship;
    }*/

    public String getMarital_status() {
        return marital_status;
    }

    public void setMarital_status(String marital_status) {
        this.marital_status = marital_status;
    }

    public String getMarital_status_other() {
        return marital_status_other;
    }

    public void setMarital_status_other(String marital_status_other) {
        this.marital_status_other = marital_status_other;
    }

    public String getMember_spouse_name() {
        return member_spouse_name;
    }

    public void setMember_spouse_name(String member_spouse_name) {
        this.member_spouse_name = member_spouse_name;
    }

    public String getMember_contact_no() {
        return member_contact_no;
    }

    public void setMember_contact_no(String member_contact_no) {
        this.member_contact_no = member_contact_no;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getReligion_other() {
        return religion_other;
    }

    public void setReligion_other(String religion_other) {
        this.religion_other = religion_other;
    }

    public String getFrom_state() {
        return from_state;
    }

    public void setFrom_state(String from_state) {
        this.from_state = from_state;
    }

    public String getFrom_state_other() {
        return from_state_other;
    }

    public void setFrom_state_other(String from_state_other) {
        this.from_state_other = from_state_other;
    }

    public String getMother_tongue() {
        return mother_tongue;
    }

    public void setMother_tongue(String mother_tongue) {
        this.mother_tongue = mother_tongue;
    }

    public String getMother_tongue_other() {
        return mother_tongue_other;
    }

    public void setMother_tongue_other(String mother_tongue_other) {
        this.mother_tongue_other = mother_tongue_other;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getEducation_other() {
        return education_other;
    }

    public void setEducation_other(String education_other) {
        this.education_other = education_other;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getOccupation_other() {
        return occupation_other;
    }

    public void setOccupation_other(String occupation_other) {
        this.occupation_other = occupation_other;
    }

    public String getPlace_of_work() {
        return place_of_work;
    }

    public void setPlace_of_work(String place_of_work) {
        this.place_of_work = place_of_work;
    }

    public String getType_of_work() {
        return type_of_work;
    }

    public void setType_of_work(String type_of_work) {
        this.type_of_work = type_of_work;
    }

    public String getType_of_work_other() {
        return type_of_work_other;
    }

    public void setType_of_work_other(String type_of_work_other) {
        this.type_of_work_other = type_of_work_other;
    }

    public String getMember_occupancy_type() {
        return member_occupancy_type;
    }

    public void setMember_occupancy_type(String member_occupancy_type) {
        this.member_occupancy_type = member_occupancy_type;
    }

    public boolean isAadhar_card() {
        return aadhar_card;
    }

    public void setAadhar_card(boolean aadhar_card) {
        this.aadhar_card = aadhar_card;
    }

    public boolean isPan_card() {
        return pan_card;
    }

    public void setPan_card(boolean pan_card) {
        this.pan_card = pan_card;
    }

    public boolean isPhotograph() {
        return photograph;
    }

    public void setPhotograph(boolean photograph) {
        this.photograph = photograph;
    }

    public String getSurveyor_name() {
        return surveyor_name;
    }

    public void setSurveyor_name(String surveyor_name) {
        this.surveyor_name = surveyor_name;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getMedia_captured_cnt() {
        return media_captured_cnt;
    }

    public void setMedia_captured_cnt(int media_captured_cnt) {
        this.media_captured_cnt = media_captured_cnt;
    }

    public int getMedia_uploaded_cnt() {
        return media_uploaded_cnt;
    }

    public void setMedia_uploaded_cnt(int media_uploaded_cnt) {
        this.media_uploaded_cnt = media_uploaded_cnt;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRelationship_with_hoh() {
        return relationship_with_hoh;
    }

    public void setRelationship_with_hoh(String relationship_with_hoh) {
        this.relationship_with_hoh = relationship_with_hoh;
    }

    public String getRelationship_with_hoh_other() {
        return relationship_with_hoh_other;
    }

    public void setRelationship_with_hoh_other(String relationship_with_hoh_other) {
        this.relationship_with_hoh_other = relationship_with_hoh_other;
    }

    public int getMember_spouse_count() {
        return member_spouse_count;
    }

    public void setMember_spouse_count(int member_spouse_count) {
        this.member_spouse_count = member_spouse_count;
    }

    public String getMember_dob() {
        return member_dob;
    }

    public void setMember_dob(String member_dob) {
        this.member_dob = member_dob;
    }

    public int getStaying_since_year() {
        return staying_since_year;
    }

    public void setStaying_since_year(int staying_since_year) {
        this.staying_since_year = staying_since_year;
    }

    public String getRation_card_colour() {
        return ration_card_colour;
    }

    public void setRation_card_colour(String ration_card_colour) {
        this.ration_card_colour = ration_card_colour;
    }

    public String getRation_card_no() {
        return ration_card_no;
    }

    public void setRation_card_no(String ration_card_no) {
        this.ration_card_no = ration_card_no;
    }

    public String getMonthly_income() {
        return monthly_income;
    }

    public void setMonthly_income(String monthly_income) {
        this.monthly_income = monthly_income;
    }

    public String getMode_of_transport() {
        return mode_of_transport;
    }

    public void setMode_of_transport(String mode_of_transport) {
        this.mode_of_transport = mode_of_transport;
    }

    public String getMode_of_transport_other() {
        return mode_of_transport_other;
    }

    public void setMode_of_transport_other(String mode_of_transport_other) {
        this.mode_of_transport_other = mode_of_transport_other;
    }

    public String getSchool_college_name_location() {
        return school_college_name_location;
    }

    public void setSchool_college_name_location(String school_college_name_location) {
        this.school_college_name_location = school_college_name_location;
    }

    public String getHandicap_or_critical_disease() {
        return handicap_or_critical_disease;
    }

    public void setHandicap_or_critical_disease(String handicap_or_critical_disease) {
        this.handicap_or_critical_disease = handicap_or_critical_disease;
    }

    public String getStaying_with() {
        return staying_with;
    }

    public void setStaying_with(String staying_with) {
        this.staying_with = staying_with;
    }

    public String getVehicle_owned_driven() {
        return vehicle_owned_driven;
    }

    public void setVehicle_owned_driven(String vehicle_owned_driven) {
        this.vehicle_owned_driven = vehicle_owned_driven;
    }

    public String getVehicle_owned_driven_other() {
        return vehicle_owned_driven_other;
    }

    public void setVehicle_owned_driven_other(String vehicle_owned_driven_other) {
        this.vehicle_owned_driven_other = vehicle_owned_driven_other;
    }

    public int getDeath_certificate() {
        return death_certificate;
    }

    public void setDeath_certificate(int death_certificate) {
        this.death_certificate = death_certificate;
    }
}
