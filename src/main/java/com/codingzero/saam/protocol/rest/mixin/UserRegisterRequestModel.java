package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public abstract class UserRegisterRequestModel {

    public UserRegisterRequestModel(@JsonProperty("applicationId") String applicationId,
                                    @JsonProperty("roleIds") List<String> roleIds) {}
    
}
