package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class IdentifierUpdateRequestModel {

    public IdentifierUpdateRequestModel(@JsonProperty("applicationId") String applicationId,
                                        @JsonProperty("userId") String userId,
                                        @JsonProperty("code") String code,
                                        @JsonProperty("currentIdentifier") String currentIdentifier,
                                        @JsonProperty("newIdentifier") String newIdentifier) {}
    
}
