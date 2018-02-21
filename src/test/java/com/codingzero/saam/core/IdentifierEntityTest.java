package com.codingzero.saam.core;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.IdentifierVerificationCode;
import com.codingzero.saam.core.application.IdentifierEntity;
import com.codingzero.saam.core.application.UserEntity;
import com.codingzero.saam.core.application.UserRepositoryService;
import com.codingzero.saam.infrastructure.database.IdentifierOS;
import com.codingzero.saam.infrastructure.database.spi.IdentifierVerificationCodeGenerator;
import com.codingzero.utilities.error.BusinessError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IdentifierEntityTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private IdentifierOS objectSegment;
    private IdentifierPolicy policy;
    private User user;
    private IdentifierVerificationCodeGenerator verificationCodeGenerator;
    private UserRepositoryService userRepository;
    private IdentifierEntity entity;

    @Before
    public void setUp() {
        objectSegment = mock(IdentifierOS.class);
        policy = mock(IdentifierPolicy.class);
        user = mock(User.class);
        verificationCodeGenerator = mock(IdentifierVerificationCodeGenerator.class);
        userRepository = mock(UserRepositoryService.class);
        entity = new IdentifierEntity(
                objectSegment,
                policy,
                user,
                verificationCodeGenerator,
                userRepository);
    }

    @Test
    public void testGetUser() {
        UserEntity userEntity = mock(UserEntity.class);
        String applicationId = "app";
        String userId = "user";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        when(policy.getApplication()).thenReturn(application);
        when(objectSegment.getUserId()).thenReturn(userId);
        when(userRepository.findById(application, userId)).thenReturn(userEntity);
        entity = new IdentifierEntity(
                objectSegment,
                policy,
                null,
                verificationCodeGenerator,
                userRepository);
        User user = entity.getUser();
        assertEquals(userEntity, user);
    }

    @Test
    public void testGetUser_NotNull() {
        UserEntity userEntity = mock(UserEntity.class);
        entity = new IdentifierEntity(
                objectSegment,
                policy,
                userEntity,
                verificationCodeGenerator,
                userRepository);
        User user = entity.getUser();
        assertEquals(userEntity, user);
    }

    @Test
    public void testGetPolicy() {
        IdentifierPolicy policy = mock(IdentifierPolicy.class);
        Application application = mock(Application.class);
        when(objectSegment.getIdentifierType()).thenReturn(IdentifierType.USERNAME);
        when(user.getApplication()).thenReturn(application);
        when(application.fetchIdentifierPolicy(IdentifierType.USERNAME)).thenReturn(policy);
        entity = new IdentifierEntity(
                objectSegment,
                null,
                user,
                verificationCodeGenerator,
                userRepository);
        IdentifierPolicy foundPolicy = entity.getPolicy();
        assertEquals(policy, foundPolicy);
    }

    @Test
    public void testGetPolicy_NotNull() {
        entity = new IdentifierEntity(
                objectSegment,
                policy,
                user,
                verificationCodeGenerator,
                userRepository);
        IdentifierPolicy foundPolicy = entity.getPolicy();
        assertEquals(policy, foundPolicy);
    }

    @Test
    public void testVerify() {
        when(objectSegment.isVerified()).thenReturn(false);
        IdentifierVerificationCode code =
                new IdentifierVerificationCode("code-abc", new Date(System.currentTimeMillis() + 10000));
        when(objectSegment.getVerificationCode()).thenReturn(code);
        entity.verify("code-abc");
        verify(objectSegment).setVerificationCode(null);
        verify(objectSegment).setVerified(true);
        verify(objectSegment).setUpdateTime(any(Date.class));
        assertEquals(true, entity.isDirty());
    }

    @Test
    public void testVerify_AlreadyVerified() {
        when(objectSegment.isVerified()).thenReturn(true);
        thrown.expect(BusinessError.class);
        entity.verify("code-abc");
    }

    @Test
    public void testVerify_InvalidVerificationCode_NullCode() {
        when(objectSegment.isVerified()).thenReturn(false);
        when(objectSegment.getVerificationCode()).thenReturn(null);
        thrown.expect(BusinessError.class);
        entity.verify("code-abc");
    }

    @Test
    public void testVerify_InvalidVerificationCode_WrongCode() {
        when(objectSegment.isVerified()).thenReturn(false);
        IdentifierVerificationCode code =
                new IdentifierVerificationCode("code-abc", new Date(System.currentTimeMillis() + 10000));
        when(objectSegment.getVerificationCode()).thenReturn(code);
        thrown.expect(BusinessError.class);
        entity.verify("code-dfg");
    }

    @Test
    public void testVerify_InvalidVerificationCode_CodeExpired() {
        when(objectSegment.isVerified()).thenReturn(false);
        IdentifierVerificationCode code =
                new IdentifierVerificationCode("code-abc", new Date(System.currentTimeMillis() - 10000));
        when(objectSegment.getVerificationCode()).thenReturn(code);
        thrown.expect(BusinessError.class);
        entity.verify("code-abc");
    }

}
