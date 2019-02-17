package com.codingzero.saam.domain;

import com.codingzero.saam.domain.resource.PermissionEntity;
import com.codingzero.saam.domain.resource.PermissionFactoryService;
import com.codingzero.saam.domain.resource.PermissionRepositoryService;
import com.codingzero.saam.infrastructure.database.PermissionOS;
import com.codingzero.saam.infrastructure.database.PermissionAccess;
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

public class PermissionRepositoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private PermissionAccess access;
    private PermissionFactoryService factory;
    private PermissionRepositoryService service;

    @Before
    public void setUp() {
        access = mock(PermissionAccess.class);
        factory = mock(PermissionFactoryService.class);
        service = new PermissionRepositoryService(
                access,
                factory);
    }

    @Test
    public void testStore_New() {
        PermissionEntity entity = mock(PermissionEntity.class);
        when(entity.isNew()).thenReturn(true);
        when(entity.isDirty()).thenReturn(true);
        PermissionOS os = mock(PermissionOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(1)).insert(os);
        verify(access, times(0)).update(os);
    }

    @Test
    public void testStore_Update() {
        PermissionEntity entity = mock(PermissionEntity.class);
        when(entity.isNew()).thenReturn(false);
        when(entity.isDirty()).thenReturn(true);
        PermissionOS os = mock(PermissionOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(0)).insert(os);
        verify(access, times(1)).update(os);
    }

    @Test
    public void testRemove() {
        PermissionEntity entity = mock(PermissionEntity.class);
        PermissionOS os = mock(PermissionOS.class);
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
        Resource resource = mock(Resource.class);
        when(resource.getApplication()).thenReturn(application);
        when(resource.getKey()).thenReturn(resourceKey);
        Principal principal = mock(Principal.class);
        String principalId = "principal-id";
        when(principal.getId()).thenReturn(principalId);
        PermissionOS os = mock(PermissionOS.class);
        when(access.selectByResourceKeyAndPrincipalId(applicationId, resourceKey, principalId)).thenReturn(os);
        service.findById(resource, principal);
        verify(factory, times(1)).reconstitute(os, resource, principal);
    }

    @Test
    public void testFindByResource() {
        String resourceKey = "resource-key";
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        Resource resource = mock(Resource.class);
        when(resource.getApplication()).thenReturn(application);
        when(resource.getKey()).thenReturn(resourceKey);

        ResultPage page = new OffsetBasedResultPage(1, 10);
        PermissionOS os = mock(PermissionOS.class);
        List<PermissionOS> osList = Arrays.asList(os);
        PaginatedResult<List<PermissionOS>> osResult = mock(PaginatedResult.class);
        when(osResult.getResult()).thenReturn(osList);
        when(osResult.start(page, null)).thenReturn(osResult);
        when(access.selectByResourceKey(applicationId, resourceKey)).thenReturn(osResult);

        PaginatedResult<List<Permission>> result = service.findByResource(resource);
        result.start(page).getResult();
        verify(factory, times(osList.size())).reconstitute(os, resource, null);
    }

}
