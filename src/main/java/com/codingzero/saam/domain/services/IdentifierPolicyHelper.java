package com.codingzero.saam.domain.services;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.infrastructure.data.IdentifierPolicyAccess;
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