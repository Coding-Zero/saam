package com.codingzero.saam.app.requests;

import java.util.Collections;
import java.util.Map;

public class UserSessionLoginRequest {

    private String applicationId;
    private String sessionKey;
    private Map<String, Object> details;
    private long extraTimeout;

    public UserSessionLoginRequest(String applicationId,
                                   String sessionKey, Map<String, Object> details, long extraTimeout) {
        this.applicationId = applicationId;
        this.sessionKey = sessionKey;
        this.details = null == details ? null : Collections.unmodifiableMap(details);
        this.extraTimeout = extraTimeout;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public long getExtraTimeout() {
        return extraTimeout;
    }
}
