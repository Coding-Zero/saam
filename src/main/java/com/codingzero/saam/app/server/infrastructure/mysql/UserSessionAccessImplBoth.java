package com.codingzero.saam.app.server.infrastructure.mysql;

import com.codingzero.saam.app.server.infrastructure.mysql.commons.AbstractAccess;
import com.codingzero.saam.infrastructure.data.UserSessionAccess;
import com.codingzero.saam.infrastructure.data.UserSessionOS;
import com.codingzero.utilities.key.Key;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.ResultFetchRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class UserSessionAccessImplBoth extends AbstractAccess implements UserSessionAccess {

//    public static final String TABLE = "user_sessions_v1";
    public static final String TABLE = "saam.user_sessions";

    private UserSessionAccessImpl helper;

    public UserSessionAccessImplBoth(UserSessionAccessImpl helper) {
        super(helper.getDataSource(), helper.getObjectSegmentMapper());
        this.helper = helper;
    }

    @Override
    public String generateKey(String applicationId) {
        return helper.generateKey(applicationId);
    }

    @Override
    public int countByUserId(String applicationId, String userId) {
        return helper.countByUserId(applicationId, userId);
    }

    private boolean isVersion2(String applicationId, String key) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT COUNT(*) FROM %s WHERE "
                            + "application_id=? AND `key`=? LIMIT 1;",
                    UserSessionAccessImpl.TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(key).getKey());
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

    private boolean isVersion1(String applicationId, String key) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT COUNT(*) FROM %s WHERE "
                            + "application_id=? AND `key`=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(key).getKey());
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
    public void insert(UserSessionOS os) {
        if (!isVersion1(os.getApplicationId(), os.getKey())) {
            insertV1(os);
        }
        helper.insert(os);
    }

    private void insertV1(UserSessionOS os) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                    TABLE,
                    "application_id, `key`, expiration_time, creation_time, user_id, metadata",
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
        helper.delete(os);
        deleteV1(os);
    }

    private void deleteV1(UserSessionOS os) {
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
        helper.deleteByUserId(applicationId, userId);
        deleteByUserIdV1(applicationId, userId);
    }

    private void deleteByUserIdV1(String applicationId, String userId) {
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
        helper.deleteByApplicationId(id);
        deleteByApplicationIdV1(id);
    }

    private void deleteByApplicationIdV1(String id) {
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
        UserSessionOS os = helper.selectByKey(applicationId, key);
        if (null != os) {
            return os;
        }
        os = selectByKeyV1(applicationId, key);
        if (null != os) {
            helper.insert(os);
        }
        return os;
    }

    private UserSessionOS selectByKeyV1(String applicationId, String key) {
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
                return getObjectSegmentMapper().toUserSessionOSV1(rs);
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

    private List<UserSessionOS> _selectByUserId(ResultFetchRequest request) {
        return helper._selectByUserId(request);
    }

}
