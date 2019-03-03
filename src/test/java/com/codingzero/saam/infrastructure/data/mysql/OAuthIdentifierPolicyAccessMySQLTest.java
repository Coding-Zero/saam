package com.codingzero.saam.infrastructure.data.mysql;

import com.codingzero.saam.infrastructure.data.OAuthIdentifierPolicyAccess;
import com.codingzero.saam.infrastructure.data.spi.OAuthIdentifierPolicyAccessTest;
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
        return Helper.sharedInstance().getMySQLAccessFactory().getOAuthIdentifierPolicyAccess();
    }

    @Override
    protected String getApplicationId() {
        return Helper.sharedInstance().getMySQLAccessFactory().getApplicationAccess().generateId();
    }

}
