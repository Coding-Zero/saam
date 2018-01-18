package com.codingzero.saam.presentation.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class PasswordResetCodeGenerateRequestModel {

    public PasswordResetCodeGenerateRequestModel(@JsonProperty("applicationId") String applicationId,
                                                 @JsonProperty("userId") String userId,
                                                 @JsonProperty("identifierPolicyCode") String identifierPolicyCode,
                                                 @JsonProperty("identifier") String identifier,
                                                 @JsonProperty("timeout") long timeout) {}
    
}
