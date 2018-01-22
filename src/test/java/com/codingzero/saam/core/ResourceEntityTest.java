package com.codingzero.saam.core;

import com.codingzero.saam.common.Action;
import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.core.application.PermissionEntity;
import com.codingzero.saam.core.application.PermissionFactoryService;
import com.codingzero.saam.core.application.PermissionRepositoryService;
import com.codingzero.saam.core.application.ResourceEntity;
import com.codingzero.saam.core.application.ResourceFactoryService;
import com.codingzero.saam.core.application.UserEntity;
import com.codingzero.saam.infrastructure.database.ResourceOS;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResourceEntityTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ResourceOS objectSegment;
    private Application application;
    private Resource parent;
    private Principal owner;
    private ResourceFactoryService factory;
    private PermissionFactoryService permissionFactory;
    private PermissionRepositoryService permissionRepository;
    private ResourceEntity entity;

    @Before
    public void setUp() {
        objectSegment = mock(ResourceOS.class);
        application = mock(Application.class);
        parent = mock(Resource.class);
        owner = mock(Principal.class);
        factory = mock(ResourceFactoryService.class);
        permissionFactory = mock(PermissionFactoryService.class);
        permissionRepository = mock(PermissionRepositoryService.class);
        createEntity();
    }

    private void createEntity() {
        entity = new ResourceEntity(
                objectSegment,
                application,
                parent,
                owner,
                factory,
                permissionFactory,
                permissionRepository);
    }

    @Test
    public void testGetParent() {
        String parentKey = "parentKey";
        when(objectSegment.getParentKey()).thenReturn(parentKey);
        Resource parentResource = mock(Resource.class);
        when(application.fetchResourceByKey(parentKey)).thenReturn(parentResource);
        parent = null;
        createEntity();
        Resource foundParent = entity.getParent();
        assertEquals(parentResource, foundParent);
    }

    @Test
    public void testGetParent_NotNull() {
        Resource foundParent = entity.getParent();
        assertEquals(parent, foundParent);
    }

    @Test
    public void testGetParent_NoParentResource() {
        when(objectSegment.getParentKey()).thenReturn(null);
        when(application.fetchResourceByKey(null)).thenReturn(null);
        parent = null;
        createEntity();
        Resource foundParent = entity.getParent();
        assertEquals(null, foundParent);
    }

    @Test
    public void testGetOwner() {
        Principal principal = mock(Principal.class);
        String principalId = "principalId";
        when(objectSegment.getPrincipalId()).thenReturn(principalId);
        when(application.fetchPrincipalById(principalId)).thenReturn(principal);
        owner = null;
        createEntity();
        Principal foundOwner = entity.getOwner();
        assertEquals(principal, foundOwner);
    }

    @Test
    public void testGetUser_NotNull() {
        createEntity();
        Principal foundOwner = entity.getOwner();
        assertEquals(owner, foundOwner);
    }

    @Test
    public void testSetOwner() {
        String principalId = "principalId";
        when(objectSegment.getPrincipalId()).thenReturn(principalId);
        Principal principal = mock(Principal.class);
        when(principal.getId()).thenReturn("principal2");
        when(factory.getRealOwner(principal)).thenReturn(principal);
        entity.setOwner(principal);
        assertEquals(true, entity.isDirty());
    }

    @Test
    public void testSetOwner_SameOwner() {
        String principalId = "principalId";
        when(objectSegment.getPrincipalId()).thenReturn(principalId);
        Principal principal = mock(Principal.class);
        when(principal.getId()).thenReturn(principalId);
        when(factory.getRealOwner(principal)).thenReturn(principal);
        entity.setOwner(principal);
        assertEquals(false, entity.isDirty());
    }

    @Test
    public void testSetOwner_SameOwner_APIKey() {
        String principalId = "principalId";
        when(objectSegment.getPrincipalId()).thenReturn(principalId);
        Principal apiKey = mock(Principal.class);
        when(apiKey.getId()).thenReturn("apikey");
        Principal apiKeyOwner = mock(Principal.class);
        when(apiKeyOwner.getId()).thenReturn(principalId);
        when(factory.getRealOwner(apiKey)).thenReturn(apiKeyOwner);
        entity.setOwner(apiKey);
        assertEquals(false, entity.isDirty());
    }

    @Test
    public void testAddPermission() {
        List<Action> actions = Collections.emptyList();
        Principal principal = mock(Principal.class);
        when(principal.getType()).thenReturn(PrincipalType.USER);
        PermissionEntity permission = mock(PermissionEntity.class);
        Resource resource = mock(Resource.class);
        when(resource.getKey()).thenReturn("resource");
        when(permission.getResource()).thenReturn(resource);
        when(permissionFactory.generate(entity, principal, actions)).thenReturn(permission);
        entity.addPermission(principal, actions);
        List<PermissionEntity> dirtyPermissions = entity.getDirtyPermissions();
        assertEquals(1, dirtyPermissions.size());
        assertEquals(permission, dirtyPermissions.get(0));
    }

    @Test
    public void testAddPermission_APIKey() {
        List<Action> actions = Arrays.asList(new Action("act", true));
        APIKey apiKey = mock(APIKey.class);
        when(apiKey.getType()).thenReturn(PrincipalType.API_KEY);
        User apiKeyOwner = mock(User.class);
        when(apiKey.getOwner()).thenReturn(apiKeyOwner);
        PermissionEntity permission = mock(PermissionEntity.class);
        Resource resource = mock(Resource.class);
        when(resource.getKey()).thenReturn("resource");
        when(permission.getResource()).thenReturn(resource);
        when(permission.containAction("act")).thenReturn(true);
        when(permissionRepository.findById(entity, apiKeyOwner)).thenReturn(permission);
        when(permissionFactory.generate(entity, apiKey, actions)).thenReturn(permission);
        entity.addPermission(apiKey, actions);
        List<PermissionEntity> dirtyPermissions = entity.getDirtyPermissions();
        assertEquals(1, dirtyPermissions.size());
        assertEquals(permission, dirtyPermissions.get(0));
    }

    @Test
    public void testAddPermission_APIKey_NullPermission() {
        List<Action> actions = Collections.emptyList();
        APIKey apiKey = mock(APIKey.class);
        when(apiKey.getType()).thenReturn(PrincipalType.API_KEY);
        User apiKeyOwner = mock(User.class);
        when(apiKey.getOwner()).thenReturn(apiKeyOwner);
        when(permissionRepository.findById(entity, apiKeyOwner)).thenReturn(null);
        thrown.expect(IllegalArgumentException.class);
        entity.addPermission(apiKey, actions);
    }

    @Test
    public void testAddPermission_APIKey_NoActionExist() {
        List<Action> actions = Arrays.asList(new Action("act", true));
        APIKey apiKey = mock(APIKey.class);
        when(apiKey.getType()).thenReturn(PrincipalType.API_KEY);
        User apiKeyOwner = mock(User.class);
        when(apiKey.getOwner()).thenReturn(apiKeyOwner);
        Permission permission = mock(Permission.class);
        when(permission.containAction("act")).thenReturn(false);
        when(permissionRepository.findById(entity, apiKeyOwner)).thenReturn(permission);
        thrown.expect(IllegalArgumentException.class);
        entity.addPermission(apiKey, actions);
    }

}
