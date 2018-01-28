package com.codingzero.saam.core.application;

import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.User;
import com.codingzero.saam.core.UserSession;
import com.codingzero.saam.infrastructure.database.UserSessionOS;
import com.codingzero.saam.infrastructure.database.spi.UserSessionAccess;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.PaginatedResultMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserSessionRepositoryService {

    private UserSessionAccess access;
    private UserSessionFactoryService factory;

    public UserSessionRepositoryService(UserSessionAccess access, UserSessionFactoryService factory) {
        this.access = access;
        this.factory = factory;
    }

    public void store(UserSessionEntity entity) {
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        }
    }

    public void remove(UserSessionEntity entity) {
        access.delete(entity.getObjectSegment());
    }

    public void remove(User user) {
        access.deleteByUserId(user.getApplication().getId(), user.getId());
    }

    public void removeAll(Application application) {
        access.deleteByApplicationId(application.getId());
    }

    public UserSessionEntity findByKey(Application application, String key) {
        UserSessionOS os = access.selectByKey(application.getId(), key);
        return factory.reconstitute(os, application, null);
    }

    public PaginatedResult<List<UserSession>> findByOwner(User user) {
        PaginatedResult<List<UserSessionOS>> result =
                access.selectByUserId(user.getApplication().getId(), user.getId());
        return new PaginatedResult<>(new PaginatedResultMapper<List<UserSession>, List<UserSessionOS>>() {
            @Override
            protected List<UserSession> toResult(List<UserSessionOS> source, Object[] arguments) {
                return _toResult(source, arguments);
            }
        }, result, user.getApplication(), user);
    }

    private List<UserSession> _toResult(List<UserSessionOS> source, Object[] arguments) {
        Application application = (Application) arguments[1];
        User user = (User) arguments[2];
        List<UserSession> entities = new ArrayList<>(source.size());
        for (UserSessionOS os: source) {
            entities.add(factory.reconstitute(os, application, user));
        }
        return Collections.unmodifiableList(entities);
    }
}
