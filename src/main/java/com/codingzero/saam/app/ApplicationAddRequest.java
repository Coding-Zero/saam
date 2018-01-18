package com.codingzero.saam.app;


public class ApplicationAddRequest {

    private String name;
    private String description;

    public ApplicationAddRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
