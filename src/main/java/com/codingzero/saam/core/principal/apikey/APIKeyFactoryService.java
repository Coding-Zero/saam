package com.codingzero.saam.core.principal.apikey;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.common.PrincipalId;
import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.core.APIKey;
import com.codingzero.saam.core.APIKeyFactory;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.User;
import com.codingzero.saam.core.principal.user.UserRepositoryService;
import com.codingzero.saam.infrastructure.database.APIKeyOS;
import com.codingzero.saam.infrastructure.database.APIKeyAccess;
import com.codingzero.saam.infrastructure.database.PrincipalAccess;
import com.codingzero.utilities.error.BusinessError;

import java.util.Date;
import java.util.regex.Pattern;

public class APIKeyFactoryService implements APIKeyFactory {

    public static final int NAME_MIN_LENGTH = 2;
    public static final int NAME_MAX_LENGTH = 46;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]+[a-zA-Z0-9 _.-]+$");

    private APIKeyAccess access;
    private PrincipalAccess principalAccess;
    private UserRepositoryService userRepository;

    public APIKeyFactoryService(APIKeyAccess access,
                                PrincipalAccess principalAccess,
                                UserRepositoryService userRepository) {
        this.access = access;
        this.principalAccess = principalAccess;
        this.userRepository = userRepository;
    }

    @Override
    public APIKey generate(Application application, User user, String name) {
        checkForNameFormat(name);
        String id = principalAccess.generateId(application.getId(), PrincipalType.API_KEY);
        String secretKey = access.generateSecretKey();
        APIKeyOS os = new APIKeyOS(new PrincipalId(application.getId(), id), new Date(), secretKey, name, user.getId(), true);
        APIKeyEntity entity = reconstitute(os, application, user);
        entity.markAsNew();
        return entity;
    }

    public void checkForNameFormat(String name) {
        if (null == name) {
            throw new IllegalArgumentException("APIKey name is missing");
        }
        if (name.length() < NAME_MIN_LENGTH
                || name.length() > NAME_MAX_LENGTH) {
            throw BusinessError.raise(Errors.ILLEGAL_API_KEY_NAME_FORMAT)
                    .message("Need to be greater than "
                            + NAME_MIN_LENGTH
                            + " characters and less than "
                            + NAME_MAX_LENGTH + " characters")
                    .details("minLength", NAME_MIN_LENGTH)
                    .details("maxLength", NAME_MAX_LENGTH)
                    .build();
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw BusinessError.raise(Errors.ILLEGAL_API_KEY_NAME_FORMAT)
                    .message("May only contain digits (0-9), alphabets (a-z), "
                            + "dash (-), dot (.), space and underscore (_).")
                    .build();
        }
    }

    public APIKeyEntity reconstitute(APIKeyOS os, Application application, User owner) {
        if (null == os) {
            return null;
        }
        return new APIKeyEntity(os, application, owner, this, userRepository);
    }

}
