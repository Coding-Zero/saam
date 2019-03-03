package com.codingzero.saam.domain.principal.apikey;

import com.codingzero.saam.domain.APIKey;
import com.codingzero.saam.domain.APIKeyRepository;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.User;
import com.codingzero.saam.infrastructure.data.APIKeyAccess;
import com.codingzero.saam.infrastructure.data.APIKeyOS;
import com.codingzero.saam.infrastructure.data.PrincipalAccess;
import com.codingzero.saam.infrastructure.data.PrincipalOS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class APIKeyRepositoryService implements APIKeyRepository {

    private APIKeyAccess access;
    private PrincipalAccess principalAccess;
    private APIKeyFactoryService factory;

    public APIKeyRepositoryService(APIKeyAccess access,
                                   PrincipalAccess principalAccess,
                                   APIKeyFactoryService factory) {
        this.access = access;
        this.principalAccess = principalAccess;
        this.factory = factory;
    }

    @Override
    public APIKey store(APIKey apiKey) {
        APIKeyEntity entity = (APIKeyEntity) apiKey;
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update(entity.getObjectSegment());
        }
        return factory.reconstitute(
                entity.getObjectSegment(),
                apiKey.getApplication(),
                apiKey.getOwner());
    }

    @Override
    public void remove(APIKey apiKey) {
        APIKeyEntity entity = (APIKeyEntity) apiKey;
        access.delete(entity.getObjectSegment());
    }

    @Override
    public void removeByOwner(User user) {
        access.deleteByUserId(user.getApplication().getId(), user.getId());
    }

    @Override
    public void removeByApplication(Application application) {
        access.deleteByApplicationId(application.getId());
    }

    @Override
    public APIKeyEntity findById(Application application, String id) {
        PrincipalOS principalOS = principalAccess.selectById(application.getId(), id);
        return load(application, principalOS);
    }

    @Override
    public List<APIKey> findByOwner(User user) {
        List<APIKeyOS> osList = access.selectByUserId(user.getApplication().getId(), user.getId());
        List<APIKeyEntity> entities = new ArrayList<>(osList.size());
        for (APIKeyOS os: osList) {
            entities.add(factory.reconstitute(os, user.getApplication(), user));
        }
        return Collections.unmodifiableList(entities);
    }

    public APIKeyEntity load(Application application, PrincipalOS principalOS) {
        if (null == principalOS) {
            return null;
        }
        APIKeyOS os = access.selectByPrincipalOS(principalOS);
        return factory.reconstitute(os, application, null);
    }

}
