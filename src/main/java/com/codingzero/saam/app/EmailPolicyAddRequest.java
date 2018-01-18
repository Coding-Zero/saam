package com.codingzero.saam.app;

import java.util.Collections;
import java.util.List;


public class EmailPolicyAddRequest {

    private String applicationId;
    private String code;
    private boolean isVerificationRequired;
    private List<String> domains;

    public EmailPolicyAddRequest(String applicationId, String code,
                                 boolean isVerificationRequired, List<String> domains) {
        this.applicationId = applicationId;
        this.code = code;
        this.isVerificationRequired = isVerificationRequired;
        this.domains = Collections.unmodifiableList(domains);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getCode() {
        return code;
    }

    public boolean isVerificationRequired() {
        return isVerificationRequired;
    }

    public List<String> getDomains() {
        return domains;
    }
}
