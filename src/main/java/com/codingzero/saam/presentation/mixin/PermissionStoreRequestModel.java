package com.codingzero.saam.presentation.mixin;

import com.codingzero.saam.common.Action;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.codingzero.saam.common.PermissionType;

import java.util.List;

public abstract class PermissionStoreRequestModel {

    public PermissionStoreRequestModel(@JsonProperty("applicationId") String applicationId,
                                       @JsonProperty("resourceKey") String resourceKey,
                                       @JsonProperty("principalId") String principalId,
                                       @JsonProperty("actions") List<Action> actions) {}
    
}
