package com.codingzero.saam.presentation.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class APIKeyUpdateRequestModel {

    public APIKeyUpdateRequestModel(@JsonProperty("applicationId") String applicationId,
                                    @JsonProperty("key") String key,
                                    @JsonProperty("name") String name,
                                    @JsonProperty("active") boolean isActive) {}
    
}
