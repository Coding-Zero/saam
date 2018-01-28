package com.codingzero.saam.app.server;

import com.codingzero.saam.app.SAAM;
import com.codingzero.saam.app.server.base.IdentifierVerificationCodeGeneratorImpl;
import com.codingzero.saam.app.server.base.password.PasswordHelperImpl;
import com.codingzero.saam.core.ApplicationRepository;
import com.codingzero.saam.core.application.APIKeyFactoryService;
import com.codingzero.saam.core.application.APIKeyRepositoryService;
import com.codingzero.saam.core.application.ApplicationFactoryService;
import com.codingzero.saam.core.application.ApplicationRepositoryService;
import com.codingzero.saam.core.application.EmailPolicyFactoryService;
import com.codingzero.saam.core.application.EmailPolicyRepositoryService;
import com.codingzero.saam.core.application.IdentifierFactoryService;
import com.codingzero.saam.core.application.IdentifierPolicyRepositoryService;
import com.codingzero.saam.core.application.IdentifierRepositoryService;
import com.codingzero.saam.core.application.OAuthIdentifierFactoryService;
import com.codingzero.saam.core.application.OAuthIdentifierPolicyFactoryService;
import com.codingzero.saam.core.application.OAuthIdentifierPolicyRepositoryService;
import com.codingzero.saam.core.application.OAuthIdentifierRepositoryService;
import com.codingzero.saam.core.application.PermissionFactoryService;
import com.codingzero.saam.core.application.PermissionRepositoryService;
import com.codingzero.saam.core.application.PrincipalRepositoryService;
import com.codingzero.saam.core.application.ResourceFactoryService;
import com.codingzero.saam.core.application.ResourceRepositoryService;
import com.codingzero.saam.core.application.RoleFactoryService;
import com.codingzero.saam.core.application.RoleRepositoryService;
import com.codingzero.saam.core.application.UserFactoryService;
import com.codingzero.saam.core.application.UserRepositoryService;
import com.codingzero.saam.core.application.UserSessionFactoryService;
import com.codingzero.saam.core.application.UserSessionRepositoryService;
import com.codingzero.saam.core.application.UsernamePolicyFactoryService;
import com.codingzero.saam.core.application.UsernamePolicyRepositoryService;
import com.codingzero.saam.core.services.UserAuthenticator;
import com.codingzero.saam.infrastructure.database.spi.APIKeyAccess;
import com.codingzero.saam.infrastructure.database.spi.ApplicationAccess;
import com.codingzero.saam.infrastructure.database.spi.EmailPolicyAccess;
import com.codingzero.saam.infrastructure.database.spi.IdentifierAccess;
import com.codingzero.saam.infrastructure.database.spi.IdentifierPolicyAccess;
import com.codingzero.saam.infrastructure.database.spi.IdentifierVerificationCodeGenerator;
import com.codingzero.saam.infrastructure.database.spi.OAuthIdentifierAccess;
import com.codingzero.saam.infrastructure.database.spi.OAuthIdentifierPolicyAccess;
import com.codingzero.saam.infrastructure.database.spi.OAuthPlatformAgent;
import com.codingzero.saam.infrastructure.database.spi.PasswordHelper;
import com.codingzero.saam.infrastructure.database.spi.PermissionAccess;
import com.codingzero.saam.infrastructure.database.spi.PrincipalAccess;
import com.codingzero.saam.infrastructure.database.spi.ResourceAccess;
import com.codingzero.saam.infrastructure.database.spi.RoleAccess;
import com.codingzero.saam.infrastructure.database.spi.UserAccess;
import com.codingzero.saam.infrastructure.database.spi.UserSessionAccess;
import com.codingzero.saam.infrastructure.database.spi.UsernamePolicyAccess;
import com.codingzero.utilities.transaction.TransactionManager;

public class SAAMBuilder {

