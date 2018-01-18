package com.codingzero.saam.presentation;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.*;
import javax.validation.constraints.*;
import java.util.Map;

public class SAAMConfiguration extends Configuration {

    @NotEmpty
    private Map<String, Object> mysql;

    @JsonProperty("mysql")
    public Map<String, Object> getMysql() {
        return mysql;
    }

    @JsonProperty("mysql")
    public void setMysql(Map<String, Object> mysql) {
        this.mysql = mysql;
    }
}
