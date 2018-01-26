package com.codingzero.saam.app;

import com.codingzero.saam.common.IdentifierType;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public class CredentialRegisterRequest {

    private String applicationId;
    private Map<IdentifierType, String> identifiers;
    private String password;
    private List<String> roleIds;

    public CredentialRegisterRequest(String applicationId, Map<IdentifierType, String> identifiers, String password) {
        this(applicationId, identifiers, password, Collections.emptyList());
    }

    public CredentialRegisterRequest(String applicationId, Map<IdentifierType, String> identifiers,
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
