package com.codingzero.saam.app.server.base.mysql;

import com.codingzero.saam.infrastructure.database.IdentifierPolicyOS;
import com.codingzero.utilities.key.Key;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class IdentifierPolicyOSHelper {

    public static void insert(IdentifierPolicyOS os, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                    IdentifierPolicyAccessImpl.TABLE,
                    "application_id, type, is_verification_required, " +
                            "min_length, max_length, is_active, creation_time, update_time",
                    "?, ?, ?, ?, ?, ?, ?, ?");
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setString(2, os.getType().name());
            stmt.setBoolean(3, os.isVerificationRequired());
            stmt.setInt(4, os.getMinLength());
            stmt.setInt(5, os.getMaxLength());
            stmt.setBoolean(6, os.isActive());
            stmt.setTimestamp(7, new Timestamp(os.getCreationTime().getTime()));
            stmt.setTimestamp(8, new Timestamp(os.getUpdateTime().getTime()));
            stmt.executeUpdate();
        } finally {
            closePrepareStatement(stmt);
        }
    }

    public static void update(IdentifierPolicyOS os, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = String.format("UPDATE %s SET is_verification_required=?,"
                            + " min_length=?, max_length=?, is_active=?, update_time=? "
                            + " WHERE application_id=? AND type=? LIMIT 1;",
                    IdentifierPolicyAccessImpl.TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, os.isVerificationRequired());
            stmt.setInt(2, os.getMinLength());
            stmt.setInt(3, os.getMaxLength());
            stmt.setBoolean(4, os.isActive());
            stmt.setTimestamp(5, new Timestamp(os.getUpdateTime().getTime()));
            stmt.setBytes(6, Key.fromHexString(os.getApplicationId()).getKey());
            stmt.setString(7, os.getType().name());
            stmt.executeUpdate();
        } finally {
            closePrepareStatement(stmt);
        }
    }

    private static void closePrepareStatement(PreparedStatement stmt) {
        try {
            if (null != stmt) {
                stmt.close();
            }
        } catch (SQLException ee) {
            throw new RuntimeException(ee);
        }
    }

}
