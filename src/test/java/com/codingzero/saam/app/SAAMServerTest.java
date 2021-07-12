package com.codingzero.saam.app;

import com.codingzero.saam.app.server.infrastructure.oauth.OAuthPlatformAgentManager;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.infrastructure.data.mysql.Helper;
import com.codingzero.saam.infrastructure.oauth.OAuthAccessToken;
import com.codingzero.saam.infrastructure.oauth.OAuthPlatformAgent;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SAAMServerTest extends SAAMTest {

    private static SAAM saam;

    @BeforeClass
    public static void beforeClass() {
        Helper.sharedInstance().init();
    }

    @AfterClass
    public static void afterClass() {
        Helper.sharedInstance().clean();
    }

    @Override
    protected SAAM getSAAM() {
        if (null == saam) {
            Map<OAuthPlatform, OAuthPlatformAgent> agents = new HashMap<>();
            agents.put(OAuthPlatform.GOOGLE, new OAuthPlatformAgentDouble());
            saam = Helper.sharedInstance().getSAAM(new OAuthPlatformAgentManager(agents));
        }
        return saam;
    }

    private static class OAuthPlatformAgentDouble implements OAuthPlatformAgent {

        @Override
        public String getAuthorizationUrl(Map<String, Object> configurations,
                                          Map<String, Object> parameters) {
            return "https://www.codingzero.com/saam/int-test/oauth-url/" + OAuthPlatform.GOOGLE.name();
        }

        @Override
        public OAuthAccessToken requestAccessToken(Map<String, Object> configurations,
                                                   Map<String, Object> parameters) {
            return new OAuthAccessToken(OAuthPlatform.GOOGLE,
                    OAuthPlatform.GOOGLE.name().toLowerCase() + "-account",
                    OAuthPlatform.GOOGLE.name().toLowerCase() + "-token",
                    new Date(),
                    new Date(System.currentTimeMillis() + 1000));
        }
    }
}
