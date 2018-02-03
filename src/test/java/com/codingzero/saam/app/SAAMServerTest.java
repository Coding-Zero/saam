package com.codingzero.saam.app;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.infrastructure.OAuthAccessToken;
import com.codingzero.saam.infrastructure.database.mysql.Helper;
import com.codingzero.saam.infrastructure.database.spi.OAuthPlatformAgent;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.util.Date;
import java.util.Map;

public class SAAMServerTest extends SAAMTest {

    private SAAM saam;

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
            saam = Helper.sharedInstance().getSAAM(new OAuthPlatformAgentDouble());
        }
        return saam;
    }

    private static class OAuthPlatformAgentDouble implements OAuthPlatformAgent {

        @Override
        public String getAuthorizationUrl(OAuthPlatform platform,
                                          Map<String, Object> configurations,
                                          Map<String, Object> parameters) {
            return "https://www.codingzero.com/saam/int-test/oauth-url/" + platform.name();
        }

        @Override
        public OAuthAccessToken requestAccessToken(OAuthPlatform platform,
                                                   Map<String, Object> configurations,
                                                   Map<String, Object> parameters) {
            return new OAuthAccessToken(platform,
                    platform.name().toLowerCase() + "-account",
                    platform.name().toLowerCase() + "-token",
                    new Date(),
                    new Date(System.currentTimeMillis() + 1000));
        }
    }
}
