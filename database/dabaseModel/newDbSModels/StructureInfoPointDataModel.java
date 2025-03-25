package com.igenesys.database.dabaseModel.newDbSModels;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "structureInfoPointDataTable")
public class StructureInfoPointDataModel implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    // variable for our id.
    private String structure_id;
    private String structSampleGlobalid;
    private String surveyUniqueIdNumber;
    private String grid_number;
    private String area_name;
    private String cluster_name;
    private String phase_name;
    private String work_area_name;
    private String hut_number;
    private String structure_name;
    private String structure_usage;
    private int no_of_floors;
    private String address;
    private String tenement_number;
    private String source_of_possession;
    private String existence_since;
    private String structure_since;
    private String structure_status;
    private String surveyor_name;
    private String remarks;
    private String geometry;
    private String obejctId;
    private String globalId;
    private boolean isUploaded;
    private Date date;
    private Date lastEditedDate;

    public StructureInfoPointDataModel(){}

    public StructureInfoPointDataModel(@NonNull String structure_id, String structSampleGlobalid, String surveyUniqueIdNumber, String grid_number, String area_name,
                                       String cluster_name, String phase_name, String work_area_name, String hut_number,
                                       String structure_name, String structure_usage, int no_of_floors, String address,
                                       String tenement_number, String source_of_possession, String existence_since,
                                       String structure_since, String structure_status, String surveyor_name, String remarks,
                                       String geometry, String obejctId, String globalId, boolean isUploaded, Date date, Date lastEditedDate, String zone_no, String ward_no, String sector_no,String country,String state,String city) {
        this.structure_id = structure_id;
        this.structSampleGlobalid = structSampleGlobalid;
        this.surveyUniqueIdNumber = surveyUniqueIdNumber;
        this.grid_number = grid_number;
        this.area_name = area_name;
        this.cluster_name = cluster_name;
        this.phase_name = phase_name;
        this.work_area_name = work_area_name;
        this.hut_number = hut_number;
        this.structure_name = structure_name;
        this.structure_usage = structure_usage;
        this.no_of_floors = no_of_floors;
        this.address = address;
        this.tenement_number = tenement_number;
        this.source_of_possession = source_of_possession;
        this.existence_since = existence_since;
        this.structure_since = structure_since;
        this.structure_status = structure_status;
        this.surveyor_name = surveyor_name;
        this.remarks = remarks;
        this.geometry = geometry;
        this.obejctId = obejctId;
        this.globalId = globalId;
        this.isUploaded = isUploaded;
        this.date = date;
        this.lastEditedDate = lastEditedDate;
        this.zone_no=zone_no;
        this.ward_no=ward_no;
        this.sector_no=sector_no;
        this.country_name=country;
        this.state_name=state;
        this.city_name=city;
    }

    @Ignore
    public StructureInfoPointDataModel(@NonNull String structure_id, String grid_number, String area_name, String cluster_name, String phase_name, String work_area_name, String hut_number,
                                       String structure_name, String structure_usage, int no_of_floors, String address, String tenement_number, String source_of_possession,
                                       String existence_since, String structure_since, String structure_status, String surveyor_name, String geometry, Date lastEditedDate) {
        this.structure_id = structure_id;
        this.grid_number = grid_number;
        this.area_name = area_name;
        this.cluster_name = cluster_name;
        this.phase_name = phase_name;
        this.work_area_name = work_area_name;
        this.hut_number = hut_number;
        this.structure_name = structure_name;
        this.structure_usage = structure_usage;
        this.no_of_floors = no_of_floors;
        this.address = address;
        this.tenement_number = tenement_number;
        this.source_of_possession = source_of_possession;
        this.existence_since = existence_since;
        this.structure_since = structure_since;
        this.structure_status = structure_status;
        this.surveyor_name = surveyor_name;
        this.geometry = geometry;
        this.lastEditedDate = lastEditedDate;
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public Date getLastEditedDate() {
        return lastEditedDate;
    }

    public void setLastEditedDate(Date lastEditedDate) {
        this.lastEditedDate = lastEditedDate;
    }

    public String getObejctId() {
        return obejctId;
    }

    public void setObejctId(String obejctId) {
        this.obejctId = obejctId;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public String getSurveyUniqueIdNumber() {
        return surveyUniqueIdNumber;
    }

    public void setSurveyUniqueIdNumber(String surveyUniqueIdNumber) {
        this.surveyUniqueIdNumber = surveyUniqueIdNumber;
    }

    public String getHut_number() {
        return hut_number;
    }

    public void setHut_number(String hut_number) {
        this.hut_number = hut_number;
    }

    public String getSource_of_possession() {
        return source_of_possession;
    }

    public void setSource_of_possession(String source_of_possession) {
        this.source_of_possession = source_of_possession;
    }

    public String getStructure_since() {
        return structure_since;
    }

    public void setStructure_since(String structure_since) {
        this.structure_since = structure_since;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    @NonNull
    public String getStructure_id() {
        return structure_id;
    }

    public void setStructure_id(@NonNull String structure_id) {
        this.structure_id = structure_id;
    }

    public String getStructSampleGlobalid() {
        return structSampleGlobalid;
    }

    public void setStructSampleGlobalid(String structSampleGlobalid) {
        this.structSampleGlobalid = structSampleGlobalid;
    }

    public String getGrid_number() {
        return grid_number;
    }

    public void setGrid_number(String grid_number) {
        this.grid_number = grid_number;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getCluster_name() {
        return cluster_name;
    }

    public void setCluster_name(String cluster_name) {
        this.cluster_name = cluster_name;
    }

    public String getPhase_name() {
        return phase_name;
    }

    public void setPhase_name(String phase_name) {
        this.phase_name = phase_name;
    }

    public String getWork_area_name() {
        return work_area_name;
    }

    public void setWork_area_name(String work_area_name) {
        this.work_area_name = work_area_name;
    }

    public String getStructure_name() {
        return structure_name;
    }

    public void setStructure_name(String structure_name) {
        this.structure_name = structure_name;
    }

    public String getStructure_usage() {
        return structure_usage;
    }

    public void setStructure_usage(String structure_usage) {
        this.structure_usage = structure_usage;
    }

    public int getNo_of_floors() {
        return no_of_floors;
    }

    public void setNo_of_floors(int no_of_floors) {
        this.no_of_floors = no_of_floors;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTenement_number() {
        return tenement_number;
    }

    public void setTenement_number(String tenement_number) {
        this.tenement_number = tenement_number;
    }

    public String getExistence_since() {
        return existence_since;
    }

    public void setExistence_since(String existence_since) {
        this.existence_since = existence_since;
    }

    public String getStructure_status() {
        return structure_status;
    }

    public void setStructure_status(String structure_status) {
        this.structure_status = structure_status;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public String getCreated_user() {
        return created_user;
    }

    public void setCreated_user(String created_user) {
        this.created_user = created_user;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public String getLast_edited_user() {
        return last_edited_user;
    }

    public void setLast_edited_user(String last_edited_user) {
        this.last_edited_user = last_edited_user;
    }

    public Date getLast_edited_date() {
        return last_edited_date;
    }

    public void setLast_edited_date(Date last_edited_date) {
        this.last_edited_date = last_edited_date;
    }

    public String getWard_no() {
        return ward_no;
    }

    public void setWard_no(String ward_no) {
        this.ward_no = ward_no;
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

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getSector_no() {
        return sector_no;
    }

    public void setSector_no(String sector_no) {
        this.sector_no = sector_no;
    }

    /*
        Rohit
         */
    private String created_user;
    private Date created_date;
    private String last_edited_user;
    private Date last_edited_date;
    private String ward_no;
    private String zone_no;

    private String nagar_name;

    private String society_name;
    private String street_name;
    private String landmark_name;
    private String pincode;
    private String city_name;
    private String state_name;
    private String country_name;
    private String sector_no;

    /*
    Rohit
     */
    public StructureInfoPointDataModel(@NonNull String structure_id, String structSampleGlobalid, String surveyUniqueIdNumber, String grid_number, String area_name,
                                       String cluster_name, String phase_name, String work_area_name, String hut_number,
                                       String structure_name, String structure_usage, int no_of_floors, String address,
                                       String tenement_number, String source_of_possession, String existence_since,
                                       String structure_since, String structure_status, String surveyor_name, String remarks,
                                       String geometry, String obejctId, String globalId, boolean isUploaded, Date date, String ward, String sector, String zone, Date lastEditedDate,String country,String state,String city) {
        this.structure_id = structure_id;
        this.structSampleGlobalid = structSampleGlobalid;
        this.surveyUniqueIdNumber = surveyUniqueIdNumber;
        this.grid_number = grid_number;
        this.area_name = area_name;
        this.cluster_name = cluster_name;
        this.phase_name = phase_name;
        this.work_area_name = work_area_name;
        this.hut_number = hut_number;
        this.structure_name = structure_name;
        this.structure_usage = structure_usage;
        this.no_of_floors = no_of_floors;
        this.address = address;
        this.tenement_number = tenement_number;
        this.source_of_possession = source_of_possession;
        this.existence_since = existence_since;
        this.structure_since = structure_since;
        this.structure_status = structure_status;
        this.surveyor_name = surveyor_name;
        this.remarks = remarks;
        this.geometry = geometry;
        this.obejctId = obejctId;
        this.globalId = globalId;
        this.isUploaded = isUploaded;
        this.date = date;
        this.lastEditedDate = lastEditedDate;
        this.ward_no = ward;
        this.sector_no = sector;
        this.zone_no = zone;
        this.country_name = country;
        this.state_name = state;
        this.city_name = city;
    }
}
