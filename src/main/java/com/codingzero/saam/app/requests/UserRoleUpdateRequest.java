package com.codingzero.saam.app.requests;

import java.util.Collections;
import java.util.List;


public class UserRoleUpdateRequest {

    private String applicationId;
    private String userId;
    private List<String> roleIds;

    public UserRoleUpdateRequest(String applicationId, String userId, List<String> roleIds) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.roleIds = Collections.unmodifiableList(roleIds);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }
}
