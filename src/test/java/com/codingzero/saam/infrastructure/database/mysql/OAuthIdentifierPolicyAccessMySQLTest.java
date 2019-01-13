package com.codingzero.saam.infrastructure.database.mysql;

import com.codingzero.saam.infrastructure.database.OAuthIdentifierPolicyAccess;
import com.codingzero.saam.infrastructure.database.spi.OAuthIdentifierPolicyAccessTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class OAuthIdentifierPolicyAccessMySQLTest extends OAuthIdentifierPolicyAccessTest {

    @BeforeClass
    public static void beforeClass() {
        Helper.sharedInstance().init();
    }

    @AfterClass
    public static void afterClass() {
        Helper.sharedInstance().clean();
    }

    @Override
    protected OAuthIdentifierPolicyAccess getAccess() {
        return Helper.sharedInstance().getMySQLAccessModule().getOAuthIdentifierPolicyAccess();
    }

    @Override
    protected String getApplicationId() {
        return Helper.sharedInstance().getMySQLAccessModule().getApplicationAccess().generateId();
    }

}
