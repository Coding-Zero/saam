package com.codingzero.saam.domain;

import com.codingzero.saam.domain.identifier.IdentifierFactoryService;
import com.codingzero.saam.domain.identifier.IdentifierRepositoryService;
import com.codingzero.saam.domain.application.UsernamePolicyEntity;
import com.codingzero.saam.domain.application.UsernamePolicyFactoryService;
import com.codingzero.saam.infrastructure.data.UsernamePolicyOS;
import com.codingzero.utilities.error.BusinessError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UsernamePolicyEntityTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private UsernamePolicyOS objectSegment;
    private Application application;
    private IdentifierFactoryService identifierFactory;
    private IdentifierRepositoryService identifierRepository;
    private UsernamePolicyEntity entity;

    @Before
    public void setUp() {
        objectSegment = mock(UsernamePolicyOS.class);
        application = mock(Application.class);
        identifierFactory = mock(IdentifierFactoryService.class);
        identifierRepository = mock(IdentifierRepositoryService.class);
        entity = new UsernamePolicyEntity(
                objectSegment,
                application
        );
    }

    @Test
    public void testSetVerificationRequired() {
        thrown.expect(UnsupportedOperationException.class);
        entity.setVerificationRequired(true);
    }

    @Test
    public void testSetMinLength() {
        thrown.expect(UnsupportedOperationException.class);
        entity.setMinLength(1);
    }

    @Test
    public void testSetMaxLength() {
        thrown.expect(UnsupportedOperationException.class);
        entity.setMaxLength(1);
    }

    @Test
    public void testCheck() {
        when(objectSegment.getMinLength()).thenReturn(UsernamePolicyFactoryService.MIN_LENGTH);
        when(objectSegment.getMaxLength()).thenReturn(UsernamePolicyFactoryService.MAX_LENGTH);
        entity.check("username");
    }

    @Test
    public void testCheck_Length_TooShort() {
        when(objectSegment.getMinLength()).thenReturn(UsernamePolicyFactoryService.MIN_LENGTH);
        when(objectSegment.getMaxLength()).thenReturn(UsernamePolicyFactoryService.MAX_LENGTH);
        thrown.expect(BusinessError.class);
        entity.check("ab");
    }

    @Test
    public void testCheck_Length_TooLong() {
        StringBuilder identifier = new StringBuilder();
        for (int i = 0; i < UsernamePolicyFactoryService.MAX_LENGTH + 2; i ++) {
            identifier.append("a");
        }
        when(objectSegment.getMinLength()).thenReturn(UsernamePolicyFactoryService.MIN_LENGTH);
        when(objectSegment.getMaxLength()).thenReturn(UsernamePolicyFactoryService.MAX_LENGTH);
        thrown.expect(BusinessError.class);
        entity.check(identifier.toString());
    }

    @Test
    public void setCheck_IllegalFormat() {
        when(objectSegment.getMinLength()).thenReturn(UsernamePolicyFactoryService.MIN_LENGTH);
        when(objectSegment.getMaxLength()).thenReturn(UsernamePolicyFactoryService.MAX_LENGTH);
        thrown.expect(BusinessError.class);
        entity.check("123+abc");
    }

}
