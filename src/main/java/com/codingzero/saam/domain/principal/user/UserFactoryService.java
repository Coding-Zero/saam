package com.codingzero.saam.domain.principal.user;

import com.codingzero.saam.common.PrincipalId;
import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.Role;
import com.codingzero.saam.domain.UserFactory;
import com.codingzero.saam.domain.principal.role.RoleRepositoryService;
import com.codingzero.saam.domain.services.ApplicationStatusVerifier;
import com.codingzero.saam.infrastructure.database.UserOS;
import com.codingzero.saam.infrastructure.database.PasswordHelper;
import com.codingzero.saam.infrastructure.database.PrincipalAccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserFactoryService implements UserFactory {

    private PrincipalAccess principalAccess;
    private RoleRepositoryService roleRepository;
    private PasswordHelper passwordHelper;
    private ApplicationStatusVerifier applicationStatusVerifier;

    public UserFactoryService(PrincipalAccess principalAccess,
                              RoleRepositoryService roleRepository,
                              PasswordHelper passwordHelper,
                              ApplicationStatusVerifier applicationStatusVerifier) {
        this.principalAccess = principalAccess;
        this.roleRepository = roleRepository;
        this.passwordHelper = passwordHelper;
        this.applicationStatusVerifier = applicationStatusVerifier;
    }

    @Override
    public UserEntity generate(Application application) {
        applicationStatusVerifier.checkForDeactiveStatus(application);
        String id = principalAccess.generateId(application.getId(), PrincipalType.USER);
        UserOS os = new UserOS(
                new PrincipalId(application.getId(), id),
                new Date(),
                null,
                null,
                Collections.emptyList());
        UserEntity entity = reconstitute(os, application);
        entity.markAsNew();
        return entity;
    }

    public List<String> toRoleIds(List<Role> roles) {
        Set<String> idFilter = new HashSet<>();
        List<String> ids = new ArrayList<>(roles.size());
        for (Role role: roles) {
            if (!idFilter.contains(role.getId().toLowerCase())) {
                ids.add(role.getId());
                idFilter.add(role.getId().toLowerCase());
            }
        }
        return ids;
    }

    public UserEntity reconstitute(UserOS os, Application application) {
        if (null == os) {
            return null;
        }
        return new UserEntity(
                os,
                application,
                this,
                roleRepository,
                passwordHelper);
    }

}
