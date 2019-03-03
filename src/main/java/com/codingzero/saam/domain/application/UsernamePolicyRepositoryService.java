package com.codingzero.saam.domain.application;

import com.codingzero.saam.domain.Application;
import com.codingzero.saam.infrastructure.data.IdentifierPolicyOS;
import com.codingzero.saam.infrastructure.data.UsernamePolicyOS;
import com.codingzero.saam.infrastructure.data.UsernamePolicyAccess;

public class UsernamePolicyRepositoryService {

    private UsernamePolicyAccess access;
    private UsernamePolicyFactoryService factory;

    public UsernamePolicyRepositoryService(UsernamePolicyAccess access,
                                           UsernamePolicyFactoryService factory) {
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
