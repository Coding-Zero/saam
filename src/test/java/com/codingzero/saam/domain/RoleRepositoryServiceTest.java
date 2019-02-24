package com.codingzero.saam.domain;

import com.codingzero.saam.domain.principal.role.RoleEntity;
import com.codingzero.saam.domain.principal.role.RoleFactoryService;
import com.codingzero.saam.domain.principal.role.RoleRepositoryService;
import com.codingzero.saam.domain.services.ApplicationStatusVerifier;
import com.codingzero.saam.infrastructure.database.PrincipalOS;
import com.codingzero.saam.infrastructure.database.RoleOS;
import com.codingzero.saam.infrastructure.database.PrincipalAccess;
import com.codingzero.saam.infrastructure.database.RoleAccess;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RoleRepositoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private RoleAccess access;
    private PrincipalAccess principalAccess;
    private RoleFactoryService factory;
    private ApplicationStatusVerifier applicationStatusVerifier;
    private RoleRepositoryService service;

    @Before
    public void setUp() {
        access = mock(RoleAccess.class);
        principalAccess = mock(PrincipalAccess.class);
        factory = mock(RoleFactoryService.class);
        applicationStatusVerifier = mock(ApplicationStatusVerifier.class);
        service = new RoleRepositoryService(
                access,
                principalAccess,
                factory, applicationStatusVerifier);
    }

    @Test
    public void testStore_New() {
        RoleEntity entity = mock(RoleEntity.class);
        when(entity.isNew()).thenReturn(true);
        when(entity.isDirty()).thenReturn(true);
        RoleOS os = mock(RoleOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(1)).insert(os);
        verify(access, times(0)).update(os);
    }

    @Test
    public void testStore_Update() {
        RoleEntity entity = mock(RoleEntity.class);
        when(entity.isNew()).thenReturn(false);
        when(entity.isDirty()).thenReturn(true);
        RoleOS os = mock(RoleOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(0)).insert(os);
        verify(access, times(1)).update(os);
    }

    @Test
    public void testRemove() {
        RoleEntity entity = mock(RoleEntity.class);
        RoleOS os = mock(RoleOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.remove(entity);
        verify(access, times(1)).delete(os);
    }

    @Test
    public void testRemoveAll() {
        String applicationId = "application-id";
        Application entity = mock(Application.class);
        when(entity.getId()).thenReturn(applicationId);
        service.removeByApplication(entity);
        verify(access, times(1)).deleteByApplicationId(applicationId);
    }

    @Test
    public void testFindById() {
        String id = "principal-id";
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        PrincipalOS principalOS = mock(PrincipalOS.class);
        when(principalAccess.selectById(applicationId, id)).thenReturn(principalOS);
        RoleOS os = mock(RoleOS.class);
        when(access.selectByPrincipalOS(principalOS)).thenReturn(os);
        service.findById(application, id);
        verify(factory, times(1)).reconstitute(os, application);
    }

    @Test
    public void testLoad() {
        Application application = mock(Application.class);
        PrincipalOS principalOS = mock(PrincipalOS.class);
        RoleOS os = mock(RoleOS.class);
        RoleEntity entity = mock(RoleEntity.class);
        when(access.selectByPrincipalOS(principalOS)).thenReturn(os);
        when(factory.reconstitute(os, application)).thenReturn(entity);
        RoleEntity actualEntity = service.load(application, principalOS);
        assertEquals(entity, actualEntity);
    }

    @Test
    public void testLoad_NullPrincipalOS() {
        Application application = mock(Application.class);
        RoleEntity actualEntity = service.load(application, null);
        assertNull(actualEntity);
    }

}
