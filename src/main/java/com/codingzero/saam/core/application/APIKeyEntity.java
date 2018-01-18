package com.codingzero.saam.core.application;

import com.codingzero.saam.core.APIKey;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.User;
import com.codingzero.saam.infrastructure.database.APIKeyOS;

public class APIKeyEntity extends PrincipalEntity<APIKeyOS> implements APIKey {

    private User user;
    private APIKeyFactoryService factory;
    private UserRepositoryService userRepository;


    public APIKeyEntity(APIKeyOS objectSegment,
                        Application application,
                        User user,
                        APIKeyFactoryService factory,
                        UserRepositoryService userRepository) {
        super(objectSegment, application);
        this.user = user;
        this.factory = factory;
        this.userRepository = userRepository;
    }

    @Override
    public String getKey() {
        return getObjectSegment().getKey();
    }

    @Override
    public String getName() {
        return getObjectSegment().getName();
    }

    @Override
    public void setName(String name) {
        name = name.trim();
        if (name.equalsIgnoreCase(getName())) {
            return;
        }
        factory.checkForNameFormat(name);
        getObjectSegment().setName(name);
        markAsDirty();
    }

    @Override
    public User getOwner() {
        if (null == user) {
            user = userRepository.findById(getApplication(), getObjectSegment().getUserId());
        }
        return user;
    }

    @Override
    public boolean isActive() {
        return getObjectSegment().isActive();
    }

    @Override
    public void setActive(boolean isActive) {
        getObjectSegment().setActive(isActive);
        markAsDirty();
    }
}
