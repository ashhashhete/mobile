package com.igenesys.database.dabaseModel.newDbSModels;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

@Entity(tableName = "unitInfoDataTable")
public class UnitInfoDataModel implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = false)
    // variable for our id.
    private String unit_id;
    private String unitSampleGlobalid,respondent_remarks ;
    private String rel_globalid;
    private String relative_path;
    private String tenement_number;
    private String hut_number;
    private String floor;
    private String unit_no;
    private String unit_usage;
    private String structure_year;
    private String nature_of_activity;
    private double residential_area;
    private double commercial_area;
    private double rc_residential_area;
    private double rc_commercial_area;
    private double religious_area;
    private double other_area;
    private double area_sq_ft;
    private boolean member_available;
    private String unitRemark;
    private String unitOwnerShipType;
    private String no_Of_Member;
    private String existence_since;
    private String structure_since;

    private boolean resident_proof_attached;
    private boolean share_certificate_attached;
    private boolean electric_bill_attached;
    private boolean photo_pass_attached;
    private boolean na_tax_attached;
    private boolean property_tax_attached;
    private boolean electoral_roll_attached;

    private boolean additional_document_attached;
    private boolean school_college_certificate_attached;
    private boolean certificate_issued_attached;

    private boolean chain_document_attached;
    private boolean purchase_structure_attached;
    private boolean rent_agreement_attached;

    private boolean license_proof_attached;
    private boolean shop_estalishment_attached;
    private boolean restro_hotel_license_attached;
    private boolean food_drug_license_attached;
    private boolean factory_act_license_attached;

    private boolean religious_attachment_attached;
    private boolean others_attachment_attached;
    private String unit_status;
    private String surveyor_name;
    private String remarks;

    public String getGen_qc_remarks() {
        return gen_qc_remarks;
    }

    public void setGen_qc_remarks(String gen_qc_remarks) {
        this.gen_qc_remarks = gen_qc_remarks;
    }

    private String gen_qc_remarks;
    private String remarks_other;
    private String landmark_name;
    private int media_captured_cnt;
    private int media_uploaded_cnt;
    private String obejctId;
    private String globalId;
    private boolean isUploaded;
    private Date date;
    private Date lastEditedDate;

    private Date last_edited_date;

    /*
     * Adding missing data fields to model class based on v4 DB structure
     * @author : Jaid
     */

    private String surveyor_desig;
    private String survey_date;
    private String survey_time;
    private String drp_officer_name;
    private String drp_officer_name_other;
    private String drp_officer_desig;
    private String drp_officer_desig_other;
    private String object_id;
    private int visit_count;
    private String area_name;
    private String ward_no;
    private String sector_no;
    private String zone_no;
    private String nagar_name;
    private String nagar_name_other;
    private String society_name;
    private String street_name;
    private String respondent_name;
    private String respondent_dob;
    private String respondent_age;
    private String respondent_hoh_contact;
    private String respondent_hoh_relationship;
    private String respondent_hoh_relationship_other;
    private double residential_area_sqft;

    private String mobile_no_for_otp;
    private int otp_sent;
    private int otp_received;
    private short otp_verified;
    private String otp_remarks;
    private String otp_remarks_other;

    public double getResidential_area_sqft() {
        return residential_area_sqft;
    }

    public void setResidential_area_sqft(double residential_area_sqft) {
        this.residential_area_sqft = residential_area_sqft;
    }

    public double getCommercial_area_sqft() {
        return commercial_area_sqft;
    }

    public void setCommercial_area_sqft(double commercial_area_sqft) {
        this.commercial_area_sqft = commercial_area_sqft;
    }

    private double commercial_area_sqft;

    public String getRespondent_hoh_name() {
        return respondent_hoh_name;
    }

    public void setRespondent_hoh_name(String respondent_hoh_name) {
        this.respondent_hoh_name = respondent_hoh_name;
    }

    private String respondent_hoh_name;
    private String tenement_document;
    private String mashal_survey_number;
    private String ownership_status;
    private Double unit_area_sqft;
    private Boolean loft_present;
    private Double loft_area_sqft;
    private String employees_count;
    private Double ghumasta_area_sqft;

    public String getRespondent_non_available_remark() {
        return respondent_non_available_remark;
    }

    public void setRespondent_non_available_remark(String respondent_non_available_remark) {
        this.respondent_non_available_remark = respondent_non_available_remark;
    }

    private String respondent_non_available_remark;

    public String getType_of_other_structure() {
        return type_of_other_structure;
    }

    public void setType_of_other_structure(String type_of_other_structure) {
        this.type_of_other_structure = type_of_other_structure;
    }

    private String type_of_other_structure;

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    private String pincode;

    public String getRespondent_mobile() {
        return respondent_mobile;
    }

    public void setRespondent_mobile(String respondent_mobile) {
        this.respondent_mobile = respondent_mobile;
    }

    private String respondent_mobile ;

    public String getUnit_unique_id() {
        return unit_unique_id;
    }

    public void setUnit_unique_id(String unit_unique_id) {
        this.unit_unique_id = unit_unique_id;
    }

    private String unit_unique_id;

    public short getForm_lock() {
        return form_lock;
    }

    public void setForm_lock(short form_lock) {
        this.form_lock = form_lock;
    }

    public String getVisit_date() {
        return visit_date;
    }

    public void setVisit_date(String visit_date) {
        this.visit_date = visit_date;
    }

    private short form_lock;
    private String visit_date;

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    private String timeStamp;

    public boolean isYesNo() {
        return isYesNo;
    }

    public void setYesNo(boolean yesNo) {
        isYesNo = yesNo;
    }

    private boolean isYesNo;

    private String thumb_remarks;

    public String getExistence_since_year() {
        return existence_since_year;
    }

    public void setExistence_since_year(String existence_since_year) {
        this.existence_since_year = existence_since_year;
    }

    public String getSurvey_pavti_no() {
        return survey_pavti_no;
    }

    public void setSurvey_pavti_no(String survey_pavti_no) {
        this.survey_pavti_no = survey_pavti_no;
    }

    private String existence_since_year;
    private String survey_pavti_no;

    public short getHoh_avaibility() {
        return hoh_avaibility;
    }

    public void setHoh_avaibility(short hoh_avaibility) {
        this.hoh_avaibility = hoh_avaibility;
    }

    public String getAnnexure_remarks() {
        return annexure_remarks;
    }

    public void setAnnexure_remarks(String annexure_remarks) {
        this.annexure_remarks = annexure_remarks;
    }

    public short getAnnexure_uploaded() {
        return annexure_uploaded;
    }

    public void setAnnexure_uploaded(short annexure_uploaded) {
        this.annexure_uploaded = annexure_uploaded;
    }

