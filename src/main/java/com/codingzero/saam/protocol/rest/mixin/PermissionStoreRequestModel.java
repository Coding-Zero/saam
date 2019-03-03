package com.codingzero.saam.protocol.rest.mixin;

import com.codingzero.saam.common.Action;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public abstract class PermissionStoreRequestModel {

    public PermissionStoreRequestModel(@JsonProperty("applicationId") String applicationId,
                                       @JsonProperty("resourceKey") String resourceKey,
                                       @JsonProperty("principalId") String principalId,
                                       @JsonProperty("actions") List<Action> actions) {}
    
}
