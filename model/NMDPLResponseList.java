
package com.igenesys.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class NMDPLResponseList {

    public NMDPLStatus getStatus() {
        return status;
    }

    public void setStatus(NMDPLStatus status) {
        this.status = status;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

    @SerializedName("status")
    @Expose
    private NMDPLStatus status;
    @SerializedName("data")
    @Expose
    private ArrayList<String> data;

}


