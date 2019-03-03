package com.codingzero.saam.domain.principal.role;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.common.PrincipalId;
import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.RoleFactory;
import com.codingzero.saam.domain.services.ApplicationStatusVerifier;
import com.codingzero.saam.infrastructure.data.PrincipalAccess;
import com.codingzero.saam.infrastructure.data.RoleAccess;
import com.codingzero.saam.infrastructure.data.RoleOS;
import com.codingzero.utilities.error.BusinessError;

import java.util.Date;
import java.util.regex.Pattern;

public class RoleFactoryService implements RoleFactory {

    public static final int NAME_MIN_LENGTH = 2;
    public static final int NAME_MAX_LENGTH = 46;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]+[a-zA-Z0-9 _.-]+$");

    private RoleAccess access;
    private PrincipalAccess principalAccess;
    private ApplicationStatusVerifier applicationStatusVerifier;

    public RoleFactoryService(RoleAccess access,
                              PrincipalAccess principalAccess,
                              ApplicationStatusVerifier applicationStatusVerifier) {
        this.access = access;
        this.principalAccess = principalAccess;
        this.applicationStatusVerifier = applicationStatusVerifier;
    }

    @Override
    public RoleEntity generate(Application application, String name) {
        applicationStatusVerifier.checkForDeactiveStatus(application);
        checkForNameFormat(name);
        checkForDuplicateName(application, name);
        String id = principalAccess.generateId(application.getId(), PrincipalType.ROLE);
        RoleOS os = new RoleOS(new PrincipalId(application.getId(), id), new Date(), name);
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
        if (name.length() < NAME_MIN_LENGTH
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
