package com.codingzero.saam.app.requests;


public class PasswordResetRequest {

    private String applicationId;
    private String userId;
    private String resetCode;
    private String newPassword;

    public PasswordResetRequest(String applicationId, String userId, String resetCode, String newPassword) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.resetCode = resetCode;
        this.newPassword = newPassword;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getResetCode() {
        return resetCode;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
