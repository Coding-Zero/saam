package com.codingzero.saam.protocol.rest.resources;

import com.codahale.metrics.annotation.Timed;
import com.codingzero.saam.app.requests.PermissionCheckRequest;
import com.codingzero.saam.app.responses.PermissionCheckResponse;
import com.codingzero.saam.app.responses.PermissionResponse;
import com.codingzero.saam.app.requests.PermissionStoreRequest;
import com.codingzero.saam.app.SAAM;
import com.codingzero.saam.protocol.rest.auth.Auth;
import com.codingzero.utilities.pagination.OffsetBasedResultPage;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

@Auth("APIKEY")
@Path("/applications/{applicationId}/resources/{resourceKey}/permissions")
@Produces(MediaType.APPLICATION_JSON)
public class PermissionResource extends AbstractResource {

    public PermissionResource(Supplier<SAAM> appSupplier, ObjectMapper objectMapper) {
        super(appSupplier, objectMapper);
    }

    @PUT
    @Path("/{principalId}")
    @Timed(name = "store-permission")
    public Response register(@PathParam("applicationId") String applicationId,
                             @PathParam("resourceKey") String resourceKey,
                             @PathParam("principalId") String principalId,
                             ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", applicationId);
        requestBody.put("resourceKey", resourceKey);
        requestBody.put("principalId", principalId);
        PermissionStoreRequest request = getObjectMapper().readValue(
                requestBody.toString(), PermissionStoreRequest.class);
        PermissionResponse response = getApp().storePermission(request);
        return created(response);
    }

    @DELETE
    @Path("/{principalId}")
    @Timed(name = "delete-permission")
    public Response delete(@PathParam("applicationId") String applicationId,
                           @PathParam("resourceKey") String resourceKey,
                           @PathParam("principalId") String principalId) {
        getApp().removePermission(applicationId, resourceKey, principalId);
        return noContent();
    }

    @GET
    @Path("/{principalId}")
    @Timed(name = "get-permission")
    public Response getByKey(@PathParam("applicationId") String applicationId,
                             @PathParam("resourceKey") String resourceKey,
                             @PathParam("principalId") String principalId) {
        PermissionResponse response = getApp().getPermissionByPrincipalId(applicationId, resourceKey, principalId);
        return ok(response);
    }

    @GET
    @Timed(name = "list-permissions")
    public Response list(@PathParam("applicationId") String applicationId,
                         @PathParam("resourceKey") String resourceKey,
                         @QueryParam("start") int start,
                         @QueryParam("size") int size) {
        PaginatedResult<List<PermissionResponse>> result =
                getApp().listPermissions(applicationId, resourceKey);
        result = result.start(new OffsetBasedResultPage(start, size));
        List<PermissionResponse> response = result.getResult();
        return ok(response);
    }

    @GET
    @Path("/{principalId}/_check/{actionCode}")
    @Timed(name = "check-permission")
    public Response checkPermission(@PathParam("applicationId") String applicationId,
                                    @PathParam("resourceKey") String resourceKey,
                                    @PathParam("principalId") String principalId,
                                    @PathParam("actionCode") String actionCode) {
        PermissionCheckRequest request =
                new PermissionCheckRequest(applicationId, resourceKey, principalId, actionCode);
        PermissionCheckResponse response = getApp().checkPermission(request);
        return ok(response);
    }

}
