package com.codingzero.saam.core.application;

import com.codingzero.saam.core.Application;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierPolicyOS;
import com.codingzero.saam.infrastructure.database.spi.OAuthIdentifierPolicyAccess;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.core.OAuthIdentifierPolicy;

import java.util.ArrayList;
import java.util.List;

public class OAuthIdentifierPolicyRepositoryService {

    private OAuthIdentifierPolicyAccess access;
    private OAuthIdentifierPolicyFactoryService factory;
    private OAuthIdentifierRepositoryService oAuthIdentifierRepository;

    public OAuthIdentifierPolicyRepositoryService(OAuthIdentifierPolicyAccess access,
                                                  OAuthIdentifierPolicyFactoryService factory,
                                                  OAuthIdentifierRepositoryService oAuthIdentifierRepository) {
        this.access = access;
        this.factory = factory;
        this.oAuthIdentifierRepository = oAuthIdentifierRepository;
    }

    public void store(OAuthIdentifierPolicyEntity entity) {
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update(entity.getObjectSegment());
        }
        flushDirtyIdentifiers(entity);
    }

    private void flushDirtyIdentifiers(OAuthIdentifierPolicyEntity policy) {
        List<OAuthIdentifierEntity> entities = policy.getDirtyIdentifiers();
        for (OAuthIdentifierEntity entity: entities) {
            if (entity.isVoid()) {
                oAuthIdentifierRepository.remove(entity);
            } else {
                oAuthIdentifierRepository.store(entity);
            }
        }
    }

    public void remove(OAuthIdentifierPolicyEntity entity) {
        access.delete(entity.getObjectSegment());
    }

    public void removeAll(Application application) {
        access.deleteByApplicationId(application.getId());
    }

    public OAuthIdentifierPolicyEntity findByPlatform(Application application, OAuthPlatform platform) {
        OAuthIdentifierPolicyOS os = access.selectByPlatform(application.getId(), platform);
        return factory.reconstitute(os, application);
    }

    public List<OAuthIdentifierPolicy> findAll(Application application) {
        List<OAuthIdentifierPolicyOS> osList = access.selectByApplicationId(application.getId());
        List<OAuthIdentifierPolicy> entities = new ArrayList<>(osList.size());
        for (OAuthIdentifierPolicyOS os: osList) {
            entities.add(factory.reconstitute(os, application));
        }
        return entities;
    }

}
