package com.codingzero.saam.domain.resource;

import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.Principal;
import com.codingzero.saam.domain.Resource;
import com.codingzero.saam.domain.ResourceRepository;
import com.codingzero.saam.domain.services.ApplicationStatusVerifier;
import com.codingzero.saam.infrastructure.data.PermissionAccess;
import com.codingzero.saam.infrastructure.data.PermissionOS;
import com.codingzero.saam.infrastructure.data.ResourceAccess;
import com.codingzero.saam.infrastructure.data.ResourceOS;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.ResultFetchRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResourceRepositoryService implements ResourceRepository {

    private ResourceAccess access;
    private PermissionAccess permissionAccess;
    private ResourceFactoryService factory;
    private PermissionRepositoryService permissionRepository;
    private ApplicationStatusVerifier applicationStatusVerifier;

    public ResourceRepositoryService(ResourceAccess access,
                                     PermissionAccess permissionAccess,
                                     ResourceFactoryService factory,
                                     PermissionRepositoryService permissionRepository,
                                     ApplicationStatusVerifier applicationStatusVerifier) {
        this.access = access;
        this.permissionAccess = permissionAccess;
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

    public ResourceEntity loadParent(Application application, String key, Principal owner) {
        String parentKey = factory.readParentKey(key);
        if (null == parentKey) {
            return null;
        }
        ResourceOS objectSegment = access.selectByKey(application.getId(), parentKey);
        return factory.reconstitute(objectSegment, application, owner, null);
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
        PaginatedResult<List<PermissionOS>> result = permissionAccess.selectByPrincipalId(
                application.getId(), principal.getId());
        List<PermissionOS> indexes = result.start(request.getPage(), request.getSorting()).getResult();
        List<Resource> entities = new ArrayList<>(indexes.size());
        for (PermissionOS index: indexes) {
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
