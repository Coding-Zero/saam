package com.codingzero.saam.app.requests;

import com.codingzero.saam.common.OAuthPlatform;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public class UserRegisterWithOAuthRequest {

    private String applicationId;
    private Map<OAuthPlatform, OAuthIdentifier> oAuthIdentifiers;
    private List<String> roleIds;

    public UserRegisterWithOAuthRequest(String applicationId,
                                        Map<OAuthPlatform, OAuthIdentifier> oAuthIdentifiers,
                                        List<String> roleIds) {
        this.applicationId = applicationId;
        this.oAuthIdentifiers = Collections.unmodifiableMap(oAuthIdentifiers);
        this.roleIds = Collections.unmodifiableList(roleIds);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public Map<OAuthPlatform, OAuthIdentifier> getOAuthIdentifiers() {
        return oAuthIdentifiers;
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
