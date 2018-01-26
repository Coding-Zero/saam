package com.codingzero.saam.presentation.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.codingzero.saam.common.UsernameFormat;

public abstract class UsernamePolicyAddRequestModel {

    public UsernamePolicyAddRequestModel(@JsonProperty("applicationId") String applicationId) {}
    
}
