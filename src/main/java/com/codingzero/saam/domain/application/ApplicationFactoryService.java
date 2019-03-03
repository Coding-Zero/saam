package com.codingzero.saam.domain.application;

import com.codingzero.saam.common.ApplicationStatus;
import com.codingzero.saam.common.Errors;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.ApplicationFactory;
import com.codingzero.saam.domain.principal.PrincipalRepositoryService;
import com.codingzero.saam.domain.principal.apikey.APIKeyFactoryService;
import com.codingzero.saam.domain.principal.apikey.APIKeyRepositoryService;
import com.codingzero.saam.domain.principal.role.RoleFactoryService;
import com.codingzero.saam.domain.principal.role.RoleRepositoryService;
import com.codingzero.saam.domain.principal.user.UserFactoryService;
import com.codingzero.saam.domain.principal.user.UserRepositoryService;
import com.codingzero.saam.domain.resource.ResourceFactoryService;
import com.codingzero.saam.domain.resource.ResourceRepositoryService;
import com.codingzero.saam.domain.services.ApplicationStatusVerifier;
import com.codingzero.saam.domain.usersession.UserSessionFactoryService;
import com.codingzero.saam.domain.usersession.UserSessionRepositoryService;
import com.codingzero.saam.infrastructure.data.ApplicationAccess;
import com.codingzero.saam.infrastructure.data.ApplicationOS;
import com.codingzero.utilities.error.BusinessError;

import java.util.Date;
import java.util.regex.Pattern;

public class ApplicationFactoryService implements ApplicationFactory {

    public static final int NAME_MIN_LENGTH = 2;
    public static final int NAME_MAX_LENGTH = 46;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]+[a-zA-Z0-9 _.-]+$");

    private ApplicationAccess access;
    private UsernamePolicyFactoryService usernamePolicyFactory;
    private EmailPolicyFactoryService emailPolicyFactory;
    private IdentifierPolicyRepositoryService identifierPolicyRepository;
    private OAuthIdentifierPolicyFactoryService ssoIdentifierPolicyFactory;
    private OAuthIdentifierPolicyRepositoryService ssoIdentifierPolicyRepository;
    private ApplicationStatusVerifier applicationStatusVerifier;

    public ApplicationFactoryService(ApplicationAccess access,
                                     UsernamePolicyFactoryService usernamePolicyFactory,
                                     EmailPolicyFactoryService emailPolicyFactory,
                                     IdentifierPolicyRepositoryService identifierPolicyRepository,
                                     OAuthIdentifierPolicyFactoryService ssoIdentifierPolicyFactory,
                                     OAuthIdentifierPolicyRepositoryService ssoIdentifierPolicyRepository,
                                     ApplicationStatusVerifier applicationStatusVerifier) {
        this.access = access;
        this.usernamePolicyFactory = usernamePolicyFactory;
        this.emailPolicyFactory = emailPolicyFactory;
        this.identifierPolicyRepository = identifierPolicyRepository;
        this.ssoIdentifierPolicyFactory = ssoIdentifierPolicyFactory;
        this.ssoIdentifierPolicyRepository = ssoIdentifierPolicyRepository;
        this.applicationStatusVerifier = applicationStatusVerifier;
    }

    @Override
    public Application generate(String name, String description) {
        checkForNameFormat(name);
        checkForDuplicateName(name);
        String id = access.generateId();
        ApplicationOS os = new ApplicationOS(
                id,
                name,
                description,
                new Date(),
                null,
                ApplicationStatus.ACTIVE);
        ApplicationRoot entity = reconstitute(os);
        entity.markAsNew();
        return entity;
    }

    public void checkForNameFormat(String name) {
        if (null == name) {
            throw new IllegalArgumentException("Application name is missing");
        }
        name = name.trim();
        if (name.length() < NAME_MIN_LENGTH
                || name.length() > NAME_MAX_LENGTH) {
            throw BusinessError.raise(Errors.ILLEGAL_APPLICATION_NAME_FORMAT)
                    .message("Need to be greater than "
                            + NAME_MIN_LENGTH
                            + " characters and less than "
                            + NAME_MAX_LENGTH + " characters")
                    .details("minLength", NAME_MIN_LENGTH)
                    .details("maxLength", NAME_MAX_LENGTH)
                    .build();
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw BusinessError.raise(Errors.ILLEGAL_APPLICATION_NAME_FORMAT)
                    .message("May only contain digits (0-9), alphabets (a-z), "
                            + "dash (-), dot (.), space and underscore (_).")
                    .build();
        }
    }

    public void checkForDuplicateName(String name) {
        name = name.trim();
        if (access.isDuplicateName(name)) {
            throw BusinessError.raise(Errors.DUPLICATE_APPLICATION_NAME)
                    .message("Application name \"" + name + "\" has been taken.")
                    .details("name", name)
                    .build();
        }
    }

    public ApplicationRoot reconstitute(ApplicationOS os) {
        if (null == os) {
            return null;
        }
        return new ApplicationRoot(
                os,
                this,
                identifierPolicyRepository,
                usernamePolicyFactory,
                emailPolicyFactory,
                ssoIdentifierPolicyFactory,
                ssoIdentifierPolicyRepository,
                applicationStatusVerifier);
    }
}
