package com.codingzero.saam.app.server.infrastructure.mysql.commons;

import com.codingzero.saam.app.server.infrastructure.mysql.APIKeyAccessImpl;
import com.codingzero.saam.app.server.infrastructure.mysql.ApplicationAccessImpl;
import com.codingzero.saam.app.server.infrastructure.mysql.EmailPolicyAccessImpl;
import com.codingzero.saam.app.server.infrastructure.mysql.IdentifierAccessImpl;
import com.codingzero.saam.app.server.infrastructure.mysql.IdentifierAccessImplBoth;
import com.codingzero.saam.app.server.infrastructure.mysql.IdentifierPolicyAccessImpl;
import com.codingzero.saam.app.server.infrastructure.mysql.OAuthIdentifierAccessImpl;
import com.codingzero.saam.app.server.infrastructure.mysql.OAuthIdentifierAccessImplBoth;
import com.codingzero.saam.app.server.infrastructure.mysql.OAuthIdentifierPolicyAccessImpl;
import com.codingzero.saam.app.server.infrastructure.mysql.ObjectSegmentMapper;
import com.codingzero.saam.app.server.infrastructure.mysql.PermissionAccessImpl;
import com.codingzero.saam.app.server.infrastructure.mysql.PrincipalAccessImpl;
import com.codingzero.saam.app.server.infrastructure.mysql.PrincipalAccessImplBoth;
import com.codingzero.saam.app.server.infrastructure.mysql.ResourceAccessImpl;
import com.codingzero.saam.app.server.infrastructure.mysql.RoleAccessImpl;
import com.codingzero.saam.app.server.infrastructure.mysql.UserAccessImpl;
import com.codingzero.saam.app.server.infrastructure.mysql.UserAccessImplBoth;
import com.codingzero.saam.app.server.infrastructure.mysql.UserSessionAccessImpl;
import com.codingzero.saam.app.server.infrastructure.mysql.UserSessionAccessImplBoth;
import com.codingzero.saam.app.server.infrastructure.mysql.UsernamePolicyAccessImpl;
import com.codingzero.saam.infrastructure.data.APIKeyAccess;
import com.codingzero.saam.infrastructure.data.ApplicationAccess;
import com.codingzero.saam.infrastructure.data.EmailPolicyAccess;
import com.codingzero.saam.infrastructure.data.IdentifierAccess;
import com.codingzero.saam.infrastructure.data.IdentifierPolicyAccess;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierAccess;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierPolicyAccess;
import com.codingzero.saam.infrastructure.data.PermissionAccess;
import com.codingzero.saam.infrastructure.data.PrincipalAccess;
import com.codingzero.saam.infrastructure.data.ResourceAccess;
import com.codingzero.saam.infrastructure.data.RoleAccess;
import com.codingzero.saam.infrastructure.data.UserAccess;
import com.codingzero.saam.infrastructure.data.UserSessionAccess;
import com.codingzero.saam.infrastructure.data.UsernamePolicyAccess;

import javax.sql.DataSource;


public class MySQLAccessFactory {

    private DataSource dataSource;
    private ObjectSegmentMapper objectSegmentMapper;

    public MySQLAccessFactory(DataSource dataSource) {
        this(dataSource, new ObjectSegmentMapper());
    }

    public MySQLAccessFactory(DataSource dataSource,
                              ObjectSegmentMapper objectSegmentMapper) {
        this.dataSource = dataSource;
        this.objectSegmentMapper = objectSegmentMapper;
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
        return new IdentifierAccessImplBoth(new IdentifierAccessImpl(dataSource, objectSegmentMapper));
    }

    public IdentifierPolicyAccess getIdentifierPolicyAccess() {
        return new IdentifierPolicyAccessImpl(dataSource, objectSegmentMapper);
    }

    public PermissionAccess getPermissionAccess() {
        return new PermissionAccessImpl(dataSource, objectSegmentMapper);
    }

    public PrincipalAccess getPrincipalAccess() {
        return new PrincipalAccessImplBoth(new PrincipalAccessImpl(dataSource, objectSegmentMapper));
    }

    public ResourceAccess getResourceAccess() {
        return new ResourceAccessImpl(dataSource, objectSegmentMapper);
    }

    public RoleAccess getRoleAccess() {
        return new RoleAccessImpl(dataSource, objectSegmentMapper);
    }

    public OAuthIdentifierAccess getOAuthIdentifierAccess() {
        return new OAuthIdentifierAccessImplBoth(new OAuthIdentifierAccessImpl(dataSource, objectSegmentMapper));
    }

    public OAuthIdentifierPolicyAccess getOAuthIdentifierPolicyAccess() {
        return new OAuthIdentifierPolicyAccessImpl(dataSource, objectSegmentMapper);
    }

    public UserAccess getUserAccess() {
        return new UserAccessImplBoth(new UserAccessImpl(dataSource, objectSegmentMapper));
    }

    public UsernamePolicyAccess getUsernamePolicyAccess() {
        return new UsernamePolicyAccessImpl(dataSource, objectSegmentMapper);
    }

    public UserSessionAccess getUserSessionAccess() {
        return new UserSessionAccessImplBoth(new UserSessionAccessImpl(dataSource, objectSegmentMapper));
    }

}
