package com.codingzero.saam.domain;

import com.codingzero.saam.common.ApplicationStatus;
import com.codingzero.saam.domain.application.ApplicationFactoryService;
import com.codingzero.saam.domain.application.ApplicationRoot;
import com.codingzero.saam.domain.application.EmailPolicyFactoryService;
import com.codingzero.saam.domain.application.IdentifierPolicyRepositoryService;
import com.codingzero.saam.domain.application.OAuthIdentifierPolicyFactoryService;
import com.codingzero.saam.domain.application.OAuthIdentifierPolicyRepositoryService;
import com.codingzero.saam.domain.application.UsernamePolicyFactoryService;
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
import com.codingzero.saam.infrastructure.database.ApplicationOS;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApplicationRootTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ApplicationOS objectSegment;
    private ApplicationFactoryService factory;
    private IdentifierPolicyRepositoryService identifierPolicyRepository;
    private UsernamePolicyFactoryService usernameIdentifierPolicyFactory;
    private EmailPolicyFactoryService emailIdentifierPolicyFactory;
    private OAuthIdentifierPolicyFactoryService oAuthIdentifierPolicyFactory;
    private OAuthIdentifierPolicyRepositoryService oAuthIdentifierPolicyRepository;
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
    private ApplicationRoot entity;

    @Before
    public void setUp() {
        objectSegment = mock(ApplicationOS.class);
        factory = mock(ApplicationFactoryService.class);
        identifierPolicyRepository = mock(IdentifierPolicyRepositoryService.class);
        usernameIdentifierPolicyFactory = mock(UsernamePolicyFactoryService.class);
        emailIdentifierPolicyFactory = mock(EmailPolicyFactoryService.class);
        oAuthIdentifierPolicyFactory = mock(OAuthIdentifierPolicyFactoryService.class);
        oAuthIdentifierPolicyRepository = mock(OAuthIdentifierPolicyRepositoryService.class);
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
        createEntity();
    }

    private void createEntity() {
        entity = new ApplicationRoot(
                objectSegment,
                factory,
                identifierPolicyRepository,
                usernameIdentifierPolicyFactory,
                emailIdentifierPolicyFactory,
                oAuthIdentifierPolicyFactory,
                oAuthIdentifierPolicyRepository,
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
    public void testSetName() {
        String name = "name";
        when(objectSegment.getName()).thenReturn(name);
        entity.setName("name1");
        assertEquals(true, entity.isDirty());
    }

    @Test
    public void testSetName_SameValue() {
        String name = "name";
        when(objectSegment.getName()).thenReturn(name);
        entity.setName(name);
        assertEquals(false, entity.isDirty());
    }

    @Test
    public void testSetDescription() {
        String description = "description";
        when(objectSegment.getDescription()).thenReturn(description);
        entity.setDescription("description1");
        assertEquals(true, entity.isDirty());
    }

    @Test
    public void testSetDescription_SameValue() {
        String description = "description";
        when(objectSegment.getDescription()).thenReturn(description);
        entity.setDescription(description);
        assertEquals(false, entity.isDirty());
    }

    @Test
    public void testSetStatus() {
        ApplicationStatus status = ApplicationStatus.ACTIVE;
        when(objectSegment.getStatus()).thenReturn(status);
        entity.setStatus(ApplicationStatus.DEACTIVE);
        assertEquals(true, entity.isDirty());
    }

    @Test
    public void testSetStatus_SameValue() {
        ApplicationStatus status = ApplicationStatus.ACTIVE;
        when(objectSegment.getStatus()).thenReturn(status);
        entity.setStatus(status);
        assertEquals(false, entity.isDirty());
    }

}
