package com.codingzero.saam.core.application;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.oauthidentifier.OAuthIdentifierFactoryService;
import com.codingzero.saam.core.oauthidentifier.OAuthIdentifierRepositoryService;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierPolicyOS;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierPolicyAccess;
import com.codingzero.utilities.error.BusinessError;

import java.util.Date;
import java.util.Map;

public class OAuthIdentifierPolicyFactoryService {

    private OAuthIdentifierPolicyAccess access;
    private OAuthIdentifierFactoryService oAuthIdentifierFactory;
    private OAuthIdentifierRepositoryService oAuthIdentifierRepository;

    public OAuthIdentifierPolicyFactoryService(OAuthIdentifierPolicyAccess access,
                                               OAuthIdentifierFactoryService oAuthIdentifierFactory,
                                               OAuthIdentifierRepositoryService oAuthIdentifierRepository) {
        this.access = access;
        this.oAuthIdentifierFactory = oAuthIdentifierFactory;
        this.oAuthIdentifierRepository = oAuthIdentifierRepository;
    }

    public OAuthIdentifierPolicyEntity generate(Application application,
                                                OAuthPlatform platform,
                                                Map<String, Object> configurations) {
        checkForDuplicatePlatform(application, platform);
        Date currentDateTime = new Date(System.currentTimeMillis());
        OAuthIdentifierPolicyOS os = new OAuthIdentifierPolicyOS(
                application.getId(), platform, configurations, true, currentDateTime, currentDateTime);
        OAuthIdentifierPolicyEntity entity = reconstitute(os, application);
        entity.markAsNew();
        return entity;
    }

    private void checkForDuplicatePlatform(Application application, OAuthPlatform platform) {
        if (access.isDuplicatePlatform(application.getId(), platform)) {
            throw BusinessError.raise(Errors.DUPLICATE_OAUTH_PLATFORM)
                    .message("OAuth platform \"" + platform.name() + "\" already exist.")
                    .details("applicationId", application.getId())
                    .details("platform", platform)
                    .build();
        }
    }

    public OAuthIdentifierPolicyEntity reconstitute(OAuthIdentifierPolicyOS os, Application application) {
        if (null == os) {
            return null;
        }
        return new OAuthIdentifierPolicyEntity(os, application, oAuthIdentifierFactory, oAuthIdentifierRepository);
    }

}
