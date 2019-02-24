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
        return Helper.sharedInstance().getMySQLAccessHelper().getIdentifierAccess();
    }

    @Override
    protected String getApplicationId() {
        return Helper.sharedInstance().getMySQLAccessHelper().getApplicationAccess().generateId();
    }

    @Override
    protected String getUserId(String applicationId) {
        return Helper.sharedInstance()
                .getMySQLAccessHelper()
                .getPrincipalAccess()
                .generateId(applicationId, PrincipalType.USER);
    }

}
