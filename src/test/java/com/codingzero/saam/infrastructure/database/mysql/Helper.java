package com.codingzero.saam.infrastructure.database.mysql;

import com.codingzero.saam.app.SAAM;
import com.codingzero.saam.app.server.SAAMBuilder;
import com.codingzero.saam.app.server.base.mysql.MySQLAccessModule;
import com.codingzero.saam.infrastructure.database.SAAMTestApplication;
import com.codingzero.saam.infrastructure.database.OAuthPlatformAgent;
import com.codingzero.saam.presentation.DataSourceProvider;
import com.codingzero.saam.presentation.SAAMConfiguration;
import com.codingzero.utilities.transaction.TransactionManager;
import com.codingzero.utilities.transaction.manager.TransactionManagerImpl;
import io.dropwizard.testing.DropwizardTestSupport;
import io.dropwizard.testing.ResourceHelpers;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Helper {

    private static String SCHEMA_NAME_BASE = "saam-test-";
    private static String CREATE_SCHEMA = "CREATE SCHEMA `%s` DEFAULT CHARACTER SET utf8 ;";
    private static String DROP_SCHEMA = "DROP DATABASE `%s`;";

    private static final DropwizardTestSupport<SAAMConfiguration> SUPPORT = new DropwizardTestSupport<>(
            SAAMTestApplication.class,
            ResourceHelpers.resourceFilePath("config-int.yml"));

    private static Helper instance;

    private MySQLAccessModule mySQLAccessModule;
    private String schemaName;

    private Helper() {
        schemaName = generateSchemaName();
    }

    public String getSchemaName() {
        return schemaName;
    }

    private String generateSchemaName() {
        return SCHEMA_NAME_BASE + System.currentTimeMillis();
    }

    public static Helper sharedInstance() {
        if (null == instance) {
            instance = new Helper();
        }
        return instance;
    }

    public void init() {
        SUPPORT.before();
        try {
            initDatabase();
            initTable();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateConfiguration() {
        String url = (String) SUPPORT.getConfiguration().getMysql().get("url");
        url = url + "/" + getSchemaName();
        SUPPORT.getConfiguration().getMysql().put("url", url);
    }


    public void clean() {
        SUPPORT.after();
        try {
            cleanDatabase();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public MySQLAccessModule getMySQLAccessModule() {
        if (null == mySQLAccessModule) {
            DataSourceProvider provider = new DataSourceProvider(SUPPORT.getConfiguration());
            mySQLAccessModule = new MySQLAccessModule(provider.get());
        }
        return mySQLAccessModule;
    }

    public SAAM getSAAM(OAuthPlatformAgent oAuthPlatformAgent) {
            DataSource dataSource = new DataSourceProvider(SUPPORT.getConfiguration()).get();
            SAAMBuilder builder = getSAAMBuilder(oAuthPlatformAgent, dataSource);
            return  builder.build();
    }

    private SAAMBuilder getSAAMBuilder(OAuthPlatformAgent oAuthPlatformAgent, DataSource dataSource) {
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
                .setOAuthPlatformAgent(oAuthPlatformAgent);
    }

    private void initDatabase() throws SQLException {
        DataSourceProvider provider = new DataSourceProvider(SUPPORT.getConfiguration());
        DataSource dataSource = provider.get();
        try {
            runScript(dataSource, getDropSchemaSql());
        } catch (RuntimeException e) {
            //nothing
        }
        runScript(dataSource, getCreateSchemaSql());
        updateConfiguration();
    }

    private void initTable() throws IOException, SQLException {
        DataSourceProvider provider = new DataSourceProvider(SUPPORT.getConfiguration());
        DataSource dataSource = provider.get();
        createTables(dataSource);
    }

    private String getCreateSchemaSql() {
        return String.format(CREATE_SCHEMA, getSchemaName());
    }

    private String getDropSchemaSql() {
        return String.format(DROP_SCHEMA, getSchemaName());
    }

    private void createTables(DataSource dataSource) throws SQLException, IOException {
        String sqlFile = ResourceHelpers.resourceFilePath("schema-latest.sql");
        StringBuilder sql = new StringBuilder();
        FileReader sqlFileReader = new FileReader(new File(sqlFile));
        BufferedReader br = new BufferedReader(sqlFileReader);
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("--")
                    || line.startsWith("/*")
                    || line.trim().length() == 0) {
                continue;
            }
            sql.append(line);
        }
        br.close();
        String[] sqlCommands = sql.toString().split(";");
        for (String sqlCmd: sqlCommands) {
            runScript(dataSource, sqlCmd);
        }
    }

    private void cleanDatabase() throws SQLException {
        DataSourceProvider provider = new DataSourceProvider(SUPPORT.getConfiguration());
        DataSource dataSource = provider.get();
        runScript(dataSource, getDropSchemaSql());
    }


    private void runScript(DataSource source, String sql) throws SQLException {
        Connection conn = source.getConnection();
        PreparedStatement stmt=null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            stmt.close();
            conn.close();
        }
    }
}
