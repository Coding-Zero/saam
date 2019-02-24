package com.codingzero.saam.domain.identifier;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.common.IdentifierKey;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.ApplicationRepository;
import com.codingzero.saam.domain.Identifier;
import com.codingzero.saam.domain.IdentifierFactory;
import com.codingzero.saam.domain.IdentifierPolicy;
import com.codingzero.saam.domain.User;
import com.codingzero.saam.domain.principal.user.UserRepositoryService;
import com.codingzero.saam.infrastructure.database.IdentifierOS;
import com.codingzero.saam.infrastructure.database.IdentifierAccess;
import com.codingzero.saam.infrastructure.database.IdentifierVerificationCodeGenerator;
import com.codingzero.utilities.error.BusinessError;

import java.util.Date;

public class IdentifierFactoryService implements IdentifierFactory {

    private IdentifierAccess access;
    private IdentifierVerificationCodeGenerator verificationCodeGenerator;
    private UserRepositoryService userRepository;
    private ApplicationRepository applicationRepository;

    public IdentifierFactoryService(IdentifierAccess access,
                                    IdentifierVerificationCodeGenerator verificationCodeGenerator,
                                    UserRepositoryService userRepository, ApplicationRepository applicationRepository) {
        this.access = access;
        this.verificationCodeGenerator = verificationCodeGenerator;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public Identifier generate(IdentifierPolicy policy, String content, User user) {
        Application application = policy.getApplication();
        IdentifierType type = policy.getType();
        checkForPolicy(policy);
        policy.check(content);
        checkForDuplicateIdentifierContent(policy, content);
        boolean isVerified = false;
        if (!policy.isVerificationRequired()) {
            isVerified = true;
        }
        Date currentDateTime = new Date(System.currentTimeMillis());
        IdentifierOS os = new IdentifierOS(
                new IdentifierKey(application.getId(), content),
                type,
                user.getId(),
                isVerified,
                null,
                currentDateTime,
                currentDateTime
        );
        IdentifierEntity entity = reconstitute(os, application, user);
        entity.markAsNew();
        return entity;
    }

    private void checkForPolicy(IdentifierPolicy policy) {
        if (null == policy) {
            throw new IllegalArgumentException("Identifier policy cannot be null.");
        }
        if (!policy.isActive()) {
            throw BusinessError.raise(Errors.IDENTIFIER_POLICY_UNAVAILABLE)
                    .message("Identifier policy is unavailable.")
                    .details("applicationId", policy.getApplication().getId())
                    .details("type", policy.getType())
                    .build();
        }
    }

    private void checkForDuplicateIdentifierContent(IdentifierPolicy policy, String content) {
        if (access.isDuplicateContent(policy.getApplication().getId(), content)) {
            throw BusinessError.raise(Errors.DUPLICATE_IDENTIFIER)
                    .message("Identifier, " + content + " has already been taken")
                    .details("applicationId", policy.getApplication().getId())
                    .details("type", policy.getType())
                    .details("content", content)
                    .build();
        }
    }

    public IdentifierEntity reconstitute(IdentifierOS os, Application application, User user) {
        if (null == os) {
            return null;
        }
        return new IdentifierEntity(
                os,
                application,
                user,
                verificationCodeGenerator,
                userRepository,
                applicationRepository);
    }

}
