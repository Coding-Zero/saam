package com.codingzero.saam.core.application;

import com.codingzero.saam.infrastructure.database.PrincipalOS;
import com.codingzero.saam.infrastructure.database.spi.PrincipalAccess;
import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.core.Application;

public class PrincipalRepositoryService {

    private PrincipalAccess access;
    private UserRepositoryService userRepository;
    private RoleRepositoryService roleRepository;
    private APIKeyRepositoryService apiKeyRepository;

    public PrincipalRepositoryService(PrincipalAccess access,
                                      UserRepositoryService userRepository,
                                      RoleRepositoryService roleRepository,
                                      APIKeyRepositoryService apiKeyRepository) {
        this.access = access;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.apiKeyRepository = apiKeyRepository;
    }

    public void store(PrincipalEntity entity) {
        if (entity.isNew()) {
            access.insert((PrincipalOS) entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update((PrincipalOS) entity.getObjectSegment());
        }
        if (entity.getType() == PrincipalType.USER) {
            userRepository.store((UserEntity) entity);
        } else if (entity.getType() == PrincipalType.ROLE) {
            roleRepository.store((RoleEntity) entity);
        } else if (entity.getType() == PrincipalType.API_KEY) {
            apiKeyRepository.store((APIKeyEntity) entity);
        } else {
            throw new UnsupportedOperationException("Unsupported principal type, " + entity.getType());
        }
    }

    public void remove(PrincipalEntity entity) {
        access.delete((PrincipalOS) entity.getObjectSegment());
        if (entity.getType() == PrincipalType.USER) {
            userRepository.remove((UserEntity) entity);
        } else if (entity.getType() == PrincipalType.ROLE) {
            roleRepository.remove((RoleEntity) entity);
        } else if (entity.getType() == PrincipalType.API_KEY) {
            apiKeyRepository.remove((APIKeyEntity) entity);
        } else {
            throw new UnsupportedOperationException("Unsupported principal type, " + entity.getType());
        }
    }

    public void removeAll(Application application) {
        access.deleteByApplicationId(application.getId());
        apiKeyRepository.removeAll(application);
        roleRepository.removeAll(application);
        userRepository.removeAll(application);
    }

    public PrincipalEntity findById(Application application, String id) {
        PrincipalOS os = access.selectById(application.getId(), id);
        if (os.getType() == PrincipalType.USER) {
            return userRepository.load(application, os);
        } else if (os.getType() == PrincipalType.ROLE) {
            return roleRepository.load(application, os);
        } else if (os.getType() == PrincipalType.API_KEY) {
            return apiKeyRepository.load(application, os);
        }
        throw new IllegalArgumentException("Unsupported principal type, " + os.getType());
    }

}
