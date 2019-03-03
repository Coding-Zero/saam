package com.codingzero.saam.app.server.infrastructure.mysql.commons;

import com.codingzero.saam.app.server.infrastructure.mysql.PrincipalAccessImpl;
import com.codingzero.saam.infrastructure.data.PrincipalOS;
import com.codingzero.utilities.key.Key;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PrincipalOSHelper {

    public static void insert(PrincipalOS os, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                    PrincipalAccessImpl.TABLE,
                    "application_id, id, type, creation_time",
                    "?, ?, ?, ?");
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getId().getApplicationId()).getKey());
            stmt.setBytes(2, Key.fromHexString(os.getId().getId()).getKey());
            stmt.setString(3, os.getType().name());
            stmt.setTimestamp(4, new Timestamp(os.getCreationTime().getTime()));
            stmt.executeUpdate();
        } finally {
            try {
                if (null != stmt) {
                    stmt.close();
                }
            } catch (SQLException ee) {
                throw new RuntimeException(ee);
            }
        }
    }

}
