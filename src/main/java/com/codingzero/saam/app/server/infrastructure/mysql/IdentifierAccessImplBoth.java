package com.codingzero.saam.app.server.infrastructure.mysql;

import com.codingzero.saam.app.server.infrastructure.mysql.commons.AbstractAccess;
import com.codingzero.saam.app.server.infrastructure.mysql.commons.MySQLQueryBuilder;
import com.codingzero.saam.common.IdentifierKey;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.infrastructure.data.IdentifierAccess;
import com.codingzero.saam.infrastructure.data.IdentifierOS;
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


public class IdentifierAccessImplBoth extends AbstractAccess implements IdentifierAccess {

//    public static final String TABLE = "identifiers_v1";
    public static final String TABLE = "saam.identifiers";

    private IdentifierAccessImpl helper;

    public IdentifierAccessImplBoth(IdentifierAccessImpl helper) {
        super(helper.getDataSource(), helper.getObjectSegmentMapper());
        this.helper = helper;
    }

    @Override
    public boolean isDuplicateContent(String applicationId, String content) {
        if (isDuplicateContentV1(applicationId, content)) {
            return true;
        }
        return helper.isDuplicateContent(applicationId, content);
    }

    private boolean isDuplicateContentV1(String applicationId, String content) {
        String contentHash = hash(content);
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT COUNT(*) FROM %s WHERE "
                            + "application_id=? AND content_hash=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(contentHash).getKey());
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

    private boolean isVersion2(String applicationId, String content) {
        String contentHash = hash(content);
        Connection conn = getConnection();
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT COUNT(*) FROM %s WHERE "
                            + "application_id=? AND content_hash=? LIMIT 1;",
                    IdentifierAccessImpl.TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(contentHash).getKey());
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

    private boolean isVersion1(String applicationId, String content) {
        String contentHash = hash(content);
        Connection conn = getConnection();
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT COUNT(*) FROM %s WHERE "
                            + "application_id=? AND content_hash=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(contentHash).getKey());
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
    public int countByType(String applicationId, IdentifierType type) {
        return helper.countByType(applicationId, type);
    }

    private String hash(String key) {
        return Key.fromString(key.toLowerCase())
                .toHMACKey(HMACKey.Algorithm.SHA256)
                .toHexString();
    }

    @Override
    public void insert(IdentifierOS os) {
        if (!isVersion1(os.getKey().getApplicationId(), os.getKey().getContent())) {
            insertV1(os);
        }
        helper.insert(os);
    }

    private void insertV1(IdentifierOS os) {
        String contentHash = hash(os.getKey().getContent());
        Connection conn = getConnection();
        
        PreparedStatement stmt = null;
        try {
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                    TABLE,
                    "application_id, identifier_policy_code, type, content_hash, content, user_id,"
                            + " is_verified, verification_code, creation_time ",
                    "?, ?, ?, ?, ?, ?, ?, ?, ?");
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getKey().getApplicationId()).getKey());
            stmt.setString(2, os.getType().name());
            stmt.setString(3, os.getType().name());
            stmt.setBytes(4, Key.fromHexString(contentHash).getKey());
            stmt.setString(5, os.getKey().getContent());
            stmt.setBytes(6, Key.fromHexString(os.getUserId()).getKey());
            stmt.setBoolean(7, os.isVerified());
            stmt.setString(8, getObjectSegmentMapper().toJson(os.getVerificationCode()));
            stmt.setTimestamp(9, new Timestamp(os.getCreationTime().getTime()));
            stmt.executeUpdate();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void update(IdentifierOS os) {
        if (isVersion1(os.getKey().getApplicationId(), os.getKey().getContent())
                && !isVersion2(os.getKey().getApplicationId(), os.getKey().getContent())) {
            helper.insert(os);
        }
        helper.update(os);
    }

    @Override
    public void delete(IdentifierOS os) {
        helper.delete(os);
        deleteV1(os);
    }

    private void deleteV1(IdentifierOS os) {
        String contentHash = hash(os.getKey().getContent());
        Connection conn = getConnection();
        
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE "
                            + "application_id=? AND content_hash=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getKey().getApplicationId()).getKey());
            stmt.setString(2, os.getType().name());
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
    public void deleteByType(String applicationId, IdentifierType type) {
        helper.deleteByType(applicationId, type);
        deleteByTypeV1(applicationId, type);
    }

    private void deleteByTypeV1(String applicationId, IdentifierType type) {
        Connection conn = getConnection();
        
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE application_id=? AND type=? LIMIT 10000;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setString(2, type.name());

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
    public void deleteByUserId(String applicationId, String userId) {
        helper.deleteByUserId(applicationId, userId);
        deleteByUserIdV1(applicationId, userId);
    }

    private void deleteByUserIdV1(String applicationId, String userId) {
        Connection conn = getConnection();
        
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE application_id=? AND user_id=? LIMIT 10000;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(userId).getKey());

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
    public void deleteByTypeAndUserId(String applicationId, IdentifierType type, String userId) {
        helper.deleteByTypeAndUserId(applicationId, type, userId);
        deleteByTypeAndUserIdV1(applicationId, type, userId);
    }

    public void deleteByTypeAndUserIdV1(String applicationId, IdentifierType type, String userId) {
        Connection conn = getConnection();
        
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE application_id=? AND type=? AND user_id=?;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setString(2, type.name());
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
    public IdentifierOS selectByKey(IdentifierKey key) {
        IdentifierOS os = helper.selectByKey(key);
        if (null != os) {
            return os;
        }
        os = selectByKeyV1(key);
        if (null != os) {
            helper.insert(os);
        }
        return os;
    }

    private IdentifierOS selectByKeyV1(IdentifierKey key) {
        String contentHash = hash(key.getContent());
        Connection conn = getConnection();
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT * FROM %s WHERE"
                            + " application_id=? AND content_hash=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(key.getApplicationId()).getKey());
            stmt.setBytes(2, Key.fromHexString(contentHash).getKey());
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return getObjectSegmentMapper().toIdentifierOSV1(rs);
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
    public List<IdentifierOS> selectByUserId(String applicationId, String userId) {
        if (hasMigratedForSelectByUserId(applicationId, userId)) {
            List<IdentifierOS> osList = selectByUserIdV1(applicationId, userId);
            for (IdentifierOS os: osList) {
                if (!isVersion2(os.getKey().getApplicationId(), os.getKey().getContent())) {
                    helper.insert(os);
                }
            }
        }
        return helper.selectByUserId(applicationId, userId);
    }

    private List<IdentifierOS> selectByUserIdV1(String applicationId, String userId) {
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
                            "\t\t\tAND v1.content_hash = v2.content_hash\n" +
                            "\tWHERE v1.application_id= ? \n" +
                            "\t\t\tAND v1.user_id = ? \n" +
                            "            AND v2.identifier_type IS NULL LIMIT 1",
                    TABLE,
                    IdentifierAccessImpl.TABLE);
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
    public PaginatedResult<List<IdentifierOS>> selectByType(String applicationId, IdentifierType type) {
        return new PaginatedResult<>(request -> _selectByType(request), applicationId, type);
    }

    @Override
    public PaginatedResult<List<IdentifierOS>> selectByApplicationId(String applicationId) {
        return new PaginatedResult<>(request -> _selectByApplicationId(request), applicationId);
    }

    private List<IdentifierOS> _selectByType(ResultFetchRequest request) {
        return helper._selectByType(request);
    }

    private List<IdentifierOS> _selectByApplicationId(ResultFetchRequest request) {
        if (hasMigratedForSelectByApplicationId(request)) {
            List<IdentifierOS> osList = _selectByApplicationIdV1(request);
            for (IdentifierOS os: osList) {
                if (!isVersion2(os.getKey().getApplicationId(), os.getKey().getContent())) {
                    helper.insert(os);
                }
            }
        }
        return helper._selectByApplicationId(request);

    }

    private List<IdentifierOS> _selectByApplicationIdV1(ResultFetchRequest request) {
        String applicationId = (String) request.getArguments()[0];
        Connection conn = getConnection();
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(String.format("SELECT * FROM %s WHERE "
                            + " application_id=? ",
                    TABLE));
            sql.append(MySQLQueryBuilder.buildSortingQuery(request.getSorting()));
            sql.append(" ");
            sql.append(MySQLQueryBuilder.buildPagingQuery((OffsetBasedResultPage) request.getPage()));
            stmt = conn.prepareCall(sql.toString());
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
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

    public boolean hasMigratedForSelectByApplicationId(ResultFetchRequest request) {
        String applicationId = (String) request.getArguments()[0];
        Connection conn = getConnection();
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT count(*) FROM %s v1 \n" +
                            "\tLEFT JOIN %s v2\n" +
                            "\t\tON v1.application_id = v2.application_id \n" +
                            "\t\t\tAND v1.content_hash = v2.content_hash\n" +
                            "\tWHERE v1.application_id= ? \n" +
                            "            AND v2.identifier_type IS NULL ",
                    TABLE,
                    IdentifierAccessImpl.TABLE);
            StringBuilder sqlBuilder = new StringBuilder(sql);
            sqlBuilder.append(MySQLQueryBuilder.buildSortingQuery(request.getSorting(), "v1"));
            sqlBuilder.append(" LIMIT 1; ");
            sql = sqlBuilder.toString();
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
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

    private List<IdentifierOS> toOSList(ResultSet rs, int size) throws SQLException, IOException {
        List<IdentifierOS> result = new ArrayList<>(size);
        while (rs.next()) {
            result.add(getObjectSegmentMapper().toIdentifierOSV1(rs));
        }
        return result;
    }
}
