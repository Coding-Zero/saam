package com.codingzero.saam.app.requests;


public class IdentifierVerificationCodeGenerateRequest {

    private String applicationId;
    private String identifier;
    private long timeout;

    public IdentifierVerificationCodeGenerateRequest(String applicationId,
                                                     String identifier, long timeout) {
        this.applicationId = applicationId;
        this.identifier = identifier;
        this.timeout = timeout;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public long getTimeout() {
        return timeout;
    }
}