    private TransactionManager transactionManager;
    private IdentifierPolicyAccess identifierPolicyAccess;
    private UsernamePolicyAccess usernamePolicyAccess;
    private EmailPolicyAccess emailPolicyAccess;
    private OAuthIdentifierPolicyAccess oAuthIdentifierPolicyAccess;
    private PrincipalAccess principalAccess;
    private UserAccess userAccess;
    private IdentifierAccess identifierAccess;
    private OAuthIdentifierAccess oAuthIdentifierAccess;
    private PermissionAccess permissionAccess;
    private ResourceAccess resourceAccess;
    private RoleAccess roleAccess;
    private APIKeyAccess apiKeyAccess;
    private ApplicationAccess applicationAccess;
    private UserSessionAccess userSessionAccess;
    private RoleFactoryService roleFactory;
    private RoleRepositoryService roleRepository;
    private UserFactoryService userFactory;
    private UserRepositoryService userRepository;
    private UserSessionFactoryService userSessionFactory;
    private UserSessionRepositoryService userSessionRepository;
    private APIKeyFactoryService apiKeyFactory;
    private APIKeyRepositoryService apiKeyRepository;
    private PrincipalRepositoryService principalRepository;
    private IdentifierFactoryService identifierFactory;
    private IdentifierRepositoryService identifierRepository;
    private UsernamePolicyFactoryService usernamePolicyFactory;
    private UsernamePolicyRepositoryService usernamePolicyRepository;
    private EmailPolicyFactoryService emailPolicyFactory;
    private EmailPolicyRepositoryService emailPolicyRepository;
    private IdentifierPolicyRepositoryService identifierPolicyRepository;
    private OAuthIdentifierFactoryService oAuthIdentifierFactory;
    private OAuthIdentifierRepositoryService oAuthIdentifierRepository;
    private OAuthIdentifierPolicyFactoryService oAuthIdentifierPolicyFactory;
    private OAuthIdentifierPolicyRepositoryService oAuthIdentifierPolicyRepository;
    private PermissionFactoryService permissionFactory;
    private PermissionRepositoryService permissionRepository;
    private ResourceFactoryService resourceFactory;
    private ResourceRepositoryService resourceRepository;
    private ApplicationFactoryService applicationFactory;
    private ApplicationRepository applicationRepository;
    private PasswordHelper passwordHelper;
    private IdentifierVerificationCodeGenerator identifierVerificationCodeGenerator;
    private OAuthPlatformAgent oAuthPlatformAgent;
    private ResponseMapper responseMapper;

    public UserAuthenticator getUserAuthenticator() {
        if (null == userAuthenticator) {
            userAuthenticator = new UserAuthenticator();
        }
        return userAuthenticator;
    }

    public SAAMBuilder setUserAuthenticator(UserAuthenticator userAuthenticator) {
        this.userAuthenticator = userAuthenticator;
        return this;
    }

    private UserAuthenticator userAuthenticator;

    private void checkForMissedValue(Object value, Class<?> clazz) {
        if (null == value) {
            throw new RuntimeException(clazz.getCanonicalName()
                    + " is missing, please assign with corresponding setter.");
        }
    }

    public TransactionManager getTransactionManager() {
        checkForMissedValue(transactionManager, TransactionManager.class);
        return transactionManager;
    }

