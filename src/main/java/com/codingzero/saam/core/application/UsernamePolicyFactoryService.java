package com.codingzero.saam.core.application;

import com.codingzero.saam.common.UsernameFormat;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.infrastructure.database.UsernamePolicyOS;
import com.codingzero.saam.infrastructure.database.spi.IdentifierPolicyAccess;

import java.util.Date;

public class UsernamePolicyFactoryService {

    public static final int MIN_LENGTH = 3;
    public static final int MAX_LENGTH = 45;

    private IdentifierPolicyHelper identifierPolicyHelper;
    private IdentifierFactoryService identifierFactory;
    private IdentifierRepositoryService identifierRepository;

    public UsernamePolicyFactoryService(IdentifierPolicyAccess access,
                                        IdentifierFactoryService identifierFactory,
                                        IdentifierRepositoryService identifierRepository) {
        this(new IdentifierPolicyHelper(access), identifierFactory, identifierRepository);
    }

    public UsernamePolicyFactoryService(IdentifierPolicyHelper identifierPolicyHelper,
                                        IdentifierFactoryService identifierFactory,
                                        IdentifierRepositoryService identifierRepository) {
        this.identifierPolicyHelper = identifierPolicyHelper;
        this.identifierFactory = identifierFactory;
        this.identifierRepository = identifierRepository;
    }

    public UsernamePolicyEntity generate(
            Application application, String code) {
        identifierPolicyHelper.checkForCodeFormat(code);
        identifierPolicyHelper.checkForDuplicateCode(application, code);
        Date currentDateTime = new Date(System.currentTimeMillis());
        UsernamePolicyOS os = new UsernamePolicyOS(
                application.getId(),
                code,
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
        return new UsernamePolicyEntity(os, application, identifierFactory, identifierRepository);
    }

}
