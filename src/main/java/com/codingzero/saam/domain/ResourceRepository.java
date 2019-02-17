package com.codingzero.saam.domain;

import com.codingzero.utilities.pagination.PaginatedResult;

import java.util.List;

public interface ResourceRepository {

    Resource store(Resource resource);

    void remove(Resource resource);

    void removeByApplication(Application application);

    Resource findByKey(Application application, String key);

    PaginatedResult<List<Resource>> findByOwner(Application application, Principal owner, Resource parent);

    PaginatedResult<List<Resource>> findPermissionAssignedResources(
            Application application, Principal principal);

    PaginatedResult<List<Resource>> findByApplication(Application application, Resource parent);
}
