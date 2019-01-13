package com.codingzero.saam.infrastructure.database.mysql;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.database.IdentifierAccess;
import com.codingzero.saam.infrastructure.database.spi.IdentifierAccessTest;
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
        return Helper.sharedInstance().getMySQLAccessModule().getIdentifierAccess();
    }

    @Override
    protected String getApplicationId() {
        return Helper.sharedInstance().getMySQLAccessModule().getApplicationAccess().generateId();
    }

    @Override
    protected String getUserId(String applicationId) {
        return Helper.sharedInstance()
                .getMySQLAccessModule()
                .getPrincipalAccess()
                .generateId(applicationId, PrincipalType.USER);
    }

}
