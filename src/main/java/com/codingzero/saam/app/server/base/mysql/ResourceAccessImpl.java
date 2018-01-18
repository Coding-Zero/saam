package com.codingzero.saam.app.server.base.mysql;

import com.codingzero.saam.infrastructure.database.ResourceOS;
import com.codingzero.saam.infrastructure.database.spi.ResourceAccess;
import com.codingzero.utilities.key.HMACKey;
import com.codingzero.utilities.key.Key;
import com.codingzero.utilities.pagination.OffsetBasedResultPage;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.ResultFetchRequest;
import com.codingzero.utilities.pagination.ResultPage;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class ResourceAccessImpl extends AbstractAccess implements ResourceAccess {

    public static final String TABLE = "resources";

    public ResourceAccessImpl(DataSource dataSource, ObjectSegmentMapper objectSegmentMapper) {
        super(dataSource, objectSegmentMapper);
    }

    @Override
    public boolean isDuplicateKey(String applicationId, String key) {
        String keyHash = hash(key);
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT COUNT(*) FROM %s WHERE application_id=? AND key_hash=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(keyHash).getKey());
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
        if (null == key) {
            return null;
        }
        return Key.fromString(key.toLowerCase())
                .toHMACKey(HMACKey.Algorithm.SHA256)
                .toHexString();
    }

    @Override
    public void insert(ResourceOS os) {
        String keyHash = hash(os.getKey());
        String parentKeyHash = hash(os.getParentKey());
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                    TABLE,
                    "application_id, key_hash, `key`, principal_id, creation_time, parent_key_hash",
                    "?, ?, ?, ?, ?, ?");
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setBytes(2, Key.fromHexString(keyHash).getKey());
            stmt.setString(3, os.getKey());
            stmt.setBytes(4, Key.fromHexString(os.getPrincipalId()).getKey());
            stmt.setTimestamp(5, new Timestamp(os.getCreationTime().getTime()));
            stmt.setBytes(6, null == parentKeyHash? null : Key.fromHexString(parentKeyHash).getKey());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void update(ResourceOS os) {
        String keyHash = hash(os.getKey());
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("UPDATE %s SET principal_id=? "
                            + " WHERE application_id=? AND key_hash=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getPrincipalId()).getKey());
            stmt.setBytes(2, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setBytes(3, Key.fromHexString(keyHash).getKey());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void delete(ResourceOS os) {
        deleteSingleResource(os);
        deleteRemovedParentResources();
    }

    private void deleteSingleResource(ResourceOS os) {
        String keyHash = hash(os.getKey());
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE application_id=? AND key_hash=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setBytes(2, Key.fromHexString(keyHash).getKey());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    private void deleteRemovedParentResources() {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = "DELETE r FROM " + TABLE + " r INNER JOIN ("
                    + "     SELECT r1.* FROM " + TABLE + " r1 "
                    + "     LEFT JOIN "+ TABLE + " r2 ON r1.parent_key_hash=r2.key_hash "
                    + "     WHERE r2.application_id IS NULL AND r1.parent_key_hash IS NOT NULL "
                    + "     LIMIT 1000"
                    + "  ) AS rr ON r.key_hash = rr.key_hash;";
            stmt = conn.prepareStatement(sql);
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

    private void deleteByParentKey(String applicationId, String parentKey) {
        String parentKeyHash = hash(parentKey);
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE application_id=? AND %s LIMIT 1000;",
                    TABLE, getParentKeyHashQuery(parentKeyHash));
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            if (null != parentKeyHash) {
                stmt.setBytes(2, Key.fromHexString(parentKeyHash).getKey());
            }
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

    private String getParentKeyHashQuery(String parentKeyHash) {
        String query = "parent_key_hash";
        if (null != parentKeyHash) {
            query += " = ?";
        } else {
            query += " IS NULL";
        }
        return query;
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
    public ResourceOS selectByKey(String applicationId, String key) {
        String keyHash = hash(key);
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT * FROM %s WHERE application_id=? AND key_hash=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, null == keyHash? null : Key.fromHexString(keyHash).getKey());
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return getObjectSegmentMapper().toResourceOS(rs);
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
    public PaginatedResult<List<ResourceOS>> selectByPrincipalId(
            String applicationId, String parentKey, String principalId) {
        return new PaginatedResult<>(
                request -> _selectByPrincipalId(request),
                parentKey, principalId);
    }

    @Override
    public PaginatedResult<List<ResourceOS>> selectAll(String applicationId, String parentKey) {
        return new PaginatedResult<>(
                request -> _selectAll(request),
                applicationId, parentKey);
    }

    private List<ResourceOS> _selectByPrincipalId(ResultFetchRequest request) {
        String parentKey = (String) request.getArguments()[0];
        String parentKeyHash = hash(parentKey);
        String principalId = (String) request.getArguments()[1];
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(String.format("SELECT * FROM %s WHERE principal_id=? AND %s ",
                    TABLE, getParentKeyHashQuery(parentKeyHash)));
            sql.append(MySQLHelper.buildSortingQuery(request.getSorting()));
            sql.append(" ");
            sql.append(MySQLHelper.buildPagingQuery((OffsetBasedResultPage) request.getPage()));
            stmt = conn.prepareCall(sql.toString());
            stmt.setBytes(1, Key.fromHexString(principalId).getKey());
            if (null != parentKeyHash) {
                stmt.setBytes(2, Key.fromHexString(parentKeyHash).getKey());
            }
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

    private List<ResourceOS> _selectAll(ResultFetchRequest request) {
        String applicationId = (String) request.getArguments()[0];
        String parentKey = (String) request.getArguments()[1];
        String parentKeyHash = hash(parentKey);
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(String.format("SELECT * FROM %s WHERE application_id=? AND %s ",
                    TABLE, getParentKeyHashQuery(parentKeyHash)));
            sql.append(MySQLHelper.buildSortingQuery(request.getSorting()));
            sql.append(" ");
            sql.append(MySQLHelper.buildPagingQuery((OffsetBasedResultPage) request.getPage()));
            stmt = conn.prepareCall(sql.toString());
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            if (null != parentKeyHash) {
                stmt.setBytes(2, Key.fromHexString(parentKeyHash).getKey());
            }
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

    private List<ResourceOS> toOSList(ResultSet rs, ResultPage pageOffset) throws SQLException, IOException {
        List<ResourceOS> result = new ArrayList<>(pageOffset.getSize());
        while (rs.next()) {
            result.add(getObjectSegmentMapper().toResourceOS(rs));
        }
        return result;
    }
}
