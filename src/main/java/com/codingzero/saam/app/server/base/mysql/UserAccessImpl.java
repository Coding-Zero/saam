package com.codingzero.saam.app.server.base.mysql;

import com.codingzero.saam.infrastructure.database.PrincipalOS;
import com.codingzero.saam.infrastructure.database.UserOS;
import com.codingzero.saam.infrastructure.database.spi.UserAccess;
import com.codingzero.utilities.key.Key;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserAccessImpl extends AbstractAccess implements UserAccess {

    public static final String TABLE = "users";

    public UserAccessImpl(DataSource dataSource,
                          ObjectSegmentMapper objectSegmentMapper) {
        super(dataSource, objectSegmentMapper);
    }

    @Override
    public void insert(UserOS os) {
        Connection conn = getConnection();
        try {
            insertUserOS(os, conn);
            PrincipalOSHelper.insert(os, conn);
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(conn);
        }
    }

    private void insertUserOS(UserOS os, Connection conn) throws SQLException, JsonProcessingException {
        PreparedStatement stmt = null;
        try {
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                    TABLE,
                    "application_id, id, password, password_reset_code, role_ids",
                    "?, ?, ?, ?, ?");
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setBytes(2, Key.fromHexString(os.getId()).getKey());
            stmt.setString(3, os.getPassword());
            stmt.setString(4, getObjectSegmentMapper().toJson(os.getPasswordResetCode()));
            stmt.setString(5, getObjectSegmentMapper().toJson(os.getRoleIds()));
            stmt.executeUpdate();
        } finally {
            closePreparedStatement(stmt);
        }
    }

    @Override
    public void update(UserOS os) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("UPDATE %s SET password=?, password_reset_code=?, role_ids=? "
                            + " WHERE application_id=? AND id=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, os.getPassword());
            stmt.setString(2, getObjectSegmentMapper().toJson(os.getPasswordResetCode()));
            stmt.setString(3, getObjectSegmentMapper().toJson(os.getRoleIds()));
            stmt.setBytes(4, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setBytes(5, Key.fromHexString(os.getId()).getKey());
            stmt.executeUpdate();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void delete(UserOS os) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE ur, ppl FROM %S ur"
                            + " LEFT JOIN %S ppl"
                            + " ON ur.application_id = ppl.application_id AND ur.id = ppl.id"
                            + " WHERE ur.application_id=? AND ur.id=? ;",
                    TABLE,
                    PrincipalAccessImpl.TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setBytes(2, Key.fromHexString(os.getId()).getKey());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void deleteByApplicationId(String id) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE ur, ppl FROM %S ur"
                            + " LEFT JOIN %S ppl"
                            + " ON ur.application_id = ppl.application_id AND ur.id = ppl.id"
                            + " WHERE ur.application_id=?;",
                    TABLE,
                    PrincipalAccessImpl.TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(id).getKey());
            int deletedRows = stmt.executeUpdate();
            while (deletedRows > 0) {
                deletedRows = stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public UserOS selectByPrincipalOS(PrincipalOS principalOS) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT * FROM %s WHERE application_id=? AND id=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(principalOS.getApplicationId()).getKey());
            stmt.setBytes(2, Key.fromHexString(principalOS.getId()).getKey());
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return getObjectSegmentMapper().toUserOS(principalOS, rs);
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
