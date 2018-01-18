package com.codingzero.saam.app;

import com.codingzero.saam.common.PasswordPolicy;


public class PasswordPolicyUpdateRequest {

    private String applicationId;
    private PasswordPolicy passwordPolicy;

    public PasswordPolicyUpdateRequest(String applicationId, PasswordPolicy passwordPolicy) {
        this.applicationId = applicationId;
        this.passwordPolicy = passwordPolicy;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public PasswordPolicy getPasswordPolicy() {
        return passwordPolicy;
    }
}
