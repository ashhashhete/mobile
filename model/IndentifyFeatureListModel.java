package com.igenesys.model;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.layers.FeatureLayer;

import java.util.ArrayList;

public class IndentifyFeatureListModel {

    String layerName, header1, header2, header3, globalId;
    Feature feature;
    FeatureLayer featureLayer;
    ArrayList<IdentifyItemListModel> identifyItemListModels;

    public IndentifyFeatureListModel(String layerName, String header1, String header2, String header3, Feature feature, FeatureLayer featureLayer, ArrayList<IdentifyItemListModel> identifyItemListModels) {
        this.layerName = layerName;
        this.header1 = header1;
        this.header2 = header2;
        this.header3 = header3;
        this.feature = feature;
        this.featureLayer = featureLayer;
        this.identifyItemListModels = identifyItemListModels;
    }

    public IndentifyFeatureListModel(String layerName, String header1, String header2, String header3, String globalId, Feature feature, FeatureLayer featureLayer) {
        this.layerName = layerName;
        this.header1 = header1;
        this.header2 = header2;
        this.header3 = header3;
        this.globalId = globalId;
        this.feature = feature;
        this.featureLayer = featureLayer;
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public FeatureLayer getFeatureLayer() {
        return featureLayer;
    }

    public void setFeatureLayer(FeatureLayer featureLayer) {
        this.featureLayer = featureLayer;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public String getHeader1() {
        return header1;
    }

    public void setHeader1(String header1) {
        this.header1 = header1;
    }

    public String getHeader2() {
        return header2;
    }

    public void setHeader2(String header2) {
        this.header2 = header2;
    }

    public String getHeader3() {
        return header3;
    }

    public void setHeader3(String header3) {
        this.header3 = header3;
    }

    public ArrayList<IdentifyItemListModel> getIdentifyItemListModels() {
        return identifyItemListModels;
    }

    public void setIdentifyItemListModels(ArrayList<IdentifyItemListModel> identifyItemListModels) {
        this.identifyItemListModels = identifyItemListModels;
    }
}
