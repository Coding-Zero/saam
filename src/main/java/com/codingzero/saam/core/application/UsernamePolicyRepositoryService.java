package com.codingzero.saam.core.application;

import com.codingzero.saam.core.Application;
import com.codingzero.saam.infrastructure.database.IdentifierPolicyOS;
import com.codingzero.saam.infrastructure.database.UsernamePolicyOS;
import com.codingzero.saam.infrastructure.database.spi.IdentifierPolicyAccess;
import com.codingzero.saam.infrastructure.database.spi.UsernamePolicyAccess;

public class UsernamePolicyRepositoryService {

    private IdentifierPolicyAccess identifierPolicyAccess;
    private UsernamePolicyAccess access;
    private UsernamePolicyFactoryService factory;

    public UsernamePolicyRepositoryService(IdentifierPolicyAccess identifierPolicyAccess,
                                           UsernamePolicyAccess access,
                                           UsernamePolicyFactoryService factory) {
        this.identifierPolicyAccess = identifierPolicyAccess;
        this.access = access;
        this.factory = factory;
    }

    public void store(UsernamePolicyEntity entity) {
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update(entity.getObjectSegment());
        }
    }

    public void remove(UsernamePolicyEntity entity) {
        access.delete(entity.getObjectSegment());
    }

    public void removeAll(Application application) {
        access.deleteByApplicationId(application.getId());
    }

    public UsernamePolicyEntity load(Application application, IdentifierPolicyOS identifierPolicyOS) {
        UsernamePolicyOS os = access.selectByIdentifierPolicyOS(identifierPolicyOS);
        UsernamePolicyEntity entity = factory.reconstitute(os, application);
        return entity;
    }

}
