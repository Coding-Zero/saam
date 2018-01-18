package com.codingzero.saam.core.application;

import com.codingzero.saam.infrastructure.database.OAuthIdentifierOS;
import com.codingzero.saam.core.OAuthIdentifier;
import com.codingzero.saam.core.OAuthIdentifierPolicy;
import com.codingzero.saam.core.User;
import com.codingzero.utilities.ddd.EntityObject;

import java.util.Date;
import java.util.Map;

public class OAuthIdentifierEntity extends EntityObject<OAuthIdentifierOS> implements OAuthIdentifier {

    private User user;
    private OAuthIdentifierPolicy policy;
    private UserRepositoryService userRepository;

    public OAuthIdentifierEntity(OAuthIdentifierOS objectSegment,
                                 OAuthIdentifierPolicy policy,
                                 User user,
                                 UserRepositoryService userRepository) {
        super(objectSegment);
        this.user = user;
        this.policy = policy;
        this.userRepository = userRepository;
    }

    @Override
    public User getUser() {
        if (null == user) {
            user = userRepository.findById(getPolicy().getApplication(), getObjectSegment().getUserId());
        }
        return user;
    }

    @Override
    public OAuthIdentifierPolicy getPolicy() {
        if (null == policy) {
            policy = user.getApplication().fetchOAuthIdentifierPolicy(getObjectSegment().getPlatform());
        }
        return policy;
    }

    @Override
    public String getContent() {
        return getObjectSegment().getContent();
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
