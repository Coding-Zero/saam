package com.codingzero.saam.core.usersession;

import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.User;
import com.codingzero.saam.core.UserSession;
import com.codingzero.saam.core.UserSessionFactory;
import com.codingzero.saam.core.principal.user.UserRepositoryService;
import com.codingzero.saam.core.services.ApplicationStatusVerifier;
import com.codingzero.saam.infrastructure.database.UserSessionOS;
import com.codingzero.saam.infrastructure.database.UserSessionAccess;

import java.util.Date;
import java.util.Map;

public class UserSessionFactoryService implements UserSessionFactory {

    private UserSessionAccess access;
    private UserRepositoryService userRepository;
    private ApplicationStatusVerifier applicationStatusVerifier;

    public UserSessionFactoryService(UserSessionAccess access,
                                     UserRepositoryService userRepository, ApplicationStatusVerifier applicationStatusVerifier) {
        this.access = access;
        this.userRepository = userRepository;
        this.applicationStatusVerifier = applicationStatusVerifier;
    }

    @Override
    public UserSession generate(Application application, User user, Map<String, Object> details, long timeout) {
        applicationStatusVerifier.checkForDeactiveStatus(application);
        String key = access.generateKey(user.getApplication().getId());
        Date expirationTime = new Date(System.currentTimeMillis() + timeout);
        UserSessionOS os = new UserSessionOS(
                user.getApplication().getId(), key, user.getId(), expirationTime, new Date(), details);
        UserSessionEntity entity = reconstitute(os, user.getApplication(), user);
        entity.markAsNew();
        return entity;
    }

    public UserSessionEntity reconstitute(UserSessionOS os, Application application, User user) {
        if (null == os) {
            return null;
        }
        return new UserSessionEntity(os, application, user, userRepository);
    }

}
