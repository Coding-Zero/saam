package com.codingzero.saam.app.server.base.mysql;

import com.codingzero.saam.common.IdentifierKey;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.infrastructure.database.IdentifierOS;
import com.codingzero.saam.infrastructure.database.IdentifierAccess;
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


public class IdentifierAccessImpl extends AbstractAccess implements IdentifierAccess {

    public static final String TABLE = "identifiers";

    public IdentifierAccessImpl(DataSource dataSource, ObjectSegmentMapper objectSegmentMapper) {
        super(dataSource, objectSegmentMapper);
    }


    @Override
    public boolean isDuplicateContent(String applicationId, String content) {
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
        return 0;
    }

    @Override
    public int countByType(String applicationId, IdentifierType type) {
        return 0;
    }

    private String hash(String key) {
        return Key.fromString(key.toLowerCase())
                .toHMACKey(HMACKey.Algorithm.SHA256)
                .toHexString();
    }

    @Override
    public void insert(IdentifierOS os) {
        String contentHash = hash(os.getKey().getContent());
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                    TABLE,
                    "application_id, identifier_type, content_hash, content, user_id,"
                            + " is_verified, verification_code, creation_time, update_time ",
                    "?, ?, ?, ?, ?, ?, ?, ?, ?");
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getKey().getApplicationId()).getKey());
            stmt.setString(2, os.getType().name());
            stmt.setBytes(3, Key.fromHexString(contentHash).getKey());
            stmt.setString(4, os.getKey().getContent());
            stmt.setBytes(5, Key.fromHexString(os.getUserId()).getKey());
            stmt.setBoolean(6, os.isVerified());
            stmt.setString(7, getObjectSegmentMapper().toJson(os.getVerificationCode()));
            stmt.setTimestamp(8, new Timestamp(os.getCreationTime().getTime()));
            stmt.setTimestamp(9, new Timestamp(os.getUpdateTime().getTime()));
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
        String contentHash = hash(os.getKey().getContent());
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("UPDATE %s SET is_verified=?, verification_code=?, update_time=? "
                            + " WHERE application_id=? AND identifier_type=? AND content_hash=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, os.isVerified());
            stmt.setString(2, getObjectSegmentMapper().toJson(os.getVerificationCode()));
            stmt.setTimestamp(3, new Timestamp(os.getUpdateTime().getTime()));
            stmt.setBytes(4, Key.fromHexString(os.getKey().getApplicationId()).getKey());
            stmt.setString(5, os.getType().name());
            stmt.setBytes(6, Key.fromHexString(contentHash).getKey());
            stmt.executeUpdate();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void delete(IdentifierOS os) {
        String contentHash = hash(os.getKey().getContent());
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE "
                            + "application_id=? AND identifier_type=? AND content_hash=? LIMIT 1;",
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
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE application_id=? AND identifier_type=? LIMIT 10000;",
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
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE application_id=? AND identifier_type=? AND user_id=?;",
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
            stmt.setBytes(3, Key.fromHexString(contentHash).getKey());
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return getObjectSegmentMapper().toIdentifierOS(rs);
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
    public PaginatedResult<List<IdentifierOS>> selectByType(String applicationId, IdentifierType type) {
        return new PaginatedResult<>(request -> _selectByType(request), applicationId, type);
    }

    @Override
    public PaginatedResult<List<IdentifierOS>> selectByApplicationId(String applicationId) {
        return new PaginatedResult<>(request -> _selectByApplicationId(request), applicationId);
    }

    private List<IdentifierOS> _selectByType(ResultFetchRequest request) {
        String applicationId = (String) request.getArguments()[0];
        IdentifierType type = (IdentifierType) request.getArguments()[1];
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(String.format("SELECT * FROM %s WHERE "
                    + " application_id=? AND identifier_type=?",
                    TABLE));
            sql.append(MySQLQueryHelper.buildSortingQuery(request.getSorting()));
            sql.append(" ");
            sql.append(MySQLQueryHelper.buildPagingQuery((OffsetBasedResultPage) request.getPage()));
            stmt = conn.prepareCall(sql.toString());
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setString(2, type.name());
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

    private List<IdentifierOS> _selectByApplicationId(ResultFetchRequest request) {
        String applicationId = (String) request.getArguments()[0];
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(String.format("SELECT * FROM %s WHERE "
                            + " application_id=? ",
                    TABLE));
            sql.append(MySQLQueryHelper.buildSortingQuery(request.getSorting()));
            sql.append(" ");
            sql.append(MySQLQueryHelper.buildPagingQuery((OffsetBasedResultPage) request.getPage()));
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

    private List<IdentifierOS> toOSList(ResultSet rs, int size) throws SQLException, IOException {
        List<IdentifierOS> result = new ArrayList<>(size);
        while (rs.next()) {
            result.add(getObjectSegmentMapper().toIdentifierOS(rs));
        }
        return result;
    }
}
