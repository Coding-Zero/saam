package com.codingzero.saam.app.responses;

import com.codingzero.saam.common.IdentifierType;

import java.util.Date;


public class IdentifierVerificationCodeResponse {

    private String applicationId;
    private String userId;
    private IdentifierType identifierType;
    private String identifier;
    private String code;
    private long expirationTime;

    public IdentifierVerificationCodeResponse(String applicationId,
                                              String userId,
                                              IdentifierType identifierType,
                                              String identifier,
                                              String code,
                                              Date expirationTime) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.identifierType = identifierType;
        this.identifier = identifier;
        this.code = code;
        this.expirationTime = expirationTime.getTime();
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

    public String getCode() {
        return code;
    }

    public Date getExpirationTime() {
        return new Date(expirationTime);
    }
}
