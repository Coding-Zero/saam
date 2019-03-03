package com.codingzero.saam.infrastructure.data.mysql;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierAccess;
import com.codingzero.saam.infrastructure.data.spi.OAuthIdentifierAccessTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class OAuthIdentifierAccessMySQLTest extends OAuthIdentifierAccessTest {

    @BeforeClass
    public static void beforeClass() {
        Helper.sharedInstance().init();
    }

    @AfterClass
    public static void afterClass() {
        Helper.sharedInstance().clean();
    }

    @Override
    protected OAuthIdentifierAccess getAccess() {
        return Helper.sharedInstance().getMySQLAccessFactory().getOAuthIdentifierAccess();
    }

    @Override
    protected String getApplicationId() {
        return Helper.sharedInstance().getMySQLAccessFactory().getApplicationAccess().generateId();
    }

    @Override
    protected String getUserId(String applicationId) {
        return Helper.sharedInstance()
                .getMySQLAccessFactory()
                .getPrincipalAccess()
                .generateId(applicationId, PrincipalType.USER);
    }

}
