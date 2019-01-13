package com.codingzero.saam.app.requests;


public class PasswordChangeRequest {

    private String applicationId;
    private String userId;
    private String oldPassword;
    private String newPassword;

    public PasswordChangeRequest(String applicationId, String userId, String oldPassword, String newPassword) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
