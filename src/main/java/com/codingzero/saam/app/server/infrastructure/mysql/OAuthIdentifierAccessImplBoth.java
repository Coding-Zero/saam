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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class OAuthIdentifierAccessImplBoth extends AbstractAccess implements OAuthIdentifierAccess {

//    public static final String TABLE = "sso_identifiers_v1";
    public static final String TABLE = "saam.sso_identifiers";

    private OAuthIdentifierAccessImpl helper;

    public OAuthIdentifierAccessImplBoth(OAuthIdentifierAccessImpl helper) {
        super(helper.getDataSource(), helper.getObjectSegmentMapper());
        this.helper = helper;
    }

    @Override
    public boolean isDuplicateKey(OAuthIdentifierKey key) {
        if (isDuplicateKeyV1(key)) {
            return true;
        }
        return helper.isDuplicateKey(key);
    }

    private boolean isDuplicateKeyV1(OAuthIdentifierKey key) {
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

    private boolean isVersion2(OAuthIdentifierKey key) {
        String contentHash = hash(key.getContent());
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT COUNT(*) FROM %s WHERE "
                            + "application_id=? AND platform=? AND content_hash=? LIMIT 1;",
                    OAuthIdentifierAccessImpl.TABLE);
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

    private boolean isVersion1(OAuthIdentifierKey key) {
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

    @Override
    public int countByUserId(String applicationId, String userId) {
        return helper.countByUserId(applicationId, userId);
    }

    @Override
    public int countByPlatform(String applicationId, OAuthPlatform platform) {
        return helper.countByPlatform(applicationId, platform);
    }

    @Override
    public void insert(OAuthIdentifierOS os) {
        if (!isVersion1(os.getKey())) {
            insertV1(os);
        }
        helper.insert(os);
    }

    private void insertV1(OAuthIdentifierOS os) {
        String contentHash = hash(os.getKey().getContent());
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                    TABLE,
                    "application_id, platform, content_hash, content, user_id, properties, creation_time ",
                    "?, ?, ?, ?, ?, ?, ?");
            stmt = conn.prepareStatement(sql);

            stmt.setBytes(1, Key.fromHexString(os.getKey().getApplicationId()).getKey());
            stmt.setString(2, os.getKey().getPlatform().name());
            stmt.setBytes(3, Key.fromHexString(contentHash).getKey());
            stmt.setString(4, os.getKey().getContent());
            stmt.setBytes(5, Key.fromHexString(os.getUserId()).getKey());
            stmt.setString(6, getObjectSegmentMapper().toJson(os.getProperties()));
            stmt.setTimestamp(7, new Timestamp(os.getCreationTime().getTime()));
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
        if (isVersion1(os.getKey())
                && !isVersion2(os.getKey())) {
            helper.insert(os);
        }
        helper.update(os);
    }

    @Override
    public void delete(OAuthIdentifierOS os) {
        helper.delete(os);
        deleteV1(os);
    }

    private void deleteV1(OAuthIdentifierOS os) {
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
        helper.deleteByUserId(applicationId, userId);
        deleteByUserIdV1(applicationId, userId);
    }

    private void deleteByUserIdV1(String applicationId, String userId) {
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
        helper.deleteByPlatform(applicationId, policyPlatform);
        deleteByPlatformV1(applicationId, policyPlatform);
    }

    private void deleteByPlatformV1(String applicationId, OAuthPlatform policyPlatform) {
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
        helper.deleteByApplicationId(id);
        deleteByApplicationIdV1(id);
    }

    private void deleteByApplicationIdV1(String id) {
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
        OAuthIdentifierOS os = helper.selectByKey(key);
        if (null != os) {
            return os;
        }
        os = selectByKeyV1(key);
        if (null != os) {
            helper.insert(os);
        }
        return os;
    }

    private OAuthIdentifierOS selectByKeyV1(OAuthIdentifierKey key) {
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
                return getObjectSegmentMapper().toOAuthIdentifierOSV1(rs);
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
        if (hasMigratedForSelectByUserId(applicationId, userId)) {
            List<OAuthIdentifierOS> osList = selectByUserIdV1(applicationId, userId);
            for (OAuthIdentifierOS os: osList) {
                if (!isVersion2(os.getKey())) {
                    helper.insert(os);
                }
            }
        }
        return helper.selectByUserId(applicationId, userId);
    }

    private List<OAuthIdentifierOS> selectByUserIdV1(String applicationId, String userId) {
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

    public boolean hasMigratedForSelectByUserId(String applicationId, String userId) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT count(*) FROM %s v1 \n" +
                            "\tLEFT JOIN %s v2\n" +
                            "\t\tON v1.application_id = v2.application_id \n" +
                            "\t\t\tAND v1.platform = v2.platform\n" +
                            "\t\t\tAND v1.content_hash = v2.content_hash\n" +
                            "\tWHERE v1.application_id= ? \n" +
                            "\t\t\tAND v1.user_id = ? \n" +
                            "            AND v2.update_time IS NULL LIMIT 1",
                    TABLE,
                    OAuthIdentifierAccessImpl.TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(userId).getKey());
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
    public PaginatedResult<List<OAuthIdentifierOS>> selectByPlatform(String applicationId, OAuthPlatform platform) {
        return new PaginatedResult<>(request -> _selectByPlatform(request), applicationId, platform);
    }

    private List<OAuthIdentifierOS> _selectByPlatform(ResultFetchRequest request) {
        if (hasMigratedForSelectByPlatform(request)) {
            List<OAuthIdentifierOS> osList = _selectByPlatformV1(request);
            for (OAuthIdentifierOS os: osList) {
                if (!isVersion2(os.getKey())) {
                    helper.insert(os);
                }
            }
        }
        return helper._selectByPlatform(request);
    }

    private List<OAuthIdentifierOS> _selectByPlatformV1(ResultFetchRequest request) {
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

    public boolean hasMigratedForSelectByPlatform(ResultFetchRequest request) {
        String applicationId = (String) request.getArguments()[0];
        OAuthPlatform platform = (OAuthPlatform) request.getArguments()[1];
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT count(*) FROM %s v1 \n" +
                            "\tLEFT JOIN %s v2\n" +
                            "\t\tON v1.application_id = v2.application_id \n" +
                            "\t\t\tAND v1.platform = v2.platform\n" +
                            "\t\t\tAND v1.content_hash = v2.content_hash\n" +
                            "\tWHERE v1.application_id= ? \n" +
                            "\t\t\tAND v1.platform = ? \n" +
                            "            AND v2.update_time IS NULL ",
                    TABLE,
                    OAuthIdentifierAccessImpl.TABLE);
            StringBuilder sqlBuilder = new StringBuilder(sql);
            sqlBuilder.append(MySQLQueryBuilder.buildSortingQuery(request.getSorting(), "v1"));
            sqlBuilder.append(" LIMIT 1; ");
            sql = sqlBuilder.toString();
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setString(2, platform.name());
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

    private List<OAuthIdentifierOS> toOSList(ResultSet rs, int size) throws SQLException, IOException {
        List<OAuthIdentifierOS> result = new ArrayList<>(size);
        while (rs.next()) {
            result.add(getObjectSegmentMapper().toOAuthIdentifierOSV1(rs));
        }
        return result;
    }
}
