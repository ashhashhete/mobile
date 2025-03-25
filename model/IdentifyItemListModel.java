package com.igenesys.model;

import com.esri.arcgisruntime.data.Feature;

public class IdentifyItemListModel {
    int position;
    private String header, values;
    Feature feature;

    public IdentifyItemListModel(int position, String header, String values) {
        this.position = position;
        this.header = header;
        this.values = values;
    }

    public IdentifyItemListModel(int position, String header, String values, Feature feature) {
        this.position = position;
        this.header = header;
        this.values = values;
        this.feature = feature;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }
}
