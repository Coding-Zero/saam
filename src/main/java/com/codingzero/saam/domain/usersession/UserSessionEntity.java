package com.codingzero.saam.domain.usersession;

import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.UserSession;
import com.codingzero.saam.domain.principal.user.UserRepositoryService;
import com.codingzero.saam.infrastructure.database.UserSessionOS;
import com.codingzero.saam.domain.User;
import com.codingzero.utilities.ddd.EntityObject;

import java.util.Date;
import java.util.Map;

public class UserSessionEntity extends EntityObject<UserSessionOS> implements UserSession {

    private Application application;
    private User user;
    private UserRepositoryService userRepositoryService;

    public UserSessionEntity(UserSessionOS objectSegment,
                             Application application,
                             User user,
                             UserRepositoryService userRepositoryService) {
        super(objectSegment);
        this.application = application;
        this.user = user;
        this.userRepositoryService = userRepositoryService;
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public User getUser() {
        if (null == user) {
            user = userRepositoryService.findById(getApplication(), getObjectSegment().getUserId());
        }
        return user;
    }

    @Override
    public Map<String, Object> getDetails() {
        return getObjectSegment().getDetails();
    }

    @Override
    public String getKey() {
        return getObjectSegment().getKey();
    }

    @Override
    public Date getExpirationTime() {
        return getObjectSegment().getExpirationTime();
    }

    @Override
    public Date getCreationTime() {
        return getObjectSegment().getCreationTime();
    }

    @Override
    public boolean isExpired() {
        return (getExpirationTime().getTime() <= System.currentTimeMillis());
    }
}
