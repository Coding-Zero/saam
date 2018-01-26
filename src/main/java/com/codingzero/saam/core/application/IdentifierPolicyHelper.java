package com.codingzero.saam.core.application;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.infrastructure.database.spi.IdentifierPolicyAccess;
import com.codingzero.utilities.error.BusinessError;

public class IdentifierPolicyHelper {

    private IdentifierPolicyAccess access;

    public IdentifierPolicyHelper(IdentifierPolicyAccess access) {
        this.access = access;
    }

    public void checkForDuplicateType(Application application, IdentifierType type) {
        if (access.isDuplicateType(application.getId(), type)) {
            throw BusinessError.raise(Errors.DUPLICATE_IDENTIFIER_POLICY_CODE)
                    .message("Identifier type has been taken.")
                    .details("applicationId", application.getId())
                    .details("code", type)
                    .build();
        }
    }

}
