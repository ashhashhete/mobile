package com.igenesys.model;

import java.util.List;

public class AddRecordResponse {


    private List<AddFeatureResult> addResults = null;

    public List<AddFeatureResult> getAddResults() {
        return addResults;
    }

    public void setAddResults(List<AddFeatureResult> addResults) {
        this.addResults = addResults;
    }


    public static class AddFeatureResult {

        private Integer objectId;

        private Boolean success;
        private String globalId;

        private String filename;

        public String getGlobalId() {
            return globalId;
        }

        public void setGlobalId(String globalId) {
            this.globalId = globalId;
        }

        public Integer getObjectId() {
            return objectId;
        }

        public void setObjectId(Integer objectId) {
            this.objectId = objectId;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }
    }
}
