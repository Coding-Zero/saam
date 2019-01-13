package com.codingzero.saam.core.principal.role;

import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.Role;
import com.codingzero.saam.core.principal.PrincipalEntity;
import com.codingzero.saam.infrastructure.database.RoleOS;

public class RoleEntity extends PrincipalEntity<RoleOS> implements Role {

    private RoleFactoryService factory;

    public RoleEntity(RoleOS objectSegment,
                      Application application,
                      RoleFactoryService factory) {
        super(objectSegment, application);
        this.factory = factory;
    }

    @Override
    public String getName() {
        return getObjectSegment().getName();
    }

    @Override
    public void setName(String name) {
        if (getName().equalsIgnoreCase(name.trim())) {
            return;
        }
        factory.checkForDuplicateName(getApplication(), name);
        factory.checkForNameFormat(name);
        getObjectSegment().setName(name);
        markAsDirty();
    }
}
