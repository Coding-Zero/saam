package com.codingzero.saam.presentation.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class IdentifierVerifyRequestModel {

    public IdentifierVerifyRequestModel(@JsonProperty("applicationId") String applicationId,
                                        @JsonProperty("userId") String userId,
                                        @JsonProperty("code") String code,
                                        @JsonProperty("identifier") String identifier,
                                        @JsonProperty("verificationCode") String verificationCode) {}
    
}
