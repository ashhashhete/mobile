
package com.igenesys.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AfterLoginData {

    @SerializedName("status")
    @Expose
    private AfterLoginStatus status;
    @SerializedName("data")
    @Expose
    private AfterLoginDataCollection data;

    public AfterLoginStatus getStatus() {
        return status;
    }

    public void setStatus(AfterLoginStatus status) {
        this.status = status;
    }

    public AfterLoginDataCollection getData() {
        return data;
    }

    public void setData(AfterLoginDataCollection data) {
        this.data = data;
    }

}


