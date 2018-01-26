package com.codingzero.saam.presentation.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public abstract class EmailPolicyAddRequestModel {

    public EmailPolicyAddRequestModel(@JsonProperty("applicationId") String applicationId,
                                      @JsonProperty("verificationRequired") boolean isVerificationRequired,
                                      @JsonProperty("domains") List<String> domains) {}
    
}
