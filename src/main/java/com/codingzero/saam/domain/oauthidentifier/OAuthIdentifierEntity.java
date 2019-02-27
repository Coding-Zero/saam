package com.codingzero.saam.domain.oauthidentifier;

import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.OAuthIdentifier;
import com.codingzero.saam.domain.OAuthIdentifierPolicy;
import com.codingzero.saam.domain.User;
import com.codingzero.saam.domain.principal.user.UserRepositoryService;
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
    public OAuthIdentifierPolicy getPolicy() {
        return getApplication().fetchOAuthIdentifierPolicy(getObjectSegment().getKey().getPlatform());
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
