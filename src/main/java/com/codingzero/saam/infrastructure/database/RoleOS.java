package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.common.PrincipalType;

import java.util.Date;


public class RoleOS extends PrincipalOS {

    private String name;

    public RoleOS(String applicationId, String id, Date creationTime, String name) {
        super(applicationId, id, PrincipalType.ROLE, creationTime);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
