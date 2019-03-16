package com.codingzero.saam.protocol.rest;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.*;

import java.util.Map;

public class SAAMConfiguration extends Configuration {

    @NotEmpty
    private Map<String, Object> mysql;

    @NotEmpty
    private String apiKey;

    @JsonProperty("mysql")
    public Map<String, Object> getMysql() {
        return mysql;
    }

    @JsonProperty("mysql")
    public void setMysql(Map<String, Object> mysql) {
        this.mysql = mysql;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

}
