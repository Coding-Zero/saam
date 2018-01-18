package com.codingzero.saam.core.application;

import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.Identifier;
import com.codingzero.saam.core.IdentifierPolicy;
import com.codingzero.saam.core.User;
import com.codingzero.saam.infrastructure.database.IdentifierOS;
import com.codingzero.saam.infrastructure.database.spi.IdentifierAccess;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.PaginatedResultMapper;

import java.util.ArrayList;
import java.util.List;

public class IdentifierRepositoryService {

    private IdentifierAccess access;
    private IdentifierFactoryService factory;

    public IdentifierRepositoryService(IdentifierAccess access, IdentifierFactoryService factory) {
        this.access = access;
        this.factory = factory;
    }

    public void store(IdentifierEntity entity) {
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update(entity.getObjectSegment());
        }
    }

    public void remove(IdentifierEntity entity) {
        access.delete(entity.getObjectSegment());
    }

    public void remove(IdentifierPolicyEntity policy) {
        access.deleteByPolicyCode(policy.getApplication().getId(), policy.getCode());
    }

    public void remove(IdentifierPolicyEntity policy, User user) {
        access.deleteByPolicyCodeAndUserId(policy.getApplication().getId(), policy.getCode(), user.getId());
    }

    public void removeAll(Application application) {
        access.deleteByApplicationId(application.getId());
    }

    public IdentifierEntity findByContent(IdentifierPolicy policy, String content) {
        IdentifierOS os = access.selectByPolicyCodeAndContent(
                policy.getApplication().getId(), policy.getCode(), content);
        return factory.reconstitute(os, policy, null);
    }

    public List<Identifier> findByPolicyAndUser(IdentifierPolicy policy, User user) {
        List<IdentifierOS> osList = access.selectByPolicyCodeAndUserId(
                policy.getApplication().getId(), policy.getCode(), user.getId());
        List<Identifier> identifiers = new ArrayList<>(osList.size());
        for (IdentifierOS os: osList) {
            identifiers.add(factory.reconstitute(os, policy, user));
        }
        return identifiers;
    }

    public PaginatedResult<List<Identifier>> findByPolicy(IdentifierPolicy policy) {
        PaginatedResult<List<IdentifierOS>> result = access.selectByPolicyCode(
                policy.getApplication().getId(), policy.getCode());
        return new PaginatedResult<>(new PaginatedResultMapper<List<Identifier>, List<IdentifierOS>>() {
            @Override
            protected List<Identifier> toResult(List<IdentifierOS> source, Object[] arguments) {
                return _toResult(source, arguments);
            }
        }, result, policy);
    }

    private List<Identifier> _toResult(List<IdentifierOS> source, Object[] arguments) {
        IdentifierPolicy policy = (IdentifierPolicy) arguments[1];
        List<Identifier> entities = new ArrayList<>(source.size());
        for (IdentifierOS os: source) {
            entities.add(factory.reconstitute(os, policy, null));
        }
        return entities;
    }
}
