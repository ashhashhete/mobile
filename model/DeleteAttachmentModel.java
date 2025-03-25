package com.igenesys.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeleteAttachmentModel {

    @SerializedName("status")
    public DeleteAttachmentModel.Status status;

    @SerializedName("data")
    public JSONArray data = new JSONArray();


    public class Status {
        String code;
        int status;
        String message;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
