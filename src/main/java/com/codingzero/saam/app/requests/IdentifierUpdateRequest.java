package com.codingzero.saam.app.requests;


public class IdentifierUpdateRequest {

    private String applicationId;
    private String userId;
    private String code;
    private String currentIdentifier;
    private String newIdentifier;

    public IdentifierUpdateRequest(String applicationId, String userId, String code,
                                   String currentIdentifier, String newIdentifier) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.code = code;
        this.currentIdentifier = currentIdentifier;
        this.newIdentifier = newIdentifier;
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

    public String getCurrentIdentifier() {
        return currentIdentifier;
    }

    public String getNewIdentifier() {
        return newIdentifier;
    }
}
