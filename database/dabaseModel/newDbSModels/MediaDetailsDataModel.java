package com.igenesys.database.dabaseModel.newDbSModels;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "mediaDetailsDataTable")
public class MediaDetailsDataModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    // variable for our id.
    private int mediaId;
    private String objectid;
    private String globalid;
    private String content_type;
    private String file_path;
    private String file_name;
    private String file_ext;
    private String rel_globalid;
    private long data_size;
    private String unitId;
    // private int file_order;
    private String remarks;
    private boolean isUploaded;
    private boolean isFileUploaded;

    public MediaDetailsDataModel(String objectid, String globalid, String content_type, String file_path, String file_name,
                                 String file_ext, String rel_globalid, long data_size, String remarks, String unitId, boolean isUploaded,
                                 boolean isFileUploaded) {
        this.objectid = objectid;
        this.globalid = globalid;
        this.content_type = content_type;
        this.file_path = file_path;
        this.file_name = file_name;
        this.file_ext = file_ext;
        this.rel_globalid = rel_globalid;
        this.data_size = data_size;
        this.remarks = remarks;
        this.isUploaded = isUploaded;
        this.isFileUploaded = isFileUploaded;
        this.unitId = unitId;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public String getObjectid() {
        return objectid;
    }

    public void setObjectid(String objectid) {
        this.objectid = objectid;
    }

    public String getGlobalid() {
        return globalid;
    }

    public void setGlobalid(String globalid) {
        this.globalid = globalid;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_ext() {
        return file_ext;
    }

    public void setFile_ext(String file_ext) {
        this.file_ext = file_ext;
    }

    public String getRel_globalid() {
        return rel_globalid;
    }

    public void setRel_globalid(String rel_globalid) {
        this.rel_globalid = rel_globalid;
    }

    public long getData_size() {
        return data_size;
    }

    public void setData_size(long data_size) {
        this.data_size = data_size;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public boolean isFileUploaded() {
        return isFileUploaded;
    }

    public void setFileUploaded(boolean fileUploaded) {
        isFileUploaded = fileUploaded;
    }
}