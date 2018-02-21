package com.codingzero.saam.presentation.resources;

import com.codahale.metrics.annotation.Timed;
import com.codingzero.saam.app.ResourceStoreRequest;
import com.codingzero.saam.app.ResourceResponse;
import com.codingzero.saam.app.SAAM;
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

@Path("/applications/{applicationId}/resources")
@Produces(MediaType.APPLICATION_JSON)
public class ResourceResource extends AbstractResource {

    public ResourceResource(Supplier<SAAM> appSupplier, ObjectMapper objectMapper) {
        super(appSupplier, objectMapper);
    }

    @PUT
    @Path("/{key}")
    @Timed(name = "add-resource")
    public Response register(@PathParam("applicationId") String applicationId,
                             @PathParam("key") String key,
                             ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", applicationId);
        requestBody.put("key", key);
        ResourceStoreRequest request = getObjectMapper().readValue(
                requestBody.toString(), ResourceStoreRequest.class);
        ResourceResponse response = getApp().storeResource(request);
        return created(response);
    }

    @DELETE
    @Path("/{key}")
    @Timed(name = "delete-resource")
    public Response delete(@PathParam("applicationId") String applicationId,
                           @PathParam("key") String key) {
        getApp().removeResource(applicationId, key);
        return noContent();
    }

    @GET
    @Path("/{key}")
    @Timed(name = "get-resource")
    public Response getByKey(@PathParam("applicationId") String applicationId,
                             @PathParam("key") String key) {
        ResourceResponse response = getApp().getResourceByKey(applicationId, key);
        return ok(response);
    }

    @GET
    @Path("/owner-id/{ownerId}")
    @Timed(name = "list-resource-by-owner-id")
    public Response listByUserId(@PathParam("applicationId") String applicationId,
                                 @PathParam("ownerId") String ownerId,
                                 @QueryParam("parentKey") String parentKey,
                                 @QueryParam("start") int start,
                                 @QueryParam("size") int size) {
        PaginatedResult<List<ResourceResponse>> result =
                getApp().getResourcesByOwnerId(applicationId, ownerId, parentKey);
        result = result.start(new OffsetBasedResultPage(start, size));
        List<ResourceResponse> response = result.getResult();
        return ok(response);
    }

    @GET
    @Path("/granted/{principalId}")
    @Timed(name = "list-resource-by-owner-id")
    public Response listGrantedResources(@PathParam("applicationId") String applicationId,
                                         @PathParam("principalId") String principalId,
                                         @QueryParam("start") int start,
                                         @QueryParam("size") int size) {
        PaginatedResult<List<ResourceResponse>> result =
                getApp().getGrantedResources(applicationId, principalId);
        result = result.start(new OffsetBasedResultPage(start, size));
        List<ResourceResponse> response = result.getResult();
        return ok(response);
    }

    @GET
    @Timed(name = "list-resources")
    public Response list(@PathParam("applicationId") String applicationId,
                         @QueryParam("parentKey") String parentKey,
                         @QueryParam("start") int start,
                         @QueryParam("size") int size) {
        PaginatedResult<List<ResourceResponse>> result = getApp().listResources(applicationId, parentKey);
        result = result.start(new OffsetBasedResultPage(start, size));
        List<ResourceResponse> response = result.getResult();
        return ok(response);
    }

}
