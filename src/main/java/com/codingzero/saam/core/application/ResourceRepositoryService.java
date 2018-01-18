package com.codingzero.saam.core.application;

import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.Permission;
import com.codingzero.saam.core.Principal;
import com.codingzero.saam.core.Resource;
import com.codingzero.saam.infrastructure.database.ResourceOS;
import com.codingzero.saam.infrastructure.database.spi.ResourceAccess;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.ResultFetchRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResourceRepositoryService {

    private ResourceAccess access;
    private ResourceFactoryService factory;
    private PermissionRepositoryService permissionRepository;

    public ResourceRepositoryService(ResourceAccess access,
                                     ResourceFactoryService factory,
                                     PermissionRepositoryService permissionRepository) {
        this.access = access;
        this.factory = factory;
        this.permissionRepository = permissionRepository;
    }

    public void store(ResourceEntity entity) {
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update(entity.getObjectSegment());
        }
        flushDirtyPermissions(entity);
    }

    private void flushDirtyPermissions(ResourceEntity entity) {
        List<PermissionEntity> dirtyPermissions = entity.getDirtyPermissions();
        for (PermissionEntity permission: dirtyPermissions) {
            if (permission.isVoid()) {
                permissionRepository.remove(permission);
            } else {
                permissionRepository.store(permission);
            }
        }
    }

    public void remove(ResourceEntity entity) {
        access.delete(entity.getObjectSegment());
    }

    public void removeAll(Application application) {
        access.deleteByApplicationId(application.getId());
    }

    public ResourceEntity findByKey(Application application, String key) {
        ResourceOS os = access.selectByKey(application.getId(), key);
        return factory.reconstitute(os, application, null, null);
    }

    public PaginatedResult<List<Resource>> findByOwner(Application application, Principal owner, Resource parent) {
        return new PaginatedResult<>(
                request -> _findByOwner(request), application, owner, parent);
    }

    public PaginatedResult<List<Resource>> findPermissionAssignedResources(
            Application application, Principal principal) {
        return new PaginatedResult<>(
                request -> _findPermissionAssignedResources(request), application, principal);
    }

    public PaginatedResult<List<Resource>> findAll(Application application, Resource parent) {
        return new PaginatedResult<>(request -> _findAll(request), application, parent);
    }

    private List<Resource> _findByOwner(ResultFetchRequest request) {
        Application application = (Application) request.getArguments()[0];
        Principal owner = (Principal) request.getArguments()[1];
        Resource parent = (Resource) request.getArguments()[2];
        String parentKey = getParentKey(parent);
        PaginatedResult<List<ResourceOS>> result =
                access.selectByPrincipalId(application.getId(), parentKey, owner.getId());
        List<ResourceOS> osList = result.start(request.getPage(), request.getSorting()).getResult();
        List<Resource> entities = new ArrayList<>(osList.size());
        for (ResourceOS os: osList) {
            entities.add(factory.reconstitute(os, application, owner, parent));
        }
        return Collections.unmodifiableList(entities);
    }

    private List<Resource> _findAll(ResultFetchRequest request) {
        Application application = (Application) request.getArguments()[0];
        Resource parent = (Resource) request.getArguments()[1];
        String parentKey = getParentKey(parent);
        PaginatedResult<List<ResourceOS>> result = access.selectAll(application.getId(), parentKey);
        List<ResourceOS> osList = result.start(request.getPage(), request.getSorting()).getResult();
        List<Resource> entities = new ArrayList<>(osList.size());
        for (ResourceOS os: osList) {
            entities.add(factory.reconstitute(os, application, null, parent));
        }
        return Collections.unmodifiableList(entities);
    }

    private List<Resource> _findPermissionAssignedResources(ResultFetchRequest request) {
        Principal principal = (Principal) request.getArguments()[1];
        PaginatedResult<List<Permission>> result = permissionRepository.findByPrincipal(principal);
        List<Permission> permissions = result.start(request.getPage(), request.getSorting()).getResult();
        List<Resource> entities = new ArrayList<>(permissions.size());
        for (Permission permission: permissions) {
            entities.add(permission.getResource());
        }
        return Collections.unmodifiableList(entities);
    }

    private String getParentKey(Resource parent) {
        String parentKey = null;
        if (null != parent) {
            parentKey = parent.getKey();
        }
        return parentKey;
    }
}
