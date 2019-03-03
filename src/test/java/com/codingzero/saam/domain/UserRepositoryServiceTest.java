package com.codingzero.saam.domain;

import com.codingzero.saam.domain.principal.user.UserEntity;
import com.codingzero.saam.domain.principal.user.UserFactoryService;
import com.codingzero.saam.domain.principal.user.UserRepositoryService;
import com.codingzero.saam.domain.services.ApplicationStatusVerifier;
import com.codingzero.saam.infrastructure.data.IdentifierAccess;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierAccess;
import com.codingzero.saam.infrastructure.data.PrincipalAccess;
import com.codingzero.saam.infrastructure.data.PrincipalOS;
import com.codingzero.saam.infrastructure.data.UserAccess;
import com.codingzero.saam.infrastructure.data.UserOS;
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

public class UserRepositoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private UserAccess access;
    private PrincipalAccess principalAccess;
    private UserFactoryService factory;
    private IdentifierAccess identifierAccess;
    private OAuthIdentifierAccess oAuthIdentifierAccess;
    private ApplicationStatusVerifier applicationStatusVerifier;
    private UserRepositoryService service;

    @Before
    public void setUp() {
        access = mock(UserAccess.class);
        principalAccess = mock(PrincipalAccess.class);
        factory = mock(UserFactoryService.class);
        identifierAccess = mock(IdentifierAccess.class);
        oAuthIdentifierAccess = mock(OAuthIdentifierAccess.class);
        applicationStatusVerifier = mock(ApplicationStatusVerifier.class);
        service = new UserRepositoryService(
                access,
                principalAccess,
                identifierAccess,
                oAuthIdentifierAccess,
                factory,
                applicationStatusVerifier);
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
        service.removeByApplication(entity);
        verify(access, times(1)).deleteByApplicationId(applicationId);
    }

    @Test
    public void testLoad() {
        Application application = mock(Application.class);
        PrincipalOS principalOS = mock(PrincipalOS.class);
        UserOS os = mock(UserOS.class);
        UserEntity entity = mock(UserEntity.class);
        when(access.selectByPrincipalOS(principalOS)).thenReturn(os);
        when(factory.reconstitute(os, application)).thenReturn(entity);
        UserEntity actualEntity = service.load(application, principalOS);
        assertEquals(entity, actualEntity);
    }

    @Test
    public void testLoad_NullPrincipalOS() {
        Application application = mock(Application.class);
        UserEntity actualEntity = service.load(application, null);
        assertNull(actualEntity);
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
