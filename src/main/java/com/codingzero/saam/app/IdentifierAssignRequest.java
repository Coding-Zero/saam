package com.codingzero.saam.app;


import com.codingzero.saam.common.IdentifierType;

public class IdentifierAssignRequest {

    private String applicationId;
    private String userId;
    private IdentifierType type;
    private String identifier;

    public IdentifierAssignRequest(String applicationId, String userId, IdentifierType type, String identifier) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.type = type;
        this.identifier = identifier;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public IdentifierType getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }
}
