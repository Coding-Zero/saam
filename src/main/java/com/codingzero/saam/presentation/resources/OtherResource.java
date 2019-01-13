package com.codingzero.saam.presentation.resources;

import com.codahale.metrics.annotation.Timed;
import com.codingzero.saam.app.requests.IdentifierVerificationCodeGenerateRequest;
import com.codingzero.saam.app.responses.IdentifierVerificationCodeResponse;
import com.codingzero.saam.app.requests.OAuthAccessTokenRequest;
import com.codingzero.saam.app.responses.OAuthAccessTokenResponse;
import com.codingzero.saam.app.requests.OAuthAuthorizationUrlRequest;
import com.codingzero.saam.app.requests.PasswordResetCodeGenerateRequest;
import com.codingzero.saam.app.responses.PasswordResetCodeResponse;
import com.codingzero.saam.app.SAAM;
import com.codingzero.saam.common.OAuthPlatform;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Path("/applications")
@Produces(MediaType.APPLICATION_JSON)
public class OtherResource extends AbstractResource {

    public OtherResource(Supplier<SAAM> appSupplier, ObjectMapper objectMapper) {
        super(appSupplier, objectMapper);
    }

    @POST
    @Path("/{id}/oauth-auth-url/{platform}")
    @Timed(name = "request-oauth-auth-url")
    public Response requestOAuthAuthUrl(@PathParam("id") String id,
                                        @PathParam("platform") OAuthPlatform platform,
                                        ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", id);
        requestBody.set("platform", getObjectMapper().valueToTree(platform));
        OAuthAuthorizationUrlRequest request = getObjectMapper().readValue(
                requestBody.toString(), OAuthAuthorizationUrlRequest.class);

        String url = getApp().requestOAuthAuthorizationUrl(request);
        Map<String, Object> response = new HashMap<>();
        response.put("applicationId", id);
        response.put("platform", request.getPlatform());
        response.put("parameters", request.getParameters());
        response.put("url", url);
        return created(response);
    }

    @POST
    @Path("/{id}/oauth-access-token/{platform}")
    @Timed(name = "request-oauth-access-token")
    public Response requestOAuthAccessToken(@PathParam("id") String id,
                                            @PathParam("platform") OAuthPlatform platform,
                                            ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", id);
        requestBody.set("platform", getObjectMapper().valueToTree(platform));
        OAuthAccessTokenRequest request = getObjectMapper().readValue(
                requestBody.toString(), OAuthAccessTokenRequest.class);
        OAuthAccessTokenResponse response = getApp().requestOAuthAccessToken(request);
        return created(response);
    }

    @POST
    @Path("/{id}/identifier-verification-codes")
    @Timed(name = "create-identifier-verification-code")
    public Response createVerification(@PathParam("id") String id,
                                       ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", id);
        IdentifierVerificationCodeGenerateRequest request = getObjectMapper().readValue(
                requestBody.toString(), IdentifierVerificationCodeGenerateRequest.class);
        IdentifierVerificationCodeResponse response = getApp().generateVerificationCode(request);
        return ok(response);
    }

    @POST
    @Path("/{id}/password-reset-codes")
    @Timed(name = "create-password-reset-code")
    public Response createPasswordResetCode(@PathParam("id") String id,
                                            ObjectNode requestBody) throws IOException {
        requestBody.put("applicationId", id);
        PasswordResetCodeGenerateRequest request = getObjectMapper().readValue(
                requestBody.toString(), PasswordResetCodeGenerateRequest.class);
        PasswordResetCodeResponse response = getApp().generateResetCode(request);
        return ok(response);
    }
}
