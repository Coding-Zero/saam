package com.codingzero.saam.core.application;

import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.OAuthIdentifier;
import com.codingzero.saam.core.OAuthIdentifierPolicy;
import com.codingzero.saam.core.User;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierOS;
import com.codingzero.saam.infrastructure.database.spi.OAuthIdentifierAccess;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.PaginatedResultMapper;

import java.util.ArrayList;
import java.util.List;

public class OAuthIdentifierRepositoryService {

    private OAuthIdentifierAccess access;
    private OAuthIdentifierFactoryService factory;

    public OAuthIdentifierRepositoryService(OAuthIdentifierAccess access, OAuthIdentifierFactoryService factory) {
        this.access = access;
        this.factory = factory;
    }

    public void store(OAuthIdentifierEntity entity) {
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update(entity.getObjectSegment());
        }
    }

    public void remove(OAuthIdentifierEntity entity) {
        access.delete(entity.getObjectSegment());
    }

    public void remove(OAuthIdentifierPolicyEntity policy) {
        access.deleteByPlatform(policy.getApplication().getId(), policy.getPlatform());
    }

    public void remove(OAuthIdentifierPolicyEntity policy, User user) {
        access.deleteByPlatformAndUserId(policy.getApplication().getId(), policy.getPlatform(), user.getId());
    }

    public void removeAll(Application application) {
        access.deleteByApplicationId(application.getId());
    }

    public OAuthIdentifierEntity findByContent(OAuthIdentifierPolicy policy, String content) {
        OAuthIdentifierOS os = access.selectByPlatformAndContent(
                policy.getApplication().getId(), policy.getPlatform(), content);
        return factory.reconstitute(os, policy, null);
    }

    public List<OAuthIdentifier> findByPolicyAndUser(OAuthIdentifierPolicy policy, User user) {
        List<OAuthIdentifierOS> osList = access.selectByPlatformAndUserId(
                policy.getApplication().getId(), policy.getPlatform(), user.getId());
        List<OAuthIdentifier> entities = new ArrayList<>(osList.size());
        for (OAuthIdentifierOS os: osList) {
            entities.add(factory.reconstitute(os, policy, user));
        }
        return entities;
    }

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
        OAuthIdentifierPolicy policy = (OAuthIdentifierPolicy) arguments[1];
        List<OAuthIdentifier> entities = new ArrayList<>(source.size());
        for (OAuthIdentifierOS os: source) {
            entities.add(factory.reconstitute(os, policy, null));
        }
        return entities;
    }
}
