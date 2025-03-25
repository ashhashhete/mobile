package com.igenesys.database.dabaseModel.newDbSModels;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.igenesys.model.AttachmentItemList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = "mediaInfoDDataTable")
public class MediaInfoDataModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    // variable for our id.
    private int mediaId;
    private String content_type;
    private String relative_path;
    private String filename;
    private int data_size;
    private String md5_checksum;
    private String item_url;
    private short file_upload_checked;
    private short file_integrity_checked;
    private String parent_table_name;
    private String parent_unique_id;
    private String rel_globalid;
    private String obejctId;
    private String globalId;
    private boolean isUploaded;
    private Date date;
    private Date lastEditedDate;

    private byte[] thumb_imp_content;
    private String serverFileDirectoryPath;
    private int serverFileSize;

    public MediaInfoDataModel() {

    }

    public String getServerFileDirectoryPath() {
        return serverFileDirectoryPath;
    }

    public void setServerFileDirectoryPath(String serverFileDirectoryPath) {
        this.serverFileDirectoryPath = serverFileDirectoryPath;
    }

    public int getServerFileSize() {
        return serverFileSize;
    }

    public void setServerFileSize(int serverFileSize) {
        this.serverFileSize = serverFileSize;
    }

    public boolean isWholeObjectDeleted() {
        return wholeObjectDeleted;
    }

    public void setWholeObjectDeleted(boolean wholeObjectDeleted) {
        this.wholeObjectDeleted = wholeObjectDeleted;
    }

    private boolean wholeObjectDeleted;

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    private boolean isLocal;

    public int getUploadMediaCount() {
        return uploadMediaCount;
    }

    public void setUploadMediaCount(int uploadMediaCount) {
        this.uploadMediaCount = uploadMediaCount;
    }

    private int uploadMediaCount;

    public ArrayList<String> getUploadMediaList() {
        return uploadMediaList;
    }

    public void setUploadMediaList(ArrayList<String> uploadMediaList) {
        this.uploadMediaList = uploadMediaList;
    }

    private ArrayList<String> uploadMediaList;

    public List<MediaInfoDataModel> getTotalMediaList() {
        return totalMediaList;
    }

    public void setTotalMediaList(List<MediaInfoDataModel> totalMediaList) {
        this.totalMediaList = totalMediaList;
    }

