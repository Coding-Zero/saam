package com.codingzero.saam.domain.application;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.OAuthIdentifierPolicy;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierAccess;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierPolicyAccess;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierPolicyOS;

import java.util.ArrayList;
import java.util.List;

public class OAuthIdentifierPolicyRepositoryService {

    private OAuthIdentifierPolicyAccess access;
    private OAuthIdentifierPolicyFactoryService factory;
    private OAuthIdentifierAccess oAuthIdentifierAccess;

    public OAuthIdentifierPolicyRepositoryService(OAuthIdentifierPolicyAccess access,
                                                  OAuthIdentifierPolicyFactoryService factory,
                                                  OAuthIdentifierAccess oAuthIdentifierAccess) {
        this.access = access;
        this.factory = factory;
        this.oAuthIdentifierAccess = oAuthIdentifierAccess;
    }

    public void store(OAuthIdentifierPolicyEntity entity) {
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update(entity.getObjectSegment());
        }
//        flushDirtyIdentifiers(entity);
    }

//    private void flushDirtyIdentifiers(OAuthIdentifierPolicyEntity policy) {
//        List<OAuthIdentifierEntity> entities = policy.getDirtyIdentifiers();
//        for (OAuthIdentifierEntity entity: entities) {
//            if (entity.isVoid()) {
//                oAuthIdentifierRepository.remove(entity);
//            } else {
//                oAuthIdentifierRepository.store(entity);
//            }
//        }
//    }

    public void remove(OAuthIdentifierPolicyEntity entity) {
        checkForUnremoveableStatus(entity);
        access.delete(entity.getObjectSegment());
    }

    private void checkForUnremoveableStatus(OAuthIdentifierPolicyEntity entity) {
        if (oAuthIdentifierAccess.countByPlatform(entity.getApplication().getId(), entity.getPlatform()) > 0) {
            throw new IllegalStateException(
                    "OAuth Identifier policy " + entity.getPlatform()
                            + " cannot be removed before removing existing identifiers.");
        }
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
