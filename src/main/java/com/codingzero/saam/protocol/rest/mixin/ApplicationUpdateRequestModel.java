package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.codingzero.saam.common.ApplicationStatus;

public abstract class ApplicationUpdateRequestModel {

    public ApplicationUpdateRequestModel(@JsonProperty("id") String id,
                                         @JsonProperty("name") String name,
                                         @JsonProperty("description") String description,
                                         @JsonProperty("status") ApplicationStatus status) {}
    
}
