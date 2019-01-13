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

    List<IdentifierPolicy> fetchAllIdentifierPolicies();

    /**OAuth Identifier Policy**/

    OAuthIdentifierPolicy createOAuthIdentifierPolicy(OAuthPlatform platform, Map<String, Object> configuration);

    void updateOAuthIdentifierPolicy(OAuthIdentifierPolicy policy);

    void removeOAuthIdentifierPolicy(OAuthIdentifierPolicy policy);

    OAuthIdentifierPolicy fetchOAuthIdentifierPolicy(OAuthPlatform platform);

    List<OAuthIdentifierPolicy> fetchAllOAuthIdentifierPolicies();

    /**Principal**/

//    Principal fetchPrincipalById(String id);

    /**User**/

//    User createUser();
//
//    void updateUser(User user);
//
//    void removeUser(User user);
//
//    User fetchUserById(String id);
//
//    User fetchUserByIdentifier(String identifier);
//
//    User fetchUserByOAuthIdentifier(OAuthPlatform platform, String identifier);
//
//    PaginatedResult<List<User>> fetchAllUsers();

    /**UserSession**/

//    UserSession createUserSession(User user, Map<String, Object> details, long timeout);

//    void removeUserSession(UserSession session);
//
//    void removeAllUserSession(User user);

//    UserSession fetchUserSessionByKey(String key);

//    PaginatedResult<List<UserSession>> fetchUserSessionsBy(User user)

    /**APIKey**/

//    APIKey createAPIKey(User user, String name);
//
//    void updateAPIKey(APIKey apiKey);
//
//    void verifyAPIKey(String id, String secretKey);
//
//    void removeAPIKey(APIKey apiKey);
//
//    APIKey fetchAPIKeyById(String id);
//
//    List<APIKey> fetchAPIKeysByOwner(User user);

    /**Role**/

//    Role addRole(String name);
//
//    void updateRole(Role role);
//
//    void removeRole(Role role);
//
//    Role fetchRoleById(String id);
//
//    PaginatedResult<List<Role>> fetchAllRoles();

    /**Resource**/

//    Resource createResource(String key, Principal owner);
//
//    void updateResource(Resource resource);
//
//    void removeResource(Resource resource);
//
//    Resource fetchResourceByKey(String key);
//
//    PaginatedResult<List<Resource>> fetchResourcesByOwner(Principal owner, Resource parentResource);
//
//    PaginatedResult<List<Resource>> fetchPermissionAssignedResources(Principal principal);
//
//    PaginatedResult<List<Resource>> fetchAllResources(Resource parentResource);

}
