package com.codingzero.saam.app.server.base.mysql;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.infrastructure.database.EmailPolicyOS;
import com.codingzero.saam.infrastructure.database.IdentifierPolicyOS;
import com.codingzero.saam.infrastructure.database.EmailPolicyAccess;
import com.codingzero.utilities.key.Key;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmailPolicyAccessImpl extends AbstractAccess implements EmailPolicyAccess {

    public static final String TABLE = "email_policies";

    public EmailPolicyAccessImpl(DataSource dataSource,
                                 ObjectSegmentMapper objectSegmentMapper) {
        super(dataSource, objectSegmentMapper);
    }

    @Override
    public void insert(EmailPolicyOS os) {
        Connection conn = getConnection();
        try {
            insertEmailPolicyOS(os, conn);
            IdentifierPolicyOSHelper.insert(os, conn);
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(conn);
        }
    }

    private void insertEmailPolicyOS(EmailPolicyOS os, Connection conn) throws SQLException, JsonProcessingException {
        PreparedStatement stmt = null;
        try {
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                    TABLE,
                    "application_id, domains",
                    "?, ?");
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setString(2, getObjectSegmentMapper().toJson(os.getDomains()));
            stmt.executeUpdate();
        } finally {
            closePreparedStatement(stmt);
        }
    }

    @Override
    public void update(EmailPolicyOS os) {
        Connection conn = getConnection();
        try {
            updateEmailPolicyOS(os, conn);
            IdentifierPolicyOSHelper.update(os, conn);
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(conn);
        }
    }

    private void updateEmailPolicyOS(EmailPolicyOS os, Connection conn) throws SQLException, JsonProcessingException {
        PreparedStatement stmt = null;
        try {
            String sql = String.format("UPDATE %s SET domains=? "
                            + " WHERE application_id=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, getObjectSegmentMapper().toJson(os.getDomains()));
            stmt.setBytes(2, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.executeUpdate();
        } finally {
            closePreparedStatement(stmt);
        }
    }

    @Override
    public void delete(EmailPolicyOS os) {
        deleteByApplicationId(os.getApplicationId());
    }

    @Override
    public void deleteByApplicationId(String id) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE ep, ip FROM %S ep"
                            + " LEFT JOIN %S ip"
                            + " ON ip.application_id = ep.application_id AND ip.type = ?"
                            + " WHERE ep.application_id=?;",
                    TABLE,
                    IdentifierPolicyAccessImpl.TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, IdentifierType.EMAIL.name());
            stmt.setBytes(2, Key.fromHexString(id).getKey());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public EmailPolicyOS selectByApplicationId(String applicationId) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT * FROM %S ep"
                            + " LEFT JOIN %S ip"
                            + " ON ip.application_id = ep.application_id AND ip.type = ?"
                            + " WHERE ep.application_id=?;",
                    TABLE,
                    IdentifierPolicyAccessImpl.TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setString(1, IdentifierType.EMAIL.name());
            stmt.setBytes(2, Key.fromHexString(applicationId).getKey());
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return getObjectSegmentMapper().toEmailPolicyOS(rs);
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public EmailPolicyOS selectByIdentifierPolicyOS(IdentifierPolicyOS os) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT * FROM %s WHERE application_id=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(os.getApplicationId()).getKey());
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return getObjectSegmentMapper().toEmailPolicyOS(os, rs);
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

}
