package com.igenesys.model;

import java.util.List;

public class AttachmentQueryResult {

    private List<AttachmentInfo> attachmentInfos;

    public List<AttachmentInfo> getAttachmentInfos() {
        return attachmentInfos;
    }

    public void setAttachmentInfos(List<AttachmentInfo> attachmentInfos) {
        this.attachmentInfos = attachmentInfos;
    }

    public static class AttachmentInfo {

        private Integer id;

        private String contentType;

        private Integer size;

        private String name;

        private String globalId;

        private String parentGlobalId;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGlobalId() {
            return globalId;
        }

        public void setGlobalId(String globalId) {
            this.globalId = globalId;
        }

        public String getParentGlobalId() {
            return parentGlobalId;
        }

        public void setParentGlobalId(String parentGlobalId) {
            this.parentGlobalId = parentGlobalId;
        }

    }

}
