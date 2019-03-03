package com.codingzero.saam.infrastructure.data.mysql;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.data.IdentifierAccess;
import com.codingzero.saam.infrastructure.data.spi.IdentifierAccessTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class IdentifierAccessMySQLTest extends IdentifierAccessTest {

    @BeforeClass
    public static void beforeClass() {
        Helper.sharedInstance().init();
    }

    @AfterClass
    public static void afterClass() {
        Helper.sharedInstance().clean();
    }

    @Override
    protected IdentifierAccess getAccess() {
        return Helper.sharedInstance().getMySQLAccessFactory().getIdentifierAccess();
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
