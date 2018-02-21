package com.codingzero.saam.presentation.resources;

import com.codahale.metrics.annotation.Timed;
import com.codingzero.saam.app.IdentifierAddRequest;
import com.codingzero.saam.app.IdentifierRemoveRequest;
import com.codingzero.saam.app.IdentifierVerifyRequest;
import com.codingzero.saam.app.OAuthIdentifierConnectRequest;
import com.codingzero.saam.app.OAuthIdentifierDisconnectRequest;
import com.codingzero.saam.app.PasswordChangeRequest;
import com.codingzero.saam.app.PasswordResetCodeGenerateRequest;
import com.codingzero.saam.app.PasswordResetCodeResponse;
import com.codingzero.saam.app.PasswordResetRequest;
import com.codingzero.saam.app.SAAM;
import com.codingzero.saam.app.UserRegisterRequest;
import com.codingzero.saam.app.UserResponse;
import com.codingzero.saam.app.UserRoleUpdateRequest;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.utilities.pagination.OffsetBasedResultPage;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.dropwizard.jersey.PATCH;

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

@Path("/applications/{applicationId}/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource extends AbstractResource {

    public UserResource(Supplier<SAAM> appSupplier, ObjectMapper objectMapper) {
        super(appSupplier, objectMapper);
    }

    @POST
    @Timed(name = "user-register")
    public Response register(@PathParam("applicationId") String applicationId,
                             ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", applicationId);
        UserRegisterRequest request = getObjectMapper().readValue(
                requestBody.toString(), UserRegisterRequest.class);
        UserResponse response = getApp().register(request);
        return created(response);
    }

    @DELETE
    @Path("/{id}")
    @Timed(name = "delete-user")
    public Response delete(@PathParam("applicationId") String applicationId,
                                           @PathParam("id") String id) {
        getApp().removeUser(applicationId, id);
        return noContent();
    }

    @GET
    @Path("/{id}")
    @Timed(name = "get-user")
    public Response getById(@PathParam("applicationId") String applicationId,
                            @PathParam("id") String id,
                            @QueryParam("type") String type) {
        if (type.equalsIgnoreCase("UserId")) {
            return getUserById(applicationId, id);
        } else if (type.equalsIgnoreCase("Identifier")) {
            return getUserByIdentifier(applicationId, id);
        } else if (type.equalsIgnoreCase("OAuth")) {
            return getUserByOAuthIdentifier(applicationId, id);
        }
        throw new IllegalArgumentException("Unsupported type, " + type);
    }

    private Response getUserById(String applicationId, String id) {
        UserResponse response = getApp().getUserById(applicationId, id);
        return ok(response);
    }

    private Response getUserByIdentifier(String applicationId, String identifier) {
        UserResponse response = getApp().getUserByIdentifier(applicationId, identifier);
        return ok(response);
    }

    private Response getUserByOAuthIdentifier(String applicationId, String identifier) {
        String[] segments = identifier.split(":");
        OAuthPlatform platform = OAuthPlatform.valueOf(segments[0].toUpperCase());
        String content = segments[1];
        UserResponse response = getApp().getUserByOAuthIdentifier(applicationId, platform, content);
        return ok(response);
    }

    @GET
    @Timed(name = "list-users")
    public Response list(@PathParam("applicationId") String applicationId,
                         @QueryParam("start") int start,
                         @QueryParam("size") int size) {
        PaginatedResult<List<UserResponse>> result = getApp().listUsersByApplicationId(applicationId);
        result = result.start(new OffsetBasedResultPage(start, size));
        List<UserResponse> response = result.getResult();
        return ok(response);
    }

    @PATCH
    @Path("/{id}/roles")
    @Timed(name = "update-user-roles")
    public Response updateRoles(@PathParam("applicationId") String applicationId,
                                @PathParam("id") String id,
                                ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", applicationId);
        requestBody.put("userId", id);
        UserRoleUpdateRequest request = getObjectMapper().readValue(
                requestBody.toString(), UserRoleUpdateRequest.class);
        UserResponse response = getApp().updateRoles(request);
        return ok(response);
    }

    @PATCH
    @Path("/{id}/password")
    @Timed(name = "change-user-password")
    public Response changePassword(@PathParam("applicationId") String applicationId,
                                   @PathParam("id") String id,
                                   @QueryParam("reset") Boolean isReset,
                                   ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", applicationId);
        requestBody.put("userId", id);
        UserResponse response;
        if (null == isReset || !isReset) {
            PasswordChangeRequest request = getObjectMapper().readValue(
                    requestBody.toString(), PasswordChangeRequest.class);
            response = getApp().changePassword(request);
        } else {
            PasswordResetRequest request = getObjectMapper().readValue(
                    requestBody.toString(), PasswordResetRequest.class);
            response = getApp().resetPassword(request);
        }
        return ok(response);
    }

    @POST
    @Path("/{id}/identifiers/{type}")
    @Timed(name = "add-identifier")
    public Response createIdentifier(@PathParam("applicationId") String applicationId,
                                     @PathParam("id") String id,
                                     @PathParam("type") String type,
                                     ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", applicationId);
        requestBody.put("userId", id);
        requestBody.put("type", type);
        IdentifierAddRequest request = getObjectMapper().readValue(
                requestBody.toString(), IdentifierAddRequest.class);
        UserResponse response = getApp().addIdentifier(request);
        return ok(response);
    }

    @DELETE
    @Path("/{id}/identifiers/{type}/{content}")
    @Timed(name = "remove-identifier")
    public Response removeIdentifier(@PathParam("applicationId") String applicationId,
                                     @PathParam("id") String id,
                                     @PathParam("type") IdentifierType type,
                                     @PathParam("content") String content) {
        IdentifierRemoveRequest request =
                new IdentifierRemoveRequest(applicationId, id, type, content);
        UserResponse response = getApp().removeIdentifier(request);
        return ok(response);
    }

    @PATCH
    @Path("/{id}/identifiers/{type}/{content}/verified")
    @Timed(name = "verify-identifier")
    public Response verifyIdentifier(@PathParam("applicationId") String applicationId,
                                     @PathParam("id") String id,
                                     @PathParam("type") IdentifierType type,
                                     @PathParam("content") String content,
                                     ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", applicationId);
        requestBody.put("userId", id);
        requestBody.put("identifierType", type.name());
        requestBody.put("identifier", content);
        IdentifierVerifyRequest request = getObjectMapper().readValue(
                requestBody.toString(), IdentifierVerifyRequest.class);
        UserResponse response = getApp().verifyIdentifier(request);
        return ok(response);
    }

    @PATCH
    @Path("/{id}/oauth-identifiers/{platform}/{identifier}")
    @Timed(name = "connect-oauth-identifier")
    public Response updateOAuthIdentifier(@PathParam("applicationId") String applicationId,
                                          @PathParam("id") String id,
                                          @PathParam("platform") OAuthPlatform platform,
                                          @PathParam("identifier") String identifier,
                                          ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", applicationId);
        requestBody.put("userId", id);
        requestBody.set("platform", getObjectMapper().valueToTree(platform));
        requestBody.put("identifier", identifier);
        OAuthIdentifierConnectRequest request = getObjectMapper().readValue(
                requestBody.toString(), OAuthIdentifierConnectRequest.class);
        UserResponse response = getApp().connectOAuthIdentifier(request);
        return ok(response);
    }

    @DELETE
    @Path("/{id}/oauth-identifiers/{platform}/{content}")
    @Timed(name = "delete-oauth-identifier")
    public Response deleteOAuthIdentifier(@PathParam("applicationId") String applicationId,
                                          @PathParam("id") String id,
                                          @PathParam("platform") OAuthPlatform platform,
                                          @PathParam("content") String content) {
        OAuthIdentifierDisconnectRequest request =
                new OAuthIdentifierDisconnectRequest(applicationId, id, platform, content);
        UserResponse response = getApp().disconnectOAuthIdentifier(request);
        return ok(response);
    }

}
