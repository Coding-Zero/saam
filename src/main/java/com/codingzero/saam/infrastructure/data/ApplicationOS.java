package com.codingzero.saam.infrastructure.data;

import com.codingzero.saam.common.ApplicationStatus;
import com.codingzero.saam.common.PasswordPolicy;

import java.util.Date;


public class ApplicationOS {

    private String id;
    private String name;
    private String description;
    private long creationTime;
    private PasswordPolicy passwordPolicy;
    private ApplicationStatus status;

    public ApplicationOS(String id, String name, String description,
                         Date creationTime, PasswordPolicy passwordPolicy,
                         ApplicationStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creationTime = creationTime.getTime();
        this.passwordPolicy = passwordPolicy;
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

    public Date getCreationTime() {
        return new Date(creationTime);
    }

    public PasswordPolicy getPasswordPolicy() {
        return passwordPolicy;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPasswordPolicy(PasswordPolicy passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
}
