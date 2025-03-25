package com.igenesys.database.dabaseModel.newDbSModels;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "aadhaarVerificationData")
public class AadhaarVerificationData implements Serializable {

    @PrimaryKey(autoGenerate = true)
    // variable for our id.
    private int Id;
    private String unit_id;
    private String hoh_id;
    private String hoh_adhaar_no;
    private String gen_otp_transaction_id;
    private String gen_otp_timestamp;
    private String gen_otp_ref_id;
    private String gen_otp_message;
    private String ver_otp_transaction_id;
    private String ver_otp_timestamp;
    private String ver_otp_ref_id;
    private String ver_otp_status;
    private String ver_otp_message;
    private double validate_confidence;
    private String validate_status;
    private String validate_differences;
    private String adhaar_verify_by;
    private boolean isAadhaarVerified;
    private String remarks;
    private boolean isUploaded;

    public AadhaarVerificationData() {
        this.unit_id = "";
        this.hoh_id = "";
        this.hoh_adhaar_no = "";
        this.gen_otp_transaction_id = "";
        this.gen_otp_timestamp = "";
        this.gen_otp_ref_id = "";
        this.gen_otp_message = "";
        this.ver_otp_transaction_id = "";
        this.ver_otp_timestamp = "";
        this.ver_otp_ref_id = "";
        this.ver_otp_status = "";
        this.ver_otp_message = "";
        this.validate_confidence = 0.0;
        this.validate_status = "";
        this.validate_differences = "";
        this.adhaar_verify_by = "";
        this.isAadhaarVerified = false;
        this.remarks = "";
        this.isUploaded = false;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(String unit_id) {
        this.unit_id = unit_id;
    }

    public String getHoh_id() {
        return hoh_id;
    }

    public void setHoh_id(String hoh_id) {
        this.hoh_id = hoh_id;
    }

    public String getHoh_adhaar_no() {
        return hoh_adhaar_no;
    }

    public void setHoh_adhaar_no(String hoh_adhaar_no) {
        this.hoh_adhaar_no = hoh_adhaar_no;
    }

    public String getGen_otp_transaction_id() {
        return gen_otp_transaction_id;
    }

    public void setGen_otp_transaction_id(String gen_otp_transaction_id) {
        this.gen_otp_transaction_id = gen_otp_transaction_id;
    }

    public String getGen_otp_timestamp() {
        return gen_otp_timestamp;
    }

    public void setGen_otp_timestamp(String gen_otp_timestamp) {
        this.gen_otp_timestamp = gen_otp_timestamp;
    }

    public String getGen_otp_ref_id() {
        return gen_otp_ref_id;
    }

    public void setGen_otp_ref_id(String gen_otp_ref_id) {
        this.gen_otp_ref_id = gen_otp_ref_id;
    }

    public String getGen_otp_message() {
        return gen_otp_message;
    }

    public void setGen_otp_message(String gen_otp_message) {
        this.gen_otp_message = gen_otp_message;
    }

    public String getVer_otp_transaction_id() {
        return ver_otp_transaction_id;
    }

    public void setVer_otp_transaction_id(String ver_otp_transaction_id) {
        this.ver_otp_transaction_id = ver_otp_transaction_id;
    }

    public String getVer_otp_timestamp() {
        return ver_otp_timestamp;
    }

    public void setVer_otp_timestamp(String ver_otp_timestamp) {
        this.ver_otp_timestamp = ver_otp_timestamp;
    }

    public String getVer_otp_ref_id() {
        return ver_otp_ref_id;
    }

    public void setVer_otp_ref_id(String ver_otp_ref_id) {
        this.ver_otp_ref_id = ver_otp_ref_id;
    }

    public String getVer_otp_status() {
        return ver_otp_status;
    }

    public void setVer_otp_status(String ver_otp_status) {
        this.ver_otp_status = ver_otp_status;
    }

    public String getVer_otp_message() {
        return ver_otp_message;
    }

    public void setVer_otp_message(String ver_otp_message) {
        this.ver_otp_message = ver_otp_message;
    }

    public double getValidate_confidence() {
        return validate_confidence;
    }

    public void setValidate_confidence(double validate_confidence) {
        this.validate_confidence = validate_confidence;
    }

    public String getValidate_status() {
        return validate_status;
    }

    public void setValidate_status(String validate_status) {
        this.validate_status = validate_status;
    }

    public String getValidate_differences() {
        return validate_differences;
    }

    public void setValidate_differences(String validate_differences) {
        this.validate_differences = validate_differences;
    }

    public String getAdhaar_verify_by() {
        return adhaar_verify_by;
    }

    public void setAdhaar_verify_by(String adhaar_verify_by) {
        this.adhaar_verify_by = adhaar_verify_by;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public boolean isAadhaarVerified() {
        return isAadhaarVerified;
    }

    public void setAadhaarVerified(boolean aadhaarVerified) {
        isAadhaarVerified = aadhaarVerified;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}