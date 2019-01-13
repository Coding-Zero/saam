package com.codingzero.saam.core.application;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.Identifier;
import com.codingzero.saam.core.IdentifierPolicy;
import com.codingzero.saam.core.User;
import com.codingzero.saam.core.identifier.IdentifierEntity;
import com.codingzero.saam.core.identifier.IdentifierFactoryService;
import com.codingzero.saam.core.identifier.IdentifierRepositoryService;
import com.codingzero.saam.infrastructure.database.IdentifierPolicyOS;
import com.codingzero.utilities.ddd.EntityObject;
import com.codingzero.utilities.error.BusinessError;
import com.codingzero.utilities.pagination.PaginatedResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IdentifierPolicyEntity<T extends IdentifierPolicyOS>
        extends EntityObject<T> implements IdentifierPolicy {

    private Application application;
    private IdentifierFactoryService identifierFactory;
    private IdentifierRepositoryService identifierRepository;
    private Map<String, IdentifierEntity> dirtyIdentifiers;

    public IdentifierPolicyEntity(T objectSegment,
                                  Application application,
                                  IdentifierFactoryService identifierFactory,
                                  IdentifierRepositoryService identifierRepository) {
        super(objectSegment);
        this.application = application;
        this.identifierFactory = identifierFactory;
        this.identifierRepository = identifierRepository;
        this.dirtyIdentifiers = new HashMap<>();
    }

//    public List<IdentifierEntity> getDirtyIdentifiers() {
//        return Collections.unmodifiableList(new ArrayList<>(dirtyIdentifiers.values()));
//    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public IdentifierType getType() {
        return getObjectSegment().getType();
    }

    @Override
    public boolean isVerificationRequired() {
        return getObjectSegment().isVerificationRequired();
    }

    @Override
    public void setVerificationRequired(boolean isVerificationRequired) {
        if (isVerificationRequired() == isVerificationRequired) {
            return;
        }
        getObjectSegment().setVerificationRequired(isVerificationRequired);
        getObjectSegment().setUpdateTime(new Date(System.currentTimeMillis()));
        markAsDirty();
    }

    @Override
    public int getMinLength() {
        return getObjectSegment().getMinLength();
    }

    @Override
    public void setMinLength(int length) {
        if (getMinLength() == length) {
            return;
        }
        getObjectSegment().setMinLength(length);
        getObjectSegment().setUpdateTime(new Date(System.currentTimeMillis()));
        markAsDirty();
    }

    @Override
    public int getMaxLength() {
        return getObjectSegment().getMaxLength();
    }

    @Override
    public void setMaxLength(int length) {
        if (getMaxLength() == length) {
            return;
        }
        getObjectSegment().setMaxLength(length);
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
    @Override
    public void check(String identifier) {
        identifier = identifier.trim();
        if (null == identifier
                || identifier.length() < getMinLength()
                || identifier.length() > getMaxLength()) {
            throw BusinessError.raise(Errors.ILLEGAL_IDENTIFIER_FORMAT)
                    .message("Identifier length need to be greater or equal than "
                            + getMinLength()
                            + " and less or equal than "
                            + getMaxLength())
                    .details("applicationId", getApplication())
                    .details("minLength", getMinLength())
                    .details("maxLength", getMaxLength())
                    .details("identifier", identifier)
                    .build();
        }
    }

//    @Override
//    public Identifier addIdentifier(String content, User user) {
//        IdentifierEntity entity = identifierFactory.generate(this, content, user);
//        dirtyIdentifiers.put(getDirtyIdentifierKey(entity), entity);
//        return entity;
//    }

//    @Override
//    public void updateIdentifier(Identifier identifier) {
//        IdentifierEntity entity = (IdentifierEntity) identifier;
//        dirtyIdentifiers.put(getDirtyIdentifierKey(entity), entity);
//        markAsDirty();
//    }

//    @Override
//    public void removeIdentifier(Identifier identifier) {
//        IdentifierEntity entity = (IdentifierEntity) identifier;
//        entity.markAsVoid();
//        dirtyIdentifiers.put(getDirtyIdentifierKey(entity), entity);
//    }

//    private String getDirtyIdentifierKey(Identifier identifier) {
//        return identifier.getContent().toLowerCase();
//    }

//    @Override
//    public Identifier fetchIdentifierByContent(String content) {
//        return identifierRepository.findByContent(this, content);
//    }

//    @Override
//    public Identifier fetchIdentifierByUserAndContent(User user, String content) {
//        if (null == content) {
//            return null;
//        }
//        Identifier identifier = fetchIdentifierByContent(content);
//        if (null != identifier
//                && identifier.getUser().getId().equalsIgnoreCase(user.getId())) {
//            return identifier;
//        }
//        return null;
//    }

//    @Override
//    public List<Identifier> fetchIdentifiersByUser(User user) {
//        return identifierRepository.findByPolicyAndUser(this, user);
//    }

//    @Override
//    public PaginatedResult<List<Identifier>> fetchAllIdentifiers() {
//        return identifierRepository.findByPolicy(this);
//    }

}
