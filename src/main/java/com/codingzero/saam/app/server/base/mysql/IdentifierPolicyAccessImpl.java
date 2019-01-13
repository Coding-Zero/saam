package com.codingzero.saam.app.server.base.mysql;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.infrastructure.database.IdentifierPolicyOS;
import com.codingzero.saam.infrastructure.database.IdentifierPolicyAccess;
import com.codingzero.utilities.key.Key;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class IdentifierPolicyAccessImpl extends AbstractAccess implements IdentifierPolicyAccess {

    public static final String TABLE = "identifier_policies";

    public IdentifierPolicyAccessImpl(DataSource dataSource, ObjectSegmentMapper objectSegmentMapper) {
        super(dataSource, objectSegmentMapper);
    }

    @Override
    public boolean isDuplicateType(String applicationId, IdentifierType type) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT COUNT(*) FROM %s WHERE application_id=? AND type=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setString(2, type.name());
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

//    @Override
//    public void insert(IdentifierPolicyOS os) {
//        Connection conn = getConnection();
//        PreparedStatement stmt = null;
//        ResultSet rs = null;
//        try {
//            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
//                    TABLE,
//                    "application_id, type, is_verification_required, " +
//                            "min_length, max_length, is_active, creation_time, update_time",
//                    "?, ?, ?, ?, ?, ?, ?, ?");
//            stmt = conn.prepareStatement(sql);
//            stmt.setBytes(1, Key.fromHexString(os.getApplicationId()).getKey());
//            stmt.setString(2, os.getType().name());
//            stmt.setBoolean(3, os.isVerificationRequired());
//            stmt.setInt(4, os.getMinLength());
//            stmt.setInt(5, os.getMaxLength());
//            stmt.setBoolean(6, os.isActive());
//            stmt.setTimestamp(7, new Timestamp(os.getCreationTime().getTime()));
//            stmt.setTimestamp(8, new Timestamp(os.getUpdateTime().getTime()));
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        } finally {
//            closeResultSet(rs);
//            closePreparedStatement(stmt);
//            closeConnection(conn);
//        }
//    }

//    @Override
//    public void update(IdentifierPolicyOS os) {
//        Connection conn = getConnection();
//        PreparedStatement stmt = null;
//        try {
//            String sql = String.format("UPDATE %s SET is_verification_required=?,"
//                            + " min_length=?, max_length=?, is_active=?, update_time=? "
//                            + " WHERE application_id=? AND type=? LIMIT 1;",
//                    TABLE);
//            stmt = conn.prepareStatement(sql);
//            stmt.setBoolean(1, os.isVerificationRequired());
//            stmt.setInt(2, os.getMinLength());
//            stmt.setInt(3, os.getMaxLength());
//            stmt.setBoolean(4, os.isActive());
//            stmt.setTimestamp(5, new Timestamp(os.getUpdateTime().getTime()));
//            stmt.setBytes(6, Key.fromHexString(os.getApplicationId()).getKey());
//            stmt.setString(7, os.getType().name());
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        } finally {
//            closePreparedStatement(stmt);
//            closeConnection(conn);
//        }
//    }

//    @Override
//    public void delete(IdentifierPolicyOS os) {
//        Connection conn = getConnection();
//        PreparedStatement stmt=null;
//        try {
//            String sql = String.format("DELETE FROM %s WHERE application_id=? AND type=? LIMIT 1;",
//                    TABLE);
//            stmt = conn.prepareStatement(sql);
//            stmt.setBytes(1, Key.fromHexString(os.getApplicationId()).getKey());
//            stmt.setString(2, os.getType().name());
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        } finally {
//            closePreparedStatement(stmt);
//            closeConnection(conn);
//        }
//    }

//    @Override
//    public void deleteByApplicationId(String applicationId) {
//        Connection conn = getConnection();
//        PreparedStatement stmt=null;
//        try {
//            String sql = String.format("DELETE FROM %s WHERE application_id=?;",
//                    TABLE);
//            stmt = conn.prepareStatement(sql);
//            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        } finally {
//            closePreparedStatement(stmt);
//            closeConnection(conn);
//        }
//    }

    @Override
    public IdentifierPolicyOS selectByType(String applicationId, IdentifierType type) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT * FROM %s WHERE application_id=? AND type=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setString(2, type.name());
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return getObjectSegmentMapper().toIdentifierPolicyOS(rs);
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
    public List<IdentifierPolicyOS> selectByApplicationId(String applicationId) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT * FROM %s WHERE application_id=? ", TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());

            rs = stmt.executeQuery();
            return toOSList(rs);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    private List<IdentifierPolicyOS> toOSList(ResultSet rs) throws SQLException, IOException {
        List<IdentifierPolicyOS> result = new ArrayList<>(10);
        while (rs.next()) {
            result.add(getObjectSegmentMapper().toIdentifierPolicyOS(rs));
        }
        return result;
    }

}
