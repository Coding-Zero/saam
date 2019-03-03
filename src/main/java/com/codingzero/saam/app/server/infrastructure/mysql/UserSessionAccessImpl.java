package com.codingzero.saam.app.server.infrastructure.mysql;

import com.codingzero.saam.app.server.infrastructure.mysql.commons.AbstractAccess;
import com.codingzero.saam.app.server.infrastructure.mysql.commons.MySQLQueryBuilder;
import com.codingzero.saam.infrastructure.data.UserSessionAccess;
import com.codingzero.saam.infrastructure.data.UserSessionOS;
import com.codingzero.utilities.key.HMACKey;
import com.codingzero.utilities.key.Key;
import com.codingzero.utilities.key.RandomKey;
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

public class UserSessionAccessImpl extends AbstractAccess implements UserSessionAccess {

    public static final String TABLE = "saam_v2.user_sessions";

    public UserSessionAccessImpl(DataSource dataSource, ObjectSegmentMapper objectSegmentMapper) {
        super(dataSource, objectSegmentMapper);
    }

    @Override
    public String generateKey(String applicationId) {
        return RandomKey.nextUUIDKey()
                .toRandomHMACKey(HMACKey.Algorithm.SHA256)
                .toBase64String(true);
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
    public void insert(UserSessionOS os) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                    TABLE,
                    "application_id, `key`, expiration_time, creation_time, user_id, details",
                    "?, ?, ?, ?, ?, ?");
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setString(2, os.getKey());
            stmt.setTimestamp(3, new Timestamp(os.getExpirationTime().getTime()));
            stmt.setTimestamp(4, new Timestamp(os.getCreationTime().getTime()));
            stmt.setBytes(5, Key.fromHexString(os.getUserId()).getKey());
            stmt.setString(6, getObjectSegmentMapper().toJson(os.getDetails()));

            stmt.executeUpdate();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void delete(UserSessionOS os) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE application_id=? AND `key`=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setString(2, os.getKey());
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
            String sql = String.format("DELETE FROM %s WHERE application_id=? AND user_id=? LIMIT 1000;",
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
    public void deleteByApplicationId(String id) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE application_id=? LIMIT 1000;",
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
    public UserSessionOS selectByKey(String applicationId, String key) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT * FROM %s WHERE application_id=? AND `key`=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setString(2, key);
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return getObjectSegmentMapper().toUserSessionOS(rs);
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
    public PaginatedResult<List<UserSessionOS>> selectByUserId(String applicationId, String userId) {
        return new PaginatedResult<>(
                request -> _selectByUserId(request),
                applicationId, userId);
    }

    public List<UserSessionOS> _selectByUserId(ResultFetchRequest request) {
        String applicationId = (String) request.getArguments()[0];
        String userId = (String) request.getArguments()[1];
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(String.format("SELECT * FROM %s WHERE application_id=? AND user_id=? ",
                    TABLE));
            sql.append(MySQLQueryBuilder.buildSortingQuery(request.getSorting()));
            sql.append(" ");
            sql.append(MySQLQueryBuilder.buildPagingQuery((OffsetBasedResultPage) request.getPage()));
            stmt = conn.prepareCall(sql.toString());
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(userId).getKey());
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

    private List<UserSessionOS> toOSList(ResultSet rs, ResultPage pageOffset) throws SQLException, IOException {
        List<UserSessionOS> result = new ArrayList<>(pageOffset.getSize());
        while (rs.next()) {
            result.add(getObjectSegmentMapper().toUserSessionOS(rs));
        }
        return result;
    }
}
