package com.codingzero.saam.app.server.infrastructure.mysql;

import com.codingzero.saam.app.server.infrastructure.mysql.commons.AbstractAccess;
import com.codingzero.saam.app.server.infrastructure.mysql.commons.MySQLQueryBuilder;
import com.codingzero.saam.common.OAuthIdentifierKey;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierAccess;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierOS;
import com.codingzero.utilities.key.HMACKey;
import com.codingzero.utilities.key.Key;
import com.codingzero.utilities.pagination.OffsetBasedResultPage;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.ResultFetchRequest;
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


public class OAuthIdentifierAccessImpl extends AbstractAccess implements OAuthIdentifierAccess {

    public static final String TABLE = "saam_v2.oauth_identifiers";

    public OAuthIdentifierAccessImpl(DataSource dataSource, ObjectSegmentMapper objectSegmentMapper) {
        super(dataSource, objectSegmentMapper);
    }

    @Override
    public boolean isDuplicateKey(OAuthIdentifierKey key) {
        String contentHash = hash(key.getContent());
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT COUNT(*) FROM %s WHERE "
                            + "application_id=? AND platform=? AND content_hash=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(key.getApplicationId()).getKey());
            stmt.setString(2, key.getPlatform().name());
            stmt.setBytes(3, Key.fromHexString(contentHash).getKey());
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
    public int countByUserId(String applicationId, String userId) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT COUNT(*) FROM %s WHERE "
                            + "application_id=? AND user_id=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(userId).getKey());
            rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public int countByPlatform(String applicationId, OAuthPlatform platform) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT COUNT(*) FROM %s WHERE "
                            + "application_id=? AND platform=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setString(2, platform.name());
            rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void insert(OAuthIdentifierOS os) {
        String contentHash = hash(os.getKey().getContent());
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                    TABLE,
                    "application_id, platform, content_hash, content, user_id, properties, creation_time, update_time ",
                    "?, ?, ?, ?, ?, ?, ?, ?");
            stmt = conn.prepareStatement(sql);

            stmt.setBytes(1, Key.fromHexString(os.getKey().getApplicationId()).getKey());
            stmt.setString(2, os.getKey().getPlatform().name());
            stmt.setBytes(3, Key.fromHexString(contentHash).getKey());
            stmt.setString(4, os.getKey().getContent());
            stmt.setBytes(5, Key.fromHexString(os.getUserId()).getKey());
            stmt.setString(6, getObjectSegmentMapper().toJson(os.getProperties()));
            stmt.setTimestamp(7, new Timestamp(os.getCreationTime().getTime()));
            stmt.setTimestamp(8, new Timestamp(os.getUpdateTime().getTime()));
            stmt.executeUpdate();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void update(OAuthIdentifierOS os) {
        String contentHash = hash(os.getKey().getContent());
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("UPDATE %s SET properties=?, update_time=? "
                            + " WHERE application_id=? AND platform=? AND content_hash=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, getObjectSegmentMapper().toJson(os.getProperties()));
            stmt.setTimestamp(2, new Timestamp(os.getUpdateTime().getTime()));
            stmt.setBytes(3, Key.fromHexString(os.getKey().getApplicationId()).getKey());
            stmt.setString(4, os.getKey().getPlatform().name());
            stmt.setBytes(5, Key.fromHexString(contentHash).getKey());
            stmt.executeUpdate();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void delete(OAuthIdentifierOS os) {
        String contentHash = hash(os.getKey().getContent());
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE "
                            + " application_id=? AND platform=? AND content_hash=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getKey().getApplicationId()).getKey());
            stmt.setString(2, os.getKey().getPlatform().name());
            stmt.setBytes(3, Key.fromHexString(contentHash).getKey());
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
            String sql = String.format("DELETE FROM %s WHERE "
                            + " application_id=? AND user_id=?;",
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
    public void deleteByPlatform(String applicationId, OAuthPlatform policyPlatform) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE "
                            + " application_id=? AND platform=?;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setString(2, policyPlatform.name());
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
    public OAuthIdentifierOS selectByKey(OAuthIdentifierKey key) {
        String contentHash = hash(key.getContent());
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT * FROM %s WHERE"
                            + " application_id=? AND platform=? AND content_hash=? LIMIT 1 ",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(key.getApplicationId()).getKey());
            stmt.setString(2, key.getPlatform().name());
            stmt.setBytes(3, Key.fromHexString(contentHash).getKey());
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return getObjectSegmentMapper().toOAuthIdentifierOS(rs);
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
    public List<OAuthIdentifierOS> selectByUserId(String applicationId, String userId) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT * FROM %s WHERE "
                            + " application_id=? AND user_id=? ",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(userId).getKey());
            rs = stmt.executeQuery();
            return toOSList(rs, 5);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public PaginatedResult<List<OAuthIdentifierOS>> selectByPlatform(String applicationId, OAuthPlatform platform) {
        return new PaginatedResult<>(request -> _selectByPlatform(request), applicationId, platform);
    }

    public List<OAuthIdentifierOS> _selectByPlatform(ResultFetchRequest request) {
        String applicationId = (String) request.getArguments()[0];
        OAuthPlatform platform = (OAuthPlatform) request.getArguments()[1];
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(String.format("SELECT * FROM %s WHERE "
                            + " application_id=? AND platform=? ",
                    TABLE));
            sql.append(MySQLQueryBuilder.buildSortingQuery(request.getSorting()));
            sql.append(" ");
            sql.append(MySQLQueryBuilder.buildPagingQuery((OffsetBasedResultPage) request.getPage()));
            stmt = conn.prepareCall(sql.toString());
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setString(2, platform.name());
            rs = stmt.executeQuery();
            return toOSList(rs, request.getPage().getSize());
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    private List<OAuthIdentifierOS> toOSList(ResultSet rs, int size) throws SQLException, IOException {
        List<OAuthIdentifierOS> result = new ArrayList<>(size);
        while (rs.next()) {
            result.add(getObjectSegmentMapper().toOAuthIdentifierOS(rs));
        }
        return result;
    }
}