//    public String getAnnexure_upload_date() {
//        return annexure_upload_date;
//    }
//
//    public void setAnnexure_upload_date(String annexure_upload_date) {
//        this.annexure_upload_date = annexure_upload_date;
//    }

    private short hoh_avaibility;
    private String annexure_remarks;

    public String getPanchnama_form_remarks() {
        return panchnama_form_remarks;
    }

    public void setPanchnama_form_remarks(String panchnama_form_remarks) {
        this.panchnama_form_remarks = panchnama_form_remarks;
    }

    private String panchnama_form_remarks;

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    private String city_name;
    private String country_name;
    private String state_name;
    private short annexure_uploaded;

    public String getAccess_to_unit() {
        return access_to_unit;
    }

    public void setAccess_to_unit(String access_to_unit) {
        this.access_to_unit = access_to_unit;
    }

    private String access_to_unit;
    private double latitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getGenesys_device_name() {
        return genesys_device_name;
    }

    public void setGenesys_device_name(String genesys_device_name) {
        this.genesys_device_name = genesys_device_name;
    }

    public String getPrimary_imei_no() {
        return primary_imei_no;
    }

    public void setPrimary_imei_no(String primary_imei_no) {
        this.primary_imei_no = primary_imei_no;
    }

    public String getSecond_imei_no() {
        return second_imei_no;
    }

    public void setSecond_imei_no(String second_imei_no) {
        this.second_imei_no = second_imei_no;
    }

    public short getIs_drppl_officer_available() {
        return is_drppl_officer_available;
    }

    public void setIs_drppl_officer_available(short is_drppl_officer_available) {
        this.is_drppl_officer_available = is_drppl_officer_available;
    }

    public String getDrppl_officer_name() {
        return drppl_officer_name;
    }

    public void setDrppl_officer_name(String drppl_officer_name) {
        this.drppl_officer_name = drppl_officer_name;
    }

    private double longitude;
    private String genesys_device_name;
    private String primary_imei_no;
    private String second_imei_no;
    private short is_drppl_officer_available;
    private String drppl_officer_name;
//    private String annexure_upload_date;

    /*
     * Additional constructor to add only surveyor data fields
     * @author : Jaid
     */
//    @Ignore
//    public UnitInfoDataModel(String surveyor_name, String surveyor_desig, String drp_officer_name, String drp_officer_desig, Date survey_date,
//                             String survey_time) {
//
//        this.surveyor_name = surveyor_name;
//        this.surveyor_desig = surveyor_desig;
//        this.drp_officer_name = drp_officer_name;
//        this.drp_officer_desig = drp_officer_desig;
//        this.survey_date = survey_date;
//        this.survey_time = survey_time;
//
//    }
    /*
     * Additional constructor to add all fields in one query
     * @author : Jaid
     */

    //add all required fields, pass null based on conditions
