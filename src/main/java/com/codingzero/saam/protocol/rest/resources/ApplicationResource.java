package com.codingzero.saam.protocol.rest.resources;

import com.codahale.metrics.annotation.Timed;
import com.codingzero.saam.app.requests.ApplicationAddRequest;
import com.codingzero.saam.app.responses.ApplicationResponse;
import com.codingzero.saam.app.requests.ApplicationUpdateRequest;
import com.codingzero.saam.app.requests.EmailPolicyAddRequest;
import com.codingzero.saam.app.requests.EmailPolicyUpdateRequest;
import com.codingzero.saam.app.requests.OAuthIdentifierPolicyAddRequest;
import com.codingzero.saam.app.requests.OAuthIdentifierPolicyUpdateRequest;
import com.codingzero.saam.app.requests.PasswordPolicySetRequest;
import com.codingzero.saam.app.SAAM;
import com.codingzero.saam.app.requests.UsernamePolicyAddRequest;
import com.codingzero.saam.app.requests.UsernamePolicyUpdateRequest;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.OAuthPlatform;
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

@Path("/applications")
@Produces(MediaType.APPLICATION_JSON)
public class ApplicationResource extends AbstractResource {

    public ApplicationResource(Supplier<SAAM> appSupplier, ObjectMapper objectMapper) {
        super(appSupplier, objectMapper);
    }

    @POST
    @Timed(name = "add-application")
    public Response create(ApplicationAddRequest request) {
        ApplicationResponse response = getApp().addApplication(request);
        return created(response);
    }

    @PUT
    @Path("/{id}")
    @Timed(name = "update-application")
    public Response update(@PathParam("id") String id, ObjectNode requestBody) throws IOException {
        requestBody.put("id", id);
        ApplicationUpdateRequest request = getObjectMapper().readValue(
                requestBody.toString(), ApplicationUpdateRequest.class);
        ApplicationResponse response = getApp().updateApplication(request);
        return ok(response);
    }

    @PUT
    @Path("/{id}/password-policy")
    @Timed(name = "update-password-policy")
    public Response updatePasswordPolicy(@PathParam("id") String id, ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", id);
        PasswordPolicySetRequest request = getObjectMapper().readValue(
                requestBody.toString(), PasswordPolicySetRequest.class);
        ApplicationResponse response = getApp().setPasswordPolicy(request);
        return ok(response);
    }

    @DELETE
    @Path("/{id}")
    @Timed(name = "remove-application")
    public Response delete(@PathParam("id") String id) {
        getApp().removeApplication(id);
        return noContent();
    }

    @GET
    @Path("/{id}")
    @Timed(name = "get-application")
    public Response getById(@PathParam("id") String id) {
        return Response.ok(getApp().getApplicationById(id)).build();
    }

    @GET
    @Timed(name = "list-applications")
    @Auth
    public Response list(@QueryParam("start") int start, @QueryParam("size") int size) {
        PaginatedResult<List<ApplicationResponse>> result = getApp().listApplications();
        result = result.start(new OffsetBasedResultPage(start, size));
        List<ApplicationResponse> response = result.getResult();
        return ok(response);
    }

    @POST
    @Path("/{id}/identifier-policies/{type}")
    @Timed(name = "create-identifier-policy")
    public Response createIdentifierPolicy(@PathParam("id") String id,
                                           @PathParam("type") String type,
                                           ObjectNode requestBody) throws IOException {
        if (IdentifierType.USERNAME.name().equalsIgnoreCase(type)) {
            return createUsernamePolicy(id, requestBody);
        }
        if (IdentifierType.EMAIL.name().equalsIgnoreCase(type)) {
            return createEmailPolicy(id, requestBody);
        }
        throw new IllegalArgumentException("Unsupported type, " + type);
    }

