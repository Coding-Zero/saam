package com.codingzero.saam.domain.resource;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.common.ResourceKeySeparator;
import com.codingzero.saam.domain.APIKey;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.Principal;
import com.codingzero.saam.domain.PrincipalRepository;
import com.codingzero.saam.domain.Resource;
import com.codingzero.saam.domain.ResourceFactory;
import com.codingzero.saam.domain.services.ApplicationStatusVerifier;
import com.codingzero.saam.infrastructure.database.ResourceAccess;
import com.codingzero.saam.infrastructure.database.ResourceOS;
import com.codingzero.utilities.error.BusinessError;

import java.util.Date;
import java.util.regex.Pattern;

public class ResourceFactoryService implements ResourceFactory {

    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 125;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]+[a-zA-Z0-9_-]+$");

    private ResourceAccess access;
    private PermissionFactoryService permissionFactory;
    private PermissionRepositoryService permissionRepository;
    private PrincipalRepository principalRepository;
    private ApplicationStatusVerifier applicationStatusVerifier;

    public ResourceFactoryService(ResourceAccess access,
                                  PermissionFactoryService permissionFactory,
                                  PermissionRepositoryService permissionRepository,
                                  PrincipalRepository principalRepository,
                                  ApplicationStatusVerifier applicationStatusVerifier) {
        this.access = access;
        this.permissionFactory = permissionFactory;
        this.permissionRepository = permissionRepository;
        this.principalRepository = principalRepository;
        this.applicationStatusVerifier = applicationStatusVerifier;
    }

    @Override
    public Resource generate(Application application, String key, Principal owner) {
        applicationStatusVerifier.checkForDeactiveStatus(application);
        String name = readName(key);
        checkForNameFormat(name);
        String parentKey = readParentKey(key);
        checkForIllegalParent(application, parentKey);
        checkForDuplicateKey(application, key);
        owner = getRealOwner(owner);
        ResourceOS os = new ResourceOS(application.getId(), key, parentKey, owner.getId(), new Date());
        ResourceEntity entity = reconstitute(os, application, owner, null);
        entity.markAsNew();
        return entity;
    }

    private String readName(String key) {
        int position = key.lastIndexOf(ResourceKeySeparator.VALUE);
        if (-1 == position) {
            return key;
        }
        return key.substring(position + 1);
    }

    public ResourceEntity loadParent(Application application, String key, Principal owner) {
        String parentKey = readParentKey(key);
        if (null == parentKey) {
            return null;
        }
        ResourceOS objectSegment = access.selectByKey(application.getId(), parentKey);
        return reconstitute(objectSegment, application, owner, null);
    }

    private void checkForIllegalParent(Application application, String parentKey) {
        if (null != parentKey) {
            ResourceOS parentOS = access.selectByKey(application.getId(), parentKey);
            if (null == parentOS) {
                throw BusinessError.raise(Errors.NO_SUCH_RESOURCE_FOUND)
                        .message("No such parent resource, " + parentKey + " found")
                        .details("applicationId", application.getId())
                        .details("key", parentKey)
                        .build();
            }
        }
    }

    private String readParentKey(String key) {
        int position = key.lastIndexOf(ResourceKeySeparator.VALUE);
        if (-1 == position) {
            return null;
        }
        return key.substring(0, position);
    }

//    private String getKey(String name, Resource parent) {
//        String key;
//        if (null != parent) {
//            key = parent.getKey() + ResourceKeySeparator.VALUE + name;
//        } else {
//            key = name;
//        }
//        return key;
//    }

    public Principal getRealOwner(Principal owner) {
        if (owner.getType() == PrincipalType.API_KEY) {
            APIKey apiKey = (APIKey) owner;
            return apiKey.getOwner();
        }
        return owner;
    }

    private void checkForDuplicateKey(Application application, String key) {
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
        if (name.length() < NAME_MIN_LENGTH
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
        return new ResourceEntity(
                os,
                application,
                parent,
                owner,
                this,
                permissionFactory,
                permissionRepository,
                principalRepository);
    }
}
