package com.codingzero.saam.infrastructure.database.mysql;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.database.spi.PrincipalAccess;
import com.codingzero.saam.infrastructure.database.spi.PrincipalAccessTest;
import com.codingzero.saam.infrastructure.database.spi.ResourceAccess;
import com.codingzero.saam.infrastructure.database.spi.ResourceAccessTest;
import com.codingzero.saam.infrastructure.database.spi.RoleAccess;
import com.codingzero.saam.infrastructure.database.spi.UserAccess;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class ResourceAccessMySQLTest extends ResourceAccessTest {

    @BeforeClass
    public static void beforeClass() {
        Helper.sharedInstance().init();
    }

    @AfterClass
    public static void afterClass() {
        Helper.sharedInstance().clean();
    }

    @Override
    protected ResourceAccess getAccess() {
        return Helper.sharedInstance().getMySQLAccessModule().getResourceAccess();
    }

    @Override
    protected String getApplicationId() {
        return Helper.sharedInstance().getMySQLAccessModule().getApplicationAccess().generateId();
    }

    @Override
    protected String getPrincipalId(String applicationId, PrincipalType type) {
        return Helper.sharedInstance().getMySQLAccessModule().getPrincipalAccess().generateId(applicationId, type);
    }
}
