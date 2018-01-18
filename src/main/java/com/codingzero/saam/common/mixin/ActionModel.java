package com.codingzero.saam.common.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ActionModel {

    public ActionModel(@JsonProperty("code") String code,
                       @JsonProperty("allowed") boolean isAllowed) {}
    
}
