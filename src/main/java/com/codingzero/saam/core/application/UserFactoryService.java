package com.codingzero.saam.core.application;

import com.codingzero.saam.core.Application;
import com.codingzero.saam.infrastructure.database.UserOS;
import com.codingzero.saam.infrastructure.database.spi.PasswordHelper;
import com.codingzero.saam.infrastructure.database.spi.PrincipalAccess;
import com.codingzero.saam.core.Role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserFactoryService {

    private PrincipalAccess principalAccess;
    private RoleRepositoryService roleRepository;
    private PasswordHelper passwordHelper;

    public UserFactoryService(PrincipalAccess principalAccess,
                              RoleRepositoryService roleRepository,
                              PasswordHelper passwordHelper) {
        this.principalAccess = principalAccess;
        this.roleRepository = roleRepository;
        this.passwordHelper = passwordHelper;
    }

    public UserEntity generate(Application application) {
        String id = principalAccess.generateId(application.getId());
        UserOS os = new UserOS(
                application.getId(),
                id,
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
