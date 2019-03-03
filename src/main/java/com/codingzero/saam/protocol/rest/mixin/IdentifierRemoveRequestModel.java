package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class IdentifierRemoveRequestModel {

    public IdentifierRemoveRequestModel(@JsonProperty("applicationId") String applicationId,
                                        @JsonProperty("userId") String userId,
                                        @JsonProperty("code") String code,
                                        @JsonProperty("identifier") String identifier) {}
    
}
