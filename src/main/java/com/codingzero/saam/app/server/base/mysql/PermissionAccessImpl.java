package com.codingzero.saam.app.server.base.mysql;

import com.codingzero.saam.infrastructure.database.PermissionOS;
import com.codingzero.saam.infrastructure.database.spi.PermissionAccess;
import com.codingzero.utilities.key.HMACKey;
import com.codingzero.utilities.key.Key;
import com.codingzero.utilities.pagination.OffsetBasedResultPage;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.ResultFetchRequest;
import com.codingzero.utilities.pagination.ResultPage;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class PermissionAccessImpl extends AbstractAccess implements PermissionAccess {

    public static final String TABLE = "permissions";

    public PermissionAccessImpl(DataSource dataSource, ObjectSegmentMapper objectSegmentMapper) {
        super(dataSource, objectSegmentMapper);
    }

    @Override
    public boolean isDuplicate(String applicationId, String resourceKey, String principalId) {
        String resourceKeyHash = hash(resourceKey);
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT COUNT(*) FROM %s WHERE "
                            + "application_id=? AND resource_key_hash=? AND principal_id=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);

            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(resourceKeyHash).getKey());
            stmt.setBytes(3, Key.fromHexString(principalId).getKey());
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

    private String hash(String key) {
        return Key.fromString(key.toLowerCase())
                .toHMACKey(HMACKey.Algorithm.SHA256)
                .toHexString();
    }

    @Override
    public void insert(PermissionOS os) {
        String resourceKeyHash = hash(os.getResourceKey());
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                    TABLE,
                    "application_id, resource_key_hash, resource_key, principal_id, creation_time, actions",
                    "?, ?, ?, ?, ?, ?");
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setBytes(2, Key.fromHexString(resourceKeyHash).getKey());
            stmt.setString(3, os.getResourceKey());
            stmt.setBytes(4, Key.fromHexString(os.getPrincipalId()).getKey());
            stmt.setTimestamp(5, new Timestamp(os.getCreationTime().getTime()));
            stmt.setString(6, getObjectSegmentMapper().toJson(os.getActions()));
            stmt.executeUpdate();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void update(PermissionOS os) {
        String resourceKeyHash = hash(os.getResourceKey());
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("UPDATE %s SET actions=? "
                            + " WHERE application_id=? AND resource_key_hash=? AND principal_id=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, getObjectSegmentMapper().toJson(os.getActions()));
            stmt.setBytes(2, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setBytes(3, Key.fromHexString(resourceKeyHash).getKey());
            stmt.setBytes(4, Key.fromHexString(os.getPrincipalId()).getKey());
            stmt.executeUpdate();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void delete(PermissionOS os) {
        String resourceKeyHash = hash(os.getResourceKey());
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE "
                            + "application_id=? AND resource_key_hash=? AND principal_id=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setBytes(2, Key.fromHexString(resourceKeyHash).getKey());
            stmt.setBytes(3, Key.fromHexString(os.getPrincipalId()).getKey());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void deleteByPrincipalId(String applicationId, String principalId) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE application_id=? AND principal_id=? LIMIT 1000;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(principalId).getKey());

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
    public void deleteByResourceKey(String applicationId, String resourceKey) {
        String resourceKeyHash = hash(resourceKey);
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE application_id=? AND resource_key_hash=? LIMIT 1000;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(resourceKeyHash).getKey());

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
    public void deleteByApplicationId(String applicationId) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE application_id=? LIMIT 10000;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());

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
    public PermissionOS selectByResourceKeyAndPrincipalId(String applicationId, String resourceKey, String principalId) {
        String resourceKeyHash = hash(resourceKey);
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT * FROM %s WHERE"
                            + " application_id=? AND resource_key_hash=? AND principal_id=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(resourceKeyHash).getKey());
            stmt.setBytes(3, Key.fromHexString(principalId).getKey());
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return getObjectSegmentMapper().toPermissionOS(rs);
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
    public PaginatedResult<List<PermissionOS>> selectByResourceKey(String applicationId, String resourceKey) {
        return new PaginatedResult<>(
                request -> _selectByResourceKey(request),
                applicationId, resourceKey);
    }

    @Override
    public PaginatedResult<List<PermissionOS>> selectByPrincipalId(String applicationId, String principalId) {
        return new PaginatedResult<>(
                request -> _selectByPrincipalId(request),
                applicationId, principalId);
    }

    private List<PermissionOS> _selectByPrincipalId(ResultFetchRequest request) {
        String applicationId = (String) request.getArguments()[0];
        String principalId = (String) request.getArguments()[1];
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(String.format("SELECT * FROM %s WHERE application_id=? AND principal_id=? ",
                    TABLE));
            sql.append(MySQLHelper.buildSortingQuery(request.getSorting()));
            sql.append(" ");
            sql.append(MySQLHelper.buildPagingQuery((OffsetBasedResultPage) request.getPage()));
            stmt = conn.prepareCall(sql.toString());
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(principalId).getKey());

            rs = stmt.executeQuery();
            return toOSList(rs, request.getPage());
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    private List<PermissionOS> _selectByResourceKey(ResultFetchRequest request) {
        String applicationId = (String) request.getArguments()[0];
        String resourceKey = (String) request.getArguments()[1];
        String resourceKeyHash = hash(resourceKey);
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(String.format("SELECT * FROM %s WHERE application_id=? AND resource_key_hash=? ",
                    TABLE));
            sql.append(MySQLHelper.buildSortingQuery(request.getSorting()));
            sql.append(" ");
            sql.append(MySQLHelper.buildPagingQuery((OffsetBasedResultPage) request.getPage()));
            stmt = conn.prepareCall(sql.toString());
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(resourceKeyHash).getKey());
            rs = stmt.executeQuery();
            return toOSList(rs, request.getPage());
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    private List<PermissionOS> toOSList(ResultSet rs, ResultPage pageOffset) throws SQLException, IOException {
        List<PermissionOS> result = new ArrayList<>(pageOffset.getSize());
        while (rs.next()) {
            result.add(getObjectSegmentMapper().toPermissionOS(rs));
        }
        return result;
    }
}
