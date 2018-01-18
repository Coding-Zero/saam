package com.codingzero.saam.app;


public class IdentifierAssignRequest {

    private String applicationId;
    private String userId;
    private String code;
    private String identifier;

    public IdentifierAssignRequest(String applicationId, String userId, String code, String identifier) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.code = code;
        this.identifier = identifier;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getCode() {
        return code;
    }

    public String getIdentifier() {
        return identifier;
    }
}
