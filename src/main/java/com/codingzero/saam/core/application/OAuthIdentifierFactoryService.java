package com.codingzero.saam.core.application;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.core.OAuthIdentifierPolicy;
import com.codingzero.saam.core.User;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierOS;
import com.codingzero.saam.infrastructure.database.spi.OAuthIdentifierAccess;
import com.codingzero.utilities.error.BusinessError;

import java.util.Date;
import java.util.Map;

public class OAuthIdentifierFactoryService {

    private OAuthIdentifierAccess access;
    private UserRepositoryService userRepository;

    public OAuthIdentifierFactoryService(OAuthIdentifierAccess access,
                                         UserRepositoryService userRepository) {
        this.access = access;
        this.userRepository = userRepository;
    }

    public OAuthIdentifierEntity generate(OAuthIdentifierPolicy policy, String content,
                                          Map<String, Object> properties, User user) {
        checkForDuplicateIdentifierContent(policy, content);
        Date currentDateTime = new Date(System.currentTimeMillis());
        OAuthIdentifierOS os = new OAuthIdentifierOS(
                user.getApplication().getId(),
                policy.getPlatform(), content, user.getId(),
                properties,
                currentDateTime,
                currentDateTime);
        OAuthIdentifierEntity entity = reconstitute(os, policy, user);
        entity.markAsNew();
        return entity;
    }

    private void checkForDuplicateIdentifierContent(OAuthIdentifierPolicy policy, String content) {
        if (access.isDuplicateContent(policy.getApplication().getId(), policy.getPlatform(), content)) {
            throw BusinessError.raise(Errors.DUPLICATE_OAUTH_IDENTIFIER)
                    .message(policy.getPlatform() + " oauth account has been connected.")
                    .details("applicationId", policy.getApplication().getId())
                    .details("platform", policy.getPlatform())
                    .details("content", content)
                    .build();
        }
    }

    public OAuthIdentifierEntity reconstitute(OAuthIdentifierOS os, OAuthIdentifierPolicy policy, User user) {
        if (null == os) {
            return null;
        }
        return new OAuthIdentifierEntity(os, policy, user, userRepository);
    }

}
