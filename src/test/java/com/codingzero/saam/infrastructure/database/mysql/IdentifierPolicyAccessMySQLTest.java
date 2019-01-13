package com.codingzero.saam.infrastructure.database.mysql;

import com.codingzero.saam.infrastructure.database.EmailPolicyAccess;
import com.codingzero.saam.infrastructure.database.IdentifierPolicyAccess;
import com.codingzero.saam.infrastructure.database.spi.IdentifierPolicyAccessTest;
import com.codingzero.saam.infrastructure.database.UsernamePolicyAccess;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class IdentifierPolicyAccessMySQLTest extends IdentifierPolicyAccessTest {

    @BeforeClass
    public static void beforeClass() {
        Helper.sharedInstance().init();
    }

    @AfterClass
    public static void afterClass() {
        Helper.sharedInstance().clean();
    }

    @Override
    protected IdentifierPolicyAccess getAccess() {
        return Helper.sharedInstance().getMySQLAccessModule().getIdentifierPolicyAccess();
    }

    @Override
    protected UsernamePolicyAccess getUsernamePolicyAccess() {
        return Helper.sharedInstance().getMySQLAccessModule().getUsernamePolicyAccess();
    }

    @Override
    protected EmailPolicyAccess getEmailPolicyAccess() {
        return Helper.sharedInstance().getMySQLAccessModule().getEmailPolicyAccess();
    }

    @Override
    protected String getApplicationId() {
        return Helper.sharedInstance().getMySQLAccessModule().getApplicationAccess().generateId();
    }

}
