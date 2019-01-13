package com.codingzero.saam.app.requests;


import com.codingzero.saam.common.IdentifierType;

public class IdentifierVerificationCodeGenerateRequest {

    private String applicationId;
    private String userId;
    private IdentifierType identifierType;
    private String identifier;
    private long timeout;

    public IdentifierVerificationCodeGenerateRequest(String applicationId, String userId,
                                                     IdentifierType identifierType,
                                                     String identifier, long timeout) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.identifierType = identifierType;
        this.identifier = identifier;
        this.timeout = timeout;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public IdentifierType getIdentifierType() {
        return identifierType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public long getTimeout() {
        return timeout;
    }
}
