package com.codingzero.saam.app;

import com.codingzero.saam.common.ApplicationStatus;


public class ApplicationUpdateRequest {

    private String id;
    private String name;
    private String description;
    private ApplicationStatus status;

    public ApplicationUpdateRequest(String id, String name, String description,
                                    ApplicationStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "ApplicationUpdateRequest{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
