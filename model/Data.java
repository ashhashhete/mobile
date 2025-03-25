package com.igenesys.model;

public class Data {

    public String fileName;
    public String extension;
    public int fileSize;
    public String filePath;
    public boolean checkedAllPermissions;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isCheckedAllPermissions() {
        return checkedAllPermissions;
    }

    public void setCheckedAllPermissions(boolean checkedAllPermissions) {
        this.checkedAllPermissions = checkedAllPermissions;
    }
}