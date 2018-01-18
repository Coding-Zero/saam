package com.codingzero.saam.presentation.resources;

import com.codingzero.saam.app.SAAM;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AbstractResource {

    private final Supplier<SAAM> appSupplier;
    private final ObjectMapper objectMapper;

    public AbstractResource(Supplier<SAAM> appSupplier, ObjectMapper objectMapper) {
        this.appSupplier = appSupplier;
        this.objectMapper = objectMapper;
    }

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    protected Response created(Object result) {
        Map<String, Object> response = new LinkedHashMap<>(1);
        response.put("result", result);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    protected Response ok(Object result) {
        Map<String, Object> response = new LinkedHashMap<>(1);
        response.put("result", result);
        return Response.ok(response).build();
    }

    protected Response noContent() {
        return Response.noContent().build();
    }

    protected SAAM getApp() {
        return appSupplier.get();
    }

}
