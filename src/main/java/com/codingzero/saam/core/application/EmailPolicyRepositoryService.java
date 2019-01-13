package com.codingzero.saam.core.application;

import com.codingzero.saam.core.Application;
import com.codingzero.saam.infrastructure.database.EmailPolicyOS;
import com.codingzero.saam.infrastructure.database.IdentifierPolicyOS;
import com.codingzero.saam.infrastructure.database.EmailPolicyAccess;

public class EmailPolicyRepositoryService {

    private EmailPolicyAccess access;
    private EmailPolicyFactoryService factory;

    public EmailPolicyRepositoryService(EmailPolicyAccess access,
                                        EmailPolicyFactoryService factory) {
        this.access = access;
        this.factory = factory;
    }

    public void store(EmailPolicyEntity entity) {
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update(entity.getObjectSegment());
        }
    }

    public void remove(EmailPolicyEntity entity) {
        access.delete(entity.getObjectSegment());
    }

    public void removeAll(Application application) {
        access.deleteByApplicationId(application.getId());
    }

    public EmailPolicyEntity load(Application application, IdentifierPolicyOS identifierPolicyOS) {
        EmailPolicyOS os = access.selectByIdentifierPolicyOS(identifierPolicyOS);
        EmailPolicyEntity entity = factory.reconstitute(os, application);
        return entity;
    }

}
