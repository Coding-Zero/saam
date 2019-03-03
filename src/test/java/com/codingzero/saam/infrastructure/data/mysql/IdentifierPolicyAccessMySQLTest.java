package com.codingzero.saam.infrastructure.data.mysql;

import com.codingzero.saam.infrastructure.data.EmailPolicyAccess;
import com.codingzero.saam.infrastructure.data.IdentifierPolicyAccess;
import com.codingzero.saam.infrastructure.data.spi.IdentifierPolicyAccessTest;
import com.codingzero.saam.infrastructure.data.UsernamePolicyAccess;
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
        return Helper.sharedInstance().getMySQLAccessFactory().getIdentifierPolicyAccess();
    }

    @Override
    protected UsernamePolicyAccess getUsernamePolicyAccess() {
        return Helper.sharedInstance().getMySQLAccessFactory().getUsernamePolicyAccess();
    }

    @Override
    protected EmailPolicyAccess getEmailPolicyAccess() {
        return Helper.sharedInstance().getMySQLAccessFactory().getEmailPolicyAccess();
    }

    @Override
    protected String getApplicationId() {
        return Helper.sharedInstance().getMySQLAccessFactory().getApplicationAccess().generateId();
    }

}
