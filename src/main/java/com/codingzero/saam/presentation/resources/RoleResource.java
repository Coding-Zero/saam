package com.codingzero.saam.presentation.resources;

import com.codahale.metrics.annotation.Timed;
import com.codingzero.saam.app.requests.RoleAddRequest;
import com.codingzero.saam.app.responses.RoleResponse;
import com.codingzero.saam.app.requests.RoleUpdateRequest;
import com.codingzero.saam.app.SAAM;
import com.codingzero.utilities.pagination.OffsetBasedResultPage;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

@Path("/applications/{applicationId}/roles")
@Produces(MediaType.APPLICATION_JSON)
public class RoleResource extends AbstractResource {

    public RoleResource(Supplier<SAAM> appSupplier, ObjectMapper objectMapper) {
        super(appSupplier, objectMapper);
    }

    @POST
    @Timed(name = "create-role")
    public Response register(@PathParam("applicationId") String applicationId,
                             ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", applicationId);
        RoleAddRequest request = getObjectMapper().readValue(
                requestBody.toString(), RoleAddRequest.class);
        RoleResponse response = getApp().addRole(request);
        return created(response);
    }

    @PUT
    @Path("/{id}")
    @Timed(name = "update-role")
    public Response update(@PathParam("applicationId") String applicationId,
                           @PathParam("id") String id,
                           ObjectNode requestBody)  throws IOException {
        requestBody.put("applicationId", applicationId);
        requestBody.put("id", id);
        RoleUpdateRequest request = getObjectMapper().readValue(
                requestBody.toString(), RoleUpdateRequest.class);
        RoleResponse response = getApp().updateRole(request);
        return ok(response);
    }

    @DELETE
    @Path("/{id}")
    @Timed(name = "delete-role")
    public Response delete(@PathParam("applicationId") String applicationId,
                           @PathParam("id") String id) {
        getApp().removeRole(applicationId, id);
        return noContent();
    }

    @GET
    @Path("/{id}")
    @Timed(name = "get-user-usersession")
    public Response getById(@PathParam("applicationId") String applicationId,
                             @PathParam("id") String id) {
        RoleResponse response = getApp().getRoleById(applicationId, id);
        return ok(response);
    }

    @GET
    @Timed(name = "list-roles")
    public Response listByUserId(@PathParam("applicationId") String applicationId,
                                 @QueryParam("start") int start,
                                 @QueryParam("size") int size) {
        PaginatedResult<List<RoleResponse>> result = getApp().listRoles(applicationId);
        result = result.start(new OffsetBasedResultPage(start, size));
        List<RoleResponse> response = result.getResult();
        return ok(response);
    }

}
