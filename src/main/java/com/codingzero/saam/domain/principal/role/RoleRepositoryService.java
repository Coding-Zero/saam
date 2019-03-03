package com.codingzero.saam.domain.principal.role;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.Role;
import com.codingzero.saam.domain.RoleRepository;
import com.codingzero.saam.domain.services.ApplicationStatusVerifier;
import com.codingzero.saam.infrastructure.data.PrincipalAccess;
import com.codingzero.saam.infrastructure.data.PrincipalOS;
import com.codingzero.saam.infrastructure.data.RoleAccess;
import com.codingzero.saam.infrastructure.data.RoleOS;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.ResultFetchRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoleRepositoryService implements RoleRepository {

    private RoleAccess access;
    private PrincipalAccess principalAccess;
    private RoleFactoryService factory;
    private ApplicationStatusVerifier applicationStatusVerifier;

    public RoleRepositoryService(RoleAccess access,
                                 PrincipalAccess principalAccess,
                                 RoleFactoryService factory,
                                 ApplicationStatusVerifier applicationStatusVerifier) {
        this.access = access;
        this.principalAccess = principalAccess;
        this.factory = factory;
        this.applicationStatusVerifier = applicationStatusVerifier;
    }

    @Override
    public Role store(Role role) {
        applicationStatusVerifier.checkForDeactiveStatus(role.getApplication());
        RoleEntity entity = (RoleEntity) role;
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update(entity.getObjectSegment());
        }
        return factory.reconstitute(entity.getObjectSegment(), role.getApplication());
    }

    @Override
    public void remove(Role role) {
        applicationStatusVerifier.checkForDeactiveStatus(role.getApplication());
        RoleEntity entity = (RoleEntity) role;
        access.delete(entity.getObjectSegment());
    }

    @Override
    public void removeByApplication(Application application) {
        applicationStatusVerifier.checkForDeactiveStatus(application);
        access.deleteByApplicationId(application.getId());
    }

    @Override
    public Role findById(Application application, String id) {
        PrincipalOS principalOS = principalAccess.selectById(application.getId(), id);
        return load(application, principalOS);
    }

    @Override
    public Role findByName(Application application, String name) {
        RoleOS os = access.selectByName(application.getId(), name);
        return factory.reconstitute(os, application);
    }

    @Override
    public PaginatedResult<List<Role>> findAll(Application application) {
        return new PaginatedResult<>(request -> _findAll(request), application);
    }

    public RoleEntity load(Application application, PrincipalOS principalOS) {
        if (null == principalOS) {
            return null;
        }
        RoleOS os = access.selectByPrincipalOS(principalOS);
        return factory.reconstitute(os, application);
    }

    private List<Role> _findAll(ResultFetchRequest request) {
        Application application = (Application) request.getArguments()[0];
        PaginatedResult<List<PrincipalOS>> result =
                principalAccess.selectByApplicationIdAndType(application.getId(), PrincipalType.ROLE);
        result = result.start(request.getPage(), request.getSorting());
        List<PrincipalOS> osList = result.getResult();
        List<Role> entities = new ArrayList<>(osList.size());
        for (PrincipalOS os: osList) {
            entities.add(load(application, os));
        }
        return Collections.unmodifiableList(entities);
    }
}
