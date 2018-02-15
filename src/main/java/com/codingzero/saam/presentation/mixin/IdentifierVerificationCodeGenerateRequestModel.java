package com.codingzero.saam.presentation.mixin;

import com.codingzero.saam.common.IdentifierType;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class IdentifierVerificationCodeGenerateRequestModel {

    public IdentifierVerificationCodeGenerateRequestModel(@JsonProperty("applicationId") String applicationId,
                                                          @JsonProperty("userId") String userId,
                                                          @JsonProperty("identifierType") IdentifierType identifierType,
                                                          @JsonProperty("identifier") String identifier,
                                                          @JsonProperty("timeout") long timeout) {}
    
}