//    @Ignore
    public UnitInfoDataModel(@NonNull String unit_id, String unit_unique_id, String unitSampleGlobalid, String rel_globalid, String relative_path, String tenement_number,
                             String hut_number, String floor, String unit_no, String unit_usage, String structure_year, String nature_of_activity,
                             double residential_area, double commercial_area, double rc_residential_area, double rc_commercial_area, double religious_area,
                             double other_area, double area_sq_ft, boolean member_available, String member_non_available_remarks,
                             String existence_since, String structure_since, boolean resident_proof_attached, boolean share_certificate_attached,
                             boolean electric_bill_attached, boolean photo_pass_attached, boolean na_tax_attached, boolean property_tax_attached,
                             boolean electoral_roll_attached, boolean additional_document_attached, boolean school_college_certificate_attached,
                             boolean certificate_issued_attached, boolean chain_document_attached, boolean purchase_structure_attached, boolean rent_agreement_attached,
                             boolean license_proof_attached, boolean shop_estalishment_attached, boolean restro_hotel_license_attached, boolean food_drug_license_attached,
                             boolean factory_act_license_attached, boolean religious_attachment_attached, boolean others_attachment_attached, String unit_status,
                             String surveyor_name, String surveyor_desig, String drp_officer_name, String drp_officer_name_other, String drp_officer_desig,
                             String drp_officer_desig_other, String remarks, String remarks_other, int media_captured_cnt, int media_uploaded_cnt,
                             String obejctId, String globalId, Date date, Date last_edited_date, int visit_count, String area_name, String ward_no, String sector_no,
                             String zone_no, String nagar_name, String nagar_name_other, String society_name, String street_name, String landmark_name, String respondent_name, String respondent_dob, String respondent_age,
                             String respondent_hoh_name, String respondent_hoh_relationship, String respondent_hoh_relationship_other, String tenement_document, String mashal_survey_number, String ownership_status,
                             double unit_area_sqft, boolean loft_present, double loft_area_sqft, String employees_count, double ghumasta_area_sqft, boolean isUploaded, String pincode,
                             String respondent_mobile, String visit_date, short form_lock,String respondent_hoh_contact,
                             String survey_date,String survey_time,String type_of_other_structure,boolean isYesNo,String respondent_remarks,String pavtiNo,String year,
                             String country,String state,String city,String accessUnit,double resRcArea,double commRcArea,String thumb_remarks,String annexure_remarks,
                             String structure_type_religious,String structure_religious_other,String structure_type_amenities,String structure_amenities_other,String name_of_structure,String type_of_diety,String type_of_diety_other,String name_of_diety,
                             String category_of_faith,String category_of_faith_other,String sub_category_of_faith,String religion_of_structure_belongs,String structure_ownership_status,
                             String name_of_trust_or_owner,String nature_of_structure,String construction_material,String daily_visited_people_count,String tenement_number_rel_amenities,String tenement_doc_used,String tenement_doc_other,
                             String is_structure_registered,String structure_registered_with,String other_religious_authority,String name_of_trustee,String name_of_landowner,String noc_from_landlord_or_govt,
                             String approval_from_govt,String yearly_festival_conducted,String survey_pavti_no_rel_amenities,String mashal_rel_amenities,String ra_total_no_of_floors,String panchnamaRemarks,String gen_qc_remarks,
                             double latitude,double longitude,String genesys_device_name,String primary_imei_no,String second_imei_no,short is_drppl_officer_available,String drppl_officer_name) {
        this.unit_id = unit_id;
        this.unit_unique_id = unit_unique_id;
        this.unitSampleGlobalid = unitSampleGlobalid;
        this.rel_globalid = rel_globalid;
        this.relative_path = relative_path;
        this.tenement_number = tenement_number;
        this.hut_number = hut_number;
        this.floor = floor;
        this.unit_no = unit_no;
        this.unit_usage = unit_usage;
        this.structure_year = structure_year;
        this.nature_of_activity = nature_of_activity;
        this.residential_area = residential_area;
        this.commercial_area = commercial_area;
        this.rc_residential_area = rc_residential_area;
        this.rc_commercial_area = rc_commercial_area;
        this.religious_area = religious_area;
        this.other_area = other_area;
        this.area_sq_ft = area_sq_ft;
        this.member_available = member_available;
        this.respondent_non_available_remark = member_non_available_remarks;
        this.no_Of_Member = no_Of_Member;
        this.existence_since = existence_since;
        this.structure_since = structure_since;
        this.resident_proof_attached = resident_proof_attached;
        this.share_certificate_attached = share_certificate_attached;
        this.electric_bill_attached = electric_bill_attached;
        this.photo_pass_attached = photo_pass_attached;
        this.na_tax_attached = na_tax_attached;
        this.property_tax_attached = property_tax_attached;
        this.electoral_roll_attached = electoral_roll_attached;
        this.additional_document_attached = additional_document_attached;
        this.school_college_certificate_attached = school_college_certificate_attached;
        this.certificate_issued_attached = certificate_issued_attached;
        this.chain_document_attached = chain_document_attached;
        this.purchase_structure_attached = purchase_structure_attached;
        this.rent_agreement_attached = rent_agreement_attached;
        this.license_proof_attached = license_proof_attached;
        this.shop_estalishment_attached = shop_estalishment_attached;
        this.restro_hotel_license_attached = restro_hotel_license_attached;
        this.food_drug_license_attached = food_drug_license_attached;
        this.factory_act_license_attached = factory_act_license_attached;
        this.religious_attachment_attached = religious_attachment_attached;
        this.others_attachment_attached = others_attachment_attached;
        this.unit_status = unit_status;
        this.surveyor_name = surveyor_name;
        this.surveyor_desig = surveyor_desig;
        this.drp_officer_name = drp_officer_name;
        this.drp_officer_name_other = drp_officer_name_other;
        this.drp_officer_desig = drp_officer_desig;
        this.drp_officer_desig_other = drp_officer_desig_other;
        this.survey_date=survey_date;
        this.survey_time=survey_time;
        this.remarks = remarks;
        this.remarks_other = remarks_other;
        this.media_captured_cnt = media_captured_cnt;
        this.media_uploaded_cnt = media_uploaded_cnt;
        this.obejctId = obejctId;
        this.globalId = globalId;
        this.date = date;
        this.last_edited_date = last_edited_date;
        this.visit_count = visit_count;
        this.area_name = area_name;
        this.ward_no = ward_no;
        this.sector_no = sector_no;
        this.zone_no = zone_no;
        this.nagar_name = nagar_name;
        this.nagar_name_other = nagar_name_other;
        this.society_name = society_name;
        this.street_name = street_name;
        this.landmark_name = landmark_name;
        this.respondent_name = respondent_name;
        this.respondent_dob = respondent_dob;
        this.respondent_age = respondent_age;
        this.respondent_hoh_contact = respondent_hoh_contact;
        this.respondent_hoh_relationship = respondent_hoh_relationship;
        this.respondent_hoh_relationship_other = respondent_hoh_relationship_other;
        this.tenement_document = tenement_document;
        this.mashal_survey_number = mashal_survey_number;
        this.ownership_status = ownership_status;
        this.unit_area_sqft = unit_area_sqft;
        this.loft_present = loft_present;
        this.loft_area_sqft = loft_area_sqft;
        this.employees_count = employees_count;
        this.ghumasta_area_sqft = ghumasta_area_sqft;
        this.isUploaded = isUploaded;
        this.pincode = pincode;
        this.respondent_mobile  = respondent_mobile ;
        this.visit_date=visit_date;
        this.form_lock=form_lock;
        this.respondent_hoh_name=respondent_hoh_name;
        this.type_of_other_structure =type_of_other_structure;
        this.isYesNo =isYesNo;
        this.respondent_remarks  = respondent_remarks;
        this.survey_pavti_no  = pavtiNo;
        this.existence_since_year  = year;
        this.country_name  = country;
        this.state_name  = state;
        this.city_name  = city;
        this.access_to_unit  = accessUnit;
        this.residential_area_sqft  = resRcArea;
        this.commercial_area_sqft  = commRcArea;
        this.thumb_remarks =thumb_remarks;
        this.annexure_remarks = annexure_remarks;
        this.structure_type_religious = structure_type_religious;
        this.structure_religious_other = structure_religious_other;
        this.structure_type_amenities = structure_type_amenities;
        this.structure_amenities_other = structure_amenities_other;
        this.name_of_structure = name_of_structure;
        this.type_of_diety = type_of_diety;
        this.type_of_diety_other = type_of_diety_other;
        this.name_of_diety = name_of_diety;
        this.category_of_faith = category_of_faith;
        this.category_of_faith_other = category_of_faith_other;
        this.sub_category_of_faith = sub_category_of_faith;
        this.religion_of_structure_belongs = religion_of_structure_belongs;
        this.structure_ownership_status = structure_ownership_status;
        this.name_of_trust_or_owner = name_of_trust_or_owner;
        this.nature_of_structure = nature_of_structure;
        this.construction_material = construction_material;
        this.daily_visited_people_count = daily_visited_people_count;
        this.tenement_number_rel_amenities = tenement_number_rel_amenities;
        this.tenement_doc_used = tenement_doc_used;
        this.tenement_doc_other = tenement_doc_other;
        this.is_structure_registered = is_structure_registered;
        this.structure_registered_with = structure_registered_with;
        this.other_religious_authority = other_religious_authority;
        this.name_of_trustee = name_of_trustee;
        this.name_of_landowner = name_of_landowner;
        this.noc_from_landlord_or_govt = noc_from_landlord_or_govt;
        this.approval_from_govt = approval_from_govt;
        this.yearly_festival_conducted = yearly_festival_conducted;
        this.survey_pavti_no_rel_amenities = survey_pavti_no_rel_amenities;
        this.mashal_rel_amenities = mashal_rel_amenities;
        this.ra_total_no_of_floors = ra_total_no_of_floors;
        this.panchnama_form_remarks = panchnamaRemarks;
        this.gen_qc_remarks = gen_qc_remarks;
        this.latitude=latitude;
        this.longitude=longitude;
        this.genesys_device_name=genesys_device_name;
        this.primary_imei_no=primary_imei_no;
        this.second_imei_no=second_imei_no;
        this.is_drppl_officer_available=is_drppl_officer_available;
        this.drppl_officer_name=drppl_officer_name;
    }

    public String getRespondent_remarks() {
        return respondent_remarks;
    }

    public void setRespondent_remarks(String respondent_remarks) {
        this.respondent_remarks = respondent_remarks;
    }
/*
    Rohit new object
     */
