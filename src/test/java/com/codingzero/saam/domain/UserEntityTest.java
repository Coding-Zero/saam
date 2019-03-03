package com.codingzero.saam.domain;

import com.codingzero.saam.common.PasswordPolicy;
import com.codingzero.saam.domain.principal.role.RoleEntity;
import com.codingzero.saam.domain.principal.role.RoleRepositoryService;
import com.codingzero.saam.domain.principal.user.UserEntity;
import com.codingzero.saam.domain.principal.user.UserFactoryService;
import com.codingzero.saam.infrastructure.data.UserOS;
import com.codingzero.saam.infrastructure.data.PasswordHelper;
import com.codingzero.utilities.error.BusinessError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserEntityTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private UserOS objectSegment;
    private Application application;
    private UserFactoryService factory;
    private RoleRepositoryService roleRepository;
    private PasswordHelper passwordHelper;
    private UserEntity entity;

    @Before
    public void setUp() {
        objectSegment = mock(UserOS.class);
        application = mock(Application.class);
        factory = mock(UserFactoryService.class);
        roleRepository = mock(RoleRepositoryService.class);
        passwordHelper = mock(PasswordHelper.class);
        entity = new UserEntity(
                objectSegment,
                application,
                factory,
                roleRepository,
                passwordHelper);
    }

    @Test
    public void testGetPlayingRoles() {
        List<String> roleIds = Arrays.asList("role1", "role2");
        when(objectSegment.getRoleIds()).thenReturn(roleIds);
        RoleEntity role1 = mock(RoleEntity.class);
        when(roleRepository.findById(application, "role1")).thenReturn(role1);
        when(roleRepository.findById(application, "role2")).thenReturn(null);
        List<Role> roles = entity.getPlayingRoles();
        assertEquals(1, roles.size());
        assertEquals(role1, roles.get(0));
    }

    @Test
    public void testVerifyPassword() {
        String password = "password";
        PasswordPolicy passwordPolicy = mock(PasswordPolicy.class);
        when(application.getPasswordPolicy()).thenReturn(passwordPolicy);
        when(objectSegment.getPassword()).thenReturn(password);
        when(passwordHelper.verify(password, password)).thenReturn(true);
        boolean isVerified = entity.verifyPassword(password);
        assertEquals(true, isVerified);
    }

    @Test
    public void testVerifyPassword_NoPasswordPolicySet() {
        String password = "password";
        when(application.getPasswordPolicy()).thenReturn(null);
        thrown.expect(BusinessError.class);
        entity.verifyPassword(password);
    }

    @Test
    public void testVerifyPassword_NoPasswordSet() {
        String password = "password";
        PasswordPolicy passwordPolicy = mock(PasswordPolicy.class);
        when(application.getPasswordPolicy()).thenReturn(passwordPolicy);
        when(objectSegment.getPassword()).thenReturn(null);
        boolean isVerified = entity.verifyPassword(password);
        assertEquals(false, isVerified);
    }

    @Test
    public void testVerifyPassword_NullPassword() {
        String password = "password";
        PasswordPolicy passwordPolicy = mock(PasswordPolicy.class);
        when(application.getPasswordPolicy()).thenReturn(passwordPolicy);
        when(objectSegment.getPassword()).thenReturn(password);
        boolean isVerified = entity.verifyPassword(null);
        assertEquals(false, isVerified);
    }

    @Test
    public void testChangePassword() {
        String newPassword = "new-password";
        String oldPassword = "password";
        PasswordPolicy passwordPolicy = mock(PasswordPolicy.class);
        when(application.getPasswordPolicy()).thenReturn(passwordPolicy);
        when(objectSegment.getPassword()).thenReturn(oldPassword);
        when(passwordHelper.verify(oldPassword, oldPassword)).thenReturn(true);
        when(passwordHelper.verify(newPassword, oldPassword)).thenReturn(false);
        entity.changePassword(oldPassword, newPassword);
        assertEquals(true, entity.isDirty());
    }

    @Test
    public void testChangePassword_NoPasswordPolicySet() {
        String password = "password";
        when(application.getPasswordPolicy()).thenReturn(null);
        thrown.expect(BusinessError.class);
        entity.verifyPassword(password);
    }

    @Test
    public void testChangePassword_NoPasswordSet() {
        String newPassword = "new-password";
        PasswordPolicy passwordPolicy = mock(PasswordPolicy.class);
        when(application.getPasswordPolicy()).thenReturn(passwordPolicy);
        when(objectSegment.getPassword()).thenReturn(null);
        entity.changePassword(null, newPassword);
        assertEquals(true, entity.isDirty());
    }

    @Test
    public void testChangePassword_NewPassword_NullValue() {
        String password = "password";
        PasswordPolicy passwordPolicy = mock(PasswordPolicy.class);
        when(application.getPasswordPolicy()).thenReturn(passwordPolicy);
        when(objectSegment.getPassword()).thenReturn(password);
        entity.changePassword(password, null);
        assertEquals(false, entity.isDirty());
    }

    @Test
    public void testChangePassword_WrongOldPassword() {
        String newPassword = "new-password";
        String oldPassword = "password";
        PasswordPolicy passwordPolicy = mock(PasswordPolicy.class);
        when(application.getPasswordPolicy()).thenReturn(passwordPolicy);
        when(objectSegment.getPassword()).thenReturn(oldPassword);
        when(passwordHelper.verify(oldPassword, oldPassword)).thenReturn(false);
        thrown.expect(BusinessError.class);
        entity.changePassword(oldPassword, newPassword);
    }

    @Test
    public void testChangePassword_SameOldPasswordAndNewPassword() {
        String newPassword = "new-password";
        String oldPassword = "password";
        PasswordPolicy passwordPolicy = mock(PasswordPolicy.class);
        when(application.getPasswordPolicy()).thenReturn(passwordPolicy);
        when(objectSegment.getPassword()).thenReturn(oldPassword);
        when(passwordHelper.verify(oldPassword, oldPassword)).thenReturn(true);
        when(passwordHelper.verify(newPassword, oldPassword)).thenReturn(true);
        entity.changePassword(oldPassword, newPassword);
        assertEquals(false, entity.isDirty());
    }

}
