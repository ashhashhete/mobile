package com.igenesys.model;

public class DahboardListModel {

    String inputType,clusterName, date, statusTxt;
    int statusBackColor;

    public DahboardListModel(String inputType, String clusterName, String date, String statusTxt, int statusBackColor) {
        this.inputType = inputType;
        this.clusterName = clusterName;
        this.date = date;
        this.statusTxt = statusTxt;
        this.statusBackColor = statusBackColor;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatusTxt() {
        return statusTxt;
    }

    public void setStatusTxt(String statusTxt) {
        this.statusTxt = statusTxt;
    }

    public int getStatusBackColor() {
        return statusBackColor;
    }

    public void setStatusBackColor(int statusBackColor) {
        this.statusBackColor = statusBackColor;
    }
}
