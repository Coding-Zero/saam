package com.codingzero.saam.domain;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.common.ResourceKeySeparator;
import com.codingzero.saam.domain.resource.PermissionFactoryService;
import com.codingzero.saam.domain.resource.PermissionRepositoryService;
import com.codingzero.saam.domain.resource.ResourceEntity;
import com.codingzero.saam.domain.resource.ResourceFactoryService;
import com.codingzero.saam.domain.services.ApplicationStatusVerifier;
import com.codingzero.saam.infrastructure.data.ResourceAccess;
import com.codingzero.saam.infrastructure.data.ResourceOS;
import com.codingzero.utilities.error.BusinessError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResourceFactoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ResourceAccess access;
    private PermissionFactoryService permissionFactory;
    private PermissionRepositoryService permissionRepository;
    private PrincipalRepository principalRepository;
    private ApplicationStatusVerifier applicationStatusVerifier;
    private ResourceFactoryService service;

    @Before
    public void setUp() {
        access = mock(ResourceAccess.class);
        permissionFactory = mock(PermissionFactoryService.class);
        permissionRepository = mock(PermissionRepositoryService.class);
        principalRepository = mock(PrincipalRepository.class);
        applicationStatusVerifier = mock(ApplicationStatusVerifier.class);
        service = new ResourceFactoryService(
                access,
                permissionFactory,
                permissionRepository,
                principalRepository,
                applicationStatusVerifier);
    }

    @Test
    public void testGenerate() {
        String applicationId = "applicationId";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        String key = "resource";
        Principal owner = mock(Principal.class);
        when(owner.getType()).thenReturn(PrincipalType.USER);
        when(access.isDuplicateKey(applicationId, key)).thenReturn(false);
        Resource entity = service.generate(application, key, owner);
        assertEquals(application, entity.getApplication());
        assertEquals(key, entity.getKey());
        assertEquals(null, entity.getParent());
        assertEquals(owner, entity.getOwner());
    }

    @Test
    public void testGenerate_HasParent() {
        String applicationId = "applicationId";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        String key = "parent" + ResourceKeySeparator.VALUE + "resource";
        Principal owner = mock(Principal.class);
        when(owner.getType()).thenReturn(PrincipalType.USER);
        Resource parent = mock(Resource.class);
        when(access.isDuplicateKey(applicationId, key)).thenReturn(false);
        Resource entity = service.generate(application, key, owner);
        assertEquals(application, entity.getApplication());
        assertEquals(key, entity.getKey());
        assertEquals(parent, entity.getParent());
        assertEquals(owner, entity.getOwner());
    }

    @Test
    public void testGenerate_DuplicateKey() {
        String applicationId = "applicationId";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        String key = "resource";
        Principal owner = mock(Principal.class);
        when(owner.getType()).thenReturn(PrincipalType.USER);
        when(access.isDuplicateKey(applicationId, key)).thenReturn(true);
        thrown.expect(BusinessError.class);
        service.generate(application, key, owner);
    }

    @Test
    public void testCheckForNameFormat() {
        String name = "resource";
        service.checkForNameFormat(name);
    }

    @Test
    public void testCheckForNameFormat_NullName() {
        String name = null;
        thrown.expect(IllegalArgumentException.class);
        service.checkForNameFormat(name);
    }

    @Test
    public void testCheckForNameFormat_TooShort() {
        String name = "";
        thrown.expect(BusinessError.class);
        service.checkForNameFormat(name);
    }

    @Test
    public void testCheckForNameFormat_TooLong() {
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < ResourceFactoryService.NAME_MAX_LENGTH; i ++) {
            name.append("a");
        }
        name.append("a");
        thrown.expect(BusinessError.class);
        service.checkForNameFormat(name.toString());
    }

    @Test
    public void testCheckForNameFormat_IllegalFormat() {
        String name = "abc:abc";
        thrown.expect(BusinessError.class);
        service.checkForNameFormat(name);
    }

    @Test
    public void testGetRealOwner() {
        User owner = mock(User.class);
        when(owner.getType()).thenReturn(PrincipalType.USER);
        Principal actualOwner = service.getRealOwner(owner);
        assertEquals(owner, actualOwner);
    }

    @Test
    public void testGetRealOwner_APIKey() {
        APIKey apiKey = mock(APIKey.class);
        when(apiKey.getType()).thenReturn(PrincipalType.API_KEY);
        User apiKeyOwner = mock(User.class);
        when(apiKey.getOwner()).thenReturn(apiKeyOwner);
        Principal actualOwner = service.getRealOwner(apiKey);
        assertEquals(apiKeyOwner, actualOwner);
    }

    @Test
    public void testReconstitute() {
        Application application = mock(Application.class);
        Principal owner = mock(Principal.class);
        Resource parent = null;
        ResourceOS os = mock(ResourceOS.class);
        ResourceEntity entity = service.reconstitute(os, application, owner, parent);
        assertEquals(application, entity.getApplication());
        assertEquals(null, entity.getParent());
        assertEquals(owner, entity.getOwner());
        assertEquals(false, entity.isNew());
        assertEquals(false, entity.isDirty());
        assertEquals(false, entity.isVoid());
    }

    @Test
    public void testReconstitute_NullValue() {
        Application application = mock(Application.class);
        Principal owner = mock(Principal.class);
        Resource parent = null;
        ResourceOS os = null;
        ResourceEntity entity = service.reconstitute(os, application, owner, parent);
        assertEquals(null, entity);
    }

}
