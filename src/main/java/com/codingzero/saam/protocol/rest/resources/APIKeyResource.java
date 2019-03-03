package com.codingzero.saam.protocol.rest.resources;

import com.codahale.metrics.annotation.Timed;
import com.codingzero.saam.app.requests.APIKeyAddRequest;
import com.codingzero.saam.app.responses.APIKeyResponse;
import com.codingzero.saam.app.requests.APIKeyUpdateRequest;
import com.codingzero.saam.app.SAAM;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

@Path("/applications/{applicationId}/api-keys")
@Produces(MediaType.APPLICATION_JSON)
public class APIKeyResource extends AbstractResource {

    public APIKeyResource(Supplier<SAAM> appSupplier, ObjectMapper objectMapper) {
        super(appSupplier, objectMapper);
    }

    @POST
    @Timed(name = "add-api-key")
    public Response register(@PathParam("applicationId") String applicationId,
                             ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", applicationId);
        APIKeyAddRequest request = getObjectMapper().readValue(
                requestBody.toString(), APIKeyAddRequest.class);
        APIKeyResponse response = getApp().addAPIKey(request);
        return created(response);
    }

    @PUT
    @Path("/{key}")
    @Timed(name = "update-api-key")
    public Response update(@PathParam("applicationId") String applicationId,
                           @PathParam("key") String key,
                           ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", applicationId);
        requestBody.put("key", key);
        APIKeyUpdateRequest request = getObjectMapper().readValue(
                requestBody.toString(), APIKeyUpdateRequest.class);
        APIKeyResponse response = getApp().updateAPIKey(request);
        return ok(response);
    }

    @DELETE
    @Path("/{key}")
    @Timed(name = "delete-api-key")
    public Response update(@PathParam("applicationId") String applicationId,
                           @PathParam("key") String key) {
        getApp().removeAPIKeyById(applicationId, key);
        return noContent();
    }

    @GET
    @Path("/{key}")
    @Timed(name = "get-api-key")
    public Response getByKey(@PathParam("applicationId") String applicationId,
                             @PathParam("key") String key) {
        APIKeyResponse response = getApp().getAPIKeyById(applicationId, key);
        return ok(response);
    }

    @GET
    @Path("/user-id/{userId}")
    @Timed(name = "list-api-keys-by-user-id")
    public Response listByUserId(@PathParam("applicationId") String applicationId,
                                 @PathParam("userId") String userId) {
        List<APIKeyResponse> response = getApp().listAPIKeysByApplicationIdAndUserId(applicationId, userId);
        return ok(response);
    }

}
