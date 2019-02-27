package com.codingzero.saam.domain;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.domain.principal.PrincipalRepositoryService;
import com.codingzero.saam.domain.principal.apikey.APIKeyEntity;
import com.codingzero.saam.domain.principal.apikey.APIKeyRepositoryService;
import com.codingzero.saam.domain.principal.role.RoleEntity;
import com.codingzero.saam.domain.principal.role.RoleRepositoryService;
import com.codingzero.saam.domain.principal.user.UserEntity;
import com.codingzero.saam.domain.principal.user.UserRepositoryService;
import com.codingzero.saam.infrastructure.database.APIKeyOS;
import com.codingzero.saam.infrastructure.database.PrincipalAccess;
import com.codingzero.saam.infrastructure.database.RoleOS;
import com.codingzero.saam.infrastructure.database.UserOS;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PrincipalRepositoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private PrincipalAccess access;
    private UserRepositoryService userRepository;
    private RoleRepositoryService roleRepository;
    private APIKeyRepositoryService apiKeyRepository;
    private PrincipalRepositoryService service;

    @Before
    public void setUp() {
        access = mock(PrincipalAccess.class);
        userRepository = mock(UserRepositoryService.class);
        roleRepository = mock(RoleRepositoryService.class);
        apiKeyRepository = mock(APIKeyRepositoryService.class);
        service = new PrincipalRepositoryService(
                access,
                userRepository,
                roleRepository,
                apiKeyRepository);
    }

    @Test
    public void testStore_User_New() {
        UserEntity entity = mock(UserEntity.class);
        when(entity.isNew()).thenReturn(true);
        when(entity.isDirty()).thenReturn(true);
        when(entity.getType()).thenReturn(PrincipalType.USER);
        UserOS os = mock(UserOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(userRepository, times(1)).store(entity);
    }

    @Test
    public void testStore_User_Update() {
        UserEntity entity = mock(UserEntity.class);
        when(entity.isNew()).thenReturn(false);
        when(entity.isDirty()).thenReturn(true);
        when(entity.getType()).thenReturn(PrincipalType.USER);
        UserOS os = mock(UserOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(userRepository, times(1)).store(entity);
    }

    @Test
    public void testStore_Role_New() {
        RoleEntity entity = mock(RoleEntity.class);
        when(entity.isNew()).thenReturn(true);
        when(entity.isDirty()).thenReturn(true);
        when(entity.getType()).thenReturn(PrincipalType.ROLE);
        RoleOS os = mock(RoleOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(roleRepository, times(1)).store(entity);
    }

    @Test
    public void testStore_Role_Update() {
        RoleEntity entity = mock(RoleEntity.class);
        when(entity.isNew()).thenReturn(false);
        when(entity.isDirty()).thenReturn(true);
        when(entity.getType()).thenReturn(PrincipalType.ROLE);
        RoleOS os = mock(RoleOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(roleRepository, times(1)).store(entity);
    }

    @Test
    public void testStore_APIKey_New() {
        APIKeyEntity entity = mock(APIKeyEntity.class);
        when(entity.isNew()).thenReturn(true);
        when(entity.isDirty()).thenReturn(true);
        when(entity.getType()).thenReturn(PrincipalType.API_KEY);
        APIKeyOS os = mock(APIKeyOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(apiKeyRepository, times(1)).store(entity);
    }

    @Test
    public void testStore_APIKey_Update() {
        APIKeyEntity entity = mock(APIKeyEntity.class);
        when(entity.isNew()).thenReturn(false);
        when(entity.isDirty()).thenReturn(true);
        when(entity.getType()).thenReturn(PrincipalType.API_KEY);
        APIKeyOS os = mock(APIKeyOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(apiKeyRepository, times(1)).store(entity);
    }

    @Test
    public void testRemove_User() {
        UserEntity entity = mock(UserEntity.class);
        when(entity.getType()).thenReturn(PrincipalType.USER);
        UserOS os = mock(UserOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.remove(entity);
        verify(userRepository, times(1)).remove(entity);
    }

    @Test
    public void testRemove_Role() {
        RoleEntity entity = mock(RoleEntity.class);
        when(entity.getType()).thenReturn(PrincipalType.ROLE);
        RoleOS os = mock(RoleOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.remove(entity);
        verify(roleRepository, times(1)).remove(entity);
    }

    @Test
    public void testRemove_APIKey() {
        APIKeyEntity entity = mock(APIKeyEntity.class);
        when(entity.getType()).thenReturn(PrincipalType.API_KEY);
        APIKeyOS os = mock(APIKeyOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.remove(entity);
        verify(apiKeyRepository, times(1)).remove(entity);
    }

    @Test
    public void testRemoveAll() {
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        service.removeAll(application);
        verify(userRepository, times(1)).removeByApplication(application);
        verify(roleRepository, times(1)).removeByApplication(application);
        verify(apiKeyRepository, times(1)).removeByApplication(application);
    }

    @Test
    public void testFindById_User() {
        String id = "principal-key";
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        UserOS os = mock(UserOS.class);
        when(os.getType()).thenReturn(PrincipalType.USER);
        when(access.selectById(applicationId, id)).thenReturn(os);
        service.findById(application, id);
        verify(userRepository, times(1)).load(application, os);
    }

    @Test
    public void testFindById_Role() {
        String id = "principal-key";
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        RoleOS os = mock(RoleOS.class);
        when(os.getType()).thenReturn(PrincipalType.ROLE);
        when(access.selectById(applicationId, id)).thenReturn(os);
        service.findById(application, id);
        verify(roleRepository, times(1)).load(application, os);
    }

    @Test
    public void testFindById_APIKey() {
        String id = "principal-key";
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        APIKeyOS os = mock(APIKeyOS.class);
        when(os.getType()).thenReturn(PrincipalType.API_KEY);
        when(access.selectById(applicationId, id)).thenReturn(os);
        service.findById(application, id);
        verify(apiKeyRepository, times(1)).load(application, os);
    }

    @Test
    public void testFindById_NullValue() {
        String id = "principal-key";
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        when(access.selectById(applicationId, id)).thenReturn(null);
        Principal entity = service.findById(application, id);
        assertNull(entity);
    }

}
