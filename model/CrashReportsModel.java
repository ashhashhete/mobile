package com.igenesys.model;

public class CrashReportsModel {

    String name,path,lastModifiedOn;

    public CrashReportsModel(String name, String path, String lastModifiedOn) {
        this.name = name;
        this.path = path;
        this.lastModifiedOn = lastModifiedOn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLastModifiedOn() {
        return lastModifiedOn;
    }

    public void setLastModifiedOn(String lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }
}
