package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ResourceAddRequestModel {

    public ResourceAddRequestModel(@JsonProperty("applicationId") String applicationId,
                                   @JsonProperty("userId") String userId,
                                   @JsonProperty("key") String key) {}
    
}
