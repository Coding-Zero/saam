package com.codingzero.saam.app;

import java.util.Date;


public class IdentifierVerificationCodeResponse {

    private String applicationId;
    private String userId;
    private String identifierPolicyCode;
    private String identifier;
    private String code;
    private long expirationTime;

    public IdentifierVerificationCodeResponse(String applicationId,
                                              String userId,
                                              String identifierPolicyCode,
                                              String identifier,
                                              String code,
                                              Date expirationTime) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.identifierPolicyCode = identifierPolicyCode;
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

    public String getIdentifierPolicyCode() {
        return identifierPolicyCode;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getCode() {
        return code;
    }

    public long getExpirationTime() {
        return expirationTime;
    }
}
