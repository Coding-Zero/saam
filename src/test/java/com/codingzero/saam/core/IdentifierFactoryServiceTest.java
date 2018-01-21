package com.codingzero.saam.core;

import com.codingzero.saam.core.application.IdentifierEntity;
import com.codingzero.saam.core.application.IdentifierFactoryService;
import com.codingzero.saam.core.application.UserRepositoryService;
import com.codingzero.saam.infrastructure.database.IdentifierOS;
import com.codingzero.saam.infrastructure.database.spi.IdentifierAccess;
import com.codingzero.saam.infrastructure.database.spi.IdentifierVerificationCodeGenerator;
import com.codingzero.utilities.error.BusinessError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IdentifierFactoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private IdentifierAccess access;
    private IdentifierVerificationCodeGenerator verificationCodeGenerator;
    private UserRepositoryService userRepository;
    private IdentifierFactoryService service;

    @Before
    public void setUp() {
        access = mock(IdentifierAccess.class);
        verificationCodeGenerator = mock(IdentifierVerificationCodeGenerator.class);
        userRepository = mock(UserRepositoryService.class);
        service = new IdentifierFactoryService(
                access,
                verificationCodeGenerator,
                userRepository);
    }

    @Test
    public void testGenerate() {
        String content = "foo";
        String applicationId = "applicationId";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        String policyCode = "USERNAME";
        IdentifierPolicy policy = mock(IdentifierPolicy.class);
        when(policy.isVerificationRequired()).thenReturn(true);
        when(policy.isActive()).thenReturn(true);
        when(policy.getApplication()).thenReturn(application);
        when(policy.getCode()).thenReturn(policyCode);
        when(access.isDuplicateContent(applicationId, policyCode, content)).thenReturn(false);
        User user = mock(User.class);
        when(user.getApplication()).thenReturn(application);
        IdentifierEntity identifier = service.generate(policy, content, user);
        assertEquals(user, identifier.getUser());
        assertEquals(policy, identifier.getPolicy());
        assertEquals(content, identifier.getContent());
        assertEquals(false, identifier.isVerified());
        assertEquals(null, identifier.getVerificationCode());
        assertEquals(identifier.getCreationTime(), identifier.getUpdateTime());
        assertEquals(true, identifier.isNew());
    }

    @Test
    public void testGenerate_NotVerificationRequiredPolicy() {
        String content = "foo";
        String applicationId = "applicationId";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        String policyCode = "USERNAME";
        IdentifierPolicy policy = mock(IdentifierPolicy.class);
        when(policy.isVerificationRequired()).thenReturn(false);
        when(policy.isActive()).thenReturn(true);
        when(policy.getApplication()).thenReturn(application);
        when(policy.getCode()).thenReturn(policyCode);
        when(access.isDuplicateContent(applicationId, policyCode, content)).thenReturn(false);
        User user = mock(User.class);
        when(user.getApplication()).thenReturn(application);
        IdentifierEntity identifier = service.generate(policy, content, user);
        assertEquals(user, identifier.getUser());
        assertEquals(policy, identifier.getPolicy());
        assertEquals(content, identifier.getContent());
        assertEquals(true, identifier.isVerified());
        assertEquals(null, identifier.getVerificationCode());
        assertEquals(identifier.getCreationTime(), identifier.getUpdateTime());
        assertEquals(true, identifier.isNew());
    }

    @Test
    public void testGenerate_NullPolicy() {
        User user = mock(User.class);
        thrown.expect(IllegalArgumentException.class);
        service.generate(null, "foo", user);
    }

    @Test
    public void testGenerate_InactivePolicy() {
        String applicationId = "applicationId";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        User user = mock(User.class);
        IdentifierPolicy policy = mock(IdentifierPolicy.class);
        when(policy.isActive()).thenReturn(false);
        when(policy.getApplication()).thenReturn(application);
        thrown.expect(BusinessError.class);
        service.generate(policy, "foo", user);
    }

    @Test
    public void testGenerate_DuplicateIdentifierContent() {
        String content = "foo";
        String applicationId = "applicationId";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        String policyCode = "USERNAME";
        IdentifierPolicy policy = mock(IdentifierPolicy.class);
        when(policy.isVerificationRequired()).thenReturn(true);
        when(policy.isActive()).thenReturn(true);
        when(policy.getApplication()).thenReturn(application);
        when(policy.getCode()).thenReturn(policyCode);
        when(access.isDuplicateContent(applicationId, policyCode, content)).thenReturn(true);
        User user = mock(User.class);
        when(user.getApplication()).thenReturn(application);
        thrown.expect(BusinessError.class);
        service.generate(policy, content, user);
    }

    @Test
    public void testReconstitute() {
        IdentifierPolicy policy = mock(IdentifierPolicy.class);
        User user = mock(User.class);
        IdentifierOS os = mock(IdentifierOS.class);
        IdentifierEntity entity = service.reconstitute(os, policy, user);
        assertEquals(policy, entity.getPolicy());
        assertEquals(user, entity.getUser());
        assertEquals(os, entity.getObjectSegment());
    }

    @Test
    public void testReconstitute_NullValue() {
        IdentifierPolicy policy = mock(IdentifierPolicy.class);
        User user = mock(User.class);
        IdentifierEntity entity = service.reconstitute(null, policy, user);
        assertEquals(null, entity);
    }

}
