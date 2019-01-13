package com.codingzero.saam.core.oauthidentifier;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.OAuthIdentifier;
import com.codingzero.saam.core.User;
import com.codingzero.saam.core.principal.user.UserRepositoryService;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierOS;
import com.codingzero.utilities.ddd.EntityObject;

import java.util.Date;
import java.util.Map;

public class OAuthIdentifierEntity extends EntityObject<OAuthIdentifierOS> implements OAuthIdentifier {

    private Application application;
    private User user;
    private UserRepositoryService userRepository;

    public OAuthIdentifierEntity(OAuthIdentifierOS objectSegment,
                                 Application application, User user,
                                 UserRepositoryService userRepository) {
        super(objectSegment);
        this.application = application;
        this.user = user;
        this.userRepository = userRepository;
    }

    @Override
    public User getUser() {
        if (null == user) {
            user = userRepository.findById(getApplication(), getObjectSegment().getUserId());
        }
        return user;
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public OAuthPlatform getPlatform() {
        return getObjectSegment().getKey().getPlatform();
    }

    @Override
    public String getContent() {
        return getObjectSegment().getKey().getContent();
    }

    @Override
    public Map<String, Object> getProperties() {
        return getObjectSegment().getProperties();
    }

    @Override
    public void setProperties(Map<String, Object> properties) {
        if (properties.equals(getProperties())) {
            return;
        }
        getObjectSegment().setProperties(properties);
        getObjectSegment().setUpdateTime(new Date(System.currentTimeMillis()));
        markAsDirty();
    }

    @Override
    public Date getCreationTime() {
        return getObjectSegment().getCreationTime();
    }

    @Override
    public Date getUpdateTime() {
        return getObjectSegment().getUpdateTime();
    }
}
