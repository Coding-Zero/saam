package com.codingzero.saam.app.server.base.mysql;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.infrastructure.database.IdentifierPolicyOS;
import com.codingzero.saam.infrastructure.database.UsernamePolicyOS;
import com.codingzero.saam.infrastructure.database.spi.UsernamePolicyAccess;
import com.codingzero.utilities.key.Key;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UsernamePolicyAccessImpl extends AbstractAccess implements UsernamePolicyAccess {

    public static final String TABLE = "username_policies";

    public UsernamePolicyAccessImpl(DataSource dataSource,
                                    ObjectSegmentMapper objectSegmentMapper) {
        super(dataSource, objectSegmentMapper);
    }

    @Override
    public void insert(UsernamePolicyOS os) {
        Connection conn = getConnection();
        try {
            insertUsernamePolicyOS(os, conn);
            IdentifierPolicyOSHelper.insert(os, conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(conn);
        }
    }

    private void insertUsernamePolicyOS(UsernamePolicyOS os, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                    TABLE,
                    "application_id, format",
                    "?, ?");
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setString(2, os.getFormat().name());
            stmt.executeUpdate();
        } finally {
            closePreparedStatement(stmt);
        }
    }

    @Override
    public void update(UsernamePolicyOS os) {
        Connection conn = getConnection();
        try {
            IdentifierPolicyOSHelper.update(os, conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(conn);
        }
    }

    @Override
    public void delete(UsernamePolicyOS os) {
        deleteByApplicationId(os.getApplicationId());
    }

    @Override
    public void deleteByApplicationId(String id) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE up, ip FROM %S up"
                            + " LEFT JOIN %S ip"
                            + " ON ip.application_id = up.application_id AND ip.type = ?"
                            + " WHERE up.application_id=?;",
                    TABLE,
                    IdentifierPolicyAccessImpl.TABLE);
//            String sql = String.format("DELETE FROM %s WHERE application_id=?;",
//                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, IdentifierType.USERNAME.name());
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
    public UsernamePolicyOS selectByApplicationId(String applicationId) {
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
            stmt.setString(1, IdentifierType.USERNAME.name());
            stmt.setBytes(2, Key.fromHexString(applicationId).getKey());
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return getObjectSegmentMapper().toUsernamePolicyOS(rs);
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
    public UsernamePolicyOS selectByIdentifierPolicyOS(IdentifierPolicyOS os) {
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
                return getObjectSegmentMapper().toUsernamePolicyOS(os, rs);
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