    public SAAMBuilder setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        return this;
    }

    public IdentifierPolicyAccess getIdentifierPolicyAccess() {
        checkForMissedValue(identifierPolicyAccess, IdentifierPolicyAccess.class);
        return identifierPolicyAccess;
    }

    public SAAMBuilder setIdentifierPolicyAccess(IdentifierPolicyAccess identifierPolicyAccess) {
        this.identifierPolicyAccess = identifierPolicyAccess;
        getTransactionManager().register("IdentifierPolicyAccess", identifierPolicyAccess);
        return this;
    }

    public UsernamePolicyAccess getUsernamePolicyAccess() {
        checkForMissedValue(usernamePolicyAccess, UsernamePolicyAccess.class);
        return usernamePolicyAccess;
    }

    public SAAMBuilder setUsernamePolicyAccess(UsernamePolicyAccess usernamePolicyAccess) {
        this.usernamePolicyAccess = usernamePolicyAccess;
        getTransactionManager().register("UsernamePolicyAccess", usernamePolicyAccess);
        return this;
    }

    public EmailPolicyAccess getEmailPolicyAccess() {
        checkForMissedValue(emailPolicyAccess, EmailPolicyAccess.class);
        return emailPolicyAccess;
    }

    public SAAMBuilder setEmailPolicyAccess(EmailPolicyAccess emailPolicyAccess) {
        this.emailPolicyAccess = emailPolicyAccess;
        getTransactionManager().register("EmailPolicyAccess", emailPolicyAccess);
        return this;
    }

    public OAuthIdentifierPolicyAccess getOAuthIdentifierPolicyAccess() {
        checkForMissedValue(oAuthIdentifierPolicyAccess, OAuthIdentifierPolicyAccess.class);
        return oAuthIdentifierPolicyAccess;
    }

    public SAAMBuilder setOAuthIdentifierPolicyAccess(OAuthIdentifierPolicyAccess oAuthIdentifierPolicyAccess) {
        this.oAuthIdentifierPolicyAccess = oAuthIdentifierPolicyAccess;
        getTransactionManager().register("OAuthIdentifierPolicyAccess", oAuthIdentifierPolicyAccess);
        return this;
    }

    public PrincipalAccess getPrincipalAccess() {
        checkForMissedValue(principalAccess, PrincipalAccess.class);
        return principalAccess;
    }

    public SAAMBuilder setPrincipalAccess(PrincipalAccess principalAccess) {
        this.principalAccess = principalAccess;
        getTransactionManager().register("PrincipalAccess", principalAccess);
        return this;
    }

    public UserAccess getUserAccess() {
        checkForMissedValue(userAccess, UserAccess.class);
        return userAccess;
    }

    public SAAMBuilder setUserAccess(UserAccess userAccess) {
        this.userAccess = userAccess;
        getTransactionManager().register("UserAccess", userAccess);
        return this;
    }

    public IdentifierAccess getIdentifierAccess() {
        checkForMissedValue(identifierAccess, IdentifierAccess.class);
        return identifierAccess;
    }

    public SAAMBuilder setIdentifierAccess(IdentifierAccess identifierAccess) {
        this.identifierAccess = identifierAccess;
        getTransactionManager().register("IdentifierAccess", identifierAccess);
        return this;
    }

    public OAuthIdentifierAccess getOAuthIdentifierAccess() {
        checkForMissedValue(oAuthIdentifierAccess, OAuthIdentifierAccess.class);
        return oAuthIdentifierAccess;
    }

    public SAAMBuilder setOAuthIdentifierAccess(OAuthIdentifierAccess oAuthIdentifierAccess) {
        this.oAuthIdentifierAccess = oAuthIdentifierAccess;
        getTransactionManager().register("OAuthIdentifierAccess", oAuthIdentifierAccess);
        return this;
    }

    public PermissionAccess getPermissionAccess() {
        checkForMissedValue(permissionAccess, PermissionAccess.class);
        return permissionAccess;
    }

    public SAAMBuilder setPermissionAccess(PermissionAccess permissionAccess) {
        this.permissionAccess = permissionAccess;
        getTransactionManager().register("PermissionAccess", permissionAccess);
        return this;
    }

    public ResourceAccess getResourceAccess() {
        checkForMissedValue(resourceAccess, ResourceAccess.class);
        return resourceAccess;
    }

    public SAAMBuilder setResourceAccess(ResourceAccess resourceAccess) {
        this.resourceAccess = resourceAccess;
        getTransactionManager().register("ResourceAccess", resourceAccess);
        return this;
    }

    public RoleAccess getRoleAccess() {
        checkForMissedValue(roleAccess, RoleAccess.class);
        return roleAccess;
    }

    public SAAMBuilder setRoleAccess(RoleAccess roleAccess) {
        this.roleAccess = roleAccess;
        getTransactionManager().register("RoleAccess", roleAccess);
        return this;
    }

    public APIKeyAccess getApiKeyAccess() {
        checkForMissedValue(apiKeyAccess, APIKeyAccess.class);
        return apiKeyAccess;
    }

    public SAAMBuilder setApiKeyAccess(APIKeyAccess apiKeyAccess) {
        this.apiKeyAccess = apiKeyAccess;
        getTransactionManager().register("APIKeyAccess", apiKeyAccess);
        return this;
    }

    public ApplicationAccess getApplicationAccess() {
        checkForMissedValue(applicationAccess, ApplicationAccess.class);
        return applicationAccess;
    }

    public SAAMBuilder setApplicationAccess(ApplicationAccess applicationAccess) {
        this.applicationAccess = applicationAccess;
        getTransactionManager().register("ApplicationAccess", applicationAccess);
        return this;
    }

    public UserSessionAccess getUserSessionAccess() {
        checkForMissedValue(userSessionAccess, UserSessionAccess.class);
        return userSessionAccess;
    }

    public SAAMBuilder setUserSessionAccess(UserSessionAccess userSessionAccess) {
        this.userSessionAccess = userSessionAccess;
        getTransactionManager().register("UserSessionAccess", userSessionAccess);
        return this;
    }

    public RoleFactoryService getRoleFactory() {
        if (null == roleFactory) {
            roleFactory = new RoleFactoryService(getRoleAccess(), getPrincipalAccess());
        }
        return roleFactory;
    }

    public SAAMBuilder setRoleFactory(RoleFactoryService roleFactory) {
        this.roleFactory = roleFactory;
        return this;
    }

    public RoleRepositoryService getRoleRepository() {
        if (null == roleRepository) {
            roleRepository = new RoleRepositoryService(
                    getRoleAccess(), getPrincipalAccess(), getRoleFactory());
        }
        return roleRepository;
    }

    public SAAMBuilder setRoleRepository(RoleRepositoryService roleRepository) {
        this.roleRepository = roleRepository;
        return this;
    }

    public UserFactoryService getUserFactory() {
        if (null == userFactory) {
            userFactory = new UserFactoryService(
                    principalAccess,
                    roleRepository,
                    getPasswordHelper());
        }
        return userFactory;
    }

    public SAAMBuilder setUserFactory(UserFactoryService userFactory) {
        this.userFactory = userFactory;
        return this;
    }

    public UserRepositoryService getUserRepository() {
        if (null == userRepository) {
            userRepository = new UserRepositoryService(
                    getUserAccess(),
                    getPrincipalAccess(),
                    getUserFactory()
            );
        }
        return userRepository;
    }

    public SAAMBuilder setUserRepository(UserRepositoryService userRepository) {
        this.userRepository = userRepository;
        return this;
    }

    public UserSessionFactoryService getUserSessionFactory() {
        if (null == userSessionFactory) {
            userSessionFactory =
                    new UserSessionFactoryService(getUserSessionAccess(), getUserRepository());
        }
        return userSessionFactory;
    }

    public SAAMBuilder setUserSessionFactory(UserSessionFactoryService userSessionFactory) {
        this.userSessionFactory = userSessionFactory;
        return this;
    }

    public UserSessionRepositoryService getUserSessionRepository() {
        if (null == userSessionRepository) {
            userSessionRepository =
                    new UserSessionRepositoryService(getUserSessionAccess(), getUserSessionFactory());
        }
        return userSessionRepository;
    }

    public SAAMBuilder setUserSessionRepository(UserSessionRepositoryService userSessionRepository) {
        this.userSessionRepository = userSessionRepository;
        return this;
    }

    public APIKeyFactoryService getApiKeyFactory() {
        if (null == apiKeyFactory) {
            apiKeyFactory = new APIKeyFactoryService(
                    getApiKeyAccess(), getPrincipalAccess(), getUserRepository());
        }
        return apiKeyFactory;
    }

    public SAAMBuilder setApiKeyFactory(APIKeyFactoryService apiKeyFactory) {
        this.apiKeyFactory = apiKeyFactory;
        return this;
    }

    public APIKeyRepositoryService getApiKeyRepository() {
        if (null == apiKeyRepository) {
            apiKeyRepository = new APIKeyRepositoryService(
                    getApiKeyAccess(), getPrincipalAccess(), getApiKeyFactory());
        }
        return apiKeyRepository;
    }

    public SAAMBuilder setApiKeyRepository(APIKeyRepositoryService apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
        return this;
    }

    public PrincipalRepositoryService getPrincipalRepository() {
        if (null == principalRepository) {
            principalRepository = new PrincipalRepositoryService(
                    getPrincipalAccess(),
                    getUserRepository(),
                    getRoleRepository(),
                    getApiKeyRepository());
        }
        return principalRepository;
    }

    public SAAMBuilder setPrincipalRepository(PrincipalRepositoryService principalRepository) {
        this.principalRepository = principalRepository;
        return this;
    }

    public IdentifierFactoryService getIdentifierFactory() {
        if (null == identifierFactory) {
            identifierFactory = new IdentifierFactoryService(
                    getIdentifierAccess(), getIdentifierVerificationCodeGenerator(), getUserRepository());
        }
        return identifierFactory;
    }

    public SAAMBuilder setIdentifierFactory(IdentifierFactoryService identifierFactory) {
        this.identifierFactory = identifierFactory;
        return this;
    }

    public IdentifierRepositoryService getIdentifierRepository() {
        if (null == identifierRepository) {
            identifierRepository = new IdentifierRepositoryService(
                    getIdentifierAccess(), getIdentifierFactory());
        }
        return identifierRepository;
    }

    public SAAMBuilder setIdentifierRepository(IdentifierRepositoryService identifierRepository) {
        this.identifierRepository = identifierRepository;
        return this;
    }

    public UsernamePolicyFactoryService getUsernamePolicyFactory() {
        if (null == usernamePolicyFactory) {
            usernamePolicyFactory = new UsernamePolicyFactoryService(
                    getIdentifierPolicyAccess(), getIdentifierFactory(), getIdentifierRepository());
        }
        return usernamePolicyFactory;
    }

    public SAAMBuilder setUsernamePolicyFactory(UsernamePolicyFactoryService usernamePolicyFactory) {
        this.usernamePolicyFactory = usernamePolicyFactory;
        return this;
    }

    public UsernamePolicyRepositoryService getUsernamePolicyRepository() {
        if (null == usernamePolicyRepository) {
            usernamePolicyRepository = new UsernamePolicyRepositoryService(
                    getUsernamePolicyAccess(), getUsernamePolicyFactory());
        }
        return usernamePolicyRepository;
    }

    public SAAMBuilder setUsernamePolicyRepository(UsernamePolicyRepositoryService usernamePolicyRepository) {
        this.usernamePolicyRepository = usernamePolicyRepository;
        return this;
    }

    public EmailPolicyFactoryService getEmailPolicyFactory() {
        if (null == emailPolicyFactory) {
            emailPolicyFactory = new EmailPolicyFactoryService(
                    getIdentifierPolicyAccess(),
                    getIdentifierFactory(),
                    getIdentifierRepository());
        }
        return emailPolicyFactory;
    }

    public SAAMBuilder setEmailPolicyFactory(EmailPolicyFactoryService emailPolicyFactory) {
        this.emailPolicyFactory = emailPolicyFactory;
        return this;
    }

    public EmailPolicyRepositoryService getEmailPolicyRepository() {
        if (null == emailPolicyRepository) {
            emailPolicyRepository = new EmailPolicyRepositoryService(
                    getEmailPolicyAccess(),
                    getEmailPolicyFactory());
        }
        return emailPolicyRepository;
    }

    public SAAMBuilder setEmailPolicyRepository(EmailPolicyRepositoryService emailPolicyRepository) {
        this.emailPolicyRepository = emailPolicyRepository;
        return this;
    }

    public IdentifierPolicyRepositoryService getIdentifierPolicyRepository() {
        if (null == identifierPolicyRepository) {
            identifierPolicyRepository = new IdentifierPolicyRepositoryService(
                    getIdentifierPolicyAccess(),
                    getEmailPolicyRepository(),
                    getUsernamePolicyRepository(),
                    getIdentifierRepository());
        }
        return identifierPolicyRepository;
    }

    public SAAMBuilder setIdentifierPolicyRepository(IdentifierPolicyRepositoryService identifierPolicyRepository) {
        this.identifierPolicyRepository = identifierPolicyRepository;
        return this;
    }

    public OAuthIdentifierFactoryService getoAuthIdentifierFactory() {
        if (null == oAuthIdentifierFactory) {
            oAuthIdentifierFactory = new OAuthIdentifierFactoryService(
                    getOAuthIdentifierAccess(),
                    getUserRepository());
        }
        return oAuthIdentifierFactory;
    }

    public SAAMBuilder setoAuthIdentifierFactory(OAuthIdentifierFactoryService oAuthIdentifierFactory) {
        this.oAuthIdentifierFactory = oAuthIdentifierFactory;
        return this;
    }

    public OAuthIdentifierRepositoryService getoAuthIdentifierRepository() {
        if (null == oAuthIdentifierRepository) {
            oAuthIdentifierRepository = new OAuthIdentifierRepositoryService(
                    getOAuthIdentifierAccess(),
                    getoAuthIdentifierFactory());
        }
        return oAuthIdentifierRepository;
    }

    public SAAMBuilder setoAuthIdentifierRepository(OAuthIdentifierRepositoryService oAuthIdentifierRepository) {
        this.oAuthIdentifierRepository = oAuthIdentifierRepository;
        return this;
    }

    public OAuthIdentifierPolicyFactoryService getOAuthIdentifierPolicyFactory() {
        if (null == oAuthIdentifierPolicyFactory) {
            oAuthIdentifierPolicyFactory = new OAuthIdentifierPolicyFactoryService(
                    getOAuthIdentifierPolicyAccess(),
                    getoAuthIdentifierFactory(),
                    getoAuthIdentifierRepository());
        }
        return oAuthIdentifierPolicyFactory;
    }

    public SAAMBuilder setOAuthIdentifierPolicyFactory(OAuthIdentifierPolicyFactoryService oAuthIdentifierPolicyFactory) {
        this.oAuthIdentifierPolicyFactory = oAuthIdentifierPolicyFactory;
        return this;
    }

    public OAuthIdentifierPolicyRepositoryService getOAuthIdentifierPolicyRepository() {
        if (null == oAuthIdentifierPolicyRepository) {
            oAuthIdentifierPolicyRepository = new OAuthIdentifierPolicyRepositoryService(
                    getOAuthIdentifierPolicyAccess(),
                    getOAuthIdentifierPolicyFactory(),
                    getoAuthIdentifierRepository());
        }
        return oAuthIdentifierPolicyRepository;
    }

    public SAAMBuilder setOAuthIdentifierPolicyRepository(OAuthIdentifierPolicyRepositoryService oAuthIdentifierPolicyRepository) {
        this.oAuthIdentifierPolicyRepository = oAuthIdentifierPolicyRepository;
        return this;
    }

    public PermissionFactoryService getPermissionFactory() {
        if (null == permissionFactory) {
            permissionFactory = new PermissionFactoryService(
                    getPermissionAccess(),
                    getPrincipalRepository()
            );
        }
        return permissionFactory;
    }

    public SAAMBuilder setPermissionFactory(PermissionFactoryService permissionFactory) {
        this.permissionFactory = permissionFactory;
        return this;
    }

    public PermissionRepositoryService getPermissionRepository() {
        if (null == permissionRepository) {
            permissionRepository = new PermissionRepositoryService(
                    getPermissionAccess(),
                    getPermissionFactory());
        }
        return permissionRepository;
    }

    public SAAMBuilder setPermissionRepository(PermissionRepositoryService permissionRepository) {
        this.permissionRepository = permissionRepository;
        return this;
    }

    public ResourceFactoryService getResourceFactory() {
        if (null == resourceFactory) {
            resourceFactory = new ResourceFactoryService(
                    getResourceAccess(),
                    getPermissionFactory(),
                    getPermissionRepository());
        }
        return resourceFactory;
    }

    public SAAMBuilder setResourceFactory(ResourceFactoryService resourceFactory) {
        this.resourceFactory = resourceFactory;
        return this;
    }

    public ResourceRepositoryService getResourceRepository() {
        if (null == resourceRepository) {
            resourceRepository = new ResourceRepositoryService(
                    getResourceAccess(),
                    getResourceFactory(),
                    getPermissionRepository());
        }
        return resourceRepository;
    }

    public SAAMBuilder setResourceRepository(ResourceRepositoryService resourceRepository) {
        this.resourceRepository = resourceRepository;
        return this;
    }

    public ApplicationFactoryService getApplicationFactory() {
        if (null == applicationFactory) {
            applicationFactory = new ApplicationFactoryService(
                    getApplicationAccess(),
                    getUsernamePolicyFactory(),
                    getEmailPolicyFactory(),
                    getIdentifierPolicyRepository(),
                    getOAuthIdentifierPolicyFactory(),
                    getOAuthIdentifierPolicyRepository(),
                    getPrincipalRepository(),
                    getUserFactory(),
                    getUserRepository(),
                    getApiKeyFactory(),
                    getApiKeyRepository(),
                    getUserSessionFactory(),
                    getUserSessionRepository(),
                    getRoleFactory(),
                    getRoleRepository(),
                    getResourceFactory(),
                    getResourceRepository()
            );
        }
        return applicationFactory;
    }

    public SAAMBuilder setApplicationFactory(ApplicationFactoryService applicationFactory) {
        this.applicationFactory = applicationFactory;
        return this;
    }

    public ApplicationRepository getApplicationRepository() {
        if (null == applicationRepository) {
            applicationRepository = new ApplicationRepositoryService(
                    getApplicationAccess(),
                    getApplicationFactory(),
                    getIdentifierPolicyRepository(),
                    getOAuthIdentifierPolicyRepository(),
                    getPrincipalRepository(),
                    getResourceRepository(),
                    getPermissionRepository(),
                    getUserSessionRepository());
        }
        return applicationRepository;
    }

    public SAAMBuilder setApplicationRepository(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
        return this;
    }

    public PasswordHelper getPasswordHelper() {
        if (null == passwordHelper) {
            passwordHelper = new PasswordHelperImpl();
        }
        return passwordHelper;
    }

    public SAAMBuilder setPasswordHelper(PasswordHelper passwordHelper) {
        this.passwordHelper = passwordHelper;
        return this;
    }

    public IdentifierVerificationCodeGenerator getIdentifierVerificationCodeGenerator() {
        if (null == identifierVerificationCodeGenerator) {
            identifierVerificationCodeGenerator = new IdentifierVerificationCodeGeneratorImpl();
        }
        return identifierVerificationCodeGenerator;
    }

    public SAAMBuilder setIdentifierVerificationCodeGenerator(IdentifierVerificationCodeGenerator identifierVerificationCodeGenerator) {
        this.identifierVerificationCodeGenerator = identifierVerificationCodeGenerator;
        return this;
    }

    public OAuthPlatformAgent getOAuthPlatformAgent() {
        checkForMissedValue(oAuthPlatformAgent, OAuthPlatformAgent.class);
        return oAuthPlatformAgent;
    }

    public SAAMBuilder setOAuthPlatformAgent(OAuthPlatformAgent oAuthPlatformAgent) {
        this.oAuthPlatformAgent = oAuthPlatformAgent;
        return this;
    }

    public ResponseMapper getResponseMapper() {
        if (null == responseMapper) {
            responseMapper = new ResponseMapper();
        }
        return responseMapper;
    }

    public SAAMBuilder setResponseMapper(ResponseMapper responseMapper) {
        this.responseMapper = responseMapper;
        return this;
    }

    public SAAM build() {
        return new SAAMServer(this);
    }

}
