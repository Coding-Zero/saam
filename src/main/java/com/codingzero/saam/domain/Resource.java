package com.codingzero.saam.domain;

import com.codingzero.saam.common.Action;
import com.codingzero.saam.common.PermissionType;
import com.codingzero.utilities.pagination.PaginatedResult;

import java.util.Date;
import java.util.List;

public interface Resource {

    Application getApplication();

    Resource getParent();

    String getKey();

    Principal getOwner();

    void setOwner(Principal principal);

    Date getCreationTime();

    boolean isRoot();

    /**Permission**/

    Permission assignPermission(Principal principal, List<Action> actions);

    void changePermission(Permission permission);

    void removePermission(Permission permission);

    Permission fetchPermissionById(Principal principal);

    PaginatedResult<List<Permission>> fetchAllPermissions();

    PermissionType verifyPermission(Principal principal, String actionCode);

}
