package com.codingzero.saam.core.resource;

import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.Principal;
import com.codingzero.saam.core.Resource;
import com.codingzero.saam.core.ResourceRepository;
import com.codingzero.saam.core.services.ApplicationStatusVerifier;
import com.codingzero.saam.infrastructure.database.PermissionResourceIndex;
import com.codingzero.saam.infrastructure.database.PermissionResourceIndexAccess;
import com.codingzero.saam.infrastructure.database.ResourceAccess;
import com.codingzero.saam.infrastructure.database.ResourceOS;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.ResultFetchRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResourceRepositoryService implements ResourceRepository {

    private ResourceAccess access;
    private PermissionResourceIndexAccess permissionResourceIndexAccess;
    private ResourceFactoryService factory;
    private PermissionRepositoryService permissionRepository;
    private ApplicationStatusVerifier applicationStatusVerifier;

    public ResourceRepositoryService(ResourceAccess access,
                                     PermissionResourceIndexAccess permissionResourceIndexAccess,
                                     ResourceFactoryService factory,
                                     PermissionRepositoryService permissionRepository,
                                     ApplicationStatusVerifier applicationStatusVerifier) {
        this.access = access;
        this.permissionResourceIndexAccess = permissionResourceIndexAccess;
        this.factory = factory;
        this.permissionRepository = permissionRepository;
        this.applicationStatusVerifier = applicationStatusVerifier;
    }

    @Override
    public Resource store(Resource resource) {
        applicationStatusVerifier.checkForDeactiveStatus(resource.getApplication());
        ResourceEntity entity = (ResourceEntity) resource;
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update(entity.getObjectSegment());
        }
        flushDirtyPermissions(entity);
        return factory.reconstitute(
                entity.getObjectSegment(),
                resource.getApplication(),
                resource.getOwner(),
                resource.getParent());
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

    @Override
    public void remove(Resource resource) {
        applicationStatusVerifier.checkForDeactiveStatus(resource.getApplication());
        ResourceEntity entity = (ResourceEntity) resource;
        access.delete(entity.getObjectSegment());
    }

    @Override
    public void removeByApplication(Application application) {
        access.deleteByApplicationId(application.getId());
    }

    @Override
    public Resource findByKey(Application application, String key) {
        ResourceOS os = access.selectByKey(application.getId(), key);
        return factory.reconstitute(os, application, null, null);
    }

    @Override
    public PaginatedResult<List<Resource>> findByOwner(Application application, Principal owner, Resource parent) {
        return new PaginatedResult<>(
                request -> _findByOwner(request), application, owner, parent);
    }

    @Override
    public PaginatedResult<List<Resource>> findPermissionAssignedResources(
            Application application, Principal principal) {
        return new PaginatedResult<>(
                request -> _findPermissionAssignedResources(request), application, principal);
    }

    @Override
    public PaginatedResult<List<Resource>> findByApplication(Application application, Resource parent) {
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
        Application application = principal.getApplication();
        PaginatedResult<List<PermissionResourceIndex>> result = permissionResourceIndexAccess.selectByPrincipalId(
                application.getId(), principal.getId());
        List<PermissionResourceIndex> indexes = result.start(request.getPage(), request.getSorting()).getResult();
        List<Resource> entities = new ArrayList<>(indexes.size());
        for (PermissionResourceIndex index: indexes) {
            Resource resource = findByKey(application, index.getResourceKey());
            entities.add(resource);
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
