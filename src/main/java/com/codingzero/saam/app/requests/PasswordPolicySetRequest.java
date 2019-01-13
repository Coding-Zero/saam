package com.codingzero.saam.app.requests;

import com.codingzero.saam.common.PasswordPolicy;


public class PasswordPolicySetRequest {

    private String applicationId;
    private PasswordPolicy passwordPolicy;

    public PasswordPolicySetRequest(String applicationId, PasswordPolicy passwordPolicy) {
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
