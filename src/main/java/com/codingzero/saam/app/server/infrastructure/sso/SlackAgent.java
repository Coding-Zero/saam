package com.codingzero.saam.app.server.infrastructure.sso;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.infrastructure.OAuthAccessToken;
import com.codingzero.saam.infrastructure.data.OAuthPlatformAgent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class SlackAgent implements OAuthPlatformAgent {

    private static final String ENDPOINT_GET_TEAM_INFO = "https://slack.com/api/auth.test";
    private static final Long TIME_OUT = 1000L * 60L * 60L * 24L * 90L; //90 days

    private final OAuth20ServiceFactory oAuth20ServiceFactory;
    private final CloseableHttpClient client;
    private final ObjectMapper objectMapper;
    private final OAuthPlatformAgentHelper helper;

    public SlackAgent(CloseableHttpClient client) {
        this(new OAuth20ServiceFactory(), client, new ObjectMapper(), new OAuthPlatformAgentHelper());
    }

    public SlackAgent(OAuth20ServiceFactory oAuth20ServiceFactory,
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
    public OAuthAccessToken requestAccessToken(
            OAuthPlatform platform, Map<String, Object> configurations, Map<String, Object> parameters) {
        configurations = helper.prepareConfiguration(configurations, parameters);
        OAuth20Service service = oAuth20ServiceFactory.generate(platform, configurations);
        try {
            String token = (String) parameters.get("accessToken");
            if (null == token){
                String code = (String) parameters.get("code");
                OAuth2AccessToken accessToken = service.getAccessToken(code);
                String accountId = readAccountId(accessToken.getAccessToken());
                Date expirationTime = new Date(System.currentTimeMillis() + TIME_OUT);
                if (null !=accessToken.getExpiresIn()) {
                    expirationTime = new Date(System.currentTimeMillis() + accessToken.getExpiresIn() * 1000);
                }
                return new OAuthAccessToken(platform, accountId, accessToken.getAccessToken(), new Date(), expirationTime);
            } else {
                String accountId = readAccountId(token);
                long timeout = System.currentTimeMillis() + TIME_OUT;
                Date expirationTime = new Date(timeout);
                return new OAuthAccessToken(platform, accountId, token, new Date(), expirationTime);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get account id by assemble teamId and userId, "teamId__userId"
     *
     * @param accessToken
     * @return String
     */
    private String readAccountId(String accessToken) {
        String url = ENDPOINT_GET_TEAM_INFO + "?token=" + accessToken;
        CloseableHttpResponse response = null;
        try {
            HttpPost request = new HttpPost(url);
            request.addHeader("Content-Type", "application/x-www-form-urlencoded");
            List<NameValuePair> params = new ArrayList<>();
            HttpEntity entity = new UrlEncodedFormEntity(params, Consts.UTF_8);
            request.setEntity(entity);
            response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed to get team info");
            }
            JsonNode result = objectMapper.readTree(response.getEntity().getContent());
            Boolean isSuccess = result.get("ok").asBoolean();
            if (!isSuccess) {
                throw new RuntimeException("Failed to get team info");
            }
            String teamId = result.get("team_id").asText();
            String userId = result.get("user_id").asText();
            return teamId + "_" + userId;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeResponse(response);
        }
    }

    private void closeResponse(CloseableHttpResponse response) {
        if (null == response) {
            return;
        }
        try {
            response.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
