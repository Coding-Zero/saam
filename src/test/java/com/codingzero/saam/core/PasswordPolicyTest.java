package com.codingzero.saam.core;

import com.codingzero.saam.common.PasswordPolicy;
import com.codingzero.utilities.error.BusinessError;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PasswordPolicyTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCheck() {
        PasswordPolicy policy = new PasswordPolicy(
                3,
                8,
                false,
                false);
        policy.check("pass1234");
    }

    @Test
    public void testCheck_TooShort() {
        PasswordPolicy policy = new PasswordPolicy(
                3,
                8,
                false,
                false);
        thrown.expect(BusinessError.class);
        policy.check("pa");
    }

    @Test
    public void testCheck_TooLong() {
        PasswordPolicy policy = new PasswordPolicy(
                3,
                8,
                false,
                false);
        thrown.expect(BusinessError.class);
        policy.check("password123456");
    }

    @Test
    public void testCheck_Capital() {
        PasswordPolicy policy = new PasswordPolicy(
                3,
                8,
                true,
                false);
        policy.check("Pass1234");
    }

    @Test
    public void testCheck_NoCapital() {
        PasswordPolicy policy = new PasswordPolicy(
                3,
                8,
                true,
                false);
        thrown.expect(BusinessError.class);
        policy.check("pass1234");
    }

    @Test
    public void testCheck_SpecialChar() {
        PasswordPolicy policy = new PasswordPolicy(
                3,
                8,
                false,
                true);
        policy.check("pass123+");
    }

    @Test
    public void testCheck_NoSpecialChar() {
        PasswordPolicy policy = new PasswordPolicy(
                3,
                8,
                false,
                true);
        thrown.expect(BusinessError.class);
        policy.check("pass1234");
    }

    @Test
    public void testCheck_Capital_SpecialChar() {
        PasswordPolicy policy = new PasswordPolicy(
                3,
                8,
                true,
                true);
        policy.check("Pass123+");
    }

}
