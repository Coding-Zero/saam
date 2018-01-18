package com.codingzero.saam.presentation.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class IdentifierAssignRequestModel {

    public IdentifierAssignRequestModel(@JsonProperty("applicationId") String applicationId,
                                        @JsonProperty("userId") String userId,
                                        @JsonProperty("code") String code,
                                        @JsonProperty("identifier") String identifier) {}
    
}
