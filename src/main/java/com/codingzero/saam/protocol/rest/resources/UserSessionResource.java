package com.codingzero.saam.protocol.rest.resources;

import com.codahale.metrics.annotation.Timed;
import com.codingzero.saam.app.SAAM;
import com.codingzero.saam.app.requests.CredentialLoginRequest;
import com.codingzero.saam.app.requests.OAuthLoginRequest;
import com.codingzero.saam.app.requests.UserSessionCreateRequest;
import com.codingzero.saam.app.requests.UserSessionLoginRequest;
import com.codingzero.saam.app.responses.UserSessionResponse;
import com.codingzero.saam.protocol.rest.auth.Auth;
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

@Auth("APIKEY")
@Path("/applications/{applicationId}/user-sessions")
@Produces(MediaType.APPLICATION_JSON)
public class UserSessionResource extends AbstractResource {

    public UserSessionResource(Supplier<SAAM> appSupplier, ObjectMapper objectMapper) {
        super(appSupplier, objectMapper);
    }

    @POST
    @Timed(name = "create-user-session")
    public Response createUserSession(@PathParam("applicationId") String applicationId,
                                      ObjectNode requestBody,
                                      @QueryParam("type") String type) throws IOException {
        requestBody.put("applicationId", applicationId);
        UserSessionResponse response;
        if ("Credential".equalsIgnoreCase(type)) {
            CredentialLoginRequest request = getObjectMapper().readValue(
                    requestBody.toString(), CredentialLoginRequest.class);
            response = getApp().login(request);
        } else if ("OAuth".equalsIgnoreCase(type)) {
            OAuthLoginRequest request = getObjectMapper().readValue(
                    requestBody.toString(), OAuthLoginRequest.class);
            response = getApp().login(request);
        } else {
            UserSessionCreateRequest request = getObjectMapper().readValue(
                    requestBody.toString(), UserSessionCreateRequest.class);
            response = getApp().createUserSession(request);
        }
        return created(response);
    }

    @PUT
    @Path("/{key}")
    @Timed(name = "session-login")
    public Response loginWithSession(@PathParam("applicationId") String applicationId,
                                   ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", applicationId);
        UserSessionLoginRequest request = getObjectMapper().readValue(
                requestBody.toString(), UserSessionLoginRequest.class);
        UserSessionResponse response = getApp().login(request);
        return ok(response);
    }

    @DELETE
    @Path("/{key}")
    @Timed(name = "delete-user-usersession")
    public Response delete(@PathParam("applicationId") String applicationId,
                           @PathParam("key") String key) {
        getApp().removeUserSessionByKey(applicationId, key);
        return noContent();
    }

    @DELETE
    @Path("/user-id/{userId}")
    @Timed(name = "delete-user-sessions")
    public Response deleteAll(@PathParam("applicationId") String applicationId,
                           @PathParam("userId") String userId) {
        getApp().removeUserSessionsByUserId(applicationId, userId);
        return noContent();
    }

    @GET
    @Path("/{key}")
    @Timed(name = "get-user-usersession")
    public Response getByKey(@PathParam("applicationId") String applicationId,
                             @PathParam("key") String key) {
        UserSessionResponse response = getApp().getUserSessionByKey(applicationId, key);
        return ok(response);
    }

    @GET
    @Path("/user-id/{userId}")
    @Timed(name = "list-api-keys-by-user-id")
    public Response listByUserId(@PathParam("applicationId") String applicationId,
                                 @PathParam("userId") String userId,
                                 @QueryParam("start") int start,
                                 @QueryParam("size") int size) {
        PaginatedResult<List<UserSessionResponse>> result = getApp().listUserSessionsByUserId(applicationId, userId);
        result = result.start(new OffsetBasedResultPage(start, size));
        List<UserSessionResponse> response = result.getResult();
        return ok(response);
    }

}
