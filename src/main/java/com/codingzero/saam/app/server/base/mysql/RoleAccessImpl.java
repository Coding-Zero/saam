package com.codingzero.saam.app.server.base.mysql;

import com.codingzero.saam.infrastructure.database.PrincipalOS;
import com.codingzero.saam.infrastructure.database.RoleOS;
import com.codingzero.saam.infrastructure.database.spi.RoleAccess;
import com.codingzero.utilities.key.Key;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class RoleAccessImpl extends AbstractAccess implements RoleAccess {

    public static final String TABLE = "roles";

    public RoleAccessImpl(DataSource dataSource,
                          ObjectSegmentMapper objectSegmentMapper) {
        super(dataSource, objectSegmentMapper);
    }

    @Override
    public boolean isDuplicateName(String applicationId, String name) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT COUNT(*) FROM %s WHERE application_id=? AND name=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setString(2, name);
            rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void insert(RoleOS os) {
        Connection conn = getConnection();
        try {
            PrincipalOSHelper.insert(os, conn);
            insertRoleOS(os, conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(conn);
        }
    }

    private void insertRoleOS(RoleOS os, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                    TABLE,
                    "application_id, id, name",
                    "?, ?, ?");
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setBytes(2, Key.fromHexString(os.getId()).getKey());
            stmt.setString(3, os.getName());
            stmt.executeUpdate();
        } finally {
            closePreparedStatement(stmt);
        }
    }

    @Override
    public void update(RoleOS os) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("UPDATE %s SET name=? "
                            + " WHERE application_id=? AND id=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, os.getName());
            stmt.setBytes(2, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setBytes(3, Key.fromHexString(os.getId()).getKey());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void delete(RoleOS os) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE rl, ppl FROM %S rl"
                            + " LEFT JOIN %S ppl"
                            + " ON rl.application_id = ppl.application_id AND rl.id = ppl.id"
                            + " WHERE rl.application_id=? AND rl.id=? LIMIT 1;",
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
            String sql = String.format("DELETE rl, ppl FROM %S rl"
                            + " LEFT JOIN %S ppl"
                            + " ON rl.application_id = ppl.application_id AND rl.id = ppl.id"
                            + " WHERE rl.application_id=?;",
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
    public RoleOS selectByName(String applicationId, String name) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT * FROM %S rl"
                            + " LEFT JOIN %S ppl"
                            + " ON rl.application_id = ppl.application_id AND rl.id = ppl.id"
                            + " WHERE rl.application_id=? AND rl.name=? LIMIT 1;",
                    TABLE,
                    PrincipalAccessImpl.TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setString(2, name);
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return getObjectSegmentMapper().toRoleOS(rs);
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

//    @Override
//    public RoleOS selectByName(String applicationId, String name, PrincipalAccess principalAccess) {
//        Connection conn = getConnection();
//        PreparedStatement stmt = null;
//        ResultSet rs = null;
//        try {
//            String sql = String.format(
//                    "SELECT * FROM %s WHERE application_id=? AND name=? ",
//                    TABLE);
//            stmt = conn.prepareCall(sql);
//            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
//            stmt.setString(2, name);
//            rs = stmt.executeQuery();
//            if (!rs.next()) {
//                return null;
//            } else {
//                PrincipalOS principalOS = principalAccess.selectById(
//                        applicationId,
//                        Key.fromBytes(rs.getBytes("id")).toHexString());
//                return getObjectSegmentMapper().toRoleOS(principalOS, rs);
//            }
//        } catch (SQLException | IOException e) {
//            throw new RuntimeException(e);
//        } finally {
//            closeResultSet(rs);
//            closePreparedStatement(stmt);
//            closeConnection(conn);
//        }
//    }

    @Override
    public RoleOS selectByPrincipalOS(PrincipalOS principalOS) {
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
                return getObjectSegmentMapper().toRoleOS(principalOS, rs);
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
