package com.igenesys.model;


import android.graphics.Bitmap;

public class ChildTocModel {

    String name;
    Bitmap icon;

    public ChildTocModel(String name, Bitmap icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }
}