    private Response createUsernamePolicy(String applicationId, ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", applicationId);
        UsernamePolicyAddRequest request = getObjectMapper().readValue(
                requestBody.toString(), UsernamePolicyAddRequest.class);
        ApplicationResponse response = getApp().addUsernamePolicy(request);
        return created(response);
    }

    private Response createEmailPolicy(String applicationId, ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", applicationId);
        EmailPolicyAddRequest request = getObjectMapper().readValue(
                requestBody.toString(), EmailPolicyAddRequest.class);
        ApplicationResponse response = getApp().addEmailPolicy(request);
        return created(response);
    }

    @PUT
    @Path("/{id}/identifier-policies/{type}")
    @Timed(name = "update-identifier-policy")
    public Response updateIdentifierPolicy(@PathParam("id") String id,
                                           @PathParam("type") String type,
                                           ObjectNode requestBody) throws IOException {
        if (IdentifierType.USERNAME.name().equalsIgnoreCase(type)) {
            return updateUsernamePolicy(id, requestBody);
        }
        if (IdentifierType.EMAIL.name().equalsIgnoreCase(type)) {
            return updateEmailPolicy(id, requestBody);
        }
        throw new IllegalArgumentException("Unsupported type, " + type);
    }

    private Response updateUsernamePolicy(String applicationId, ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", applicationId);
        UsernamePolicyUpdateRequest request = getObjectMapper().readValue(
                requestBody.toString(), UsernamePolicyUpdateRequest.class);
        ApplicationResponse response = getApp().updateUsernamePolicy(request);
        return ok(response);
    }

    private Response updateEmailPolicy(String applicationId, ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", applicationId);
        EmailPolicyUpdateRequest request = getObjectMapper().readValue(
                requestBody.toString(), EmailPolicyUpdateRequest.class);
        ApplicationResponse response = getApp().updateEmailPolicy(request);
        return ok(response);
    }

    @DELETE
    @Path("/{id}/identifier-policies/{type}")
    @Timed(name = "delete-identifier-policy")
    public Response deleteIdentifierPolicy(@PathParam("id") String id,
                                           @PathParam("type") String type) {
        ApplicationResponse response = getApp().removeIdentifierPolicy(id, IdentifierType.valueOf(type));
        return ok(response);
    }

    @POST
    @Path("/{id}/oauth-identifier-policies/{platform}")
    @Timed(name = "create-oauth-identifier-policy")
    public Response createOAuthIdentifierPolicy(@PathParam("id") String id,
                                                @PathParam("platform") OAuthPlatform platform,
                                                ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", id);
        requestBody.set("platform", getObjectMapper().valueToTree(platform));
        OAuthIdentifierPolicyAddRequest request = getObjectMapper().readValue(
                requestBody.toString(), OAuthIdentifierPolicyAddRequest.class);
        ApplicationResponse response = getApp().addOAuthIdentifierPolicy(request);
        return created(response);
    }

    @PUT
    @Path("/{id}/oauth-identifier-policies/{platform}")
    @Timed(name = "update-oauth-identifier-policy")
    public Response updateOAuthIdentifierPolicy(@PathParam("id") String id,
                                                @PathParam("platform") OAuthPlatform platform,
                                                ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", id);
        requestBody.set("platform", getObjectMapper().valueToTree(platform));
        OAuthIdentifierPolicyUpdateRequest request = getObjectMapper().readValue(
                requestBody.toString(), OAuthIdentifierPolicyUpdateRequest.class);
        ApplicationResponse response = getApp().updateOAuthIdentifierPolicy(request);
        return ok(response);
    }

    @DELETE
    @Path("/{id}/oauth-identifier-policies/{platform}")
    @Timed(name = "delete-oauth-identifier-policy")
    public Response deleteOAuthIdentifierPolicy(@PathParam("id") String id,
                                                @PathParam("platform") OAuthPlatform platform) {
        ApplicationResponse response = getApp().removeOAuthIdentifierPolicy(id, platform);
        return ok(response);
    }


}
