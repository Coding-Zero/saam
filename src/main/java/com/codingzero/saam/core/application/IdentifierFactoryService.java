package com.codingzero.saam.core.application;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.core.IdentifierPolicy;
import com.codingzero.saam.core.User;
import com.codingzero.saam.infrastructure.database.IdentifierOS;
import com.codingzero.saam.infrastructure.database.spi.IdentifierAccess;
import com.codingzero.saam.infrastructure.database.spi.IdentifierVerificationCodeGenerator;
import com.codingzero.utilities.error.BusinessError;

import java.util.Date;

public class IdentifierFactoryService {

    private IdentifierAccess access;
    private IdentifierVerificationCodeGenerator verificationCodeGenerator;
    private UserRepositoryService userRepository;

    public IdentifierFactoryService(IdentifierAccess access,
                                    IdentifierVerificationCodeGenerator verificationCodeGenerator,
                                    UserRepositoryService userRepository) {
        this.access = access;
        this.verificationCodeGenerator = verificationCodeGenerator;
        this.userRepository = userRepository;
    }

    public IdentifierEntity generate(IdentifierPolicy policy, String content, User user) {
        checkForPolicy(policy);
        policy.check(content);
        checkForDuplicateIdentifierContent(policy, content);
        boolean isVerified = false;
        if (!policy.isVerificationRequired()) {
            isVerified = true;
        }
        Date currentDateTime = new Date(System.currentTimeMillis());
        IdentifierOS os = new IdentifierOS(
                user.getApplication().getId(),
                policy.getCode(),
                content,
                user.getId(),
                policy.getType(),
                isVerified,
                null,
                currentDateTime,
                currentDateTime
                );
        IdentifierEntity entity = reconstitute(os, policy, user);
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
                    .details("code", policy.getCode())
                    .build();
        }
    }

    private void checkForDuplicateIdentifierContent(IdentifierPolicy policy, String content) {
        if (access.isDuplicateContent(policy.getApplication().getId(), policy.getCode(), content)) {
            throw BusinessError.raise(Errors.DUPLICATE_IDENTIFIER)
                    .message("Identifier, " + content + " has already been taken")
                    .details("applicationId", policy.getApplication().getId())
                    .details("policyCode", policy.getCode())
                    .details("content", content)
                    .build();
        }
    }

    public IdentifierEntity reconstitute(IdentifierOS os, IdentifierPolicy policy, User user) {
        if (null == os) {
            return null;
        }
        return new IdentifierEntity(os, policy, user, verificationCodeGenerator, userRepository);
    }

}
