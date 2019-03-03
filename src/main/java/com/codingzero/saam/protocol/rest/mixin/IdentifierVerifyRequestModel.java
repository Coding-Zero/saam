package com.codingzero.saam.protocol.rest.mixin;

import com.codingzero.saam.common.IdentifierType;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class IdentifierVerifyRequestModel {

    public IdentifierVerifyRequestModel(@JsonProperty("applicationId") String applicationId,
                                        @JsonProperty("userId") String userId,
                                        @JsonProperty("identifierType") IdentifierType identifierType,
                                        @JsonProperty("identifier") String identifier,
                                        @JsonProperty("verificationCode") String verificationCode) {}
    
}
