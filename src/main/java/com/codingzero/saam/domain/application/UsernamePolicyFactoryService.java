package com.codingzero.saam.domain.application;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.UsernameFormat;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.services.IdentifierPolicyHelper;
import com.codingzero.saam.infrastructure.data.UsernamePolicyOS;

import java.util.Date;

public class UsernamePolicyFactoryService {

    public static final int MIN_LENGTH = 3;
    public static final int MAX_LENGTH = 45;

    private IdentifierPolicyHelper identifierPolicyHelper;

    public UsernamePolicyFactoryService(IdentifierPolicyHelper identifierPolicyHelper) {
        this.identifierPolicyHelper = identifierPolicyHelper;
    }

    public UsernamePolicyEntity generate(Application application) {
        identifierPolicyHelper.checkForDuplicateType(application, IdentifierType.USERNAME);
        Date currentDateTime = new Date(System.currentTimeMillis());
        UsernamePolicyOS os = new UsernamePolicyOS(
                application.getId(),
                MIN_LENGTH,
                MAX_LENGTH,
                true,
                currentDateTime,
                currentDateTime,
                UsernameFormat.URL_SAFE
        );
        UsernamePolicyEntity entity = reconstitute(os, application);
        entity.markAsNew();
        return entity;
    }

    public UsernamePolicyEntity reconstitute(UsernamePolicyOS os, Application application) {
        if (null == os) {
            return null;
        }
        return new UsernamePolicyEntity(os, application);
    }

}
