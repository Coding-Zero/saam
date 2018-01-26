package com.codingzero.saam.core;

import com.codingzero.saam.common.ApplicationStatus;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.common.PasswordPolicy;
import com.codingzero.utilities.pagination.PaginatedResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface Application {

    String getId();
    
    String getName();
    
    void setName(String name);

    String getDescription();

    void setDescription(String description);
    
    Date getCreatedDateTime();

    ApplicationStatus getStatus();

    void setStatus(ApplicationStatus status);

    /**Password Policy**/

    PasswordPolicy getPasswordPolicy();

    void setPasswordPolicy(PasswordPolicy policy);

    /**Identifier Policy**/

    UsernamePolicy createUsernamePolicy();

    EmailPolicy createEmailPolicy(boolean isVerificationRequired, List<String> domains);

    void updateIdentifierPolicy(IdentifierPolicy policy);

    void removeIdentifierPolicy(IdentifierPolicy policy);

    IdentifierPolicy fetchIdentifierPolicy(IdentifierType type);

    UsernamePolicy fetchUsernamePolicy();

    EmailPolicy fetchEmailPolicy();

    List<IdentifierPolicy> fetchAllIdentifierPolicies();

    /**OAuth Identifier Policy**/

    OAuthIdentifierPolicy createOAuthIdentifierPolicy(OAuthPlatform platform, Map<String, Object> configuration);

    void updateOAuthIdentifierPolicy(OAuthIdentifierPolicy policy);

    void removeOAuthIdentifierPolicy(OAuthIdentifierPolicy policy);

    OAuthIdentifierPolicy fetchOAuthIdentifierPolicy(OAuthPlatform platform);

    List<OAuthIdentifierPolicy> fetchAllOAuthIdentifierPolicies();

    /**Principal**/

    Principal fetchPrincipalById(String id);

    /**User**/

    User createUser();

    void updateUser(User user);

    void removeUser(User user);

    User fetchUserById(String id);

    User fetchUserByIdentifier(String identifier);

    User fetchUserByOAuthIdentifier(OAuthPlatform platform, String identifier);

    PaginatedResult<List<User>> fetchAllUsers();

    /**UserSession**/

    UserSession createUserSession(User user, Map<String, Object> details, long timeout);

    void removeUserSession(UserSession session);

    void removeAllUserSession(User user);

    UserSession fetchUserSessionByKey(String key);

    PaginatedResult<List<UserSession>> fetchUserSessionsByUser(User user);

    /**APIKey**/

    APIKey createAPIKey(User user, String name);

    void updateAPIKey(APIKey apiKey);

    void removeAPIKey(APIKey apiKey);

    APIKey fetchAPIKeyByKey(String key);

    List<APIKey> fetchAPIKeysByOwner(User user);

    /**Role**/

    Role addRole(String name);

    void updateRole(Role role);

    void removeRole(Role role);

    Role fetchRoleById(String id);

    PaginatedResult<List<Role>> fetchAllRoles();

    /**Resource**/

    Resource createResource(String name, Principal owner, Resource parent);

    void updateResource(Resource resource);

    void removeResource(Resource resource);

    Resource fetchResourceByKey(String key);

    PaginatedResult<List<Resource>> fetchResourcesByOwner(Principal owner, Resource parentResource);

    PaginatedResult<List<Resource>> fetchPermissionAssignedResources(Principal principal, Resource parentResource);

    PaginatedResult<List<Resource>> fetchAllResources(Resource parentResource);

    /**Action**/

//    Action createAction(String code, String name, Resource resource);
//
//    void updateAction(Action action);
//
//    void removeAction(Action action);
//
//    Action fetchAction(String code);
//
//    PaginatedResult<List<Action>> fetchActionsByResource(Resource resource);
//
//    PaginatedResult<List<Action>> fetchAllActions();

    /**Permission**/

//    Permission addPermission(Principal principal, Resource resource, PermissionType type, List<Action> actions);
//
//    void updatePermission(Permission permission);
//
//    void removePermission(Permission permission);
//
//    Permission fetchPermission(Principal principal, Resource resource);
//
//    PaginatedResult<List<Permission>> fetchPermissionsByPrincipal(Principal principal);
//
//    PaginatedResult<List<Permission>> fetchPermissionsByResource(Resource resource);
//
//    PermissionType checkPermission(Principal owner, Resource resource, Action action);


}
