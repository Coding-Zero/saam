package com.codingzero.saam.app.requests;


import com.codingzero.saam.common.IdentifierType;

public class IdentifierVerifyRequest {

    private String applicationId;
    private String userId;
    private IdentifierType identifierType;
    private String identifier;
    private String verificationCode;

    public IdentifierVerifyRequest(String applicationId, String userId, IdentifierType identifierType,
                                   String identifier, String verificationCode) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.identifierType = identifierType;
        this.identifier = identifier;
        this.verificationCode = verificationCode;
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

    public String getVerificationCode() {
        return verificationCode;
    }
}
