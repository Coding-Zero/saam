package com.codingzero.saam.app.server;


import com.codingzero.saam.app.APIKeyResponse;
import com.codingzero.saam.app.ApplicationResponse;
import com.codingzero.saam.app.IdentifierVerificationCodeResponse;
import com.codingzero.saam.app.OAuthAccessTokenResponse;
import com.codingzero.saam.app.PasswordResetCodeResponse;
import com.codingzero.saam.app.PermissionCheckResponse;
import com.codingzero.saam.app.PermissionResponse;
import com.codingzero.saam.app.ResourceResponse;
import com.codingzero.saam.app.RoleResponse;
import com.codingzero.saam.app.UserResponse;
import com.codingzero.saam.app.UserSessionResponse;
import com.codingzero.saam.common.Action;
import com.codingzero.saam.common.PasswordResetCode;
import com.codingzero.saam.common.PermissionType;
import com.codingzero.saam.core.APIKey;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.EmailPolicy;
import com.codingzero.saam.core.Identifier;
import com.codingzero.saam.core.IdentifierPolicy;
import com.codingzero.saam.core.OAuthIdentifier;
import com.codingzero.saam.core.OAuthIdentifierPolicy;
import com.codingzero.saam.core.Permission;
import com.codingzero.saam.core.Principal;
import com.codingzero.saam.core.Resource;
import com.codingzero.saam.core.Role;
import com.codingzero.saam.core.User;
import com.codingzero.saam.core.UserSession;
import com.codingzero.saam.core.UsernamePolicy;
import com.codingzero.saam.infrastructure.SSOAccessToken;

import java.util.ArrayList;
import java.util.List;

public class ResponseMapper {

    public ApplicationResponse toResponse(Application source) {
        return new ApplicationResponse(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getStatus(), source.getPasswordPolicy(),
                toUsernamePolicies(source),
                toEmailPolicies(source));
    }

    private List<ApplicationResponse.UsernamePolicy> toUsernamePolicies(Application source) {
        List<UsernamePolicy> policies = source.fetchUsernamePolicies();
        List<ApplicationResponse.UsernamePolicy> responses = new ArrayList<>(policies.size());
        for (UsernamePolicy entity: policies) {
            responses.add(toUsernamePolicy(entity));
        }
        return responses;
    }

    private ApplicationResponse.UsernamePolicy toUsernamePolicy(UsernamePolicy source) {
        return new ApplicationResponse.UsernamePolicy(
                source.getApplication().getId(),
                source.getCode(),
                source.isVerificationRequired(),
                source.getMinLength(),
                source.getMaxLength(),
                source.isActive(),
                source.getCreationTime(),
                source.getUpdateTime(),
                source.getFormat()
                );
    }

    private List<ApplicationResponse.EmailPolicy> toEmailPolicies(Application source) {
        List<EmailPolicy> policies = source.fetchEmailPolicies();
        List<ApplicationResponse.EmailPolicy> responses = new ArrayList<>(policies.size());
        for (EmailPolicy entity: policies) {
            responses.add(toEmailPolicy(entity));
        }
        return responses;
    }

    private ApplicationResponse.EmailPolicy toEmailPolicy(EmailPolicy source) {
        return new ApplicationResponse.EmailPolicy(
                source.getApplication().getId(),
                source.getCode(),
                source.isVerificationRequired(),
                source.getMinLength(),
                source.getMaxLength(),
                source.isActive(),
                source.getCreationTime(),
                source.getUpdateTime(),
                source.getDomains()
        );
    }

    public UserResponse toResponse(User source) {
        return new UserResponse(
                source.getApplication().getId(),
                source.getId(),
                source.getCreationTime(),
                toUserRoles(source),
                toUserIdentifiers(source),
                toUserSSOIdentifiers(source)
        );
    }

    private List<UserResponse.Role> toUserRoles(User source) {
        List<Role> roles = source.getPlayingRoles();
        List<UserResponse.Role> responses = new ArrayList<>(roles.size());
        for (Role role: roles) {
            responses.add(new UserResponse.Role(role.getId(), role.getCreationTime(), role.getName()));
        }
        return responses;
    }

