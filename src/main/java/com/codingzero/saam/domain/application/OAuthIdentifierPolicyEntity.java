package com.codingzero.saam.domain.application;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.OAuthIdentifierPolicy;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierPolicyOS;
import com.codingzero.utilities.ddd.EntityObject;

import java.util.Date;
import java.util.Map;

public class OAuthIdentifierPolicyEntity extends EntityObject<OAuthIdentifierPolicyOS>
        implements OAuthIdentifierPolicy {

    private Application application;
//    private Map<OAuthPlatform, OAuthIdentifierEntity> dirtyIdentifiers;

    public OAuthIdentifierPolicyEntity(OAuthIdentifierPolicyOS objectSegment,
                                       Application application) {
        super(objectSegment);
        this.application = application;
//        this.dirtyIdentifiers = new HashMap<>();
    }

//    public List<OAuthIdentifierEntity> getDirtyIdentifiers() {
//        return Collections.unmodifiableList(new ArrayList<>(dirtyIdentifiers.values()));
//    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public OAuthPlatform getPlatform() {
        return getObjectSegment().getPlatform();
    }

    @Override
    public Map<String, Object> getConfigurations() {
        return getObjectSegment().getConfigurations();
    }

    @Override
    public void setConfigurations(Map<String, Object> configurations) {
        if (getConfigurations().equals(configurations)) {
            return;
        }
        getObjectSegment().setConfigurations(configurations);
        getObjectSegment().setUpdateTime(new Date(System.currentTimeMillis()));
        markAsDirty();
    }

    @Override
    public boolean isActive() {
        return getObjectSegment().isActive();
    }

    @Override
    public void setActive(boolean isActive) {
        if (isActive() == isActive) {
            return;
        }
        getObjectSegment().setActive(isActive);
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

//    @Override
//    public OAuthIdentifier addIdentifier(String content, Map<String, Object> properties, User user) {
//        OAuthIdentifierEntity entity = oAuthIdentifierFactory.generate(this, content, properties, user);
//        dirtyIdentifiers.put(entity.getPolicy().getPlatform(), entity);
//        return entity;
//    }

//    @Override
//    public void updateIdentifier(OAuthIdentifier identifier) {
//        OAuthIdentifierEntity entity = (OAuthIdentifierEntity) identifier;
//        dirtyIdentifiers.put(entity.getPolicy().getPlatform(), entity);
//    }

//    @Override
//    public void removeIdentifier(OAuthIdentifier identifier) {
//        OAuthIdentifierEntity entity = (OAuthIdentifierEntity) identifier;
//        entity.markAsVoid();
//        dirtyIdentifiers.put(entity.getPolicy().getPlatform(), entity);
//    }

//    @Override
//    public OAuthIdentifier fetchIdentifierById(String content) {
//        return oAuthIdentifierRepository.findByContent(this, content);
//    }

//    @Override
//    public OAuthIdentifier fetchIdentifierByUserAndId(User user, String content) {
//        if (null == content) {
//            return null;
//        }
//        OAuthIdentifier identifier = fetchIdentifierById(content);
//        if (null != identifier
//                && identifier.getUser().getId().equalsIgnoreCase(user.getId())) {
//            return identifier;
//        }
//        return null;
//    }

//    @Override
//    public List<OAuthIdentifier> fetchIdentifiersByUser(User user) {
//        return oAuthIdentifierRepository.findByPolicyAndUser(this, user);
//    }

//    @Override
//    public PaginatedResult<List<OAuthIdentifier>> fetchAllIdentifiers() {
//        return oAuthIdentifierRepository.findByPolicy(this);
//    }
}
