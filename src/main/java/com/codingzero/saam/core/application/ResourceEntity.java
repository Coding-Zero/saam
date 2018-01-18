package com.codingzero.saam.core.application;

import com.codingzero.saam.common.Action;
import com.codingzero.saam.common.PermissionType;
import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.core.APIKey;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.Permission;
import com.codingzero.saam.core.Principal;
import com.codingzero.saam.core.Resource;
import com.codingzero.saam.core.Role;
import com.codingzero.saam.core.User;
import com.codingzero.saam.infrastructure.database.ResourceOS;
import com.codingzero.utilities.ddd.EntityObject;
import com.codingzero.utilities.pagination.PaginatedResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceEntity extends EntityObject<ResourceOS> implements Resource {

    private Application application;
    private Resource parent;
    private Principal owner;
    private ResourceFactoryService factory;
    private PermissionFactoryService permissionFactory;
    private PermissionRepositoryService permissionRepository;
    private Map<String, PermissionEntity> dirtyPermissions;

    public ResourceEntity(ResourceOS objectSegment,
                          Application application,
                          Resource parent,
                          Principal owner,
                          ResourceFactoryService factory,
                          PermissionFactoryService permissionFactory,
                          PermissionRepositoryService permissionRepository) {
        super(objectSegment);
        this.application = application;
        this.parent = parent;
        this.owner = owner;
        this.factory = factory;
        this.permissionFactory = permissionFactory;
        this.permissionRepository = permissionRepository;
        this.dirtyPermissions = new HashMap<>();
    }

    public List<PermissionEntity> getDirtyPermissions() {
        return Collections.unmodifiableList(new ArrayList<>(dirtyPermissions.values()));
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public Resource getParent() {
        if (null == parent) {
            parent = getApplication().fetchResourceByKey(getObjectSegment().getParentKey());
        }
        return parent;
    }

    @Override
    public String getKey() {
        return getObjectSegment().getKey();
    }

    @Override
    public Principal getOwner() {
        if (null == owner) {
            owner = getApplication().fetchPrincipalById(getObjectSegment().getPrincipalId());
        }
        return owner;
    }

    @Override
    public void setOwner(Principal principal) {
        if (getObjectSegment().getPrincipalId().equalsIgnoreCase(principal.getId())) {
            return;
        }
        principal = factory.getOwner(principal);
        getObjectSegment().setPrincipalId(principal.getId());
        owner = null;
        markAsDirty();
    }

    @Override
    public Date getCreationTime() {
        return getObjectSegment().getCreationTime();
    }

    @Override
    public boolean isRoot() {
        return (null == getObjectSegment().getParentKey());
    }

    @Override
    public Permission addPermission(Principal principal, List<Action> actions) {
        checkForAPIKeyPermission(principal, actions);
        PermissionEntity entity = permissionFactory.generate(this, principal, actions);
        dirtyPermissions.put(entity.getResource().getKey(), entity);
        return entity;
    }

    private void checkForAPIKeyPermission(Principal principal, List<Action> actions) {
        if (principal.getType() != PrincipalType.API_KEY) {
            return;
        }
        APIKey apiKey = (APIKey) principal;
        User owner = apiKey.getOwner();
        Permission permission = fetchPermissionById(owner);
        if (null == permission) {
            throw new IllegalArgumentException(
                    "Owner " + owner.getId()
                            + " of APIKey, " + apiKey.getKey()
                            + " doesn't have permission granted for resource, " + getKey());
        }
        for (Action action: actions) {
            if (!permission.containAction(action.getCode())) {
                throw new IllegalArgumentException(
                        "Owner " + owner.getId()
                                + " of APIKey, " + apiKey.getKey()
                                + " doesn't have action " + action.getCode()
                                + " granted for resource, " + getKey());
            }
        }
    }

    @Override
    public void updatePermission(Permission permission) {
        PermissionEntity entity = (PermissionEntity) permission;
        dirtyPermissions.put(entity.getResource().getKey(), entity);
    }

    @Override
    public void removePermission(Permission permission) {
        PermissionEntity entity = (PermissionEntity) permission;
        entity.markAsVoid();
        dirtyPermissions.put(entity.getResource().getKey(), entity);
    }

    @Override
    public Permission fetchPermissionById(Principal principal) {
        return permissionRepository.findById(this, principal);
    }

    @Override
    public PaginatedResult<List<Permission>> fetchAllPermissions() {
        return permissionRepository.findByResource(this);
    }

    @Override
    public PermissionType checkPermission(Principal principal, String actionCode) {
        if (principal.getType() == PrincipalType.API_KEY) {
            return checkPermissionForAPIKey((APIKey) principal, actionCode);
        } else if (principal.getType() == PrincipalType.USER) {
            return checkPermissionForUser((User) principal, actionCode);
        } else if (principal.getType() == PrincipalType.USER) {
            return checkPermissionForRole((Role) principal, actionCode);
        }
        throw new IllegalArgumentException("Unsupported principal type, " + principal.getType());
    }

    private PermissionType checkPermissionForAPIKey(APIKey apiKey, String actionCode) {
        PermissionType result = checkPermissionForPrincipal(apiKey, actionCode);
        if (PermissionType.NONE != result) {
            return result;
        }
        return checkPermissionForUser(apiKey.getOwner(), actionCode);
    }

    private PermissionType checkPermissionForUser(User user, String actionCode) {
        PermissionType result = checkPermissionForPrincipal(user, actionCode);
        if (PermissionType.NONE != result) {
            return result;
        }
        return checkPermissionForPlayingRole(user, actionCode);
    }

    private PermissionType checkPermissionForPrincipal(Principal principal, String actionCode) {

        //check ownership
        if (isOwner(principal)) {
            return PermissionType.ALLOW;
        }

        //check permission
        Permission permission = fetchPermissionById(principal);
        if (null == permission) {
            return PermissionType.NONE;
        }
        Action action = getAction(permission, actionCode);
        if (null == action) {
            return PermissionType.NONE;
        }
        if (action.isAllowed()) {
            return PermissionType.ALLOW;
        }
        return PermissionType.DENY;
    }

    private PermissionType checkPermissionForPlayingRole(User user, String actionCode) {
        List<Role> roles = user.getPlayingRoles();
        for (Role role : roles) {
            PermissionType result = checkPermissionForRole(role, actionCode);
            if (PermissionType.NONE != result) {
                return result;
            }
        }
        return PermissionType.NONE;
    }

    private PermissionType checkPermissionForRole(Role role, String actionCode) {
        return checkPermissionForPrincipal(role, actionCode);
    }

    private Action getAction(Permission permission, String actionCode) {
        List<Action> actions = permission.getActions();
        for (Action action: actions) {
            if (action.getCode().equalsIgnoreCase(actionCode)) {
                return action;
            }
        }
        return null;
    }

    private boolean isOwner(Principal principal) {
        return getOwner().getId().equalsIgnoreCase(principal.getId());
    }

}
