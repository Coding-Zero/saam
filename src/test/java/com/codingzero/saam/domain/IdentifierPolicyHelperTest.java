package com.codingzero.saam.domain;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.domain.services.IdentifierPolicyHelper;
import com.codingzero.saam.infrastructure.database.IdentifierPolicyAccess;
import com.codingzero.utilities.error.BusinessError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IdentifierPolicyHelperTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private IdentifierPolicyAccess identifierPolicyAccess;
    private IdentifierPolicyHelper helper;

    @Before
    public void setUp() {
        identifierPolicyAccess = mock(IdentifierPolicyAccess.class);
        helper = new IdentifierPolicyHelper(identifierPolicyAccess);
    }

    @Test
    public void testCheckForDuplicateCode() {
        Application application = mock(Application.class);
        when(application.getId()).thenReturn("APP_1");
        when(identifierPolicyAccess.isDuplicateType("APP_1", IdentifierType.EMAIL)).thenReturn(false);
        helper.checkForDuplicateType(application, IdentifierType.EMAIL);
    }

    @Test
    public void testCheckForDuplicateCode_Duplicate() {
        Application application = mock(Application.class);
        when(application.getId()).thenReturn("APP_1");
        when(identifierPolicyAccess.isDuplicateType("APP_1", IdentifierType.EMAIL)).thenReturn(true);
        thrown.expect(BusinessError.class);
        helper.checkForDuplicateType(application, IdentifierType.EMAIL);
    }

}
