package com.codingzero.saam.domain.oauthidentifier;

import com.codingzero.saam.common.OAuthIdentifierKey;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.OAuthIdentifier;
import com.codingzero.saam.domain.OAuthIdentifierPolicy;
import com.codingzero.saam.domain.OAuthIdentifierRepository;
import com.codingzero.saam.domain.User;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierAccess;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierOS;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.PaginatedResultMapper;

import java.util.ArrayList;
import java.util.List;

public class OAuthIdentifierRepositoryService implements OAuthIdentifierRepository {

    private OAuthIdentifierAccess access;
    private OAuthIdentifierFactoryService factory;

    public OAuthIdentifierRepositoryService(OAuthIdentifierAccess access, OAuthIdentifierFactoryService factory) {
        this.access = access;
        this.factory = factory;
    }

    @Override
    public OAuthIdentifier store(OAuthIdentifier identifier) {
        OAuthIdentifierEntity entity = (OAuthIdentifierEntity) identifier;
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update(entity.getObjectSegment());
        }
        return factory.reconstitute(entity.getObjectSegment(), identifier.getApplication(), identifier.getUser());
    }

    @Override
    public void remove(OAuthIdentifier identifier) {
        OAuthIdentifierEntity entity = (OAuthIdentifierEntity) identifier;
        access.delete(entity.getObjectSegment());
    }

    @Override
    public void removeByUser(User user) {
        access.deleteByUserId(user.getApplication().getId(), user.getId());
    }

    @Override
    public void removeByPlatform(Application application, OAuthPlatform platform) {
        access.deleteByPlatform(application.getId(), platform);
    }

    @Override
    public void removeByApplication(Application application) {
        access.deleteByApplicationId(application.getId());
    }

    @Override
    public OAuthIdentifier findByKey(OAuthIdentifierPolicy policy, String content) {
        OAuthIdentifierKey key =
                new OAuthIdentifierKey(policy.getApplication().getId(), policy.getPlatform(), content);
        OAuthIdentifierOS os = access.selectByKey(key);
        return factory.reconstitute(os, policy.getApplication(), null);
    }

    @Override
    public List<OAuthIdentifier> findByUser(Application application, User user) {
        List<OAuthIdentifierOS> osList = access.selectByUserId(application.getId(), user.getId());
        Object[] arguments = new Object[] {application, user};
        return _toResult(osList, arguments);
    }

    @Override
    public PaginatedResult<List<OAuthIdentifier>> findByPolicy(OAuthIdentifierPolicy policy) {
        PaginatedResult<List<OAuthIdentifierOS>> result = access.selectByPlatform(
                policy.getApplication().getId(), policy.getPlatform());
        return new PaginatedResult<>(new PaginatedResultMapper<List<OAuthIdentifier>, List<OAuthIdentifierOS>>() {
            @Override
            protected List<OAuthIdentifier> toResult(List<OAuthIdentifierOS> source, Object[] arguments) {
                return _toResult(source, arguments);
            }
        }, result, policy);
    }

    private List<OAuthIdentifier> _toResult(List<OAuthIdentifierOS> source, Object[] arguments) {
        Application application = (Application) arguments[0];
        User user = (User) arguments[1];
        List<OAuthIdentifier> entities = new ArrayList<>(source.size());
        for (OAuthIdentifierOS os: source) {
            entities.add(factory.reconstitute(os, application, user));
        }
        return entities;
    }
}
