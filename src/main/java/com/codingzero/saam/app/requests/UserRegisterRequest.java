package com.codingzero.saam.app.requests;

import java.util.Collections;
import java.util.List;


public class UserRegisterRequest {

    private String applicationId;
    private List<String> roleIds;

    public UserRegisterRequest(String applicationId,
                               List<String> roleIds) {
        this.applicationId = applicationId;
        this.roleIds = Collections.unmodifiableList(roleIds);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

}
