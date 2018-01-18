package com.codingzero.saam.app.server.base.sso;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.infrastructure.SSOAccessToken;
import com.codingzero.saam.infrastructure.database.spi.OAuthPlatformAgent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.Date;
import java.util.Map;


public class GoogleAgent implements OAuthPlatformAgent {

    private static final String ENDPOINT_GOOGLE_PLUS_GET_PEOPLE = "https://www.googleapis.com/plus/v1/people/me";
    private static final Long TIME_OUT = 1000L * 60L * 60L * 24L * 90L; //90 days

    private final OAuth20ServiceFactory oAuth20ServiceFactory;
    private final CloseableHttpClient client;
    private final ObjectMapper objectMapper;
    private final OAuthPlatformAgentHelper helper;

    public GoogleAgent(CloseableHttpClient client) {
        this(new OAuth20ServiceFactory(), client, new ObjectMapper(), new OAuthPlatformAgentHelper());
    }

    public GoogleAgent(OAuth20ServiceFactory oAuth20ServiceFactory,
                       CloseableHttpClient client,
                       ObjectMapper objectMapper,
                       OAuthPlatformAgentHelper helper) {
        this.oAuth20ServiceFactory = oAuth20ServiceFactory;
        this.client = client;
        this.objectMapper = objectMapper;
        this.helper = helper;
    }

    @Override
    public String getAuthorizationUrl(OAuthPlatform platform,
                                      Map<String, Object> configurations,
                                      Map<String, Object> parameters) {
        configurations = helper.prepareConfiguration(configurations, parameters);
        Map<String, String> newParameters = helper.prepareParametersForAuthUrl(parameters);
        OAuth20Service service = oAuth20ServiceFactory.generate(platform, configurations);
        return service.getAuthorizationUrl(newParameters);
    }



    @Override
    public SSOAccessToken requestAccessToken(
            OAuthPlatform platform, Map<String, Object> configurations, Map<String, Object> parameters) {
        configurations = helper.prepareConfiguration(configurations, parameters);
        OAuth20Service service = oAuth20ServiceFactory.generate(platform, configurations);
        try {
            String token = (String) parameters.get("accessToken");
            if (null == token){
                String code = (String) parameters.get("code");
                OAuth2AccessToken accessToken = service.getAccessToken(code);
                String userId = readGoogleUserId(accessToken.getAccessToken());
                Date expirationTime = new Date(System.currentTimeMillis() + accessToken.getExpiresIn() * 1000);
                return new SSOAccessToken(platform, userId, accessToken.getAccessToken(), new Date(), expirationTime);
            } else {
                String userId = readGoogleUserId(token);
                long timeout = System.currentTimeMillis() + TIME_OUT;
                Date expirationTime = new Date(timeout);
                return new SSOAccessToken(platform, userId,token, new Date(), expirationTime);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readGoogleUserId(String accessToken) {
        CloseableHttpResponse response = null;
        try {
            HttpGet request = new HttpGet(ENDPOINT_GOOGLE_PLUS_GET_PEOPLE);
            request.addHeader("Authorization", "Bearer " + accessToken);
            response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed to get people's profile");
            }
            Map<String, Object> result = objectMapper.readValue(
                    response.getEntity().getContent(), new TypeReference<Map<String, Object>>() {});
            return (String) result.get("id");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
