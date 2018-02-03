package com.codingzero.saam.core.application;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.IdentifierPolicy;
import com.codingzero.saam.infrastructure.database.IdentifierPolicyOS;
import com.codingzero.saam.infrastructure.database.spi.IdentifierPolicyAccess;

import java.util.ArrayList;
import java.util.List;

public class IdentifierPolicyRepositoryService {

    private IdentifierPolicyAccess access;
    private EmailPolicyRepositoryService emailIdentifierPolicyRepository;
    private UsernamePolicyRepositoryService usernameIdentifierPolicyRepository;
    private IdentifierRepositoryService identifierRepository;

    public IdentifierPolicyRepositoryService(IdentifierPolicyAccess access,
                                             EmailPolicyRepositoryService emailIdentifierPolicyRepository,
                                             UsernamePolicyRepositoryService usernameIdentifierPolicyRepository,
                                             IdentifierRepositoryService identifierRepository) {
        this.access = access;
        this.emailIdentifierPolicyRepository = emailIdentifierPolicyRepository;
        this.usernameIdentifierPolicyRepository = usernameIdentifierPolicyRepository;
        this.identifierRepository = identifierRepository;
    }

    public void store(IdentifierPolicyEntity entity) {
        if (entity.getType() == IdentifierType.USERNAME) {
            usernameIdentifierPolicyRepository.store((UsernamePolicyEntity) entity);
        } else if (entity.getType() == IdentifierType.EMAIL) {
            emailIdentifierPolicyRepository.store((EmailPolicyEntity) entity);
        } else {
            throw new IllegalArgumentException("Unknown identifier type, " + entity.getType());
        }
        flushDirtyIdentifiers(entity);
    }

    private void flushDirtyIdentifiers(IdentifierPolicyEntity policy) {
        List<IdentifierEntity> entities = policy.getDirtyIdentifiers();
        for (IdentifierEntity entity: entities) {
            if (entity.isVoid()) {
                identifierRepository.remove(entity);
            } else {
                identifierRepository.store(entity);
            }
        }
    }

    public void remove(IdentifierPolicyEntity entity) {
        identifierRepository.remove(entity);
        if (entity.getType() == IdentifierType.USERNAME) {
            usernameIdentifierPolicyRepository.remove((UsernamePolicyEntity) entity);
        } else if (entity.getType() == IdentifierType.EMAIL) {
            emailIdentifierPolicyRepository.remove((EmailPolicyEntity) entity);
        } else {
            throw new IllegalArgumentException("Unknown identifier type, " + entity.getType());
        }
    }

    public void removeAll(Application application) {
        identifierRepository.removeAll(application);
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