//    public List<MediaInfoDataModel> getMediaList() {
//        return mediaList;
//    }
//
//    public void setMediaList(List<MediaInfoDataModel> mediaList) {
//        this.mediaList = mediaList;
//    }

    //    List<MediaInfoDataModel> mediaList;
    List<AttachmentItemList> attachmentItemLists;

    public List<AttachmentItemList> getAttachmentItemLists() {
        return attachmentItemLists;
    }

    public void setAttachmentItemLists(List<AttachmentItemList> attachmentItemLists) {
        this.attachmentItemLists = attachmentItemLists;
    }


    List<MediaInfoDataModel> totalMediaList;

    public List<MediaInfoDataModel> getDeleteTotalMediaList() {
        return deleteTotalMediaList;
    }

    public void setDeleteTotalMediaList(List<MediaInfoDataModel> deleteTotalMediaList) {
        this.deleteTotalMediaList = deleteTotalMediaList;
    }

    List<MediaInfoDataModel> deleteTotalMediaList;

    public boolean isDeletedItem() {
        return isDeletedItem;
    }

    public void setDeletedItem(boolean deletedItem) {
        isDeletedItem = deletedItem;
    }

    boolean isDeletedItem;

    public boolean isHaveDelete() {
        return isHaveDelete;
    }

    public void setHaveDelete(boolean haveDelete) {
        isHaveDelete = haveDelete;
    }

    private boolean isHaveDelete;

    public boolean isSurveyorDoc() {
        return isSurveyorDoc;
    }

    public void setSurveyorDoc(boolean surveyorDoc) {
        isSurveyorDoc = surveyorDoc;
    }

    private boolean isSurveyorDoc;

    public MediaInfoDataModel(int mediaId, String content_type, String filename, int data_size, String md5_checksum, String item_url, short file_upload_checked,
                              short file_integrity_checked, String parent_table_name, String parent_unique_id, String rel_globalid, String relative_path,
                              String obejctId, String globalId, boolean isUploaded, Date date, Date lastEditedDate) {
        this.mediaId = mediaId;
        this.content_type = content_type;
        this.filename = filename;
        this.data_size = data_size;
        this.md5_checksum = md5_checksum;
        this.item_url = item_url;
        this.file_upload_checked = file_upload_checked;
        this.file_integrity_checked = file_integrity_checked;
        this.parent_table_name = parent_table_name;
        this.parent_unique_id = parent_unique_id;
        this.rel_globalid = rel_globalid;
        this.relative_path = relative_path;
        this.obejctId = obejctId;
        this.globalId = globalId;
        this.isUploaded = isUploaded;
        this.date = date;
        this.lastEditedDate = lastEditedDate;
    }

    @Ignore
    public MediaInfoDataModel(String content_type, String filename,
                              int data_size, String md5_checksum,
                              String item_url, short file_upload_checked,
                              short file_integrity_checked,
                              String parent_table_name, String parent_unique_id,
                              String rel_globalid, String relative_path,
                              String obejctId, String globalId, boolean isUploaded, Date date, Date lastEditedDate) {
        this.content_type = content_type;
        this.filename = filename;
        this.data_size = data_size;
        this.md5_checksum = md5_checksum;
        this.item_url = item_url;
        this.file_upload_checked = file_upload_checked;
        this.file_integrity_checked = file_integrity_checked;
        this.parent_table_name = parent_table_name;
        this.parent_unique_id = parent_unique_id;
        this.rel_globalid = rel_globalid;
        this.relative_path = relative_path;
        this.obejctId = obejctId;
        this.globalId = globalId;
        this.isUploaded = isUploaded;
        this.date = date;
        this.lastEditedDate = lastEditedDate;
    }

    @Ignore
    public MediaInfoDataModel(String filename, String item_url) {
        this.filename = filename;
        this.item_url = item_url;
    }

    public String getRelative_path() {
        return relative_path;
    }

    public void setRelative_path(String relative_path) {
        this.relative_path = relative_path;
    }

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

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getData_size() {
        return data_size;
    }

    public void setData_size(int data_size) {
        this.data_size = data_size;
    }

    public String getMd5_checksum() {
        return md5_checksum;
    }

    public void setMd5_checksum(String md5_checksum) {
        this.md5_checksum = md5_checksum;
    }

    public String getItem_url() {
        return item_url;
    }

    public void setItem_url(String item_url) {
        this.item_url = item_url;
    }

    public short getFile_upload_checked() {
        return file_upload_checked;
    }

    public void setFile_upload_checked(short file_upload_checked) {
        this.file_upload_checked = file_upload_checked;
    }

    public short getFile_integrity_checked() {
        return file_integrity_checked;
    }

    public void setFile_integrity_checked(short file_integrity_checked) {
        this.file_integrity_checked = file_integrity_checked;
    }

    public String getParent_table_name() {
        return parent_table_name;
    }

    public void setParent_table_name(String parent_table_name) {
        this.parent_table_name = parent_table_name;
    }

    public String getParent_unique_id() {
        return parent_unique_id;
    }

    public void setParent_unique_id(String parent_unique_id) {
        this.parent_unique_id = parent_unique_id;
    }

    public String getRel_globalid() {
        return rel_globalid;
    }

    public void setRel_globalid(String rel_globalid) {
        this.rel_globalid = rel_globalid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return filename;
    }


    private String document_type;

    public String getDocument_type() {
        return document_type;
    }

    public void setDocument_type(String document_type) {
        this.document_type = document_type;
    }

    public String getName_on_document() {
        return name_on_document;
    }

    public void setName_on_document(String name_on_document) {
        this.name_on_document = name_on_document;
    }

    public String getDate_on_document() {
        return date_on_document;
    }

    public void setDate_on_document(String date_on_document) {
        this.date_on_document = date_on_document;
    }

    public String getDocument_category() {
        return document_category;
    }

    public void setDocument_category(String document_category) {
        this.document_category = document_category;
    }

    public String getDocument_remarks() {
        return document_remarks;
    }

    public void setDocument_remarks(String document_remarks) {
        this.document_remarks = document_remarks;
    }

    private String name_on_document;
    private String date_on_document;
    private String document_category;
    private String document_remarks;

    public String getDocument_Title() {
        return document_Title;
    }

    public void setDocument_Title(String document_Title) {
        this.document_Title = document_Title;
    }

    private String document_Title;


    public MediaInfoDataModel(String objectId, String filename, String date_on_document, String rel_globalid, String relative_path,
                              String globalId, String document_category, String document_type,
                              String document_remarks, String name_on_document, String parent_unique_id) {
        this.obejctId = objectId;
        this.filename = filename;
        this.date_on_document = date_on_document;
        this.rel_globalid = rel_globalid;
        this.relative_path = relative_path;
        this.globalId = globalId;
        this.document_category = document_category;
        this.document_type = document_type;
        this.document_remarks = document_remarks;
        this.name_on_document = name_on_document;
        this.parent_unique_id = parent_unique_id;
    }

    public MediaInfoDataModel(String objectId, String filename, String item_url, String rel_globalid, String relative_path,
                              String globalId, String document_category, String document_type,
                              String document_remarks, String parent_unique_id, boolean have, boolean local) {
        this.obejctId = objectId;
        this.filename = filename;
        this.item_url = item_url;
        this.rel_globalid = rel_globalid;
        this.relative_path = relative_path;
        this.globalId = globalId;
        this.document_category = document_category;
        this.document_type = document_type;
        this.document_remarks = document_remarks;
        this.parent_unique_id = parent_unique_id;
        this.isHaveDelete = have;
    }


    @Ignore
    public MediaInfoDataModel(String content_type, String filename,
                              int data_size, String md5_checksum,
                              String item_url, short file_upload_checked,
                              short file_integrity_checked,
                              String parent_table_name, String parent_unique_id,
                              String rel_globalid, String relative_path,
                              String obejctId, String globalId, boolean isUploaded, Date date, Date lastEditedDate, String document_category, String document_type,
                              String document_remarks, String name_on_document) {
        this.content_type = content_type;
        this.filename = filename;
        this.data_size = data_size;
        this.md5_checksum = md5_checksum;
        this.item_url = item_url;
        this.file_upload_checked = file_upload_checked;
        this.file_integrity_checked = file_integrity_checked;
        this.parent_table_name = parent_table_name;
        this.parent_unique_id = parent_unique_id;
        this.rel_globalid = rel_globalid;
        this.relative_path = relative_path;
        this.obejctId = obejctId;
        this.globalId = globalId;
        this.isUploaded = isUploaded;
        this.date = date;
        this.lastEditedDate = lastEditedDate;
        this.document_category = document_category;
        this.document_type = document_type;
        this.document_remarks = document_remarks;
        this.name_on_document = name_on_document;
    }


    public ArrayList<String> getTempMediaList() {
        return tempMediaList;
    }

    public void setTempMediaList(ArrayList<String> tempMediaList) {
        this.tempMediaList = tempMediaList;
    }

    private ArrayList<String> tempMediaList;


    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    private String year;

    public MediaInfoDataModel(String objectId, String filename, String date_on_document, String rel_globalid, String relative_path,
                              String globalId, String document_category, String document_type,
                              String document_remarks, String name_on_document, String parent_unique_id, String url) {
        this.obejctId = objectId;
        this.filename = filename;
        this.date_on_document = date_on_document;
        this.rel_globalid = rel_globalid;
        this.relative_path = relative_path;
        this.globalId = globalId;
        this.document_category = document_category;
        this.document_type = document_type;
        this.document_remarks = document_remarks;
        this.name_on_document = name_on_document;
        this.parent_unique_id = parent_unique_id;
        this.item_url = url;
    }

    public byte[] getThumb_imp_content() {
        return thumb_imp_content;
    }

    public void setThumb_imp_content(byte[] thumb_imp_content) {
        this.thumb_imp_content = thumb_imp_content;
    }


}
