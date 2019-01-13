package com.codingzero.saam.core.oauthidentifier;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.common.OAuthIdentifierKey;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.OAuthIdentifier;
import com.codingzero.saam.core.OAuthIdentifierFactory;
import com.codingzero.saam.core.OAuthIdentifierPolicy;
import com.codingzero.saam.core.User;
import com.codingzero.saam.core.principal.user.UserRepositoryService;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierOS;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierAccess;
import com.codingzero.utilities.error.BusinessError;

import java.util.Date;
import java.util.Map;

public class OAuthIdentifierFactoryService implements OAuthIdentifierFactory {

    private OAuthIdentifierAccess access;
    private UserRepositoryService userRepository;

    public OAuthIdentifierFactoryService(OAuthIdentifierAccess access,
                                         UserRepositoryService userRepository) {
        this.access = access;
        this.userRepository = userRepository;
    }

    public OAuthIdentifier generate(OAuthIdentifierPolicy policy, String content,
                                    Map<String, Object> properties, User user) {
        OAuthIdentifierKey key = new OAuthIdentifierKey(
                policy.getApplication().getId(), policy.getPlatform(), content);
        checkForDuplicateIdentifierContent(key);
        Date currentDateTime = new Date(System.currentTimeMillis());
        OAuthIdentifierOS os = new OAuthIdentifierOS(
                key, user.getId(),
                properties,
                currentDateTime,
                currentDateTime);
        OAuthIdentifierEntity entity = reconstitute(os, policy.getApplication(), user);
        entity.markAsNew();
        return entity;
    }

    private void checkForDuplicateIdentifierContent(OAuthIdentifierKey key) {
        if (access.isDuplicateKey(key)) {
            throw BusinessError.raise(Errors.DUPLICATE_OAUTH_IDENTIFIER)
                    .message(key.getPlatform() + " oauth account has been connected.")
                    .details("applicationId", key.getApplicationId())
                    .details("platform", key.getPlatform())
                    .details("content", key.getContent())
                    .build();
        }
    }

    public OAuthIdentifierEntity reconstitute(OAuthIdentifierOS os, Application application, User user) {
        if (null == os) {
            return null;
        }
        return new OAuthIdentifierEntity(os, application, user, userRepository);
    }

}
