package com.codingzero.saam.core.application;

import com.codingzero.saam.common.Action;
import com.codingzero.saam.common.Errors;
import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.core.APIKey;
import com.codingzero.saam.core.Principal;
import com.codingzero.saam.core.Resource;
import com.codingzero.saam.infrastructure.database.PermissionOS;
import com.codingzero.saam.infrastructure.database.spi.PermissionAccess;
import com.codingzero.utilities.error.BusinessError;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class PermissionFactoryService {

    private static final int CODE_MIN_LENGTH = 3;
    private static final int CODE_MAX_LENGTH = 25;
    private static final Pattern CODE_PATTERN = Pattern.compile("^[a-zA-Z0-9]+[a-zA-Z0-9_-]+$");

    private PermissionAccess access;
    private PrincipalRepositoryService principalRepository;

    public PermissionFactoryService(PermissionAccess access,
                                    PrincipalRepositoryService principalRepository) {
        this.access = access;
        this.principalRepository = principalRepository;
    }

    public PermissionEntity generate(Resource resource, Principal principal, List<Action> actions) {
        checkForExistingPermission(resource, principal);
        checkForIllegalType(principal, actions);
        checkForIllegalActionCode(actions);
        checkForDuplicateActions(actions);
        PermissionOS os = new PermissionOS(
                resource.getApplication().getId(),
                resource.getKey(), principal.getId(),
                new Date(),
                actions);
        PermissionEntity entity = reconstitute(os, resource, principal);
        entity.markAsNew();
        return entity;
    }

    private void checkForExistingPermission(Resource resource, Principal principal) {
        if (access.isDuplicate(resource.getApplication().getId(), resource.getKey(), principal.getId())) {
            throw BusinessError.raise(Errors.DUPLICATE_PERMISSION)
                    .message("Permission has already exist")
                    .details("applicationId", resource.getApplication().getId())
                    .details("resourceKey", resource.getKey())
                    .details("principalId", principal.getId())
                    .build();
        }
    }

    private void checkForIllegalType(Principal principal, List<Action> actions) {
        for (Action action: actions) {
            if (principal.getType() == PrincipalType.API_KEY
                    && !action.isAllowed()) {
                throw BusinessError.raise(Errors.ILLEGAL_PERMISSION_TYPE)
                        .message("API Key only accept DENY permissions")
                        .details("applicationId", principal.getApplication().getId())
                        .details("apiKey", ((APIKey) principal).getKey())
                        .build();
            }
        }
    }

    public void checkForIllegalActionCode(List<Action> actions) {
        for (Action action: actions) {
            checkForIllegalActionCode(action);
        }
    }

    private void checkForIllegalActionCode(Action action) {
        String code = action.getCode();
        if (null == code) {
            throw new IllegalArgumentException("Action name is missing");
        }
        if (code.length() < CODE_MIN_LENGTH
                || code.length() > CODE_MAX_LENGTH) {
            throw BusinessError.raise(Errors.ILLEGAL_ACTION_CODE_FORMAT)
                    .message("Action code need to be greater than "
                            + CODE_MIN_LENGTH
                            + " characters and less than "
                            + CODE_MAX_LENGTH + " characters")
                    .details("minLength", CODE_MIN_LENGTH)
                    .details("maxLength", CODE_MAX_LENGTH)
                    .build();
        }
        if (!CODE_PATTERN.matcher(code).matches()) {
            throw BusinessError.raise(Errors.ILLEGAL_ACTION_CODE_FORMAT)
                    .message("May only contain digits (0-9), alphabets (a-z), "
                            + "dash (-) and underscore (_).")
                    .build();
        }
    }

    public void checkForDuplicateActions(List<Action> actions) {
        Set<String> actionCodes = new HashSet<>();
        for (Action action: actions) {
            String code = action.getCode().toLowerCase();
            if (actionCodes.contains(code)) {
                throw BusinessError.raise(Errors.DUPLICATE_ACTION_CODE)
                        .message("Action, " + action.getCode() + " already exist.")
                        .details("actionCode", action.getCode())
                        .build();
            }
            actionCodes.add(code);
        }
    }

    public List<String> toActionCodes(List<Action> actions) {
        Set<String> codeFilter = new HashSet<>();
        List<String> codes = new ArrayList<>(actions.size());
        for (Action action: actions) {
            if (!codeFilter.contains(action.getCode().toLowerCase())) {
                codes.add(action.getCode());
                codeFilter.add(action.getCode().toLowerCase());
            }
        }
        return codes;
    }

    public PermissionEntity reconstitute(PermissionOS os,
                                         Resource resource, Principal principal) {
        if (null == os) {
            return null;
        }
        return new PermissionEntity(os, resource, principal, this, principalRepository);
    }

}
