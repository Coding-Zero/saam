package com.codingzero.saam.domain.principal.apikey;

import com.codingzero.saam.domain.APIKey;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.User;
import com.codingzero.saam.domain.principal.PrincipalEntity;
import com.codingzero.saam.domain.principal.user.UserRepositoryService;
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
    public String getSecretKey() {
        return getObjectSegment().getSecretKey();
    }

    @Override
    public String getName() {
        return getObjectSegment().getName();
    }

    @Override
    public void setName(String name) {
        factory.checkForNameFormat(name);
        if (name.equalsIgnoreCase(getName())) {
            return;
        }
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

    @Override
    public boolean verify(String secretKey) {
        return (null != getSecretKey()
                && getSecretKey().equals(secretKey)
                && isActive()
                && getOwner() != null);
    }
}
