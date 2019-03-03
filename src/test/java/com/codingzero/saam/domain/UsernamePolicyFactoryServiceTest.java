package com.codingzero.saam.domain;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.UsernameFormat;
import com.codingzero.saam.domain.application.UsernamePolicyEntity;
import com.codingzero.saam.domain.application.UsernamePolicyFactoryService;
import com.codingzero.saam.domain.services.IdentifierPolicyHelper;
import com.codingzero.saam.infrastructure.data.UsernamePolicyOS;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UsernamePolicyFactoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private IdentifierPolicyHelper identifierPolicyHelper;
    private UsernamePolicyFactoryService service;

    @Before
    public void setUp() {
        identifierPolicyHelper = mock(IdentifierPolicyHelper.class);
        service = new UsernamePolicyFactoryService(
                identifierPolicyHelper
        );
    }

    @Test
    public void testGenerate() {
        Application application = mock(Application.class);
        when(application.getId()).thenReturn("APP_1");
        UsernamePolicyEntity entity = service.generate(application);
        assertEquals(application, entity.getApplication());
        assertEquals(IdentifierType.USERNAME, entity.getType());
        assertEquals(false, entity.isVerificationRequired());
        assertNewPolicy(entity);
    }

    private void assertNewPolicy(UsernamePolicyEntity policy) {
        assertEquals(IdentifierType.USERNAME, policy.getType());
        assertEquals(UsernameFormat.URL_SAFE, policy.getFormat());
        assertEquals(UsernamePolicyFactoryService.MIN_LENGTH, policy.getMinLength());
        assertEquals(UsernamePolicyFactoryService.MAX_LENGTH, policy.getMaxLength());
        assertEquals(policy.getCreationTime(), policy.getUpdateTime());
        assertEquals(true, policy.isNew());
        assertEquals(false, policy.isDirty());
        assertEquals(false, policy.isVoid());
    }

    @Test
    public void testReconstitute() {
        Application application = mock(Application.class);
        UsernamePolicyOS os = mock(UsernamePolicyOS.class);
        UsernamePolicyEntity entity = service.reconstitute(os, application);
        assertEquals(application, entity.getApplication());
        assertEquals(os, entity.getObjectSegment());
    }

    @Test
    public void testReconstitute_NullValue() {
        Application application = mock(Application.class);
        UsernamePolicyEntity entity = service.reconstitute(null, application);
        assertEquals(null, entity);
    }

}
