package com.codingzero.saam.app.server.base.mysql;

import com.codingzero.saam.infrastructure.database.APIKeyOS;
import com.codingzero.saam.infrastructure.database.PrincipalOS;
import com.codingzero.saam.infrastructure.database.spi.APIKeyAccess;
import com.codingzero.saam.infrastructure.database.spi.PrincipalAccess;
import com.codingzero.utilities.key.HMACKey;
import com.codingzero.utilities.key.Key;
import com.codingzero.utilities.key.RandomKey;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class APIKeyAccessImpl extends AbstractAccess implements APIKeyAccess {

    public static final String TABLE = "apikeys";

    public APIKeyAccessImpl(DataSource dataSource,
                            ObjectSegmentMapper objectSegmentMapper) {
        super(dataSource, objectSegmentMapper);
    }

    @Override
    public String generateKey() {
        return RandomKey.nextUUIDKey()
                .toHMACKey(HMACKey.Algorithm.SHA256)
                .toBase64String(true);
    }

    @Override
    public void insert(APIKeyOS os) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                    TABLE,
                    "application_id, id, `key`, name, user_id, is_active",
                    "?, ?, ?, ?, ?, ?");
            stmt = conn.prepareStatement(sql);

            stmt.setBytes(1, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setBytes(2, Key.fromHexString(os.getId()).getKey());
            stmt.setString(3, os.getKey());
            stmt.setString(4, os.getName());
            stmt.setBytes(5, Key.fromHexString(os.getUserId()).getKey());
            stmt.setBoolean(6, os.isActive());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void update(APIKeyOS os) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("UPDATE %s SET name=?, is_active=? "
                            + " WHERE application_id=? AND id=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, os.getName());
            stmt.setBoolean(2, os.isActive());
            stmt.setBytes(3, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setBytes(4, Key.fromHexString(os.getId()).getKey());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void delete(APIKeyOS os) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE application_id=? AND id=? LIMIT 1;",
                    TABLE);
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
    public void deleteByUserId(String applicationId, String userId) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE application_id=? AND user_id=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(userId).getKey());
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
            String sql = String.format("DELETE FROM %s WHERE application_id=? LIMIT 10000;",
                    TABLE);
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
    public APIKeyOS selectByKey(String applicationId, String key, PrincipalAccess principalAccess) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT * FROM %S WHERE application_id=? AND `key`=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setString(2, key);
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                PrincipalOS principalOS = principalAccess.selectById(
                        applicationId,
                        Key.fromBytes(rs.getBytes("id")).toHexString());
                return getObjectSegmentMapper().toAPIKeyOS(principalOS, rs);
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
    public APIKeyOS selectByPrincipalOS(PrincipalOS principalOS) {
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
                return getObjectSegmentMapper().toAPIKeyOS(principalOS, rs);
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
    public List<APIKeyOS> selectByUserId(String applicationId, String userId, PrincipalAccess principalAccess) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT * FROM %s WHERE application_id=? AND user_id=? ", TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(userId).getKey());
            rs = stmt.executeQuery();
            return toOSList(principalAccess, rs);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    private List<APIKeyOS> toOSList(PrincipalAccess principalAccess, ResultSet rs) throws SQLException, IOException {
        List<APIKeyOS> result = new ArrayList<>(10);
        while (rs.next()) {
            PrincipalOS principalOS = principalAccess.selectById(
                    Key.fromBytes(rs.getBytes("application_id")).toHexString(),
                    Key.fromBytes(rs.getBytes("id")).toHexString());
            result.add(getObjectSegmentMapper().toAPIKeyOS(principalOS, rs));
        }
        return result;
    }

}
