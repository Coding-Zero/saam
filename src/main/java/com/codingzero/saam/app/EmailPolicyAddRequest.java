package com.codingzero.saam.app;

import java.util.Collections;
import java.util.List;


public class EmailPolicyAddRequest {

    private String applicationId;
    private boolean isVerificationRequired;
    private List<String> domains;

    public EmailPolicyAddRequest(String applicationId,
                                 boolean isVerificationRequired, List<String> domains) {
        this.applicationId = applicationId;
        this.isVerificationRequired = isVerificationRequired;
        this.domains = Collections.unmodifiableList(domains);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public boolean isVerificationRequired() {
        return isVerificationRequired;
    }

    public List<String> getDomains() {
        return domains;
    }
}
