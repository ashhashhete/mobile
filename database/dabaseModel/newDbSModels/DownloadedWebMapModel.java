package com.igenesys.database.dabaseModel.newDbSModels;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "downloadedWebMapInfoDataTable")
public class DownloadedWebMapModel implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = false)
    private String workAreaName;
    private String userName;
    private String pathAddress;
    private String zoomLevel;
    private String geometry;
    private Date createdOn;
    private Date updatedOn;

    private Date surveyStartDate;

    private Date surveyEndDate;

    public DownloadedWebMapModel(@NonNull String workAreaName, String userName, String pathAddress, String zoomLevel, String geometry, Date createdOn, Date updatedOn,Date surveyStartDate,Date surveyEndDate) {
        this.workAreaName = workAreaName;
        this.userName = userName;
        this.pathAddress = pathAddress;
        this.zoomLevel = zoomLevel;
        this.geometry = geometry;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.surveyStartDate=surveyStartDate;
        this.surveyEndDate=surveyEndDate;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    public String getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(String zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    @NonNull
    public String getWorkAreaName() {
        return workAreaName;
    }

    public void setWorkAreaName(@NonNull String workAreaName) {
        this.workAreaName = workAreaName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPathAddress() {
        return pathAddress;
    }

    public void setPathAddress(String pathAddress) {
        this.pathAddress = pathAddress;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Date getSurveyStartDate() {
        return surveyStartDate;
    }

    public void setSurveyStartDate(Date surveyStartDate) {
        this.surveyStartDate = surveyStartDate;
    }

    public Date getSurveyEndDate() {
        return surveyEndDate;
    }

    public void setSurveyEndDate(Date surveyEndDate) {
        this.surveyEndDate = surveyEndDate;
    }
}