//    public UnitInfoDataModel(@NonNull String unit_id,String unit_unique_id, String unitSampleGlobalid, String rel_globalid, String relative_path, String tenement_number,
//                             String hut_number, String floor, String unit_no, String unit_usage, String structure_year, String nature_of_activity,
//                             double residential_area, double commercial_area, double rc_residential_area, double rc_commercial_area, double religious_area,
//                             double other_area, double area_sq_ft, boolean member_available, String member_non_available_remarks,
//                             String existence_since, String structure_since, boolean resident_proof_attached, boolean share_certificate_attached,
//                             boolean electric_bill_attached, boolean photo_pass_attached, boolean na_tax_attached, boolean property_tax_attached,
//                             boolean electoral_roll_attached, boolean additional_document_attached, boolean school_college_certificate_attached,
//                             boolean certificate_issued_attached, boolean chain_document_attached, boolean purchase_structure_attached, boolean rent_agreement_attached,
//                             boolean license_proof_attached, boolean shop_estalishment_attached, boolean restro_hotel_license_attached, boolean food_drug_license_attached,
//                             boolean factory_act_license_attached, boolean religious_attachment_attached, boolean others_attachment_attached, String unit_status,
//                             String surveyor_name, String surveyor_desig, String drp_officer_name, String drp_officer_desig, String remarks, int media_captured_cnt, int media_uploaded_cnt,
//                             String obejctId, String globalId, Date date, Date last_edited_date, int visit_count, String area_name, String ward_no, String sector_no,
//                             String zone_no, String nagar_name, String society_name, String street_name, String landmark_name, String respondent_name, String respondent_dob, String respondent_age,
//                             String respondent_hoh_contact, String respondent_hoh_relationship, String tenement_document, String mashal_survey_number, String ownership_status,
//                             double unit_area_mtrs, boolean loft_present, double loft_area_mtrs, String employees_count, double ghumasta_area_mtrs, boolean isUploaded,short form_lock,Date visit_date) {
//        this.unit_id = unit_id;
//        this.unit_unique_id = unit_unique_id;
//        this.unitSampleGlobalid = unitSampleGlobalid;
//        this.rel_globalid = rel_globalid;
//        this.relative_path = relative_path;
//        this.tenement_number = tenement_number;
//        this.hut_number = hut_number;
//        this.floor = floor;
//        this.unit_no = unit_no;
//        this.unit_usage = unit_usage;
//        this.structure_year = structure_year;
//        this.nature_of_activity = nature_of_activity;
//        this.residential_area = residential_area;
//        this.commercial_area = commercial_area;
//        this.rc_residential_area = rc_residential_area;
//        this.rc_commercial_area = rc_commercial_area;
//        this.religious_area = religious_area;
//        this.other_area = other_area;
//        this.area_sq_ft = area_sq_ft;
//        this.member_available = member_available;
//        this.member_non_available_remarks = member_non_available_remarks;
//        this.no_Of_Member = no_Of_Member;
//        this.existence_since = existence_since;
//        this.structure_since = structure_since;
//        this.resident_proof_attached = resident_proof_attached;
//        this.share_certificate_attached = share_certificate_attached;
//        this.electric_bill_attached = electric_bill_attached;
//        this.photo_pass_attached = photo_pass_attached;
//        this.na_tax_attached = na_tax_attached;
//        this.property_tax_attached = property_tax_attached;
//        this.electoral_roll_attached = electoral_roll_attached;
//        this.additional_document_attached = additional_document_attached;
//        this.school_college_certificate_attached = school_college_certificate_attached;
//        this.certificate_issued_attached = certificate_issued_attached;
//        this.chain_document_attached = chain_document_attached;
//        this.purchase_structure_attached = purchase_structure_attached;
//        this.rent_agreement_attached = rent_agreement_attached;
//        this.license_proof_attached = license_proof_attached;
//        this.shop_estalishment_attached = shop_estalishment_attached;
//        this.restro_hotel_license_attached = restro_hotel_license_attached;
//        this.food_drug_license_attached = food_drug_license_attached;
//        this.factory_act_license_attached = factory_act_license_attached;
//        this.religious_attachment_attached = religious_attachment_attached;
//        this.others_attachment_attached = others_attachment_attached;
//        this.unit_status = unit_status;
//        this.surveyor_name = surveyor_name;
//        this.surveyor_desig = surveyor_desig;
//        this.drp_officer_name = drp_officer_name;
//        this.drp_officer_desig = drp_officer_desig;
//        //this.survey_date=      survey_date;
//        //this.survey_time=      survey_time;
//        this.remarks = remarks;
//        this.media_captured_cnt = media_captured_cnt;
//        this.media_uploaded_cnt = media_uploaded_cnt;
//        this.obejctId = obejctId;
//        this.globalId = globalId;
//        this.date = date;
//        this.last_edited_date = last_edited_date;
//        this.visit_count = visit_count;
//        this.area_name = area_name;
//        this.ward_no = ward_no;
//        this.sector_no = sector_no;
//        this.zone_no = zone_no;
//        this.nagar_name = nagar_name;
//        this.society_name = society_name;
//        this.street_name = street_name;
//        this.landmark_name = landmark_name;
//        this.respondent_name = respondent_name;
//        this.respondent_dob = respondent_dob;
//        this.respondent_age = respondent_age;
//        this.respondent_hoh_contact = respondent_hoh_contact;
//        this.respondent_hoh_relationship = respondent_hoh_relationship;
//        this.tenement_document = tenement_document;
//        this.mashal_survey_number = mashal_survey_number;
//        this.ownership_status = ownership_status;
//        this.unit_area_mtrs = unit_area_mtrs;
//        this.loft_present = loft_present;
//        this.loft_area_mtrs = loft_area_mtrs;
//        this.employees_count = employees_count;
//        this.ghumasta_area_mtrs = ghumasta_area_mtrs;
//        this.isUploaded = isUploaded;
//        this.form_lock = form_lock;
//        this.visit_date = visit_date;
//
//
//    }



    //original
//    @Ignore
//    public UnitInfoDataModel(@NonNull String unit_id, String unitSampleGlobalid, String rel_globalid, String relative_path, String tenement_number,
//                             String hut_number, String floor, String unit_no, String unit_usage, String structure_year, String nature_of_activity,
//                             double residential_area, double commercial_area, double rc_residential_area, double rc_commercial_area, double religious_area,
//                             double other_area, double area_sq_ft, boolean member_available, String unitRemark, String unitOwnerShipType, String no_Of_Member,
//                             String existence_since, String structure_since, boolean resident_proof_attached, boolean share_certificate_attached,
//                             boolean electric_bill_attached, boolean photo_pass_attached, boolean na_tax_attached, boolean property_tax_attached,
//                             boolean electoral_roll_attached, boolean additional_document_attached, boolean school_college_certificate_attached,
//                             boolean certificate_issued_attached, boolean chain_document_attached, boolean purchase_structure_attached, boolean rent_agreement_attached,
//                             boolean license_proof_attached, boolean shop_estalishment_attached, boolean restro_hotel_license_attached, boolean food_drug_license_attached,
//                             boolean factory_act_license_attached, boolean religious_attachment_attached, boolean others_attachment_attached, String unit_status,
//                             String surveyor_name, String remarks, int media_captured_cnt, int media_uploaded_cnt,
//                             String obejctId, String globalId, boolean isUploaded, Date date, Date lastEditedDate) {
//        this.unit_id = unit_id;
//        this.unitSampleGlobalid = unitSampleGlobalid;
//        this.rel_globalid = rel_globalid;
//        this.relative_path = relative_path;
//        this.tenement_number = tenement_number;
//        this.hut_number = hut_number;
//        this.floor = floor;
//        this.unit_no = unit_no;
//        this.unit_usage = unit_usage;
//        this.structure_year = structure_year;
//        this.nature_of_activity = nature_of_activity;
//        this.residential_area = residential_area;
//        this.commercial_area = commercial_area;
//        this.rc_residential_area = rc_residential_area;
//        this.rc_commercial_area = rc_commercial_area;
//        this.religious_area = religious_area;
//        this.other_area = other_area;
//        this.area_sq_ft = area_sq_ft;
//        this.member_available = member_available;
//        this.unitRemark = unitRemark;
//        this.unitOwnerShipType = unitOwnerShipType;
//        this.no_Of_Member = no_Of_Member;
//        this.existence_since = existence_since;
//        this.structure_since = structure_since;
//        this.resident_proof_attached = resident_proof_attached;
//        this.share_certificate_attached = share_certificate_attached;
//        this.electric_bill_attached = electric_bill_attached;
//        this.photo_pass_attached = photo_pass_attached;
//        this.na_tax_attached = na_tax_attached;
//        this.property_tax_attached = property_tax_attached;
//        this.electoral_roll_attached = electoral_roll_attached;
//        this.additional_document_attached = additional_document_attached;
//        this.school_college_certificate_attached = school_college_certificate_attached;
//        this.certificate_issued_attached = certificate_issued_attached;
//        this.chain_document_attached = chain_document_attached;
//        this.purchase_structure_attached = purchase_structure_attached;
//        this.rent_agreement_attached = rent_agreement_attached;
//        this.license_proof_attached = license_proof_attached;
//        this.shop_estalishment_attached = shop_estalishment_attached;
//        this.restro_hotel_license_attached = restro_hotel_license_attached;
//        this.food_drug_license_attached = food_drug_license_attached;
//        this.factory_act_license_attached = factory_act_license_attached;
//        this.religious_attachment_attached = religious_attachment_attached;
//        this.others_attachment_attached = others_attachment_attached;
//        this.unit_status = unit_status;
//        this.surveyor_name = surveyor_name;
//        this.remarks = remarks;
//        this.media_captured_cnt = media_captured_cnt;
//        this.media_uploaded_cnt = media_uploaded_cnt;
//        this.obejctId = obejctId;
//        this.globalId = globalId;
//        this.isUploaded = isUploaded;
//        this.date = date;
//        this.lastEditedDate = lastEditedDate;
//    }

