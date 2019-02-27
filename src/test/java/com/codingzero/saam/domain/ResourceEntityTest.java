package com.codingzero.saam.domain;

import com.codingzero.saam.common.Action;
import com.codingzero.saam.common.PermissionType;
import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.domain.resource.PermissionEntity;
import com.codingzero.saam.domain.resource.PermissionFactoryService;
import com.codingzero.saam.domain.resource.PermissionRepositoryService;
import com.codingzero.saam.domain.resource.ResourceEntity;
import com.codingzero.saam.domain.resource.ResourceFactoryService;
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
    private PrincipalRepository principalRepository;
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
        principalRepository = mock(PrincipalRepository.class);
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
                permissionRepository, principalRepository);
    }

    @Test
    public void testGetParent() {
        String parentKey = "parentKey";
        when(objectSegment.getParentKey()).thenReturn(parentKey);
        ResourceEntity parentResource = mock(ResourceEntity.class);
        when(factory.loadParent(application, parentKey)).thenReturn(parentResource);
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
        when(factory.loadParent(application, null)).thenReturn(null);
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
        when(principalRepository.findById(application, principalId)).thenReturn(principal);
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
        entity.assignPermission(principal, actions);
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
        entity.assignPermission(apiKey, actions);
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
        entity.assignPermission(apiKey, actions);
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
        entity.assignPermission(apiKey, actions);
    }

    @Test
    public void testCheckPermission_User() {
        String actionCode = "READ";
        String principalId = "principal-id";
        User principal = mock(User.class);
        when(principal.getType()).thenReturn(PrincipalType.USER);
        when(principal.getId()).thenReturn(principalId);
        when(owner.getId()).thenReturn("owner-id");
        List<Action> actions = Arrays.asList(new Action(actionCode, true));
        Permission permission = mock(Permission.class);
        when(permission.getActions()).thenReturn(actions);
        when(permissionRepository.findById(entity, principal)).thenReturn(permission);
        PermissionType type = entity.verifyPermission(principal, actionCode);
        assertEquals(PermissionType.ALLOW, type);
    }

    @Test
    public void testCheckPermission_User_PlayingRoles_IsOwner() {
        String ownerId = "owner-id";
        String actionCode = "READ";
        String principalId = "principal-id";
        User principal = mock(User.class);
        when(principal.getType()).thenReturn(PrincipalType.USER);
        when(principal.getId()).thenReturn(principalId);
        when(owner.getId()).thenReturn(ownerId);
        when(permissionRepository.findById(entity, principal)).thenReturn(null);

        Role playingRole1 = mock(Role.class);
        Role playingRole2 = mock(Role.class);
        List<Role> playingRoles = Arrays.asList(playingRole1, playingRole2);
        when(principal.getPlayingRoles()).thenReturn(playingRoles);
        when(playingRole1.getId()).thenReturn("role-id-1");
        when(permissionRepository.findById(entity, playingRole1)).thenReturn(null);
        when(playingRole2.getId()).thenReturn(ownerId);
        PermissionType type = entity.verifyPermission(principal, actionCode);
        assertEquals(PermissionType.ALLOW, type);
    }

    @Test
    public void testCheckPermission_User_PlayingRoles_NoPermissionFound() {
        String ownerId = "owner-id";
        String actionCode = "READ";
        String principalId = "principal-id";
        User principal = mock(User.class);
        when(principal.getType()).thenReturn(PrincipalType.USER);
        when(principal.getId()).thenReturn(principalId);
        when(owner.getId()).thenReturn(ownerId);
        when(permissionRepository.findById(entity, principal)).thenReturn(null);

        Role playingRole = mock(Role.class);
        List<Role> playingRoles = Arrays.asList(playingRole);
        when(principal.getPlayingRoles()).thenReturn(playingRoles);
        when(playingRole.getId()).thenReturn("role-id-1");
        when(permissionRepository.findById(entity, playingRole)).thenReturn(null);

        PermissionType type = entity.verifyPermission(principal, actionCode);
        assertEquals(PermissionType.NONE, type);
    }

    @Test
    public void testCheckPermission_User_IsOwner() {
        String actionCode = "READ";
        String principalId = "principal-id";
        User principal = mock(User.class);
        when(principal.getType()).thenReturn(PrincipalType.USER);
        when(principal.getId()).thenReturn(principalId);
        when(owner.getId()).thenReturn(principalId);
        PermissionType type = entity.verifyPermission(principal, actionCode);
        assertEquals(PermissionType.ALLOW, type);
    }

    @Test
    public void testCheckPermission_User_NoPermissionFound() {
        String actionCode = "READ";
        String principalId = "principal-id";
        User principal = mock(User.class);
        when(principal.getType()).thenReturn(PrincipalType.USER);
        when(principal.getId()).thenReturn(principalId);
        when(owner.getId()).thenReturn("owner-id");
        when(permissionRepository.findById(entity, principal)).thenReturn(null);
        PermissionType type = entity.verifyPermission(principal, actionCode);
        assertEquals(PermissionType.NONE, type);
    }

    @Test
    public void testCheckPermission_User_NoActionFound() {
        String actionCode = "READ";
        String principalId = "principal-id";
        User principal = mock(User.class);
        when(principal.getType()).thenReturn(PrincipalType.USER);
        when(principal.getId()).thenReturn(principalId);
        when(owner.getId()).thenReturn("owner-id");
        List<Action> actions = Arrays.asList(new Action("act", true));
        Permission permission = mock(Permission.class);
        when(permission.getActions()).thenReturn(actions);
        when(permissionRepository.findById(entity, principal)).thenReturn(permission);
        PermissionType type = entity.verifyPermission(principal, actionCode);
        assertEquals(PermissionType.NONE, type);
    }

    @Test
    public void testCheckPermission_User_ActionNotAllowed() {
        String actionCode = "READ";
        String principalId = "principal-id";
        User principal = mock(User.class);
        when(principal.getType()).thenReturn(PrincipalType.USER);
        when(principal.getId()).thenReturn(principalId);
        when(owner.getId()).thenReturn("owner-id");
        List<Action> actions = Arrays.asList(new Action(actionCode, false));
        Permission permission = mock(Permission.class);
        when(permission.getActions()).thenReturn(actions);
        when(permissionRepository.findById(entity, principal)).thenReturn(permission);
        PermissionType type = entity.verifyPermission(principal, actionCode);
        assertEquals(PermissionType.DENY, type);
    }

    @Test
    public void testCheckPermission_Role() {
        String actionCode = "READ";
        String principalId = "principal-id";
        Role principal = mock(Role.class);
        when(principal.getType()).thenReturn(PrincipalType.ROLE);
        when(principal.getId()).thenReturn(principalId);
        when(owner.getId()).thenReturn("owner-id");
        List<Action> actions = Arrays.asList(new Action(actionCode, true));
        Permission permission = mock(Permission.class);
        when(permission.getActions()).thenReturn(actions);
        when(permissionRepository.findById(entity, principal)).thenReturn(permission);
        PermissionType type = entity.verifyPermission(principal, actionCode);
        assertEquals(PermissionType.ALLOW, type);
    }

    @Test
    public void testCheckPermission_Role_IsOwner() {
        String actionCode = "READ";
        String principalId = "principal-id";
        Role principal = mock(Role.class);
        when(principal.getType()).thenReturn(PrincipalType.ROLE);
        when(principal.getId()).thenReturn(principalId);
        when(owner.getId()).thenReturn(principalId);
        PermissionType type = entity.verifyPermission(principal, actionCode);
        assertEquals(PermissionType.ALLOW, type);
    }

    @Test
    public void testCheckPermission_Role_NoPermissionFound() {
        String actionCode = "READ";
        String principalId = "principal-id";
        Role principal = mock(Role.class);
        when(principal.getType()).thenReturn(PrincipalType.ROLE);
        when(principal.getId()).thenReturn(principalId);
        when(owner.getId()).thenReturn("owner-id");
        when(permissionRepository.findById(entity, principal)).thenReturn(null);
        PermissionType type = entity.verifyPermission(principal, actionCode);
        assertEquals(PermissionType.NONE, type);
    }

    @Test
    public void testCheckPermission_Role_NoActionFound() {
        String actionCode = "READ";
        String principalId = "principal-id";
        Role principal = mock(Role.class);
        when(principal.getType()).thenReturn(PrincipalType.ROLE);
        when(principal.getId()).thenReturn(principalId);
        when(owner.getId()).thenReturn("owner-id");
        List<Action> actions = Arrays.asList(new Action("act", true));
        Permission permission = mock(Permission.class);
        when(permission.getActions()).thenReturn(actions);
        when(permissionRepository.findById(entity, principal)).thenReturn(permission);
        PermissionType type = entity.verifyPermission(principal, actionCode);
        assertEquals(PermissionType.NONE, type);
    }

    @Test
    public void testCheckPermission_Role_ActionNotAllowed() {
        String actionCode = "READ";
        String principalId = "principal-id";
        Role principal = mock(Role.class);
        when(principal.getType()).thenReturn(PrincipalType.ROLE);
        when(principal.getId()).thenReturn(principalId);
        when(owner.getId()).thenReturn("owner-id");
        List<Action> actions = Arrays.asList(new Action(actionCode, false));
        Permission permission = mock(Permission.class);
        when(permission.getActions()).thenReturn(actions);
        when(permissionRepository.findById(entity, principal)).thenReturn(permission);
        PermissionType type = entity.verifyPermission(principal, actionCode);
        assertEquals(PermissionType.DENY, type);
    }

    @Test
    public void testCheckPermission_APIKey() {
        String actionCode = "READ";
        String principalId = "principal-id";
        APIKey principal = mock(APIKey.class);
        when(principal.getType()).thenReturn(PrincipalType.API_KEY);
        when(principal.getId()).thenReturn(principalId);
        when(owner.getId()).thenReturn("owner-id");
        List<Action> actions = Arrays.asList(new Action(actionCode, true));
        Permission permission = mock(Permission.class);
        when(permission.getActions()).thenReturn(actions);
        when(permissionRepository.findById(entity, principal)).thenReturn(permission);
        PermissionType type = entity.verifyPermission(principal, actionCode);
        assertEquals(PermissionType.ALLOW, type);
    }

    @Test
    public void testCheckPermission_APIKey_Owner_IsOwner() {
        String ownerId = "owner-id";
        String actionCode = "READ";
        String principalId = "principal-id";
        APIKey principal = mock(APIKey.class);
        when(principal.getType()).thenReturn(PrincipalType.API_KEY);
        when(principal.getId()).thenReturn(principalId);
        when(owner.getId()).thenReturn(ownerId);
        when(permissionRepository.findById(entity, principal)).thenReturn(null);

        User apiKeyOwner = mock(User.class);
        when(apiKeyOwner.getId()).thenReturn(ownerId);
        when(principal.getOwner()).thenReturn(apiKeyOwner);
        PermissionType type = entity.verifyPermission(principal, actionCode);
        assertEquals(PermissionType.ALLOW, type);
    }

    @Test
    public void testCheckPermission_APIKey_IsOwner() {
        String actionCode = "READ";
        String principalId = "principal-id";
        APIKey principal = mock(APIKey.class);
        when(principal.getType()).thenReturn(PrincipalType.API_KEY);
        when(principal.getId()).thenReturn(principalId);
        when(owner.getId()).thenReturn(principalId);
        PermissionType type = entity.verifyPermission(principal, actionCode);
        assertEquals(PermissionType.ALLOW, type);
    }

    @Test
    public void testCheckPermission_APIKey_NoPermissionFound() {
        String actionCode = "READ";
        String principalId = "principal-id";
        APIKey principal = mock(APIKey.class);
        when(principal.getType()).thenReturn(PrincipalType.API_KEY);
        when(principal.getId()).thenReturn(principalId);
        when(owner.getId()).thenReturn("owner-id");
        when(permissionRepository.findById(entity, principal)).thenReturn(null);

        User apiKeyOwner = mock(User.class);
        when(apiKeyOwner.getId()).thenReturn("api-key-owner");
        when(principal.getOwner()).thenReturn(apiKeyOwner);
        when(permissionRepository.findById(entity, apiKeyOwner)).thenReturn(null);

        PermissionType type = entity.verifyPermission(principal, actionCode);
        assertEquals(PermissionType.NONE, type);
    }

    @Test
    public void testCheckPermission_APIKey_NoActionFound() {
        String actionCode = "READ";
        String principalId = "principal-id";
        APIKey principal = mock(APIKey.class);
        when(principal.getType()).thenReturn(PrincipalType.API_KEY);
        when(principal.getId()).thenReturn(principalId);
        when(owner.getId()).thenReturn("owner-id");
        List<Action> actions = Arrays.asList(new Action("act", true));
        Permission permission = mock(Permission.class);
        when(permission.getActions()).thenReturn(actions);
        when(permissionRepository.findById(entity, principal)).thenReturn(permission);

        User apiKeyOwner = mock(User.class);
        when(apiKeyOwner.getId()).thenReturn("api-key-owner");
        when(principal.getOwner()).thenReturn(apiKeyOwner);
        List<Action> apiKeyOwnerActions = Arrays.asList(new Action("act2", true));
        Permission apiKeyOwnerPermission = mock(Permission.class);
        when(apiKeyOwnerPermission.getActions()).thenReturn(apiKeyOwnerActions);
        when(permissionRepository.findById(entity, apiKeyOwner)).thenReturn(apiKeyOwnerPermission);

        PermissionType type = entity.verifyPermission(principal, actionCode);
        assertEquals(PermissionType.NONE, type);
    }

    @Test
    public void testCheckPermission_APIKey_ActionNotAllowed() {
        String actionCode = "READ";
        String principalId = "principal-id";
        APIKey principal = mock(APIKey.class);
        when(principal.getType()).thenReturn(PrincipalType.API_KEY);
        when(principal.getId()).thenReturn(principalId);
        when(owner.getId()).thenReturn("owner-id");
        List<Action> actions = Arrays.asList(new Action(actionCode, false));
        Permission permission = mock(Permission.class);
        when(permission.getActions()).thenReturn(actions);
        when(permissionRepository.findById(entity, principal)).thenReturn(permission);
        PermissionType type = entity.verifyPermission(principal, actionCode);
        assertEquals(PermissionType.DENY, type);
    }

}
