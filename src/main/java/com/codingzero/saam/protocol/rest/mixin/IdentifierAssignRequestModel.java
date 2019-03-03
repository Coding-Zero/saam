package com.codingzero.saam.protocol.rest.mixin;

import com.codingzero.saam.common.IdentifierType;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class IdentifierAssignRequestModel {

    public IdentifierAssignRequestModel(@JsonProperty("applicationId") String applicationId,
                                        @JsonProperty("userId") String userId,
                                        @JsonProperty("type") IdentifierType type,
                                        @JsonProperty("identifier") String identifier) {}
    
}