//    @Ignore
//    public UnitInfoDataModel(@NonNull String unit_id,String unit_unique_id, String unitSampleGlobalid, String rel_globalid, String relative_path, String tenement_number,
//                             String floor, String unit_no, boolean member_available, String unitRemark, String unit_status, String surveyor_name,
//                             String obejctId, String globalId, boolean isUploaded, Date date, Date lastEditedDate) {
//        this.unit_id = unit_id;
//        this.unitSampleGlobalid = unitSampleGlobalid;
//        this.rel_globalid = rel_globalid;
//        this.relative_path = relative_path;
//        this.tenement_number = tenement_number;
//        this.floor = floor;
//        this.unit_no = unit_no;
//        this.member_available = member_available;
//        this.unitRemark = unitRemark;
//        this.unit_status = unit_status;
//        this.surveyor_name = surveyor_name;
//        this.obejctId = obejctId;
//        this.globalId = globalId;
//        this.isUploaded = isUploaded;
//        this.date = date;
//        this.lastEditedDate = lastEditedDate;
//        this.unit_unique_id = unit_unique_id;
//    }

    //  public UnitInfoDataModel(String string, String toUpperCase, String toUpperCase1, String string1, String string2, String string3, String string4, String string5, String string6, String convertDateToStringDate, String string7, double doubleFormatter, double doubleFormatter1, double doubleFormatter2, double doubleFormatter3, double doubleFormatter4, double doubleFormatter5, double doubleFormatter6, boolean b, String string8, String string9, String string10, boolean b1, boolean equals, boolean equals1, boolean equals2, boolean equals3, boolean equals4, boolean equals5, boolean b2, boolean equals6, boolean equals7, boolean equals8, boolean b3, boolean equals9, boolean b4, boolean b5, boolean equals10, boolean equals11, boolean equals12, boolean b6, boolean b7, String string11, String string12, String string13, int integer, int integer1, String string14, Date date, Date date1, int integer2, String string15, String string16, String string17, String string18, String string19, String string20, String string21, String string22, String string23, String string24, String string25, String string26, String string27, String string28, String string29, double doubleFormatter7, double doubleFormatter8, double doubleFormatter9, String string30, double doubleFormatter10)


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

    public String getHut_number() {
        return hut_number;
    }

    public void setHut_number(String hut_number) {
        this.hut_number = hut_number;
    }

    public String getNature_of_activity() {
        return nature_of_activity;
    }

    public void setNature_of_activity(String nature_of_activity) {
        this.nature_of_activity = nature_of_activity;
    }

    public double getArea_sq_ft() {
        return area_sq_ft;
    }

    public void setArea_sq_ft(double area_sq_ft) {
        this.area_sq_ft = area_sq_ft;
    }

    public String getStructure_since() {
        return structure_since;
    }

    public void setStructure_since(String structure_since) {
        this.structure_since = structure_since;
    }

    @NonNull
    public String getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(@NonNull String unit_id) {
        this.unit_id = unit_id;
    }

    public String getUnitSampleGlobalid() {
        return unitSampleGlobalid;
    }

    public void setUnitSampleGlobalid(String unitSampleGlobalid) {
        this.unitSampleGlobalid = unitSampleGlobalid;
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

    public String getTenement_number() {
        return tenement_number;
    }

    public void setTenement_number(String tenement_number) {
        this.tenement_number = tenement_number;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getUnit_no() {
        return unit_no;
    }

    public void setUnit_no(String unit_no) {
        this.unit_no = unit_no;
    }

    public boolean isMember_available() {
        return member_available;
    }

    public void setMember_available(boolean member_available) {
        this.member_available = member_available;
    }

    public String getUnitRemark() {
        return unitRemark;
    }

    public void setUnitRemark(String unitRemark) {
        this.unitRemark = unitRemark;
    }

    public String getUnitOwnerShipType() {
        return unitOwnerShipType;
    }

    public void setUnitOwnerShipType(String unitOwnerShipType) {
        this.unitOwnerShipType = unitOwnerShipType;
    }

    public String getNo_Of_Member() {
        return no_Of_Member;
    }

    public void setNo_Of_Member(String no_Of_Member) {
        this.no_Of_Member = no_Of_Member;
    }

    public String getUnit_usage() {
        return unit_usage;
    }

    public void setUnit_usage(String unit_usage) {
        this.unit_usage = unit_usage;
    }

    public double getResidential_area() {
        return residential_area;
    }

    public void setResidential_area(double residential_area) {
        this.residential_area = residential_area;
    }

    public double getCommercial_area() {
        return commercial_area;
    }

    public void setCommercial_area(double commercial_area) {
        this.commercial_area = commercial_area;
    }

    public double getRc_residential_area() {
        return rc_residential_area;
    }

    public void setRc_residential_area(double rc_residential_area) {
        this.rc_residential_area = rc_residential_area;
    }

    public double getRc_commercial_area() {
        return rc_commercial_area;
    }

    public void setRc_commercial_area(double rc_commercial_area) {
        this.rc_commercial_area = rc_commercial_area;
    }

    public double getReligious_area() {
        return religious_area;
    }

    public void setReligious_area(double religious_area) {
        this.religious_area = religious_area;
    }

    public double getOther_area() {
        return other_area;
    }

    public void setOther_area(double other_area) {
        this.other_area = other_area;
    }

    public String getExistence_since() {
        return existence_since;
    }

    public void setExistence_since(String existence_since) {
        this.existence_since = existence_since;
    }

    public String getStructure_year() {
        return structure_year;
    }

    public void setStructure_year(String structure_year) {
        this.structure_year = structure_year;
    }

    public boolean isResident_proof_attached() {
        return resident_proof_attached;
    }

    public void setResident_proof_attached(boolean resident_proof_attached) {
        this.resident_proof_attached = resident_proof_attached;
    }

    public boolean isShare_certificate_attached() {
        return share_certificate_attached;
    }

    public void setShare_certificate_attached(boolean share_certificate_attached) {
        this.share_certificate_attached = share_certificate_attached;
    }

    public boolean isElectric_bill_attached() {
        return electric_bill_attached;
    }

    public void setElectric_bill_attached(boolean electric_bill_attached) {
        this.electric_bill_attached = electric_bill_attached;
    }

    public boolean isPhoto_pass_attached() {
        return photo_pass_attached;
    }

    public void setPhoto_pass_attached(boolean photo_pass_attached) {
        this.photo_pass_attached = photo_pass_attached;
    }

    public boolean isNa_tax_attached() {
        return na_tax_attached;
    }

    public void setNa_tax_attached(boolean na_tax_attached) {
        this.na_tax_attached = na_tax_attached;
    }

    public boolean isProperty_tax_attached() {
        return property_tax_attached;
    }

    public void setProperty_tax_attached(boolean property_tax_attached) {
        this.property_tax_attached = property_tax_attached;
    }

    public boolean isElectoral_roll_attached() {
        return electoral_roll_attached;
    }

    public void setElectoral_roll_attached(boolean electoral_roll_attached) {
        this.electoral_roll_attached = electoral_roll_attached;
    }

    public boolean isAdditional_document_attached() {
        return additional_document_attached;
    }

    public void setAdditional_document_attached(boolean additional_document_attached) {
        this.additional_document_attached = additional_document_attached;
    }

    public boolean isSchool_college_certificate_attached() {
        return school_college_certificate_attached;
    }

    public void setSchool_college_certificate_attached(boolean school_college_certificate_attached) {
        this.school_college_certificate_attached = school_college_certificate_attached;
    }

    public boolean isCertificate_issued_attached() {
        return certificate_issued_attached;
    }

    public void setCertificate_issued_attached(boolean certificate_issued_attached) {
        this.certificate_issued_attached = certificate_issued_attached;
    }

    public boolean isChain_document_attached() {
        return chain_document_attached;
    }

    public void setChain_document_attached(boolean chain_document_attached) {
        this.chain_document_attached = chain_document_attached;
    }

    public boolean isPurchase_structure_attached() {
        return purchase_structure_attached;
    }

    public void setPurchase_structure_attached(boolean purchase_structure_attached) {
        this.purchase_structure_attached = purchase_structure_attached;
    }

    public boolean isRent_agreement_attached() {
        return rent_agreement_attached;
    }

    public void setRent_agreement_attached(boolean rent_agreement_attached) {
        this.rent_agreement_attached = rent_agreement_attached;
    }

    public boolean isLicense_proof_attached() {
        return license_proof_attached;
    }

    public void setLicense_proof_attached(boolean license_proof_attached) {
        this.license_proof_attached = license_proof_attached;
    }

    public boolean isShop_estalishment_attached() {
        return shop_estalishment_attached;
    }

    public void setShop_estalishment_attached(boolean shop_estalishment_attached) {
        this.shop_estalishment_attached = shop_estalishment_attached;
    }

    public boolean isRestro_hotel_license_attached() {
        return restro_hotel_license_attached;
    }

    public void setRestro_hotel_license_attached(boolean restro_hotel_license_attached) {
        this.restro_hotel_license_attached = restro_hotel_license_attached;
    }

    public boolean isFood_drug_license_attached() {
        return food_drug_license_attached;
    }

    public void setFood_drug_license_attached(boolean food_drug_license_attached) {
        this.food_drug_license_attached = food_drug_license_attached;
    }

    public boolean isFactory_act_license_attached() {
        return factory_act_license_attached;
    }

    public void setFactory_act_license_attached(boolean factory_act_license_attached) {
        this.factory_act_license_attached = factory_act_license_attached;
    }

    public boolean isReligious_attachment_attached() {
        return religious_attachment_attached;
    }

    public void setReligious_attachment_attached(boolean religious_attachment_attached) {
        this.religious_attachment_attached = religious_attachment_attached;
    }

    public boolean isOthers_attachment_attached() {
        return others_attachment_attached;
    }

    public void setOthers_attachment_attached(boolean others_attachment_attached) {
        this.others_attachment_attached = others_attachment_attached;
    }

    public String getUnit_status() {
        return unit_status;
    }

    public void setUnit_status(String unit_status) {
        this.unit_status = unit_status;
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

    public String getRemarks_other() {
        return remarks_other;
    }

    public void setRemarks_other(String remarks_other) {
        this.remarks_other = remarks_other;
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
    /*
     * Adding getter setter for newly added database fields
     * @auther : Jaid
     */

    public String getSurveyor_desig() {
        return surveyor_desig;
    }

    public void setSurveyor_desig(String surveyor_desig) {
        this.surveyor_desig = surveyor_desig;
    }

    public String getSurvey_date() {
        return survey_date;
    }

    public void setSurvey_date(String survey_date) {
        this.survey_date = survey_date;
    }

    public String getSurvey_time() {
        return survey_time;
    }

    public void setSurvey_time(String survey_time) {
        this.survey_time = survey_time;
    }

    public String getDrp_officer_name() {
        return drp_officer_name;
    }

    public void setDrp_officer_name(String drp_officer_name) {
        this.drp_officer_name = drp_officer_name;
    }

    public String getDrp_officer_desig() {
        return drp_officer_desig;
    }

    public void setDrp_officer_desig(String drp_officer_desig) {
        this.drp_officer_desig = drp_officer_desig;
    }

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public int getVisit_count() {
        return visit_count;
    }

    public void setVisit_count(int visit_count) {
        this.visit_count = visit_count;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getWard_no() {
        return ward_no;
    }

    public void setWard_no(String ward_no) {
        this.ward_no = ward_no;
    }

    public String getSector_no() {
        return sector_no;
    }

    public void setSector_no(String sector_no) {
        this.sector_no = sector_no;
    }

    public String getZone_no() {
        return zone_no;
    }

    public void setZone_no(String zone_no) {
        this.zone_no = zone_no;
    }

    public String getNagar_name() {
        return nagar_name;
    }

    public void setNagar_name(String nagar_name) {
        this.nagar_name = nagar_name;
    }

    public String getNagar_name_other() {
        return nagar_name_other;
    }

    public void setNagar_name_other(String nagar_name_other) {
        this.nagar_name_other = nagar_name_other;
    }

    public String getSociety_name() {
        return society_name;
    }

    public void setSociety_name(String society_name) {
        this.society_name = society_name;
    }

    public String getStreet_name() {
        return street_name;
    }

    public void setStreet_name(String street_name) {
        this.street_name = street_name;
    }

    public String getLandmark_name() {
        return landmark_name;
    }

    public void setLandmark_name(String landmark_name) {
        this.landmark_name = landmark_name;
    }

    public String getRespondent_name() {
        return respondent_name;
    }

    public void setRespondent_name(String respondent_name) {
        this.respondent_name = respondent_name;
    }

    public String getRespondent_dob() {
        return respondent_dob;
    }

    public void setRespondent_dob(String respondent_dob) {
        this.respondent_dob = respondent_dob;
    }

    public String getRespondent_age() {
        return respondent_age;
    }

    public void setRespondent_age(String respondent_age) {
        this.respondent_age = respondent_age;
    }

    public String getRespondent_hoh_contact() {
        return respondent_hoh_contact;
    }

    public void setRespondent_hoh_contact(String respondent_hoh_contact) {
        this.respondent_hoh_contact = respondent_hoh_contact;
    }

    public String getRespondent_hoh_relationship() {
        return respondent_hoh_relationship;
    }

    public void setRespondent_hoh_relationship(String respondent_hoh_relationship) {
        this.respondent_hoh_relationship = respondent_hoh_relationship;
    }

    public String getRespondent_hoh_relationship_other() {
        return respondent_hoh_relationship_other;
    }

    public void setRespondent_hoh_relationship_other(String respondent_hoh_relationship_other) {
        this.respondent_hoh_relationship_other = respondent_hoh_relationship_other;
    }

    public String getTenement_document() {
        return tenement_document;
    }

    public void setTenement_document(String tenement_document) {
        this.tenement_document = tenement_document;
    }

    public String getMashal_survey_number() {
        return mashal_survey_number;
    }

    public void setMashal_survey_number(String mashal_survey_number) {
        this.mashal_survey_number = mashal_survey_number;
    }

    public String getOwnership_status() {
        return ownership_status;
    }

    public void setOwnership_status(String ownership_status) {
        this.ownership_status = ownership_status;
    }

    public Double getUnit_area_sqft() {
        return unit_area_sqft;
    }

    public void setUnit_area_sqft(Double unit_area_sqft) {
        this.unit_area_sqft = unit_area_sqft;
    }

    public Boolean getLoft_present() {
        return loft_present;
    }

    public void setLoft_present(Boolean loft_present) {
        this.loft_present = loft_present;
    }

    public Double getLoft_area_sqft() {
        return loft_area_sqft;
    }

    public void setLoft_area_sqft(Double loft_area_sqft) {
        this.loft_area_sqft = loft_area_sqft;
    }

    public String getEmployees_count() {
        return employees_count;
    }

    public void setEmployees_count(String employees_count) {
        this.employees_count = employees_count;
    }

    public Double getGhumasta_area_sqft() {
        return ghumasta_area_sqft;
    }

    public void setGhumasta_area_sqft(Double ghumasta_area_sqft) {
        this.ghumasta_area_sqft = ghumasta_area_sqft;
    }

    public String getDrp_officer_name_other() {
        return drp_officer_name_other;
    }

    public void setDrp_officer_name_other(String drp_officer_name_other) {
        this.drp_officer_name_other = drp_officer_name_other;
    }

    public String getDrp_officer_desig_other() {
        return drp_officer_desig_other;
    }

    public void setDrp_officer_desig_other(String drp_officer_desig_other) {
        this.drp_officer_desig_other = drp_officer_desig_other;
    }

    //    public String getMember_non_available_remarks() {
//        return respondent_non_available_remark;
//    }
//
//    public void setMember_non_available_remarks(String member_non_available_remarks) {
//        this.respondent_non_available_remark = member_non_available_remarks;
//    }

    public Date getLast_edited_date() {
        return last_edited_date;
    }

    public void setLast_edited_date(Date last_edited_date) {
        this.last_edited_date = last_edited_date;
    }

    @Ignore
//@JvmOverloads
    public UnitInfoDataModel(@NonNull String unit_id, String unit_unique_id, String unitSampleGlobalid, String rel_globalid, String relative_path,
                             String hut_number, String floor, String unit_no, String nature_of_activity, boolean member_available, String unit_status,
                             String surveyor_name, String surveyor_desig, String drp_officer_name, String drp_officer_desig, String remarks,
                             String remarks_other, String obejctId, String globalId, Date date, Date last_edited_date, int visit_count, String area_name,
                             String ward_no, String sector_no, String zone_no, String nagar_name, String nagar_name_other, String society_name,
                             String street_name, String landmark_name, boolean isUploaded, String pincode, String visit_date, short form_lock) {
        this.unit_id = unit_id;
        this.unit_unique_id = unit_unique_id;
        this.unitSampleGlobalid = unitSampleGlobalid;
        this.rel_globalid = rel_globalid;
        this.relative_path = relative_path;
        this.hut_number = hut_number;
        this.floor = floor;
        this.unit_no = unit_no;
        this.unit_usage = unit_usage;
        this.nature_of_activity = nature_of_activity;
        this.member_available = member_available;
        this.unit_status = unit_status;
        this.surveyor_name = surveyor_name;
        this.surveyor_desig = surveyor_desig;
        this.drp_officer_name = drp_officer_name;
        this.drp_officer_desig = drp_officer_desig;
        this.remarks = remarks;
        this.remarks_other = remarks_other;
        this.obejctId = obejctId;
        this.globalId = globalId;
        this.date = date;
        this.last_edited_date = last_edited_date;
        this.visit_count = visit_count;
        this.area_name = area_name;
        this.ward_no = ward_no;
        this.sector_no = sector_no;
        this.zone_no = zone_no;
        this.nagar_name = nagar_name;
        this.nagar_name_other = nagar_name_other;
        this.society_name = society_name;
        this.street_name = street_name;
        this.landmark_name = landmark_name;
        this.isUploaded = isUploaded;
        this.pincode = pincode;
        this.visit_date=visit_date;
        this.form_lock=form_lock;
    }

    public UnitInfoDataModel(){

    }


    public String getThumb_remarks() {
        return thumb_remarks;
    }

    public void setThumb_remarks(String thumb_remarks) {
        this.thumb_remarks = thumb_remarks;
    }

    public String getMobile_no_for_otp() {
        return mobile_no_for_otp;
    }

    public void setMobile_no_for_otp(String mobile_no_for_otp) {
        this.mobile_no_for_otp = mobile_no_for_otp;
    }

    public int getOtp_sent() {
        return otp_sent;
    }

    public void setOtp_sent(int otp_sent) {
        this.otp_sent = otp_sent;
    }

    public int getOtp_received() {
        return otp_received;
    }

    public void setOtp_received(int otp_received) {
        this.otp_received = otp_received;
    }

    public short getOtp_verified() {
        return otp_verified;
    }

    public void setOtp_verified(short otp_verified) {
        this.otp_verified = otp_verified;
    }

    public String getOtp_remarks() {
        return otp_remarks;
    }

    public void setOtp_remarks(String otp_remarks) {
        this.otp_remarks = otp_remarks;
    }

    public String getOtp_remarks_other() {
        return otp_remarks_other;
    }

    public void setOtp_remarks_other(String otp_remarks_other) {
        this.otp_remarks_other = otp_remarks_other;
    }


    String structure_type_religious;

    public String getStructure_type_religious() {
        return structure_type_religious;
    }

    public void setStructure_type_religious(String structure_type_religious) {
        this.structure_type_religious = structure_type_religious;
    }

    public String getStructure_religious_other() {
        return structure_religious_other;
    }

    public void setStructure_religious_other(String structure_religious_other) {
        this.structure_religious_other = structure_religious_other;
    }

    public String getStructure_type_amenities() {
        return structure_type_amenities;
    }

    public void setStructure_type_amenities(String structure_type_amenities) {
        this.structure_type_amenities = structure_type_amenities;
    }

    public String getStructure_amenities_other() {
        return structure_amenities_other;
    }

    public void setStructure_amenities_other(String structure_amenities_other) {
        this.structure_amenities_other = structure_amenities_other;
    }

    public String getName_of_structure() {
        return name_of_structure;
    }

    public void setName_of_structure(String name_of_structure) {
        this.name_of_structure = name_of_structure;
    }

    public String getType_of_diety() {
        return type_of_diety;
    }

    public void setType_of_diety(String type_of_diety) {
        this.type_of_diety = type_of_diety;
    }

    public String getType_of_diety_other() {
        return type_of_diety_other;
    }

    public void setType_of_diety_other(String type_of_diety_other) {
        this.type_of_diety_other = type_of_diety_other;
    }

    public String getName_of_diety() {
        return name_of_diety;
    }

    public void setName_of_diety(String name_of_diety) {
        this.name_of_diety = name_of_diety;
    }

    public String getCategory_of_faith() {
        return category_of_faith;
    }

    public void setCategory_of_faith(String category_of_faith) {
        this.category_of_faith = category_of_faith;
    }

    public String getCategory_of_faith_other() {
        return category_of_faith_other;
    }

    public void setCategory_of_faith_other(String category_of_faith_other) {
        this.category_of_faith_other = category_of_faith_other;
    }

    public String getSub_category_of_faith() {
        return sub_category_of_faith;
    }

    public void setSub_category_of_faith(String sub_category_of_faith) {
        this.sub_category_of_faith = sub_category_of_faith;
    }

    public String getReligion_of_structure_belongs() {
        return religion_of_structure_belongs;
    }

    public void setReligion_of_structure_belongs(String religion_of_structure_belongs) {
        this.religion_of_structure_belongs = religion_of_structure_belongs;
    }

    public String getStructure_ownership_status() {
        return structure_ownership_status;
    }

    public void setStructure_ownership_status(String structure_ownership_status) {
        this.structure_ownership_status = structure_ownership_status;
    }

    public String getName_of_trust_or_owner() {
        return name_of_trust_or_owner;
    }

    public void setName_of_trust_or_owner(String name_of_trust_or_owner) {
        this.name_of_trust_or_owner = name_of_trust_or_owner;
    }

    public String getNature_of_structure() {
        return nature_of_structure;
    }

    public void setNature_of_structure(String nature_of_structure) {
        this.nature_of_structure = nature_of_structure;
    }

    public String getConstruction_material() {
        return construction_material;
    }

    public void setConstruction_material(String construction_material) {
        this.construction_material = construction_material;
    }

    public String getDaily_visited_people_count() {
        return daily_visited_people_count;
    }

    public void setDaily_visited_people_count(String daily_visited_people_count) {
        this.daily_visited_people_count = daily_visited_people_count;
    }

    public String getTenement_number_rel_amenities() {
        return tenement_number_rel_amenities;
    }

    public void setTenement_number_rel_amenities(String tenement_number_rel_amenities) {
        this.tenement_number_rel_amenities = tenement_number_rel_amenities;
    }

    public String getTenement_doc_used() {
        return tenement_doc_used;
    }

    public void setTenement_doc_used(String tenement_doc_used) {
        this.tenement_doc_used = tenement_doc_used;
    }

    public String getTenement_doc_other() {
        return tenement_doc_other;
    }

    public void setTenement_doc_other(String tenement_doc_other) {
        this.tenement_doc_other = tenement_doc_other;
    }

    public String getIs_structure_registered() {
        return is_structure_registered;
    }

    public void setIs_structure_registered(String is_structure_registered) {
        this.is_structure_registered = is_structure_registered;
    }

    public String getStructure_registered_with() {
        return structure_registered_with;
    }

    public void setStructure_registered_with(String structure_registered_with) {
        this.structure_registered_with = structure_registered_with;
    }

    public String getOther_religious_authority() {
        return other_religious_authority;
    }

    public void setOther_religious_authority(String other_religious_authority) {
        this.other_religious_authority = other_religious_authority;
    }

    public String getName_of_trustee() {
        return name_of_trustee;
    }

    public void setName_of_trustee(String name_of_trustee) {
        this.name_of_trustee = name_of_trustee;
    }

    public String getName_of_landowner() {
        return name_of_landowner;
    }

    public void setName_of_landowner(String name_of_landowner) {
        this.name_of_landowner = name_of_landowner;
    }

    public String getNoc_from_landlord_or_govt() {
        return noc_from_landlord_or_govt;
    }

    public void setNoc_from_landlord_or_govt(String noc_from_landlord_or_govt) {
        this.noc_from_landlord_or_govt = noc_from_landlord_or_govt;
    }

    public String getApproval_from_govt() {
        return approval_from_govt;
    }

    public void setApproval_from_govt(String approval_from_govt) {
        this.approval_from_govt = approval_from_govt;
    }

    public String getYearly_festival_conducted() {
        return yearly_festival_conducted;
    }

    public void setYearly_festival_conducted(String yearly_festival_conducted) {
        this.yearly_festival_conducted = yearly_festival_conducted;
    }

    public String getSurvey_pavti_no_rel_amenities() {
        return survey_pavti_no_rel_amenities;
    }

    public void setSurvey_pavti_no_rel_amenities(String survey_pavti_no_rel_amenities) {
        this.survey_pavti_no_rel_amenities = survey_pavti_no_rel_amenities;
    }

    public String getMashal_rel_amenities() {
        return mashal_rel_amenities;
    }

    public void setMashal_rel_amenities(String mashal_rel_amenities) {
        this.mashal_rel_amenities = mashal_rel_amenities;
    }

    public String getRa_total_no_of_floors() {
        return ra_total_no_of_floors;
    }

    public void setRa_total_no_of_floors(String ra_total_no_of_floors) {
        this.ra_total_no_of_floors = ra_total_no_of_floors;
    }

    String structure_religious_other;
    String structure_type_amenities;
    String structure_amenities_other;
    String name_of_structure;
    String type_of_diety;
    String type_of_diety_other;
    String name_of_diety;
    String category_of_faith;
    String category_of_faith_other;
    String sub_category_of_faith;
    String religion_of_structure_belongs;
    String structure_ownership_status;
    String name_of_trust_or_owner;
    String nature_of_structure;
    String construction_material;
    String daily_visited_people_count;
    String tenement_number_rel_amenities;
    String tenement_doc_used;
    String tenement_doc_other;
    String is_structure_registered;
    String structure_registered_with;
    String other_religious_authority;
    String name_of_trustee;
    String name_of_landowner;
    String noc_from_landlord_or_govt;
    String approval_from_govt;
    String yearly_festival_conducted;
    String survey_pavti_no_rel_amenities;
    String mashal_rel_amenities;
    String ra_total_no_of_floors;
}