    private List<UserResponse.Identifier> toUserIdentifiers(User source) {
        Application application = source.getApplication();
        List<IdentifierPolicy> policies = application.fetchAllIdentifierPolicies();
        List<UserResponse.Identifier> responses = new ArrayList<>(policies.size());
        for (IdentifierPolicy policy: policies) {
            List<Identifier> identifiers = policy.fetchIdentifiersByUser(source);
            for (Identifier identifier: identifiers) {
                responses.add(
                        new UserResponse.Identifier(
                                identifier.getPolicy().getCode(),
                                identifier.getContent(),
                                identifier.isVerified(),
                                identifier.getCreationTime()
                        ));
            }

        }
        return responses;
    }

    private List<UserResponse.OAuthIdentifier> toUserSSOIdentifiers(User source) {
        Application application = source.getApplication();
        List<OAuthIdentifierPolicy> policies = application.fetchAllOAuthIdentifierPolicies();
        List<UserResponse.OAuthIdentifier> responses = new ArrayList<>(policies.size());
        for (OAuthIdentifierPolicy policy: policies) {
            List<OAuthIdentifier> identifiers = policy.fetchIdentifiersByUser(source);
            for (OAuthIdentifier identifier: identifiers) {
                responses.add(
                        new UserResponse.OAuthIdentifier(
                                identifier.getPolicy().getPlatform(),
                                identifier.getContent(),
                                identifier.getProperties(),
                                identifier.getCreationTime()));
            }

        }
        return responses;
    }

    public IdentifierVerificationCodeResponse toResponse(Identifier identifier) {
        return new IdentifierVerificationCodeResponse(
                identifier.getUser().getApplication().getId(),
                identifier.getUser().getId(),
                identifier.getPolicy().getCode(),
                identifier.getContent(),
                identifier.getVerificationCode().getCode(),
                identifier.getVerificationCode().getExpirationTime());
    }

    public PasswordResetCodeResponse toResponse(User user, Identifier identifier, PasswordResetCode code) {
        return new PasswordResetCodeResponse(
                user.getApplication().getId(),
                user.getId(),
                identifier.getPolicy().getCode(),
                identifier.getContent(),
                code.getCode(),
                code.getExpirationTime());
    }

    public APIKeyResponse toResponse(APIKey source) {
        return new APIKeyResponse(
                source.getApplication().getId(),
                source.getKey(),
                source.getName(),
                source.getOwner().getId(),
                source.isActive());
    }

    public UserSessionResponse toResponse(UserSession source) {
        return new UserSessionResponse(
                source.getApplication().getId(),
                source.getKey(),
                source.getUser().getId(),
                source.getExpirationTime(),
                source.getCreationTime(),
                source.isExpired(),
                source.getDetails());
    }

    public RoleResponse toResponse(Role source) {
        return new RoleResponse(
                source.getApplication().getId(),
                source.getId(),
                source.getCreationTime(),
                source.getName());
    }

    public ResourceResponse toResponse(Resource source) {
        String parentKey = null;
        if (null != source.getParent()) {
            parentKey = source.getParent().getKey();
        }
        return new ResourceResponse(
                source.getApplication().getId(),
                parentKey,
                source.getKey(),
                source.getOwner().getId(),
                source.getCreationTime(),
                source.isRoot()
                );
    }

    public PermissionResponse toResponse(Permission source) {
        List<Action> actions = source.getActions();
        return new PermissionResponse(
                source.getResource().getApplication().getId(),
                source.getResource().getKey(),
                source.getPrincipal().getId(),
                source.getCreationTime(),
                actions);
    }

    public PermissionCheckResponse toResponse(Principal principal, Resource resource,
                                              String actionCode, PermissionType result) {
        return new PermissionCheckResponse(
                principal.getApplication().getId(),
                principal.getId(),
                resource.getKey(),
                actionCode,
                result);
    }

    public OAuthAccessTokenResponse toResponse(Application application, SSOAccessToken source) {
        return new OAuthAccessTokenResponse(
                application.getId(),
                source.getPlatform(),
                source.getAccountId(),
                source.getToken(),
                source.getCreationTime(),
                source.getExpirationTime()
        );
    }

}
