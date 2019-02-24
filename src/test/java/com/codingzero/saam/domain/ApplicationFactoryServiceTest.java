package com.codingzero.saam.domain;

import com.codingzero.saam.domain.principal.apikey.APIKeyFactoryService;
import com.codingzero.saam.domain.principal.apikey.APIKeyRepositoryService;
import com.codingzero.saam.domain.application.ApplicationFactoryService;
import com.codingzero.saam.domain.application.ApplicationRoot;
import com.codingzero.saam.domain.application.EmailPolicyFactoryService;
import com.codingzero.saam.domain.application.IdentifierPolicyRepositoryService;
import com.codingzero.saam.domain.application.OAuthIdentifierPolicyFactoryService;
import com.codingzero.saam.domain.application.OAuthIdentifierPolicyRepositoryService;
import com.codingzero.saam.domain.principal.PrincipalRepositoryService;
import com.codingzero.saam.domain.resource.ResourceFactoryService;
import com.codingzero.saam.domain.resource.ResourceRepositoryService;
import com.codingzero.saam.domain.principal.role.RoleFactoryService;
import com.codingzero.saam.domain.principal.role.RoleRepositoryService;
import com.codingzero.saam.domain.principal.user.UserFactoryService;
import com.codingzero.saam.domain.principal.user.UserRepositoryService;
import com.codingzero.saam.domain.services.ApplicationStatusVerifier;
import com.codingzero.saam.domain.usersession.UserSessionFactoryService;
import com.codingzero.saam.domain.usersession.UserSessionRepositoryService;
import com.codingzero.saam.domain.application.UsernamePolicyFactoryService;
import com.codingzero.saam.infrastructure.database.ApplicationOS;
import com.codingzero.saam.infrastructure.database.ApplicationAccess;
import com.codingzero.utilities.error.BusinessError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApplicationFactoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
    private ApplicationStatusVerifier applicationStatusVerifier;
    private ApplicationFactoryService service;

    @Before
    public void setUp() {
        access = mock(ApplicationAccess.class);
        usernamePolicyFactory = mock(UsernamePolicyFactoryService.class);
        emailPolicyFactory = mock(EmailPolicyFactoryService.class);
        identifierPolicyRepository = mock(IdentifierPolicyRepositoryService.class);
        ssoIdentifierPolicyFactory = mock(OAuthIdentifierPolicyFactoryService.class);
        ssoIdentifierPolicyRepository = mock(OAuthIdentifierPolicyRepositoryService.class);
        principalRepository = mock(PrincipalRepositoryService.class);
        userFactory = mock(UserFactoryService.class);
        userRepository = mock(UserRepositoryService.class);
        apiKeyFactory = mock(APIKeyFactoryService.class);
        apiKeyRepository = mock(APIKeyRepositoryService.class);
        userSessionFactory = mock(UserSessionFactoryService.class);
        userSessionRepository = mock(UserSessionRepositoryService.class);
        roleFactory = mock(RoleFactoryService.class);
        roleRepository = mock(RoleRepositoryService.class);
        resourceFactory = mock(ResourceFactoryService.class);
        resourceRepository = mock(ResourceRepositoryService.class);
        applicationStatusVerifier = mock(ApplicationStatusVerifier.class);
        service = new ApplicationFactoryService(
                access,
                usernamePolicyFactory,
                emailPolicyFactory,
                identifierPolicyRepository,
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
                resourceRepository,
                applicationStatusVerifier);
    }

    @Test
    public void testGenerate() {
        String name = "name";
        String description = "description";
        String id = "application-id";
        when(access.generateId()).thenReturn(id);
        Application entity = service.generate(name, description);
        assertEquals(id, entity.getId());
        assertEquals(name, entity.getName());
        assertEquals(description, entity.getDescription());
        assertEquals(null, entity.getPasswordPolicy());
    }

    @Test
    public void testCheckForNameFormat() {
        String name = "name";
        service.checkForNameFormat(name);
    }

    @Test
    public void testCheckForNameFormat_NullValue() {
        thrown.expect(IllegalArgumentException.class);
        service.checkForNameFormat(null);
    }

    @Test
    public void testCheckForNameFormat_TooShort() {
        String name = "a";
        thrown.expect(BusinessError.class);
        service.checkForNameFormat(name);
    }

    @Test
    public void testCheckForNameFormat_TooLong() {
        StringBuilder roleName = new StringBuilder();
        for (int i = 0; i < ApplicationFactoryService.NAME_MAX_LENGTH; i ++) {
            roleName.append("a");
        }
        roleName.append("a");
        thrown.expect(BusinessError.class);
        service.checkForNameFormat(roleName.toString());
    }

    @Test
    public void testCheckForNameFormat_InvalidFormat() {
        String name = "a~b";
        thrown.expect(BusinessError.class);
        service.checkForNameFormat(name);
    }

    @Test
    public void testCheckForDuplicateName() {
        String name = "name";
        when(access.isDuplicateName(name)).thenReturn(false);
        service.checkForDuplicateName(name);
    }

    @Test
    public void testCheckForDuplicateName_Duplicate() {
        String name = "name";
        when(access.isDuplicateName(name)).thenReturn(true);
        thrown.expect(BusinessError.class);
        service.checkForDuplicateName(name);
    }

    @Test
    public void testReconstitute() {
        ApplicationOS os = mock(ApplicationOS.class);
        ApplicationRoot entity = service.reconstitute(os);
        assertEquals(os, entity.getObjectSegment());
    }

    @Test
    public void testReconstitute_NullValue() {
        ApplicationRoot entity = service.reconstitute(null);
        assertEquals(null, entity);
    }

}
