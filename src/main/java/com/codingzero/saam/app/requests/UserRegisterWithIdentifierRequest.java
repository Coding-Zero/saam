package com.codingzero.saam.app.requests;

import com.codingzero.saam.common.IdentifierType;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public class UserRegisterWithIdentifierRequest {

    private String applicationId;
    private Map<IdentifierType, String> identifiers;
    private String password;
    private List<String> roleIds;

    public UserRegisterWithIdentifierRequest(String applicationId, Map<IdentifierType, String> identifiers,
                                             String password, List<String> roleIds) {
        this.applicationId = applicationId;
        this.identifiers = Collections.unmodifiableMap(identifiers);
        this.password = password;
        this.roleIds = Collections.unmodifiableList(roleIds);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public Map<IdentifierType, String> getIdentifiers() {
        return identifiers;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

}
