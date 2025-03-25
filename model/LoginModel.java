package com.igenesys.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class LoginModel {

    @SerializedName("status")
    public Status status;
    public class Status {
        @SerializedName("statusCode")
        public int statusCode;

        @SerializedName("errorCode")
        public String errorCode;

        @SerializedName("errorMessage")
        public String errorMessage;

    }

    @SerializedName("data")
    public Data data;

    public class Data {
        @SerializedName("isDeviceRegistered")
        public boolean isDeviceRegistered;

        @SerializedName("user")
        public User user;

        public User getUser() {
            return user;
        }

        @SerializedName("workArea")
        public List<WorkArea> workArea = new ArrayList();
    }

    public class WorkArea {

        @SerializedName("objectid")
        public String objectid;
        @SerializedName("globalid")
        public String globalid;
        @SerializedName("workAreaName")
        public String workAreaName;
        @SerializedName("userName")
        public String userName;
        @SerializedName("workAreaStatus")
        public String workAreaStatus;
        @SerializedName("wardNo")
        public String wardNo;
        @SerializedName("sectorNo")
        public String sectorNo;
        @SerializedName("zoneNo")
        public String zoneNo;


    }

    public class User {

        @SerializedName("userId")
        public String userId;
        @SerializedName("userName")
        public String userName;
        @SerializedName("firstName")
        public String firstName;
        @SerializedName("lastName")
        public String lastName;

        @SerializedName("mobileNumber")
        public String mobileNumber;
        @SerializedName("email")
        public String email;
        @SerializedName("employeeId")
        public String employeeId;

        @SerializedName("designationName")
        public String designationName;
        @SerializedName("userRoles")
        List<String> userRoles;

    }

}
