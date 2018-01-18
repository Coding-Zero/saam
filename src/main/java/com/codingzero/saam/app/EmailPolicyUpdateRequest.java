package com.codingzero.saam.app;

import java.util.Collections;
import java.util.List;


public class EmailPolicyUpdateRequest {

    private String applicationId;
    private String code;
    private boolean isActive;
    private boolean isVerificationRequired;
    private List<String> domains;

    public EmailPolicyUpdateRequest(String applicationId,
                                    String code,
                                    boolean isVerificationRequired,
                                    List<String> domains,
                                    boolean isActive) {
        this.applicationId = applicationId;
        this.code = code;
        this.isActive = isActive;
        this.isVerificationRequired = isVerificationRequired;
        this.domains = Collections.unmodifiableList(domains);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getCode() {
        return code;
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
