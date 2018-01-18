package com.codingzero.saam.app.server.base.mysql;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierOS;
import com.codingzero.saam.infrastructure.database.spi.OAuthIdentifierAccess;
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

    public static final String TABLE = "oauth_identifiers";

    public OAuthIdentifierAccessImpl(DataSource dataSource, ObjectSegmentMapper objectSegmentMapper) {
        super(dataSource, objectSegmentMapper);
    }

    @Override
    public boolean isDuplicateContent(String applicationId, OAuthPlatform policyPlatform, String content) {
        String contentHash = hash(content);
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT COUNT(*) FROM %s WHERE "
                            + "application_id=? AND platform=? AND content_hash=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setString(2, policyPlatform.name());
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
    public void insert(OAuthIdentifierOS os) {
        String contentHash = hash(os.getContent());
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                    TABLE,
                    "application_id, platform, content_hash, content, user_id, properties, creation_time, update_time ",
                    "?, ?, ?, ?, ?, ?, ?, ?");
            stmt = conn.prepareStatement(sql);

            stmt.setBytes(1, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setString(2, os.getPlatform().name());
            stmt.setBytes(3, Key.fromHexString(contentHash).getKey());
            stmt.setString(4, os.getContent());
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
        String contentHash = hash(os.getContent());
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("UPDATE %s SET properties=?, update_time=? "
                            + " WHERE application_id=? AND platform=? AND content_hash=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, getObjectSegmentMapper().toJson(os.getProperties()));
            stmt.setTimestamp(2, new Timestamp(os.getUpdateTime().getTime()));
            stmt.setBytes(3, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setString(4, os.getPlatform().name());
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
        String contentHash = hash(os.getContent());
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE "
                            + " application_id=? AND platform=? AND content_hash=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setString(2, os.getPlatform().name());
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
    public void deleteByPlatformAndUserId(String applicationId, OAuthPlatform platform, String userId) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE "
                            + "application_id=? AND platform=? AND user_id=?;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setString(2, platform.name());
            stmt.setBytes(3, Key.fromHexString(userId).getKey());
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
    public OAuthIdentifierOS selectByPlatformAndContent(String applicationId, OAuthPlatform platform, String content) {
        String contentHash = hash(content);
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT * FROM %s WHERE"
                            + " application_id=? AND platform=? AND content_hash=? LIMIT 1 ",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setString(2, platform.name());
            stmt.setBytes(3, Key.fromHexString(contentHash).getKey());
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return getObjectSegmentMapper().toSSOIdentifierOS(rs);
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
    public List<OAuthIdentifierOS> selectByPlatformAndUserId(String applicationId, OAuthPlatform platform, String userId) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT * FROM %s WHERE "
                            + " application_id=? AND platform=? AND user_id=? ",
                    TABLE);
            stmt = conn.prepareCall(sql.toString());
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setString(2, platform.name());
            stmt.setBytes(3, Key.fromHexString(userId).getKey());
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

    private List<OAuthIdentifierOS> _selectByPlatform(ResultFetchRequest request) {
        String applicationId = (String) request.getArguments()[0];
        OAuthPlatform platform = (OAuthPlatform) request.getArguments()[1];
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(String.format("SELECT * FROM %s WHERE "
                            + " application_id=? AND platform=?;",
                    TABLE));
            sql.append(MySQLHelper.buildSortingQuery(request.getSorting()));
            sql.append(" ");
            sql.append(MySQLHelper.buildPagingQuery((OffsetBasedResultPage) request.getPage()));
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
            result.add(getObjectSegmentMapper().toSSOIdentifierOS(rs));
        }
        return result;
    }
}
