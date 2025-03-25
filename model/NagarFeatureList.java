package com.igenesys.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NagarFeatureList {
    @SerializedName("attributes")
    @Expose
    private NagarAttributes attributes;

    public NagarAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(NagarAttributes attributes) {
        this.attributes = attributes;
    }
}
