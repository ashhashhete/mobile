package com.igenesys.model;

import java.io.File;

public class AttachmentListImageDetails {

    String fileName, filePath, MD5_CheckSum;
    File file;

    public AttachmentListImageDetails(String fileName, String filePath, String MD5_CheckSum, File file) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.MD5_CheckSum = MD5_CheckSum;
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMD5_CheckSum() {
        return MD5_CheckSum;
    }

    public void setMD5_CheckSum(String MD5_CheckSum) {
        this.MD5_CheckSum = MD5_CheckSum;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
