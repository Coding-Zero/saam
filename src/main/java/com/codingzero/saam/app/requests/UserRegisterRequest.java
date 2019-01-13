package com.codingzero.saam.app.requests;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.OAuthPlatform;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public class UserRegisterRequest {

    private String applicationId;
    private Map<IdentifierType, String> identifiers;
    private Map<OAuthPlatform, OAuthIdentifier> oAuthIdentifiers;
    private String password;
    private List<String> roleIds;

    public UserRegisterRequest(String applicationId) {
        this(applicationId, Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList());
    }

    public UserRegisterRequest(String applicationId, Map<IdentifierType, String> identifiers,
                               Map<OAuthPlatform, OAuthIdentifier> oAuthIdentifiers,
                               String password, List<String> roleIds) {
        this.applicationId = applicationId;
        this.identifiers = Collections.unmodifiableMap(identifiers);
        this.oAuthIdentifiers = Collections.unmodifiableMap(oAuthIdentifiers);
        this.password = password;
        this.roleIds = Collections.unmodifiableList(roleIds);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public Map<IdentifierType, String> getIdentifiers() {
        return identifiers;
    }

    public Map<OAuthPlatform, OAuthIdentifier> getOAuthIdentifiers() {
        return oAuthIdentifiers;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

    public static class OAuthIdentifier {

        private String identifier;
        private Map<String, Object> properties;

        public OAuthIdentifier(String identifier, Map<String, Object> properties) {
            this.identifier = identifier;
            this.properties = Collections.unmodifiableMap(properties);
        }

        public String getIdentifier() {
            return identifier;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }

    }
}
