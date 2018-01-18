package com.codingzero.saam.presentation.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ApplicationAddRequestModel {

    public ApplicationAddRequestModel(@JsonProperty("name") String name,
                                      @JsonProperty("description") String description) {}
    
}
