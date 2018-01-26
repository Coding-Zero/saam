package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.common.IdentifierType;

import java.util.Collections;
import java.util.Date;
import java.util.List;


public class EmailPolicyOS extends IdentifierPolicyOS {

    private List<String> domains;

    public EmailPolicyOS(String applicationId,
                         boolean isVerificationRequired,
                         int minLength,
                         int maxLength,
                         boolean isActive,
                         Date creationTime,
                         Date updateTime,
                         List<String> domains) {
        super(applicationId, IdentifierType.EMAIL, isVerificationRequired,
                minLength, maxLength, isActive, creationTime, updateTime);
        setDomains(domains);
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = Collections.unmodifiableList(domains);
    }

}
