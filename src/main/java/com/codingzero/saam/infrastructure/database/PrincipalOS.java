package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.common.PrincipalId;
import com.codingzero.saam.common.PrincipalType;

import java.util.Date;


public class PrincipalOS {

    private PrincipalId id;
    private PrincipalType type;
    private long creationTime;

    public PrincipalOS(PrincipalId id, PrincipalType type, Date creationTime) {
        this.id = id;
        this.type = type;
        this.creationTime = creationTime.getTime();
    }

    public PrincipalId getId() {
        return id;
    }

    public PrincipalType getType() {
        return type;
    }

    public Date getCreationTime() {
        return new Date(creationTime);
    }
}
