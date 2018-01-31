package com.codingzero.saam.app.server.base.mysql;

import com.codingzero.saam.infrastructure.database.spi.APIKeyAccess;
import com.codingzero.saam.infrastructure.database.spi.ApplicationAccess;
import com.codingzero.saam.infrastructure.database.spi.EmailPolicyAccess;
import com.codingzero.saam.infrastructure.database.spi.IdentifierAccess;
import com.codingzero.saam.infrastructure.database.spi.OAuthIdentifierPolicyAccess;
import com.codingzero.saam.infrastructure.database.spi.ResourceAccess;
import com.codingzero.saam.infrastructure.database.spi.RoleAccess;
import com.codingzero.saam.infrastructure.database.spi.UserAccess;
import com.codingzero.saam.infrastructure.database.spi.UserSessionAccess;
import com.codingzero.saam.infrastructure.database.spi.IdentifierPolicyAccess;
import com.codingzero.saam.infrastructure.database.spi.PermissionAccess;
import com.codingzero.saam.infrastructure.database.spi.PrincipalAccess;
import com.codingzero.saam.infrastructure.database.spi.OAuthIdentifierAccess;
import com.codingzero.saam.infrastructure.database.spi.UsernamePolicyAccess;

import javax.sql.DataSource;


public class MySQLAccessModule {

    private DataSource dataSource;
    private ObjectSegmentMapper objectSegmentMapper;

    public MySQLAccessModule(DataSource dataSource) {
        this(dataSource, new ObjectSegmentMapper());
    }

    public MySQLAccessModule(DataSource dataSource,
                             ObjectSegmentMapper objectSegmentMapper) {
        this.dataSource = dataSource;
        this.objectSegmentMapper = objectSegmentMapper;
    }

    private PrincipalAccessImpl getPrincipalAccessImpl() {
        return new PrincipalAccessImpl(dataSource, objectSegmentMapper);
    }

    private IdentifierPolicyAccessImpl getIdentifierPolicyAccessImpl() {
        return new IdentifierPolicyAccessImpl(dataSource, objectSegmentMapper);
    }

    public APIKeyAccess getAPIKeyAccess() {
        return new APIKeyAccessImpl(
                dataSource, objectSegmentMapper);
    }

    public ApplicationAccess getApplicationAccess() {
        return new ApplicationAccessImpl(dataSource, objectSegmentMapper);
    }

    public EmailPolicyAccess getEmailPolicyAccess() {
        return new EmailPolicyAccessImpl(dataSource, objectSegmentMapper);
    }

    public IdentifierAccess getIdentifierAccess() {
        return new IdentifierAccessImpl(dataSource, objectSegmentMapper);
    }

    public IdentifierPolicyAccess getIdentifierPolicyAccess() {
        return getIdentifierPolicyAccessImpl();
    }

    public PermissionAccess getPermissionAccess() {
        return new PermissionAccessImpl(dataSource, objectSegmentMapper);
    }

    public PrincipalAccess getPrincipalAccess() {
        return getPrincipalAccessImpl();
    }

    public ResourceAccess getResourceAccess() {
        return new ResourceAccessImpl(dataSource, objectSegmentMapper);
    }

    public RoleAccess getRoleAccess() {
        return new RoleAccessImpl(dataSource, objectSegmentMapper);
    }

    public OAuthIdentifierAccess getOAuthIdentifierAccess() {
        return new OAuthIdentifierAccessImpl(dataSource, objectSegmentMapper);
    }

    public OAuthIdentifierPolicyAccess getOAuthIdentifierPolicyAccess() {
        return new OAuthIdentifierPolicyAccessImpl(dataSource, objectSegmentMapper);
    }

    public UserAccess getUserAccess() {
        return new UserAccessImpl(dataSource, objectSegmentMapper);
    }

    public UsernamePolicyAccess getUsernamePolicyAccess() {
        return new UsernamePolicyAccessImpl(dataSource, objectSegmentMapper);
    }

    public UserSessionAccess getUserSessionAccess() {
        return new UserSessionAccessImpl(dataSource, objectSegmentMapper);
    }

}
