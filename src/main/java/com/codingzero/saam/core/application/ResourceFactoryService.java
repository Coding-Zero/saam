package com.codingzero.saam.core.application;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.common.ResourceKeySeparator;
import com.codingzero.saam.core.APIKey;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.Principal;
import com.codingzero.saam.core.Resource;
import com.codingzero.saam.infrastructure.database.ResourceOS;
import com.codingzero.saam.infrastructure.database.spi.ResourceAccess;
import com.codingzero.utilities.error.BusinessError;

import java.util.Date;
import java.util.regex.Pattern;

public class ResourceFactoryService {

    private static final int NAME_MIN_LENGTH = 1;
    private static final int NAME_MAX_LENGTH = 125;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]+[a-zA-Z0-9_-]+$");

    private ResourceAccess access;
    private PermissionFactoryService permissionFactory;
    private PermissionRepositoryService permissionRepository;

    public ResourceFactoryService(ResourceAccess access,
                                  PermissionFactoryService permissionFactory,
                                  PermissionRepositoryService permissionRepository) {
        this.access = access;
        this.permissionFactory = permissionFactory;
        this.permissionRepository = permissionRepository;
    }

    public ResourceEntity generate(Application application, String name, Principal owner, Resource parent) {
        owner = getOwner(owner);
        checkForNameFormat(name);
        String key;
        if (null != parent) {
            key = parent.getKey() + ResourceKeySeparator.VALUE + name;
        } else {
            key = name;
        }
        checkForDuplicateKey(application, key);
        ResourceOS os = new ResourceOS(application.getId(), key, owner.getId(), new Date());
        ResourceEntity entity = reconstitute(os, application, owner, parent);
        entity.markAsNew();
        return entity;
    }

    Principal getOwner(Principal owner) {
        if (owner.getType() == PrincipalType.API_KEY) {
            APIKey apiKey = (APIKey) owner;
            return apiKey.getOwner();
        }
        return owner;
    }

    public void checkForDuplicateKey(Application application, String key) {
        if (access.isDuplicateKey(application.getId(), key)) {
            throw BusinessError.raise(Errors.DUPLICATE_RESOURCE_KEY)
                    .message("Resource name has been taken.")
                    .details("applicationId", application.getId())
                    .details("key", key)
                    .build();
        }
    }

    public void checkForNameFormat(String name) {
        if (null == name) {
            throw new IllegalArgumentException("Resource name is missing");
        }
        name = name.trim();
        if (null == name
                || name.length() < NAME_MIN_LENGTH
                || name.length() > NAME_MAX_LENGTH) {
            throw BusinessError.raise(Errors.ILLEGAL_RESOURCE_NAME_FORMAT)
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
            throw BusinessError.raise(Errors.ILLEGAL_RESOURCE_NAME_FORMAT)
                    .message("May only contain digits (0-9), alphabets (a-z), "
                            + "dash (-) and underscore (_).")
                    .build();
        }
    }

    public ResourceEntity reconstitute(ResourceOS os, Application application, Principal owner, Resource parent) {
        if (null == os) {
            return null;
        }
        return new ResourceEntity(os, application, parent, owner, this, permissionFactory, permissionRepository);
    }
}
