package com.codingzero.saam.core;

import com.codingzero.saam.core.application.IdentifierPolicyHelper;
import com.codingzero.saam.infrastructure.database.spi.IdentifierPolicyAccess;
import com.codingzero.utilities.error.BusinessError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

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
    public void testCheckForCodeFormat() {
        helper.checkForCodeFormat("code_1");
    }

    @Test
    public void testCheckForCodeFormat_NullValue() {
        thrown.expect(BusinessError.class);
        helper.checkForCodeFormat(null);
    }

    @Test
    public void testCheckForCodeFormat_LengthTooShort() {
        thrown.expect(BusinessError.class);
        helper.checkForCodeFormat("ab");
    }

    @Test
    public void testCheckForCodeFormat_LengthTooLong() {
        thrown.expect(BusinessError.class);
        helper.checkForCodeFormat("abcdefghijklmnopqrstuvwxyz");
    }

    @Test
    public void testCheckForCodeFormat_IllegalFormat() {
        thrown.expect(BusinessError.class);
        helper.checkForCodeFormat("abc-abc");
    }

    @Test
    public void testCheckForDuplicateCode() {
        Application application = mock(Application.class);
        when(application.getId()).thenReturn("APP_1");
        String code = "EMAIL_1";
        when(identifierPolicyAccess.isDuplicateCode("APP_1", code)).thenReturn(false);
        helper.checkForDuplicateCode(application, code);
    }

    @Test
    public void testCheckForDuplicateCode_Duplicate() {
        Application application = mock(Application.class);
        when(application.getId()).thenReturn("APP_1");
        String code = "EMAIL_1";
        when(identifierPolicyAccess.isDuplicateCode("APP_1", code)).thenReturn(true);
        thrown.expect(BusinessError.class);
        helper.checkForDuplicateCode(application, code);
    }

}
