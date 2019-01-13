package com.codingzero.saam.core.resource;

import com.codingzero.saam.core.principal.PrincipalRepositoryService;
import com.codingzero.saam.infrastructure.database.PermissionOS;
import com.codingzero.saam.common.Action;
import com.codingzero.saam.core.Permission;
import com.codingzero.saam.core.Principal;
import com.codingzero.saam.core.Resource;
import com.codingzero.utilities.ddd.EntityObject;

import java.util.Date;
import java.util.List;

public class PermissionEntity extends EntityObject<PermissionOS> implements Permission {

    private Resource resource;
    private Principal principal;
    private PermissionFactoryService factory;
    private PrincipalRepositoryService principalRepository;

    public PermissionEntity(PermissionOS objectSegment,
                            Resource resource,
                            Principal principal,
                            PermissionFactoryService factory,
                            PrincipalRepositoryService principalRepository) {
        super(objectSegment);
        this.principal = principal;
        this.resource = resource;
        this.factory = factory;
        this.principalRepository = principalRepository;
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public Principal getPrincipal() {
        if (null == principal) {
            principal = principalRepository.findById(
                    getResource().getApplication(),
                    getObjectSegment().getPrincipalId());
        }
        return principal;
    }

    @Override
    public Date getCreationTime() {
        return getObjectSegment().getCreationTime();
    }

    @Override
    public void setActions(List<Action> actions) {
        factory.checkForIllegalActionCode(actions);
        factory.checkForDuplicateActions(actions);
        getObjectSegment().setActions(actions);
        markAsDirty();
    }

    @Override
    public List<Action> getActions() {
        return getObjectSegment().getActions();
    }

    @Override
    public boolean containAction(String actionCode) {
        for (Action action: getActions()) {
            if (action.getCode().equalsIgnoreCase(actionCode)) {
                return true;
            }
        }
        return false;
    }

}
