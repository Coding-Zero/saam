package com.codingzero.saam.domain.identifier;

import com.codingzero.saam.common.IdentifierKey;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.Identifier;
import com.codingzero.saam.domain.IdentifierRepository;
import com.codingzero.saam.domain.User;
import com.codingzero.saam.infrastructure.database.IdentifierOS;
import com.codingzero.saam.infrastructure.database.IdentifierAccess;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.PaginatedResultMapper;

import java.util.ArrayList;
import java.util.List;

public class IdentifierRepositoryService implements IdentifierRepository {

    private IdentifierAccess access;
    private IdentifierFactoryService factory;

    public IdentifierRepositoryService(IdentifierAccess access, IdentifierFactoryService factory) {
        this.access = access;
        this.factory = factory;
    }

    @Override
    public Identifier store(Identifier identifier) {
        IdentifierEntity entity = (IdentifierEntity) identifier;
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update(entity.getObjectSegment());
        }
        return factory.reconstitute(entity.getObjectSegment(), identifier.getApplication(), identifier.getUser());
    }

    @Override
    public void remove(Identifier identifier) {
        IdentifierEntity entity = (IdentifierEntity) identifier;
        access.delete(entity.getObjectSegment());
    }

    @Override
    public void removeByType(Application application, IdentifierType type) {
        access.deleteByType(application.getId(), type);
    }

    @Override
    public void removeByUser(User user) {
        access.deleteByUserId(user.getApplication().getId(), user.getId());
    }

    @Override
    public void removeByApplication(Application application) {
        access.deleteByApplicationId(application.getId());
    }

    @Override
    public Identifier findByKey(Application application, String content) {
        IdentifierOS os = access.selectByKey(new IdentifierKey(application.getId(), content));
        return factory.reconstitute(os, null, null);
    }

    @Override
    public List<Identifier> findByUser(Application application, User user) {
        List<IdentifierOS> osList = access.selectByUserId(application.getId(), user.getId());
        Object[] arguments = new Object[] {application, user};
        return _toResult(osList, arguments);
    }

    @Override
    public PaginatedResult<List<Identifier>> findByIdentifierType(Application application, IdentifierType type) {
        PaginatedResult<List<IdentifierOS>> result = access.selectByType(application.getId(), type);
        return new PaginatedResult<>(new PaginatedResultMapper<List<Identifier>, List<IdentifierOS>>() {
            @Override
            protected List<Identifier> toResult(List<IdentifierOS> source, Object[] arguments) {
                return _toResult(source, arguments);
            }
        }, result, null, null);
    }

    @Override
    public PaginatedResult<List<Identifier>> findByApplication(Application application) {
        PaginatedResult<List<IdentifierOS>> result = access.selectByApplicationId(application.getId());
        return new PaginatedResult<>(new PaginatedResultMapper<List<Identifier>, List<IdentifierOS>>() {
            @Override
            protected List<Identifier> toResult(List<IdentifierOS> source, Object[] arguments) {
                return _toResult(source, arguments);
            }
        }, result, null, null);
    }

    private List<Identifier> _toResult(List<IdentifierOS> source, Object[] arguments) {
        Application application = (Application) arguments[1];
        User user = (User) arguments[2];
        List<Identifier> entities = new ArrayList<>(source.size());
        for (IdentifierOS os: source) {
            entities.add(factory.reconstitute(os, application, user));
        }
        return entities;
    }
}
