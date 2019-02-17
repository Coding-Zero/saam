package com.codingzero.saam.app.server;


import com.codingzero.saam.app.responses.APIKeyResponse;
import com.codingzero.saam.app.responses.ApplicationResponse;
import com.codingzero.saam.app.responses.IdentifierVerificationCodeResponse;
import com.codingzero.saam.app.responses.OAuthAccessTokenResponse;
import com.codingzero.saam.app.responses.PasswordResetCodeResponse;
import com.codingzero.saam.app.responses.PermissionCheckResponse;
import com.codingzero.saam.app.responses.PermissionResponse;
import com.codingzero.saam.app.responses.ResourceResponse;
import com.codingzero.saam.app.responses.RoleResponse;
import com.codingzero.saam.app.responses.UserResponse;
import com.codingzero.saam.app.responses.UserSessionResponse;
import com.codingzero.saam.common.Action;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.PasswordResetCode;
import com.codingzero.saam.common.PermissionType;
import com.codingzero.saam.domain.APIKey;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.EmailPolicy;
import com.codingzero.saam.domain.Identifier;
import com.codingzero.saam.domain.IdentifierPolicy;
import com.codingzero.saam.domain.OAuthIdentifier;
import com.codingzero.saam.domain.OAuthIdentifierPolicy;
import com.codingzero.saam.domain.Permission;
import com.codingzero.saam.domain.Principal;
import com.codingzero.saam.domain.Resource;
import com.codingzero.saam.domain.Role;
import com.codingzero.saam.domain.User;
import com.codingzero.saam.domain.UserSession;
import com.codingzero.saam.domain.UsernamePolicy;
import com.codingzero.saam.infrastructure.OAuthAccessToken;

import java.util.ArrayList;
import java.util.List;

public class ResponseMapper {

    public ApplicationResponse toResponse(Application source) {
        if (null == source) {
            return null;
        }
        return new ApplicationResponse(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getStatus(),
                source.getPasswordPolicy(),
                toUsernamePolicy((UsernamePolicy) source.fetchIdentifierPolicy(IdentifierType.USERNAME)),
                toEmailPolicy((EmailPolicy) source.fetchIdentifierPolicy(IdentifierType.EMAIL)),
                toOAuthIdentifierPolicies(source));
    }

    private ApplicationResponse.UsernamePolicy toUsernamePolicy(UsernamePolicy source) {
        if (null == source) {
            return null;
        }
        return new ApplicationResponse.UsernamePolicy(
                source.isVerificationRequired(),
                source.getMinLength(),
                source.getMaxLength(),
                source.isActive(),
                source.getCreationTime(),
                source.getUpdateTime(),
                source.getFormat()
                );
    }

    private ApplicationResponse.EmailPolicy toEmailPolicy(EmailPolicy source) {
        if (null == source) {
            return null;
        }
        return new ApplicationResponse.EmailPolicy(
                source.isVerificationRequired(),
                source.getMinLength(),
                source.getMaxLength(),
                source.isActive(),
                source.getCreationTime(),
                source.getUpdateTime(),
                source.getDomains()
        );
    }

    private List<ApplicationResponse.OAuthIdentifierPolicy> toOAuthIdentifierPolicies(Application application) {
        List<OAuthIdentifierPolicy> policies = application.fetchAllOAuthIdentifierPolicies();
        List<ApplicationResponse.OAuthIdentifierPolicy> responses = new ArrayList<>(policies.size());
        for (OAuthIdentifierPolicy policy: policies) {
            responses.add(toOAuthIdentifierPolicy(policy));
        }
        return responses;
    }

    private ApplicationResponse.OAuthIdentifierPolicy toOAuthIdentifierPolicy(OAuthIdentifierPolicy source) {
        return new ApplicationResponse.OAuthIdentifierPolicy(
                source.getPlatform(),
                source.getConfigurations(),
                source.isActive()
        );
    }

    public UserResponse toResponse(User source) {
        if (null == source) {
            return null;
        }
        return new UserResponse(
                source.getApplication().getId(),
                source.getId(),
                source.getCreationTime(),
                toUserRoles(source),
                toUserIdentifiers(source),
                toUserSSOIdentifiers(source),
                source.isPasswordSet());
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
                                identifier.getPolicy().getType(),
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
                identifier.getPolicy().getType(),
                identifier.getContent(),
                identifier.getVerificationCode().getCode(),
                identifier.getVerificationCode().getExpirationTime());
    }

    public PasswordResetCodeResponse toResponse(User user, Identifier identifier, PasswordResetCode code) {
        return new PasswordResetCodeResponse(
                user.getApplication().getId(),
                user.getId(),
                identifier.getPolicy().getType(),
                identifier.getContent(),
                code.getCode(),
                code.getExpirationTime());
    }

    public APIKeyResponse toResponse(APIKey source) {
        if (null == source) {
            return null;
        }
        return new APIKeyResponse(
                source.getApplication().getId(),
                source.getId(),
                source.getSecretKey(),
                source.getName(),
                source.getOwner().getId(),
                source.isActive());
    }

    public UserSessionResponse toResponse(UserSession source) {
        if (null == source) {
            return null;
        }
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
        if (null == source) {
            return null;
        }
        return new RoleResponse(
                source.getApplication().getId(),
                source.getId(),
                source.getCreationTime(),
                source.getName());
    }

    public ResourceResponse toResponse(Resource source) {
        if (null == source) {
            return null;
        }
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
        if (null == source) {
            return null;
        }
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

    public OAuthAccessTokenResponse toResponse(Application application, OAuthAccessToken source) {
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
