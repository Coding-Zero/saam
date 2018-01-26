package com.codingzero.saam.core;

import com.codingzero.saam.common.Action;
import com.codingzero.saam.common.ApplicationStatus;
import com.codingzero.saam.core.application.APIKeyFactoryService;
import com.codingzero.saam.core.application.APIKeyRepositoryService;
import com.codingzero.saam.core.application.ApplicationFactoryService;
import com.codingzero.saam.core.application.ApplicationRoot;
import com.codingzero.saam.core.application.EmailPolicyFactoryService;
import com.codingzero.saam.core.application.EmailPolicyRepositoryService;
import com.codingzero.saam.core.application.IdentifierPolicyRepositoryService;
import com.codingzero.saam.core.application.OAuthIdentifierPolicyFactoryService;
import com.codingzero.saam.core.application.OAuthIdentifierPolicyRepositoryService;
import com.codingzero.saam.core.application.PermissionEntity;
import com.codingzero.saam.core.application.PermissionFactoryService;
import com.codingzero.saam.core.application.PrincipalEntity;
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
import com.codingzero.saam.infrastructure.database.ApplicationOS;
import com.codingzero.saam.infrastructure.database.PermissionOS;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

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
    private UsernamePolicyRepositoryService usernameIdentifierPolicyRepository;
    private EmailPolicyFactoryService emailIdentifierPolicyFactory;
    private EmailPolicyRepositoryService emailIdentifierPolicyRepository;
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
    private ApplicationRoot entity;

    @Before
    public void setUp() {
        objectSegment = mock(ApplicationOS.class);
        factory = mock(ApplicationFactoryService.class);
        identifierPolicyRepository = mock(IdentifierPolicyRepositoryService.class);
        usernameIdentifierPolicyFactory = mock(UsernamePolicyFactoryService.class);
        usernameIdentifierPolicyRepository = mock(UsernamePolicyRepositoryService.class);
        emailIdentifierPolicyFactory = mock(EmailPolicyFactoryService.class);
        emailIdentifierPolicyRepository = mock(EmailPolicyRepositoryService.class);
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
        createEntity();
    }

    private void createEntity() {
        entity = new ApplicationRoot(
                objectSegment,
                factory,
                identifierPolicyRepository,
                usernameIdentifierPolicyFactory,
                usernameIdentifierPolicyRepository,
                emailIdentifierPolicyFactory,
                emailIdentifierPolicyRepository,
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
                resourceRepository);
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

    @Test
    public void testFetchUserByIdentifier() {
        String identifier = "foo@foo.com";
        IdentifierPolicy usernamePolicy = mock(IdentifierPolicy.class);
        when(usernamePolicy.fetchIdentifierById(identifier)).thenReturn(null);
        IdentifierPolicy emailPolicy = mock(IdentifierPolicy.class);
        List<IdentifierPolicy> policies = Arrays.asList(usernamePolicy, emailPolicy);
        when(identifierPolicyRepository.findAll(entity)).thenReturn(policies);
//        entity.fetchUserByIdentifier()
    }

}
