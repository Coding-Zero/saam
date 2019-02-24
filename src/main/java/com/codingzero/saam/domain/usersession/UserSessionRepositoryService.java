package com.codingzero.saam.domain.usersession;

import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.User;
import com.codingzero.saam.domain.UserSession;
import com.codingzero.saam.domain.UserSessionRepository;
import com.codingzero.saam.domain.services.ApplicationStatusVerifier;
import com.codingzero.saam.infrastructure.database.UserSessionOS;
import com.codingzero.saam.infrastructure.database.UserSessionAccess;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.PaginatedResultMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserSessionRepositoryService implements UserSessionRepository {

    private UserSessionAccess access;
    private UserSessionFactoryService factory;
    private ApplicationStatusVerifier applicationStatusVerifier;

    public UserSessionRepositoryService(UserSessionAccess access, UserSessionFactoryService factory, ApplicationStatusVerifier applicationStatusVerifier) {
        this.access = access;
        this.factory = factory;
        this.applicationStatusVerifier = applicationStatusVerifier;
    }

    @Override
    public UserSession store(UserSession session) {
        UserSessionEntity entity = (UserSessionEntity) session;
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        }
        return factory.reconstitute(entity.getObjectSegment(), session.getApplication(), session.getUser());
    }

    @Override
    public void remove(UserSession session) {
        applicationStatusVerifier.checkForDeactiveStatus(session.getApplication());
        UserSessionEntity entity = (UserSessionEntity) session;
        access.delete(entity.getObjectSegment());
    }

    @Override
    public void removeByUser(User user) {
        applicationStatusVerifier.checkForDeactiveStatus(user.getApplication());
        access.deleteByUserId(user.getApplication().getId(), user.getId());
    }

    @Override
    public void removeByApplication(Application application) {
        applicationStatusVerifier.checkForDeactiveStatus(application);
        access.deleteByApplicationId(application.getId());
    }

    @Override
    public UserSession findByKey(Application application, String key) {
        UserSessionOS os = access.selectByKey(application.getId(), key);
        return factory.reconstitute(os, application, null);
    }

    @Override
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
