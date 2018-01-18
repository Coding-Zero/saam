package com.codingzero.saam.core.application;

import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.User;
import com.codingzero.saam.infrastructure.database.UserSessionOS;
import com.codingzero.saam.infrastructure.database.spi.UserSessionAccess;

import java.util.Date;
import java.util.Map;

public class UserSessionFactoryService {

    private UserSessionAccess access;
    private UserRepositoryService userRepository;

    public UserSessionFactoryService(UserSessionAccess access,
                                     UserRepositoryService userRepository) {
        this.access = access;
        this.userRepository = userRepository;
    }

    public UserSessionEntity generate(Application application, User user, Map<String, Object> details, long timeout) {
        String key = access.generateKey(application.getId());
        Date expirationTime = new Date(System.currentTimeMillis() + timeout);
        UserSessionOS os = new UserSessionOS(
                application.getId(), key, user.getId(), expirationTime, new Date(), details);
        UserSessionEntity entity = reconstitute(os, application, user);
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
