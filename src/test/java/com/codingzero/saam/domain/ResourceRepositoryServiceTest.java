package com.codingzero.saam.domain;

import com.codingzero.saam.domain.resource.PermissionRepositoryService;
import com.codingzero.saam.domain.resource.ResourceEntity;
import com.codingzero.saam.domain.resource.ResourceFactoryService;
import com.codingzero.saam.domain.resource.ResourceRepositoryService;
import com.codingzero.saam.domain.services.ApplicationStatusVerifier;
import com.codingzero.saam.infrastructure.data.PermissionAccess;
import com.codingzero.saam.infrastructure.data.ResourceAccess;
import com.codingzero.saam.infrastructure.data.ResourceOS;
import com.codingzero.utilities.pagination.OffsetBasedResultPage;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.ResultPage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ResourceRepositoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ResourceAccess access;
    private ResourceFactoryService factory;
    private PermissionRepositoryService permissionRepository;
    private PermissionAccess permissionAccess;
    private ApplicationStatusVerifier applicationStatusVerifier;
    private ResourceRepositoryService service;

    @Before
    public void setUp() {
        access = mock(ResourceAccess.class);
        factory = mock(ResourceFactoryService.class);
        permissionRepository = mock(PermissionRepositoryService.class);
        permissionAccess = mock(PermissionAccess.class);
        applicationStatusVerifier = mock(ApplicationStatusVerifier.class);
        service = new ResourceRepositoryService(
                access,
                permissionAccess,
                factory,
                permissionRepository,
                applicationStatusVerifier);
    }

    @Test
    public void testStore_New() {
        ResourceEntity entity = mock(ResourceEntity.class);
        when(entity.isNew()).thenReturn(true);
        when(entity.isDirty()).thenReturn(true);
        ResourceOS os = mock(ResourceOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(1)).insert(os);
        verify(access, times(0)).update(os);
        verify(entity, times(1)).getDirtyPermissions();
    }

    @Test
    public void testStore_Update() {
        ResourceEntity entity = mock(ResourceEntity.class);
        when(entity.isNew()).thenReturn(false);
        when(entity.isDirty()).thenReturn(true);
        ResourceOS os = mock(ResourceOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(0)).insert(os);
        verify(access, times(1)).update(os);
        verify(entity, times(1)).getDirtyPermissions();
    }

    @Test
    public void testRemove() {
        ResourceEntity entity = mock(ResourceEntity.class);
        ResourceOS os = mock(ResourceOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.remove(entity);
        verify(access, times(1)).delete(os);
    }

    @Test
    public void testFindById() {
        String resourceKey = "resource-key";
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        ResourceOS os = mock(ResourceOS.class);
        when(access.selectByKey(applicationId, resourceKey)).thenReturn(os);
        service.findByKey(application, resourceKey);
        verify(factory, times(1)).reconstitute(os, application, null, null);
    }

    @Test
    public void testFindByOwner() {
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        String ownerId = "owner-id";
        Principal owner = mock(Principal.class);
        when(owner.getId()).thenReturn(ownerId);

        ResultPage page = new OffsetBasedResultPage(1, 10);
        ResourceOS os = mock(ResourceOS.class);
        List<ResourceOS> osList = Arrays.asList(os);
        PaginatedResult<List<ResourceOS>> osResult = mock(PaginatedResult.class);
        when(osResult.getResult()).thenReturn(osList);
        when(osResult.start(page, null)).thenReturn(osResult);
        when(access.selectByPrincipalId(applicationId, null, ownerId)).thenReturn(osResult);

        PaginatedResult<List<Resource>> result = service.findByOwner(application, owner, null);
        result.start(page).getResult();
        verify(factory, times(osList.size())).reconstitute(os, application, owner, null);
    }

    @Test
    public void testFindByOwner_ParentResource() {
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        String ownerId = "owner-id";
        Principal owner = mock(Principal.class);
        when(owner.getId()).thenReturn(ownerId);
        String parentResourceKey = "parent-resource-key";
        Resource parentResource = mock(Resource.class);
        when(parentResource.getKey()).thenReturn(parentResourceKey);

        ResultPage page = new OffsetBasedResultPage(1, 10);
        ResourceOS os = mock(ResourceOS.class);
        List<ResourceOS> osList = Arrays.asList(os);
        PaginatedResult<List<ResourceOS>> osResult = mock(PaginatedResult.class);
        when(osResult.getResult()).thenReturn(osList);
        when(osResult.start(page, null)).thenReturn(osResult);
        when(access.selectByPrincipalId(applicationId, parentResourceKey, ownerId)).thenReturn(osResult);

        PaginatedResult<List<Resource>> result = service.findByOwner(application, owner, parentResource);
        result.start(page).getResult();
        verify(factory, times(osList.size())).reconstitute(os, application, owner, parentResource);
    }

}
