package com.codingzero.saam.core;

import com.codingzero.saam.core.application.UserEntity;
import com.codingzero.saam.core.application.UserFactoryService;
import com.codingzero.saam.core.application.UserRepositoryService;
import com.codingzero.saam.infrastructure.database.PrincipalOS;
import com.codingzero.saam.infrastructure.database.UserOS;
import com.codingzero.saam.infrastructure.database.spi.PrincipalAccess;
import com.codingzero.saam.infrastructure.database.spi.UserAccess;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserRepositoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private UserAccess access;
    private PrincipalAccess principalAccess;
    private UserFactoryService factory;
    private UserRepositoryService service;

    @Before
    public void setUp() {
        access = mock(UserAccess.class);
        principalAccess = mock(PrincipalAccess.class);
        factory = mock(UserFactoryService.class);
        service = new UserRepositoryService(
                access,
                principalAccess,
                factory);
    }

    @Test
    public void testStore_New() {
        UserEntity entity = mock(UserEntity.class);
        when(entity.isNew()).thenReturn(true);
        when(entity.isDirty()).thenReturn(true);
        UserOS os = mock(UserOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(1)).insert(os);
        verify(access, times(0)).update(os);
    }

    @Test
    public void testStore_Update() {
        UserEntity entity = mock(UserEntity.class);
        when(entity.isNew()).thenReturn(false);
        when(entity.isDirty()).thenReturn(true);
        UserOS os = mock(UserOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(0)).insert(os);
        verify(access, times(1)).update(os);
    }

    @Test
    public void testRemove() {
        UserEntity entity = mock(UserEntity.class);
        UserOS os = mock(UserOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.remove(entity);
        verify(access, times(1)).delete(os);
    }

    @Test
    public void testRemoveAll() {
        String applicationId = "application-id";
        Application entity = mock(Application.class);
        when(entity.getId()).thenReturn(applicationId);
        service.removeAll(entity);
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
        UserOS os = mock(UserOS.class);
        when(access.selectByPrincipalOS(principalOS)).thenReturn(os);
        service.findById(application, id);
        verify(factory, times(1)).reconstitute(os, application);
    }

}
