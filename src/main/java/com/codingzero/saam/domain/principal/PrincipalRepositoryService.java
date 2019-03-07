package com.codingzero.saam.domain.principal;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.Principal;
import com.codingzero.saam.domain.PrincipalRepository;
import com.codingzero.saam.domain.principal.apikey.APIKeyEntity;
import com.codingzero.saam.domain.principal.apikey.APIKeyRepositoryService;
import com.codingzero.saam.domain.principal.role.RoleEntity;
import com.codingzero.saam.domain.principal.role.RoleRepositoryService;
import com.codingzero.saam.domain.principal.user.UserEntity;
import com.codingzero.saam.domain.principal.user.UserRepositoryService;
import com.codingzero.saam.infrastructure.data.PrincipalAccess;
import com.codingzero.saam.infrastructure.data.PrincipalOS;

public class PrincipalRepositoryService implements PrincipalRepository {

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

    private void store(Principal principal) {
        PrincipalEntity entity = (PrincipalEntity) principal;
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
            throw new IllegalArgumentException("Unsupported principal type, " + entity.getType());
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
            throw new IllegalArgumentException("Unsupported principal type, " + entity.getType());
        }
    }

    public void removeAll(Application application) {
        userRepository.removeByApplication(application);
        roleRepository.removeByApplication(application);
        apiKeyRepository.removeByApplication(application);
    }

    @Override
    public Principal findById(Application application, String id) {
        PrincipalOS os = access.selectById(application.getId(), id);
        if (null == os) {
            return null;
        }
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
