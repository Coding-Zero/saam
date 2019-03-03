package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class UsernamePolicyAddRequestModel {

    public UsernamePolicyAddRequestModel(@JsonProperty("applicationId") String applicationId) {}
    
}
