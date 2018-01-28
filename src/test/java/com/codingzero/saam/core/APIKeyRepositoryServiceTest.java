package com.codingzero.saam.core;

import com.codingzero.saam.core.application.APIKeyEntity;
import com.codingzero.saam.core.application.APIKeyFactoryService;
import com.codingzero.saam.core.application.APIKeyRepositoryService;
import com.codingzero.saam.core.application.UserRepositoryService;
import com.codingzero.saam.infrastructure.database.APIKeyOS;
import com.codingzero.saam.infrastructure.database.PrincipalOS;
import com.codingzero.saam.infrastructure.database.spi.APIKeyAccess;
import com.codingzero.saam.infrastructure.database.spi.PrincipalAccess;
import com.codingzero.utilities.error.BusinessError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class APIKeyRepositoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private APIKeyAccess access;
    private PrincipalAccess principalAccess;
    private APIKeyFactoryService factory;
    private APIKeyRepositoryService service;

    @Before
    public void setUp() {
        access = mock(APIKeyAccess.class);
        principalAccess = mock(PrincipalAccess.class);
        factory = mock(APIKeyFactoryService.class);
        service = new APIKeyRepositoryService(
                access,
                principalAccess,
                factory);
    }

    @Test
    public void testStore_New() {
        APIKeyEntity entity = mock(APIKeyEntity.class);
        when(entity.isNew()).thenReturn(true);
        when(entity.isDirty()).thenReturn(true);
        APIKeyOS os = mock(APIKeyOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(1)).insert(os);
        verify(access, times(0)).update(os);
    }

    @Test
    public void testStore_Update() {
        APIKeyEntity entity = mock(APIKeyEntity.class);
        when(entity.isNew()).thenReturn(false);
        when(entity.isDirty()).thenReturn(true);
        APIKeyOS os = mock(APIKeyOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(0)).insert(os);
        verify(access, times(1)).update(os);
    }

    @Test
    public void testRemove() {
        APIKeyEntity entity = mock(APIKeyEntity.class);
        APIKeyOS os = mock(APIKeyOS.class);
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
    public void testFindByKey() {
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        String key = "key";
        APIKeyOS os = mock(APIKeyOS.class);
        when(access.selectByKey(applicationId, key, principalAccess)).thenReturn(os);
        service.findByKey(application, key);
        verify(factory, times(1)).reconstitute(os, application, null);
    }

    @Test
    public void testFindByOwner() {
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        String userId = "user-id";
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        APIKeyOS os = mock(APIKeyOS.class);
        List<APIKeyOS> osList = Arrays.asList(os);
        when(access.selectByUserId(applicationId, userId, principalAccess)).thenReturn(osList);
        service.findByOwner(application, user);
        verify(factory, times(osList.size())).reconstitute(os, application, user);
    }

    @Test
    public void testLoad() {
        Application application = mock(Application.class);
        PrincipalOS principalOS = mock(PrincipalOS.class);
        APIKeyOS os = mock(APIKeyOS.class);
        when(access.selectByPrincipalOS(principalOS)).thenReturn(os);
        service.load(application, principalOS);
        verify(factory, times(1)).reconstitute(os, application, null);
    }

}
