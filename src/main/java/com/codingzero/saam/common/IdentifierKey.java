package com.codingzero.saam.common;

public class IdentifierKey {

    private String applicationId;
    private String content;

    public IdentifierKey(String applicationId, String content) {
        this.applicationId = applicationId;
        this.content = content;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getContent() {
        return content;
    }
}
