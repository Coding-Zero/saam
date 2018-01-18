package com.codingzero.saam.app;

public class UsernamePolicyAddRequest {

    private String applicationId;
    private String code;

    public UsernamePolicyAddRequest(String applicationId, String code) {
        this.applicationId = applicationId;
        this.code = code;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getCode() {
        return code;
    }

}
