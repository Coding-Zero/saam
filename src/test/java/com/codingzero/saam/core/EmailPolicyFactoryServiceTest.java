package com.codingzero.saam.core;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.core.application.EmailPolicyEntity;
import com.codingzero.saam.core.application.EmailPolicyFactoryService;
import com.codingzero.saam.core.identifier.IdentifierFactoryService;
import com.codingzero.saam.core.services.IdentifierPolicyHelper;
import com.codingzero.saam.core.identifier.IdentifierRepositoryService;
import com.codingzero.saam.infrastructure.database.EmailPolicyOS;
import com.codingzero.saam.infrastructure.database.IdentifierPolicyAccess;
import com.codingzero.utilities.error.BusinessError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmailPolicyFactoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private IdentifierPolicyAccess identifierPolicyAccess;
    private IdentifierPolicyHelper identifierPolicyHelper;
    private IdentifierFactoryService identifierFactory;
    private IdentifierRepositoryService identifierRepository;
    private EmailPolicyFactoryService service;

    @Before
    public void setUp() {
        identifierPolicyAccess = mock(IdentifierPolicyAccess.class);
        identifierPolicyHelper = new IdentifierPolicyHelper(identifierPolicyAccess);
        identifierFactory = mock(IdentifierFactoryService.class);
        identifierRepository = mock(IdentifierRepositoryService.class);
        service = new EmailPolicyFactoryService(
                identifierPolicyHelper,
                identifierFactory,
                identifierRepository);
    }

    @Test
    public void testGenerate() {
        Application application = mock(Application.class);
        when(application.getId()).thenReturn("APP_1");
        boolean isVerificationRequired = false;
        List<String> domains = new ArrayList<>();
        EmailPolicyEntity entity = service.generate(application, isVerificationRequired, domains);
        assertEquals(application, entity.getApplication());
        assertEquals(isVerificationRequired, entity.isVerificationRequired());
        assertEquals(domains, entity.getDomains());
        assertNewPolicy(entity);
    }

    private void assertNewPolicy(EmailPolicyEntity policy) {
        assertEquals(IdentifierType.EMAIL, policy.getType());
        assertEquals(5, policy.getMinLength());
        assertEquals(255, policy.getMaxLength());
        assertEquals(policy.getCreationTime(), policy.getUpdateTime());
        assertEquals(true, policy.isNew());
        assertEquals(false, policy.isDirty());
        assertEquals(false, policy.isVoid());
    }

    @Test
    public void testDeduplicateDomains() {
        List<String> domains = Arrays.asList(
                "foo1.com",
                "Foo1.com",
                "foo2.com");
        domains = service.deduplicateDomains(domains);
        assertEquals(2, domains.size());
        assertEquals("foo1.com", domains.get(0));
        assertEquals("foo2.com", domains.get(1));
    }

    @Test
    public void testCheckDomainFormat() {
        List<String> domains = Arrays.asList(
                "foo.com");
        service.checkForDomainFormat(domains);
    }

    @Test
    public void testCheckDomainFormat_IllegalFormat() {
        List<String> domains = Arrays.asList(
                "foo");
        thrown.expect(BusinessError.class);
        service.checkForDomainFormat(domains);
    }

    @Test
    public void testReconstitute() {
        Application application = mock(Application.class);
        EmailPolicyOS os = mock(EmailPolicyOS.class);
        EmailPolicyEntity entity = service.reconstitute(os, application);
        assertEquals(application, entity.getApplication());
        assertEquals(os, entity.getObjectSegment());
    }

    @Test
    public void testReconstitute_NullValue() {
        Application application = mock(Application.class);
        EmailPolicyEntity entity = service.reconstitute(null, application);
        assertEquals(null, entity);
    }

}
