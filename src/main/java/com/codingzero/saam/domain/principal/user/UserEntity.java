package com.codingzero.saam.domain.principal.user;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.common.PasswordResetCode;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.Identifier;
import com.codingzero.saam.domain.Role;
import com.codingzero.saam.domain.User;
import com.codingzero.saam.domain.principal.PrincipalEntity;
import com.codingzero.saam.domain.principal.role.RoleRepositoryService;
import com.codingzero.saam.infrastructure.data.PasswordHelper;
import com.codingzero.saam.infrastructure.data.UserOS;
import com.codingzero.utilities.error.BusinessError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserEntity extends PrincipalEntity<UserOS> implements User {

    private UserFactoryService factory;
    private RoleRepositoryService roleRepository;
    private PasswordHelper passwordHelper;
    private List<Role> roles;

    public UserEntity(UserOS objectSegment, Application application,
                      UserFactoryService factory,
                      RoleRepositoryService roleRepository,
                      PasswordHelper passwordHelper) {
        super(objectSegment, application);
        this.factory = factory;
        this.roleRepository = roleRepository;
        this.passwordHelper = passwordHelper;
        this.roles = null;
    }

    @Override
    public List<Role> getPlayingRoles() {
        if (null == roles) {
            roles = new ArrayList<>(getObjectSegment().getRoleIds().size());
            for (String id: getObjectSegment().getRoleIds()) {
                Role role = roleRepository.findById(getApplication(), id);
                if (null != role) {
                    roles.add(role);
                }
            }
        }
        return roles;
    }

    @Override
    public void setPlayingRoles(List<Role> roles) {
        getObjectSegment().setRoleIds(factory.toRoleIds(roles));
        markAsDirty();
        this.roles = null;
    }

    @Override
    public boolean isPasswordSet() {
        return getObjectSegment().getPassword() != null;
    }

    @Override
    public boolean verifyPassword(String password) {
        checkForNoPasswordPolicy();
        if (null == getObjectSegment().getPassword()) {
            return false;
        }
        if (null == password) {
            return false;
        }
        return passwordHelper.verify(password, getObjectSegment().getPassword());
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        if (null == newPassword) {
            return;
        }
        checkForNoPasswordPolicy();
        if (null != getObjectSegment().getPassword()) {
            if (!verifyPassword(oldPassword)) {
                throw BusinessError.raise(Errors.WRONG_PASSWORD)
                        .message("Wrong password")
                        .build();
            }

            //abort changing password if the new one is same as old one
            if (verifyPassword(newPassword)) {
                return;
            }
        }
        getApplication().getPasswordPolicy().check(newPassword);
        getObjectSegment().setPassword(passwordHelper.encodePassword(newPassword));
        markAsDirty();
    }

    private void checkForNoPasswordPolicy() {
        if (null == getApplication().getPasswordPolicy()) {
            throw BusinessError.raise(Errors.PASSWORD_POLICY_UNAVAILABLE)
                    .message("Need to setup password policy first.")
                    .details("applicationId", getApplication().getId())
                    .build();
        }
    }

    @Override
    public PasswordResetCode generatePasswordResetCode(Identifier identifier, long timeout) {
        checkForNoPasswordPolicy();
        checkForUnverifiedIdentifier(identifier);
        String code = passwordHelper.generateResetCode(identifier.getPolicy().getType());
        Date expirationTime = new Date(System.currentTimeMillis() + timeout);
        getObjectSegment().setPasswordResetCode(
                new PasswordResetCode(code, expirationTime));
        markAsDirty();
        return getObjectSegment().getPasswordResetCode();
    }

    private void checkForUnverifiedIdentifier(Identifier identifier) {
        if (!identifier.isVerified()) {
            throw BusinessError.raise(Errors.IDENTIFIER_UNVERIFIED)
                    .message("Identifier has not been verified.")
                    .details("applicationId",
                            identifier.getPolicy().getApplication().getId())
                    .details("type", identifier.getPolicy().getType())
                    .details("identifier", identifier.getContent())
                    .build();

        }
    }

    @Override
    public void resetPassword(String code, String newPassword) {
        checkForNoPasswordPolicy();
        PasswordResetCode verificationCode = getObjectSegment().getPasswordResetCode();
        if (null == verificationCode
                || !verificationCode.getCode().equals(code)) {
            throw BusinessError.raise(Errors.INVALID_PASSWORD_RESET_CODE)
                    .message("Wrong reset code.")
                    .details("code", code)
                    .build();
        }
        if (verificationCode.getExpirationTime().getTime() <= System.currentTimeMillis()) {
            throw BusinessError.raise(Errors.INVALID_IDENTIFIER_VERIFICATION_CODE)
                    .message("Reset code has expired.")
                    .details("code", code)
                    .details("expirationTime", verificationCode.getExpirationTime())
                    .build();
        }
        getApplication().getPasswordPolicy().check(newPassword);
        getObjectSegment().setPassword(passwordHelper.encodePassword(newPassword));
        markAsDirty();
    }

}
