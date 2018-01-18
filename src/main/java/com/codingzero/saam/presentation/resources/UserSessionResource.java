package com.codingzero.saam.presentation.resources;

import com.codahale.metrics.annotation.Timed;
import com.codingzero.saam.app.APIKeyResponse;
import com.codingzero.saam.app.CredentialLoginRequest;
import com.codingzero.saam.app.CredentialRegisterRequest;
import com.codingzero.saam.app.IdentifierAssignRequest;
import com.codingzero.saam.app.IdentifierRemoveRequest;
import com.codingzero.saam.app.IdentifierVerifyRequest;
import com.codingzero.saam.app.OAuthIdentifierConnectRequest;
import com.codingzero.saam.app.OAuthIdentifierDisconnectRequest;
import com.codingzero.saam.app.OAuthIdentifierUpdateRequest;
import com.codingzero.saam.app.OAuthLoginRequest;
import com.codingzero.saam.app.OAuthRegisterRequest;
import com.codingzero.saam.app.PasswordChangeRequest;
import com.codingzero.saam.app.PasswordResetRequest;
import com.codingzero.saam.app.SAAM;
import com.codingzero.saam.app.UserResponse;
import com.codingzero.saam.app.UserRoleUpdateRequest;
import com.codingzero.saam.app.UserSessionCreateRequest;
import com.codingzero.saam.app.UserSessionResponse;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.utilities.pagination.OffsetBasedResultPage;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.dropwizard.jersey.PATCH;
import org.apache.commons.lang3.StringUtils;

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

@Path("/applications/{applicationId}/user-sessions")
@Produces(MediaType.APPLICATION_JSON)
public class UserSessionResource extends AbstractResource {

    public UserSessionResource(Supplier<SAAM> appSupplier, ObjectMapper objectMapper) {
        super(appSupplier, objectMapper);
    }

    @POST
    @Timed(name = "create-user-session")
    public Response register(@PathParam("applicationId") String applicationId,
                             @QueryParam("type") String type,
                             ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", applicationId);
        if (StringUtils.isEmpty(type)) {
            return create(requestBody);
        } else if (type.equalsIgnoreCase("CREDENTIAL-LOGIN")) {
            return loginWithCredential(requestBody);
        } else if (type.equalsIgnoreCase("OAUTH-LOGIN")) {
            return loginWithOAuth(requestBody);
        }
        throw new IllegalArgumentException("Unsupported type, " + type);
    }

    private Response loginWithCredential(ObjectNode requestBody) throws IOException {
        CredentialLoginRequest request = getObjectMapper().readValue(
                requestBody.toString(), CredentialLoginRequest.class);
        UserSessionResponse response = getApp().login(request);
        return created(response);
    }

    private Response loginWithOAuth(ObjectNode requestBody) throws IOException {
        OAuthLoginRequest request = getObjectMapper().readValue(
                requestBody.toString(), OAuthLoginRequest.class);
        UserSessionResponse response = getApp().login(request);
        return created(response);
    }

    private Response create(ObjectNode requestBody) throws IOException {
        UserSessionCreateRequest request = getObjectMapper().readValue(
                requestBody.toString(), UserSessionCreateRequest.class);
        UserSessionResponse response = getApp().createUserSession(request);
        return created(response);
    }

    @DELETE
    @Path("/{key}")
    @Timed(name = "delete-user-session")
    public Response delete(@PathParam("applicationId") String applicationId,
                           @PathParam("key") String key) {
        getApp().cleanUserSession(applicationId, key);
        return noContent();
    }

    @DELETE
    @Path("/_user-id/{userId}")
    @Timed(name = "delete-user-sessions")
    public Response deleteAll(@PathParam("applicationId") String applicationId,
                           @PathParam("userId") String userId) {
        getApp().cleanAllUserSessions(applicationId, userId);
        return noContent();
    }

    @GET
    @Path("/{key}")
    @Timed(name = "get-user-session")
    public Response getByKey(@PathParam("applicationId") String applicationId,
                             @PathParam("key") String key) {
        UserSessionResponse response = getApp().getUserSessionByKey(applicationId, key);
        return ok(response);
    }

    @GET
    @Path("/_user-id/{userId}")
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
