package com.codingzero.saam.core.application;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.infrastructure.database.spi.IdentifierPolicyAccess;
import com.codingzero.utilities.error.BusinessError;

import java.util.regex.Pattern;

public class IdentifierPolicyHelper {

    private static final int CODE_MIN_LENGTH = 3;
    private static final int CODE_MAX_LENGTH = 25;
    private static final Pattern CODE_PATTERN = Pattern.compile("^[a-zA-Z0-9]+[a-zA-Z0-9_]+$");

    private IdentifierPolicyAccess access;

    public IdentifierPolicyHelper(IdentifierPolicyAccess access) {
        this.access = access;
    }

    public void checkForCodeFormat(String code) {
        if (null == code
                || code.length() < CODE_MIN_LENGTH
                || code.length() > CODE_MAX_LENGTH) {
            throw BusinessError.raise(Errors.ILLEGAL_IDENTIFIER_POLICY_CODE_FORMAT)
                    .message("Identifier policy code need to be greater or equal than "
                            + CODE_MIN_LENGTH
                            + " characters and less or equal than "
                            + CODE_MAX_LENGTH
                            + " characters")
                    .details("minLength", CODE_MIN_LENGTH)
                    .details("maxLength", CODE_MAX_LENGTH)
                    .build();
        }
        if (!CODE_PATTERN.matcher(code).matches()) {
            throw BusinessError.raise(Errors.ILLEGAL_IDENTIFIER_POLICY_CODE_FORMAT)
                    .message("May only contain digits (0-9), alphabets (a-z), "
                            + "dash (-) and underscore (_).")
                    .build();
        }
    }

    public void checkForDuplicateCode(Application application, String code) {
        if (access.isDuplicateCode(application.getId(), code)) {
            throw BusinessError.raise(Errors.DUPLICATE_IDENTIFIER_POLICY_CODE)
                    .message("Identifier policy code has been taken.")
                    .details("applicationId", application.getId())
                    .details("code", code)
                    .build();
        }
    }

}
