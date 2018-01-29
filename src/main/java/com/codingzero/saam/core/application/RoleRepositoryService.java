package com.codingzero.saam.core.application;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.Role;
import com.codingzero.saam.infrastructure.database.PrincipalOS;
import com.codingzero.saam.infrastructure.database.RoleOS;
import com.codingzero.saam.infrastructure.database.spi.PrincipalAccess;
import com.codingzero.saam.infrastructure.database.spi.RoleAccess;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.ResultFetchRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoleRepositoryService {

    private RoleAccess access;
    private PrincipalAccess principalAccess;
    private RoleFactoryService factory;

    public RoleRepositoryService(RoleAccess access,
                                 PrincipalAccess principalAccess,
                                 RoleFactoryService factory) {
        this.access = access;
        this.principalAccess = principalAccess;
        this.factory = factory;
    }

    public void store(RoleEntity entity) {
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update(entity.getObjectSegment());
        }
    }

    public void remove(RoleEntity entity) {
        access.delete(entity.getObjectSegment());
    }

    public void removeAll(Application application) {
        access.deleteByApplicationId(application.getId());
    }

    public RoleEntity findById(Application application, String id) {
        PrincipalOS principalOS = principalAccess.selectById(application.getId(), id);
        return load(application, principalOS);
    }

    public RoleEntity findByName(Application application, String name) {
        RoleOS os = access.selectByName(application.getId(), name);
        return factory.reconstitute(os, application);
    }

    public PaginatedResult<List<Role>> findAll(Application application) {
        return new PaginatedResult<>(request -> _findAll(request), application);
    }

    public RoleEntity load(Application application, PrincipalOS principalOS) {
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
