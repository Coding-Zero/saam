package com.codingzero.saam.presentation.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public abstract class EmailPolicyUpdateRequestModel {

    public EmailPolicyUpdateRequestModel(@JsonProperty("applicationId") String applicationId,
                                         @JsonProperty("code") String code,
                                         @JsonProperty("verificationRequired") boolean isVerificationRequired,
                                         @JsonProperty("domains") List<String> domains,
                                         @JsonProperty("active") boolean isActive) {}
    
}
