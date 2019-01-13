package com.codingzero.saam.common;

public class IdentifierKey {

    private String applicationId;
    private IdentifierType type;
    private String content;

    public IdentifierKey(String applicationId, IdentifierType type, String content) {
        this.applicationId = applicationId;
        this.type = type;
        this.content = content;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public IdentifierType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
