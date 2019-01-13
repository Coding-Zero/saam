package com.codingzero.saam.app.requests;

import java.util.Collections;
import java.util.Map;


public class CredentialLoginRequest {

    private String applicationId;
    private String identifier;
    private String password;
    private Map<String, Object> sessionDetails;
    private long sessionTimeout;

    public CredentialLoginRequest(String applicationId, String identifier,
                                  String password, Map<String, Object> sessionDetails,
                                  long sessionTimeout) {
        this.applicationId = applicationId;
        this.identifier = identifier;
        this.password = password;
        this.sessionDetails = Collections.unmodifiableMap(sessionDetails);
        this.sessionTimeout = sessionTimeout;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, Object> getSessionDetails() {
        return sessionDetails;
    }

    public long getSessionTimeout() {
        return sessionTimeout;
    }
}
