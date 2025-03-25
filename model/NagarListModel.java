package com.igenesys.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NagarListModel {
    @SerializedName("objectIdFieldName")
    @Expose
    private String objectIdFieldName;
    @SerializedName("globalIdFieldName")
    @Expose
    private String globalIdFieldName;
    @SerializedName("geometryType")
    @Expose
    private String geometryType;
    @SerializedName("hasZ")
    @Expose
    private Boolean hasZ;

    @SerializedName("features")
    @Expose
    private List<NagarFeatureList> features;

    public String getObjectIdFieldName() {
        return objectIdFieldName;
    }

    public void setObjectIdFieldName(String objectIdFieldName) {
        this.objectIdFieldName = objectIdFieldName;
    }

    public String getGlobalIdFieldName() {
        return globalIdFieldName;
    }

    public void setGlobalIdFieldName(String globalIdFieldName) {
        this.globalIdFieldName = globalIdFieldName;
    }

    public String getGeometryType() {
        return geometryType;
    }

    public void setGeometryType(String geometryType) {
        this.geometryType = geometryType;
    }

    public Boolean getHasZ() {
        return hasZ;
    }

    public void setHasZ(Boolean hasZ) {
        this.hasZ = hasZ;
    }


    public List<NagarFeatureList> getFeatures() {
        return features;
    }

    public void setFeatures(List<NagarFeatureList> features) {
        this.features = features;
    }
}
