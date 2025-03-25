package com.igenesys.model;

import java.util.ArrayList;
import java.util.List;

public class ResultQueryModel {
    private List<Object> features = new ArrayList<Object>();
    private List<Object> geometries = new ArrayList<Object>();

    public List<Object> getFeatures() {
        if (features == null)
            return new ArrayList<>();

        return features;
    }

    public void setFeatures(List<Object> features) {
        this.features = features;
    }

    public List<Object> getGeometries() {
        return geometries;
    }

    public void setGeometries(List<Object> geometries) {
        this.geometries = geometries;
    }
}
