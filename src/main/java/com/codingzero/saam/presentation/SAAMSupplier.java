package com.codingzero.saam.presentation;

import com.codingzero.saam.app.SAAM;
import com.codingzero.saam.app.server.SAAMBuilder;
import com.codingzero.saam.app.server.base.mysql.MySQLAccessModule;
import com.codingzero.saam.app.server.base.sso.GoogleAgent;
import com.codingzero.saam.app.server.base.sso.OAuthPlatformAgentManager;
import com.codingzero.saam.app.server.base.sso.SlackAgent;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.infrastructure.database.spi.OAuthPlatformAgent;
import com.codingzero.utilities.transaction.TransactionManager;
import com.codingzero.utilities.transaction.manager.TransactionManagerImpl;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SAAMSupplier implements Supplier<SAAM> {

    private DataSource dataSource;
    private CloseableHttpClient httpClient;

    public SAAMSupplier(DataSource dataSource, CloseableHttpClient httpClient) {
        this.dataSource = dataSource;
        this.httpClient = httpClient;
    }

    @Override
    public SAAM get() {
        return getSAAMBuilder().build();
    }

    private SAAMBuilder getSAAMBuilder() {
        TransactionManager transactionManager = new TransactionManagerImpl();
        MySQLAccessModule accessModule = new MySQLAccessModule(dataSource);
        return new SAAMBuilder()
                .setTransactionManager(transactionManager)
                .setIdentifierPolicyAccess(accessModule.getIdentifierPolicyAccess())
                .setUsernamePolicyAccess(accessModule.getUsernamePolicyAccess())
                .setEmailPolicyAccess(accessModule.getEmailPolicyAccess())
                .setOAuthIdentifierPolicyAccess(accessModule.getOAuthIdentifierPolicyAccess())
                .setPrincipalAccess(accessModule.getPrincipalAccess())
                .setUserAccess(accessModule.getUserAccess())
                .setIdentifierAccess(accessModule.getIdentifierAccess())
                .setOAuthIdentifierAccess(accessModule.getOAuthIdentifierAccess())
                .setPermissionAccess(accessModule.getPermissionAccess())
                .setResourceAccess(accessModule.getResourceAccess())
                .setRoleAccess(accessModule.getRoleAccess())
                .setApiKeyAccess(accessModule.getAPIKeyAccess())
                .setApplicationAccess(accessModule.getApplicationAccess())
                .setUserSessionAccess(accessModule.getUserSessionAccess())
                .setOAuthPlatformAgent(getOAuthPlatformAgent());
    }

    private OAuthPlatformAgent getOAuthPlatformAgent() {
        Map<OAuthPlatform, OAuthPlatformAgent> agents = new HashMap<>();
        agents.put(OAuthPlatform.GOOGLE, new GoogleAgent(httpClient));
        agents.put(OAuthPlatform.SLACK, new SlackAgent(httpClient));
        return new OAuthPlatformAgentManager(agents);
    }
}
