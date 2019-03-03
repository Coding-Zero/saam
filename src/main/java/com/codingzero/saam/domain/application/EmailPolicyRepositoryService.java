package com.codingzero.saam.domain.application;

import com.codingzero.saam.domain.Application;
import com.codingzero.saam.infrastructure.data.EmailPolicyAccess;
import com.codingzero.saam.infrastructure.data.EmailPolicyOS;
import com.codingzero.saam.infrastructure.data.IdentifierPolicyOS;

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
