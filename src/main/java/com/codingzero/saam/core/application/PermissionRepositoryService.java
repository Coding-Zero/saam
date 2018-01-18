package com.codingzero.saam.core.application;

import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.Permission;
import com.codingzero.saam.core.Principal;
import com.codingzero.saam.core.Resource;
import com.codingzero.saam.infrastructure.database.PermissionOS;
import com.codingzero.saam.infrastructure.database.spi.PermissionAccess;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.PaginatedResultMapper;

import java.util.ArrayList;
import java.util.List;

public class PermissionRepositoryService {

    private PermissionAccess access;
    private PermissionFactoryService factory;

    public PermissionRepositoryService(PermissionAccess access, PermissionFactoryService factory) {
        this.access = access;
        this.factory = factory;
    }

    public void store(PermissionEntity entity) {
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update(entity.getObjectSegment());
        }
    }

    public void remove(PermissionEntity entity) {
        access.delete(entity.getObjectSegment());
    }

    public void remove(PrincipalEntity principal) {
        access.deleteByPrincipalId(principal.getApplication().getId(), principal.getId());
    }

    public void remove(ResourceEntity resource) {
        access.deleteByResourceKey(resource.getApplication().getId(), resource.getKey());
    }

    public void remove(Application application) {
        access.deleteByApplicationId(application.getId());
    }

    public Permission findById(Resource resource, Principal principal) {
        PermissionOS os = access.selectByResourceKeyAndPrincipalId(
                resource.getApplication().getId(), resource.getKey(), principal.getId());
        return factory.reconstitute(os, resource, principal);
    }

    public PaginatedResult<List<Permission>> findByResource(Resource resource) {
        PaginatedResult<List<PermissionOS>> result =
                access.selectByResourceKey(resource.getApplication().getId(), resource.getKey());
        return new PaginatedResult<>(new PaginatedResultMapper<List<Permission>, List<PermissionOS>>() {
            @Override
            protected List<Permission> toResult(List<PermissionOS> source, Object[] arguments) {
                return _findByResource(source, arguments);
            }
        }, result, resource);
    }

    public PaginatedResult<List<Permission>> findByPrincipal(Principal principal) {
        PaginatedResult<List<PermissionOS>> result =
                access.selectByPrincipalId(principal.getApplication().getId(), principal.getId());
        return new PaginatedResult<>(new PaginatedResultMapper<List<Permission>, List<PermissionOS>>() {
            @Override
            protected List<Permission> toResult(List<PermissionOS> source, Object[] arguments) {
                return _findByPrincipal(source, arguments);
            }
        }, result, principal);
    }

    private List<Permission> _findByResource(List<PermissionOS> source, Object[] arguments) {
        Resource resource = (Resource) arguments[1];
        List<Permission> permissions = new ArrayList<>(source.size());
        for (PermissionOS os: source) {
            permissions.add(factory.reconstitute(os, resource, null));
        }
        return permissions;
    }

    private List<Permission> _findByPrincipal(List<PermissionOS> source, Object[] arguments) {
        Principal principal  = (Principal) arguments[1];
        Application application = principal.getApplication();
        List<Permission> permissions = new ArrayList<>(source.size());
        for (PermissionOS os: source) {
            Resource resource = application.fetchResourceByKey(os.getResourceKey());
            permissions.add(factory.reconstitute(os, resource, principal));
        }
        return permissions;
    }
}
