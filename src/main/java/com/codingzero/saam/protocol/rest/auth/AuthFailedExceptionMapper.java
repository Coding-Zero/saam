package com.codingzero.saam.protocol.rest.auth;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.HashMap;
import java.util.Map;

import static com.codahale.metrics.MetricRegistry.name;

public class AuthFailedExceptionMapper implements ExceptionMapper<AuthFailedException> {

    private final Meter exceptions;

    public AuthFailedExceptionMapper(MetricRegistry metrics) {
        exceptions = metrics.meter(name(getClass(), "auth-error"));
    }

    @Override
    public Response toResponse(AuthFailedException businessError) {
        exceptions.mark();
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("message", businessError.getMessage());
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(errorBody)
                .build();
    }

}
