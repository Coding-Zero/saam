package com.codingzero.saam.app;


public class IdentifierVerifyRequest {

    private String applicationId;
    private String userId;
    private String code;
    private String identifier;
    private String verificationCode;

    public IdentifierVerifyRequest(String applicationId, String userId, String code,
                                   String identifier, String verificationCode) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.code = code;
        this.identifier = identifier;
        this.verificationCode = verificationCode;
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

    public String getVerificationCode() {
        return verificationCode;
    }
}
