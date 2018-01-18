package com.codingzero.saam.presentation.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public abstract class CredentialLoginRequestModel {

    public CredentialLoginRequestModel(@JsonProperty("applicationId") String applicationId,
                                       @JsonProperty("identifier") String identifier,
                                       @JsonProperty("password") String password,
                                       @JsonProperty("details") Map<String, String> details,
                                       @JsonProperty("timeout") long timeout) {}
    
}
