package com.codingzero.saam.protocol.rest;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codingzero.saam.common.Errors;
import com.codingzero.utilities.error.BusinessError;
import com.codingzero.utilities.error.ErrorType;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.HashMap;
import java.util.Map;

import static com.codahale.metrics.MetricRegistry.name;

public class BusinessErrorMapper implements ExceptionMapper<BusinessError> {

    private final Meter exceptions;

    public BusinessErrorMapper(MetricRegistry metrics) {
        exceptions = metrics.meter(name(getClass(), "business-error"));
    }

    @Override
    public Response toResponse(BusinessError businessError) {
        exceptions.mark();
        Map<String, Object> errorBody = new HashMap<>();
//        errorBody.put("type", businessError.getType().getName());
        errorBody.put("details", businessError.getDetails());
        errorBody.put("message", businessError.getMessage());
        errorBody.put("code", toErrorCode(businessError.getType()));
        if (BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND == businessError.getType()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(errorBody)
                    .build();
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(errorBody)
                .build();
    }

    private int toErrorCode(ErrorType type) {
        if (BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND == type) {
            return 400000;
        }
        if (Errors.DUPLICATE_ACTION_CODE == type) {
            return 400100;
        }
        if (Errors.DUPLICATE_APPLICATION_NAME == type) {
            return 400101;
        }
        if (Errors.DUPLICATE_IDENTIFIER_POLICY_CODE == type) {
            return 400102;
        }
        if (Errors.DUPLICATE_OAUTH_PLATFORM == type) {
            return 400103;
        }
        if (Errors.DUPLICATE_PERMISSION == type) {
            return 400104;
        }
        if (Errors.DUPLICATE_RESOURCE_KEY == type) {
            return 400105;
        }
        if (Errors.DUPLICATE_ROLE_NAME == type) {
            return 400106;
        }
        if (Errors.DUPLICATE_IDENTIFIER == type) {
            return 400107;
        }
        if (Errors.DUPLICATE_OAUTH_IDENTIFIER == type) {
            return 400108;
        }
        if (Errors.IDENTIFIER_POLICY_UNAVAILABLE == type) {
            return 400108;
        }
        if (Errors.ILLEGAL_ACTION_CODE_FORMAT == type) {
            return 400109;
        }
        if (Errors.ILLEGAL_API_KEY_NAME_FORMAT == type) {
            return 400110;
        }
        if (Errors.ILLEGAL_APPLICATION_NAME_FORMAT == type) {
            return 400111;
        }
        if (Errors.ILLEGAL_DOMAIN_NAME_FORMAT == type) {
            return 400112;
        }
        if (Errors.ILLEGAL_IDENTIFIER_FORMAT == type) {
            return 400113;
        }
        if (Errors.ILLEGAL_IDENTIFIER_POLICY_CODE_FORMAT == type) {
            return 400114;
        }
        if (Errors.ILLEGAL_PASSWORD_FORMAT == type) {
            return 400115;
        }
        if (Errors.ILLEGAL_PERMISSION_TYPE == type) {
            return 400116;
        }
        if (Errors.ILLEGAL_RESOURCE_NAME_FORMAT == type) {
            return 400117;
        }
        if (Errors.ILLEGAL_ROLE_NAME_FORMAT == type) {
            return 400118;
        }
        if (Errors.INVALID_IDENTIFIER_VERIFICATION_CODE == type) {
            return 400119;
        }
        if (Errors.INVALID_PASSWORD_RESET_CODE == type) {
            return 400120;
        }
        if (Errors.AUTHENTICATION_FAILED == type) {
            return 400121;
        }
        if (Errors.PASSWORD_POLICY_UNAVAILABLE == type) {
            return 400122;
        }
        if (Errors.WRONG_PASSWORD == type) {
            return 400123;
        }
        if (Errors.IDENTIFIER_UNVERIFIED == type) {
            return 400124;
        }
        if (Errors.INVALID_IDENTIFIER_POLICY == type) {
            return 400125;
        }
        if (Errors.INVALID_STATUS == type) {
            return 400126;
        }
        throw new IllegalArgumentException("No such error code found for type, " + type.getName());
    }
}
