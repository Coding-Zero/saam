package com.codingzero.saam.domain.principal;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.Principal;
import com.codingzero.saam.infrastructure.database.PrincipalOS;
import com.codingzero.utilities.ddd.EntityObject;

import java.util.Date;

public abstract class PrincipalEntity <T extends PrincipalOS> extends EntityObject<T> implements Principal {

    private Application application;

    public PrincipalEntity(T objectSegment, Application application) {
        super(objectSegment);
        this.application = application;

    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public String getId() {
        return getObjectSegment().getId().getId();
    }

    @Override
    public PrincipalType getType() {
        return getObjectSegment().getType();
    }

    @Override
    public Date getCreationTime() {
        return getObjectSegment().getCreationTime();
    }

}
