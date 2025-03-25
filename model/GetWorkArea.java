package com.igenesys.model;


public class GetWorkArea {

    private String workAreaName;
    private String workAreaCode;

    public GetWorkArea(String workAreaName, String workAreaCode) {
        this.workAreaName = workAreaName;
        this.workAreaCode = workAreaCode;
    }

    public String getWorkAreaName() {
        return workAreaName;
    }

    public void setWorkAreaName(String workAreaName) {
        this.workAreaName = workAreaName;
    }

    public String getWorkAreaCode() {
        return workAreaCode;
    }

    public void setWorkAreaCode(String workAreaCode) {
        this.workAreaCode = workAreaCode;
    }

}
