package com.igenesys.model;

public class AttachmentAddedResponse {

    private AddAttachmentResult addAttachmentResult;

    public AddAttachmentResult getAddAttachmentResult() {
        return addAttachmentResult;
    }

    public void setAddAttachmentResult(AddAttachmentResult addAttachmentResult) {
        this.addAttachmentResult = addAttachmentResult;
    }

    public static class AddAttachmentResult {

        private Integer objectId;

        private Boolean success;

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

    }
}
