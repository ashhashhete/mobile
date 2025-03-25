package com.igenesys.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DomainModel {

    @SerializedName("status")
    public JSONObject status = new JSONObject();

    @SerializedName("data")
    public List<Datum> data = new ArrayList();

    public class Datum {

        @SerializedName("domainType")
        public String domainType;
        @SerializedName("domainName")
        public String domainName;
        @SerializedName("description")
        public String description;
        @SerializedName("code")
        public String code;

    }
}
