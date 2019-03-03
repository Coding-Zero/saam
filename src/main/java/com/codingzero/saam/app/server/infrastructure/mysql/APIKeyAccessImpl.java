package com.codingzero.saam.app.server.infrastructure.mysql;

import com.codingzero.saam.app.server.infrastructure.mysql.commons.AbstractAccess;
import com.codingzero.saam.app.server.infrastructure.mysql.commons.MySQLQueryBuilder;
import com.codingzero.saam.infrastructure.data.APIKeyAccess;
import com.codingzero.saam.infrastructure.data.APIKeyOS;
import com.codingzero.saam.infrastructure.data.PrincipalOS;
import com.codingzero.utilities.key.HMACKey;
import com.codingzero.utilities.key.Key;
import com.codingzero.utilities.key.RandomKey;
import com.codingzero.utilities.pagination.OffsetBasedResultPage;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.ResultFetchRequest;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class APIKeyAccessImpl extends AbstractAccess implements APIKeyAccess {

    public static final String TABLE = "saam_v2.apikeys";

    public APIKeyAccessImpl(DataSource dataSource,
                            ObjectSegmentMapper objectSegmentMapper) {
        super(dataSource, objectSegmentMapper);
    }

    @Override
    public String generateSecretKey() {
        return RandomKey.nextUUIDKey()
                .toHMACKey(HMACKey.Algorithm.SHA256)
                .toBase64String(true);
    }

    @Override
    public void insert(APIKeyOS os) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                    TABLE,
                    "application_id, id, `secret_key`, name, user_id, is_active",
                    "?, ?, ?, ?, ?, ?");
            stmt = conn.prepareStatement(sql);

            stmt.setBytes(1, Key.fromHexString(os.getId().getApplicationId()).getKey());
            stmt.setBytes(2, Key.fromHexString(os.getId().getId()).getKey());
            stmt.setString(3, os.getSecretKey());
            stmt.setString(4, os.getName());
            stmt.setBytes(5, Key.fromHexString(os.getUserId()).getKey());
            stmt.setBoolean(6, os.isActive());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void update(APIKeyOS os) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("UPDATE %s SET name=?, is_active=? "
                            + " WHERE application_id=? AND id=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, os.getName());
            stmt.setBoolean(2, os.isActive());
            stmt.setBytes(3, Key.fromHexString(os.getId().getApplicationId()).getKey());
            stmt.setBytes(4, Key.fromHexString(os.getId().getId()).getKey());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void delete(APIKeyOS os) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s"
                            + " WHERE application_id=? AND id=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getId().getApplicationId()).getKey());
            stmt.setBytes(2, Key.fromHexString(os.getId().getId()).getKey());
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
            String sql = String.format("DELETE ak, ppl FROM %S ak"
                            + " LEFT JOIN %S ppl"
                            + " ON ak.application_id = ppl.application_id AND ak.id = ppl.id"
                            + " WHERE ak.application_id=? AND ak.user_id=?;",
                    TABLE,
                    PrincipalAccessImpl.TABLE);
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
    public void deleteByApplicationId(String id) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s"
                            + " WHERE application_id=? LIMIT 1000;",
                    TABLE,
                    PrincipalAccessImpl.TABLE);
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
    public APIKeyOS selectByPrincipalOS(PrincipalOS principalOS) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT * FROM %s WHERE application_id=? AND id=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(principalOS.getId().getApplicationId()).getKey());
            stmt.setBytes(2, Key.fromHexString(principalOS.getId().getId()).getKey());
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return getObjectSegmentMapper().toAPIKeyOS(principalOS, rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public List<APIKeyOS> selectByUserId(String applicationId, String userId) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT * FROM %S ak"
                            + " LEFT JOIN %S ppl"
                            + " ON ak.application_id = ppl.application_id AND ak.id = ppl.id"
                            + " WHERE ak.application_id=? AND ak.user_id=?;",
                    TABLE,
                    PrincipalAccessImpl.TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(userId).getKey());
            rs = stmt.executeQuery();
            return toOSList(rs, 10);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public PaginatedResult<List<APIKeyOS>> selectByApplicationId(String applicationId) {
        return new PaginatedResult<>(
                request -> _selectByApplicationId(request),
                applicationId);
    }

    private List<APIKeyOS> _selectByApplicationId(ResultFetchRequest request) {
        String applicationId = (String) request.getArguments()[0];
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String baseSQL = String.format(
                    "SELECT * FROM %S ak"
                            + " LEFT JOIN %S ppl"
                            + " ON ak.application_id = ppl.application_id AND ak.id = ppl.id"
                            + " WHERE ak.application_id=? ",
                    TABLE,
                    PrincipalAccessImpl.TABLE);
            StringBuilder sql = new StringBuilder(baseSQL);
            sql.append(MySQLQueryBuilder.buildSortingQuery(request.getSorting()));
            sql.append(" ");
            sql.append(MySQLQueryBuilder.buildPagingQuery((OffsetBasedResultPage) request.getPage()));
            sql.append(";");
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

    private List<APIKeyOS> toOSList(ResultSet rs, int size) throws SQLException, IOException {
        List<APIKeyOS> result = new ArrayList<>(size);
        while (rs.next()) {
            result.add(getObjectSegmentMapper().toAPIKeyOS(rs));
        }
        return result;
    }

}
