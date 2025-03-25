package com.igenesys.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NagarAttributes {
    @SerializedName("nagarname")
    @Expose
    private String nagarname;

    public String getNagarname() {
        return nagarname;
    }

    public void setNagarname(String nagarname) {
        this.nagarname = nagarname;
    }
}
