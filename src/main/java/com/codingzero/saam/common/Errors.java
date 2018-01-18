package com.codingzero.saam.common;

import com.codingzero.utilities.error.ErrorType;

public enum Errors implements ErrorType {

    DUPLICATE_ACTION_CODE("DuplicateActionCode"),
    DUPLICATE_APPLICATION_NAME("DuplicateApplicationName"),
    DUPLICATE_IDENTIFIER_POLICY_CODE("DuplicateIdentifierPolicyCode"),
    DUPLICATE_OAUTH_PLATFORM("DuplicateOAuthPlatform"),
    DUPLICATE_PERMISSION("DuplicatePermission"),
    DUPLICATE_RESOURCE_KEY("DuplicateResourceKey"),
    DUPLICATE_ROLE_NAME("DuplicateRoleName"),
    DUPLICATE_IDENTIFIER("DuplicateIdentifier"),
    DUPLICATE_OAUTH_IDENTIFIER("DuplicateOAuthIdentifier"),
    IDENTIFIER_POLICY_UNAVAILABLE("IdentifierPolicyUnavailable"),
    ILLEGAL_ACTION_CODE_FORMAT("IllegalActionCodeFormat"),
    ILLEGAL_API_KEY_NAME_FORMAT("IllegalAPIKeyNameFormat"),
    ILLEGAL_APPLICATION_NAME_FORMAT("IllegalApplicationNameFormat"),
    ILLEGAL_DOMAIN_NAME_FORMAT("IllegalDomainFormat"),
    ILLEGAL_IDENTIFIER_FORMAT("IllegalIdentifierFormat"),
    ILLEGAL_IDENTIFIER_POLICY_CODE_FORMAT("IllegalIdentifierPolicyCodeFormat"),
    ILLEGAL_PASSWORD_FORMAT("IllegalPasswordFormat"),
    ILLEGAL_PERMISSION_TYPE("IllegalPermissionType"),
    ILLEGAL_RESOURCE_NAME_FORMAT("IllegalResourceNameFormat"),
    ILLEGAL_ROLE_NAME_FORMAT("IllegalRoleNameFormat"),
    INVALID_IDENTIFIER_VERIFICATION_CODE("InvalidIdentifierVerificationCode"),
    INVALID_PASSWORD_RESET_CODE("InvalidPasswordResetCode"),
    AUTHENTICATION_FAILED("AuthenticationFailed"),
    PASSWORD_POLICY_UNAVAILABLE("PasswordPolicyUnavailable"),
    WRONG_PASSWORD("WrongPassword"),
    IDENTIFIER_UNVERIFIED("IdentifierUnverified")
    ;

    private final String name;

    Errors(String name) {
        this.name = name;
    }

    public String toString() {
        return this.getName();
    }

    @Override
    public String getName() {
        return name;
    }

    public static Errors toErrorType(String name) {
        Errors[] errors = values();
        for (Errors error: errors) {
            if (name.equalsIgnoreCase(error.getName())) {
                return error;
            }
        }
        return null;
    }

}
