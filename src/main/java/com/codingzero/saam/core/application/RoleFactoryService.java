package com.codingzero.saam.core.application;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.infrastructure.database.RoleOS;
import com.codingzero.saam.infrastructure.database.spi.PrincipalAccess;
import com.codingzero.saam.infrastructure.database.spi.RoleAccess;
import com.codingzero.utilities.error.BusinessError;

import java.util.Date;
import java.util.regex.Pattern;

public class RoleFactoryService {

    private static final int NAME_MIN_LENGTH = 2;
    private static final int NAME_MAX_LENGTH = 46;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]+[a-zA-Z0-9 _.-]+$");

    private RoleAccess access;
    private PrincipalAccess principalAccess;

    public RoleFactoryService(RoleAccess access,
                              PrincipalAccess principalAccess) {
        this.access = access;
        this.principalAccess = principalAccess;
    }

    public RoleEntity generate(Application application, String name) {
        checkForNameFormat(name);
        checkForDuplicateName(application, name);
        String id = principalAccess.generateId(application.getId());
        RoleOS os = new RoleOS(application.getId(), id, new Date(), name);
        RoleEntity entity = reconstitute(os, application);
        entity.markAsNew();
        return entity;
    }

    public void checkForDuplicateName(Application application, String name) {
        if (access.isDuplicateName(application.getId(), name.trim())) {
            throw BusinessError.raise(Errors.DUPLICATE_ROLE_NAME)
                    .message("Name has already exist.")
                    .details("applicationId", application.getId())
                    .details("name", name)
                    .build();
        }
    }

    public void checkForNameFormat(String name) {
        if (null == name) {
            throw new IllegalArgumentException("Role name is missing");
        }
        name = name.trim();
        if (null == name
                || name.length() < NAME_MIN_LENGTH
                || name.length() > NAME_MAX_LENGTH) {
            throw BusinessError.raise(Errors.ILLEGAL_ROLE_NAME_FORMAT)
                    .message("Need to be greater than "
                            + NAME_MIN_LENGTH
                            + " characters and less than "
                            + NAME_MAX_LENGTH + " characters")
                    .details("name", name)
                    .details("minLength", NAME_MIN_LENGTH)
                    .details("maxLength", NAME_MAX_LENGTH)
                    .build();
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw BusinessError.raise(Errors.ILLEGAL_ROLE_NAME_FORMAT)
                    .message("May only contain digits (0-9), alphabets (a-z), "
                            + "dash (-), dot (.), space and underscore (_).")
                    .details("name", name)
                    .build();
        }
    }

    public RoleEntity reconstitute(RoleOS os, Application application) {
        if (null == os) {
            return null;
        }
        return new RoleEntity(os, application, this);
    }

}
