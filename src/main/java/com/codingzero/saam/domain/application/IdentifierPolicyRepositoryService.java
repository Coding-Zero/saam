package com.codingzero.saam.domain.application;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.IdentifierPolicy;
import com.codingzero.saam.infrastructure.data.IdentifierAccess;
import com.codingzero.saam.infrastructure.data.IdentifierPolicyAccess;
import com.codingzero.saam.infrastructure.data.IdentifierPolicyOS;

import java.util.ArrayList;
import java.util.List;

public class IdentifierPolicyRepositoryService {

    private IdentifierPolicyAccess access;
    private EmailPolicyRepositoryService emailIdentifierPolicyRepository;
    private UsernamePolicyRepositoryService usernameIdentifierPolicyRepository;
    private IdentifierAccess identifierAccess;

    public IdentifierPolicyRepositoryService(IdentifierPolicyAccess access,
                                             EmailPolicyRepositoryService emailIdentifierPolicyRepository,
                                             UsernamePolicyRepositoryService usernameIdentifierPolicyRepository,
                                             IdentifierAccess identifierAccess) {
        this.access = access;
        this.emailIdentifierPolicyRepository = emailIdentifierPolicyRepository;
        this.usernameIdentifierPolicyRepository = usernameIdentifierPolicyRepository;
        this.identifierAccess = identifierAccess;
    }

    public void store(IdentifierPolicyEntity entity) {
        if (entity.isNew()) {
            access.insert((IdentifierPolicyOS) entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update((IdentifierPolicyOS) entity.getObjectSegment());
        }
        if (entity.getType() == IdentifierType.USERNAME) {
            usernameIdentifierPolicyRepository.store((UsernamePolicyEntity) entity);
        } else if (entity.getType() == IdentifierType.EMAIL) {
            emailIdentifierPolicyRepository.store((EmailPolicyEntity) entity);
        } else {
            throw new IllegalArgumentException("Unknown identifier type, " + entity.getType());
        }
//        flushDirtyIdentifiers(entity);
    }

//    private void flushDirtyIdentifiers(IdentifierPolicyEntity policy) {
//        List<IdentifierEntity> entities = policy.getDirtyIdentifiers();
//        for (IdentifierEntity entity: entities) {
//            if (entity.isVoid()) {
//                identifierRepository.remove(entity);
//            } else {
//                identifierRepository.store(entity);
//            }
//        }
//    }

    public void remove(IdentifierPolicyEntity entity) {
        checkForUnremoveableStatus(entity);
        access.delete((IdentifierPolicyOS) entity.getObjectSegment());
        if (entity.getType() == IdentifierType.USERNAME) {
            usernameIdentifierPolicyRepository.remove((UsernamePolicyEntity) entity);
        } else if (entity.getType() == IdentifierType.EMAIL) {
            emailIdentifierPolicyRepository.remove((EmailPolicyEntity) entity);
        } else {
            throw new IllegalArgumentException("Unknown identifier type, " + entity.getType());
        }
    }

    private void checkForUnremoveableStatus(IdentifierPolicyEntity entity) {
        if (identifierAccess.countByType(entity.getApplication().getId(), entity.getType()) > 0) {
            throw new IllegalStateException(
                    "Identifier policy " + entity.getType() + " cannot be removed before removing existing identifiers.");
        }
    }

    public void removeAll(Application application) {
        access.deleteByApplicationId(application.getId());
        usernameIdentifierPolicyRepository.removeAll(application);
        emailIdentifierPolicyRepository.removeAll(application);
    }

    public IdentifierPolicyEntity findByType(Application application, IdentifierType type) {
        IdentifierPolicyOS os = access.selectByType(application.getId(), type);
        return load(application, os);
    }

    public List<IdentifierPolicy> findAll(Application application) {
        List<IdentifierPolicyOS> osList = access.selectByApplicationId(application.getId());
        List<IdentifierPolicy> entities = new ArrayList<>(osList.size());
        for (IdentifierPolicyOS os: osList) {
            entities.add(load(application, os));
        }
        return entities;
    }

    private IdentifierPolicyEntity load(Application application, IdentifierPolicyOS os) {
        if (null == os) {
            return null;
        }
        if (os.getType() == IdentifierType.EMAIL) {
            return emailIdentifierPolicyRepository.load(application, os);
        } else if (os.getType() == IdentifierType.USERNAME) {
            return usernameIdentifierPolicyRepository.load(application, os);
        }
        throw new IllegalArgumentException("Unknown identifier type, " + os.getType());
    }

}
