package com.codingzero.saam.app;


public class PasswordResetCodeGenerateRequest {

    private String applicationId;
    private String userId;
    private String identifierPolicyCode;
    private String identifier;
    private long timeout;

    public PasswordResetCodeGenerateRequest(String applicationId, String userId,
                                            String identifierPolicyCode, String identifier, long timeout) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.identifierPolicyCode = identifierPolicyCode;
        this.identifier = identifier;
        this.timeout = timeout;
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

    public long getTimeout() {
        return timeout;
    }
}
