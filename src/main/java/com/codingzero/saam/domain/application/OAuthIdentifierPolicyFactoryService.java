package com.codingzero.saam.domain.application;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierPolicyAccess;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierPolicyOS;
import com.codingzero.utilities.error.BusinessError;

import java.util.Date;
import java.util.Map;

public class OAuthIdentifierPolicyFactoryService {

    private OAuthIdentifierPolicyAccess access;

    public OAuthIdentifierPolicyFactoryService(OAuthIdentifierPolicyAccess access) {
        this.access = access;
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
        return new OAuthIdentifierPolicyEntity(os, application);
    }

}
