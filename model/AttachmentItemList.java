package com.igenesys.model;

public class AttachmentItemList {

    public int id;
    public String objectId;
    public String contentType;
    public String fileName;
    public String item_url;
    public long size;
    public boolean isDeleted;
    public boolean isUpdated;

    public AttachmentItemList(int id, String fileName, String item_url, boolean isDeleted, boolean isUpdated) {
        this.id = id;
        this.fileName = fileName;
        this.item_url = item_url;
        this.isDeleted = isDeleted;
        this.isUpdated = isUpdated;
    }

    public AttachmentItemList(int id, String objectId, String contentType, String fileName, String item_url, long size, boolean isDeleted, boolean isUpdated) {
        this.id = id;
        this.objectId = objectId;
        this.contentType = contentType;
        this.fileName = fileName;
        this.item_url = item_url;
        this.size = size;
        this.isDeleted = isDeleted;
        this.isUpdated = isUpdated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getItem_url() {
        return item_url;
    }

    public void setItem_url(String item_url) {
        this.item_url = item_url;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean getIsUpdated() {
        return isUpdated;
    }

    public void setIsUpdated(boolean isUpdated) {
        this.isUpdated = isUpdated;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}