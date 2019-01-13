package com.codingzero.saam.app.requests;

import java.util.Collections;
import java.util.List;


public class EmailPolicyUpdateRequest {

    private String applicationId;
    private boolean isActive;
    private boolean isVerificationRequired;
    private List<String> domains;

    public EmailPolicyUpdateRequest(String applicationId,
                                    boolean isVerificationRequired,
                                    List<String> domains,
                                    boolean isActive) {
        this.applicationId = applicationId;
        this.isActive = isActive;
        this.isVerificationRequired = isVerificationRequired;
        this.domains = Collections.unmodifiableList(domains);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public boolean isActive() {
        return isActive;
    }

    public List<String> getDomains() {
        return domains;
    }

    public boolean isVerificationRequired() {
        return isVerificationRequired;
    }
}
