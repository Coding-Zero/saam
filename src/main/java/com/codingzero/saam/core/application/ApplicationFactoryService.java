package com.codingzero.saam.core.application;

import com.codingzero.saam.common.ApplicationStatus;
import com.codingzero.saam.common.Errors;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.ApplicationFactory;
import com.codingzero.saam.infrastructure.database.ApplicationOS;
import com.codingzero.saam.infrastructure.database.spi.ApplicationAccess;
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
    private PrincipalRepositoryService principalRepository;
    private UserFactoryService userFactory;
    private UserRepositoryService userRepository;
    private APIKeyFactoryService apiKeyFactory;
    private APIKeyRepositoryService apiKeyRepository;
    private UserSessionFactoryService userSessionFactory;
    private UserSessionRepositoryService userSessionRepository;
    private RoleFactoryService roleFactory;
    private RoleRepositoryService roleRepository;
    private ResourceFactoryService resourceFactory;
    private ResourceRepositoryService resourceRepository;

    public ApplicationFactoryService(ApplicationAccess access,
                                     UsernamePolicyFactoryService usernamePolicyFactory,
                                     EmailPolicyFactoryService emailPolicyFactory,
                                     IdentifierPolicyRepositoryService identifierPolicyRepository,
                                     OAuthIdentifierPolicyFactoryService ssoIdentifierPolicyFactory,
                                     OAuthIdentifierPolicyRepositoryService ssoIdentifierPolicyRepository,
                                     PrincipalRepositoryService principalRepository,
                                     UserFactoryService userFactory,
                                     UserRepositoryService userRepository,
                                     APIKeyFactoryService apiKeyFactory,
                                     APIKeyRepositoryService apiKeyRepository,
                                     UserSessionFactoryService userSessionFactory,
                                     UserSessionRepositoryService userSessionRepository,
                                     RoleFactoryService roleFactory,
                                     RoleRepositoryService roleRepository,
                                     ResourceFactoryService resourceFactory,
                                     ResourceRepositoryService resourceRepository) {
        this.access = access;
        this.usernamePolicyFactory = usernamePolicyFactory;
        this.emailPolicyFactory = emailPolicyFactory;
        this.identifierPolicyRepository = identifierPolicyRepository;
        this.ssoIdentifierPolicyFactory = ssoIdentifierPolicyFactory;
        this.ssoIdentifierPolicyRepository = ssoIdentifierPolicyRepository;
        this.principalRepository = principalRepository;
        this.userFactory = userFactory;
        this.userRepository = userRepository;
        this.apiKeyFactory = apiKeyFactory;
        this.apiKeyRepository = apiKeyRepository;
        this.userSessionFactory = userSessionFactory;
        this.userSessionRepository = userSessionRepository;
        this.roleFactory = roleFactory;
        this.roleRepository = roleRepository;
        this.resourceFactory = resourceFactory;
        this.resourceRepository = resourceRepository;
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
                principalRepository,
                userFactory,
                userRepository,
                apiKeyFactory,
                apiKeyRepository,
                userSessionFactory,
                userSessionRepository,
                roleFactory,
                roleRepository,
                resourceFactory,
                resourceRepository
        );
    }
}
