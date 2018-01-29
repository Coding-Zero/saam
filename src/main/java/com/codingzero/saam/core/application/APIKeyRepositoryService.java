package com.codingzero.saam.core.application;

import com.codingzero.saam.core.APIKey;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.User;
import com.codingzero.saam.infrastructure.database.APIKeyOS;
import com.codingzero.saam.infrastructure.database.PrincipalOS;
import com.codingzero.saam.infrastructure.database.spi.APIKeyAccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class APIKeyRepositoryService {

    private APIKeyAccess access;
    private APIKeyFactoryService factory;

    public APIKeyRepositoryService(APIKeyAccess access,
                                   APIKeyFactoryService factory) {
        this.access = access;
        this.factory = factory;
    }

    public void store(APIKeyEntity entity) {
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update(entity.getObjectSegment());
        }
    }

    public void remove(APIKeyEntity entity) {
        access.delete(entity.getObjectSegment());
    }

    public void removeAll(Application application) {
        access.deleteByApplicationId(application.getId());
    }

    public APIKeyEntity findByKey(Application application, String key) {
        APIKeyOS os = access.selectByKey(application.getId(), key);
        return factory.reconstitute(os, application, null);
    }

    public List<APIKey> findByOwner(Application application, User user) {
        List<APIKeyOS> osList = access.selectByUserId(application.getId(), user.getId());
        List<APIKeyEntity> entities = new ArrayList<>(osList.size());
        for (APIKeyOS os: osList) {
            entities.add(factory.reconstitute(os, application, user));
        }
        return Collections.unmodifiableList(entities);
    }

    public APIKeyEntity load(Application application, PrincipalOS principalOS) {
        APIKeyOS os = access.selectByPrincipalOS(principalOS);
        return factory.reconstitute(os, application, null);
    }

}
