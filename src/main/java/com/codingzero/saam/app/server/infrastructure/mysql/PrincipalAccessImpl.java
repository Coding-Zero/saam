package com.codingzero.saam.app.server.infrastructure.mysql;

import com.codingzero.saam.app.server.infrastructure.mysql.commons.AbstractAccess;
import com.codingzero.saam.app.server.infrastructure.mysql.commons.MySQLQueryBuilder;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.data.PrincipalOS;
import com.codingzero.saam.infrastructure.data.PrincipalAccess;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class PrincipalAccessImpl extends AbstractAccess implements PrincipalAccess {

    public static final String TABLE = "saam_v2.principals";

    public PrincipalAccessImpl(DataSource dataSource, ObjectSegmentMapper objectSegmentMapper) {
        super(dataSource, objectSegmentMapper);
    }

    @Override
    public String generateId(String applicationId, PrincipalType type) {
        return RandomKey.nextTimeBasedUUIDKey().toHexString();
    }

    @Override
    public void insert(PrincipalOS os) {
        Connection conn = getConnection();
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void update(PrincipalOS os) {

    }

    @Override
    public void delete(PrincipalOS os) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE application_id=? AND id=? LIMIT 1;",
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
    public void deleteByApplicationIdAndType(String id, PrincipalType type) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s"
                            + " WHERE application_id=? AND type=? LIMIT 1000;",
                    TABLE,
                    PrincipalAccessImpl.TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(id).getKey());
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
    public PrincipalOS selectById(String applicationId, String id) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT * FROM %s WHERE application_id=? AND id=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(applicationId).getKey());
            stmt.setBytes(2, Key.fromHexString(id).getKey());
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return getObjectSegmentMapper().toPrincipalOS(rs);
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
    public PaginatedResult<List<PrincipalOS>> selectByApplicationIdAndType(String applicationId, PrincipalType type) {
        return new PaginatedResult<>(
                request -> _selectByApplicationIdAndType(request),
                applicationId, type);
    }

    @Override
    public PaginatedResult<List<PrincipalOS>> selectByApplicationId(String applicationId) {
        return new PaginatedResult<>(
                request -> _selectByApplicationId(request),
                applicationId);
    }

    public List<PrincipalOS> _selectByApplicationIdAndType(ResultFetchRequest request) {
        String applicationId = (String) request.getArguments()[0];
        PrincipalType type = (PrincipalType) request.getArguments()[1];
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(String.format("SELECT * FROM %s WHERE "
                            + " application_id=? AND type=? ",
                    TABLE));
            sql.append(MySQLQueryBuilder.buildSortingQuery(request.getSorting()));
            sql.append(" ");
            sql.append(MySQLQueryBuilder.buildPagingQuery((OffsetBasedResultPage) request.getPage()));
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

    public List<PrincipalOS> _selectByApplicationId(ResultFetchRequest request) {
        String applicationId = (String) request.getArguments()[0];
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String baseSQL = String.format("SELECT * FROM %s WHERE "
                            + " application_id=? ",
                    TABLE);
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

    private List<PrincipalOS> toOSList(ResultSet rs, int size) throws SQLException, IOException {
        List<PrincipalOS> result = new ArrayList<>(size);
        while (rs.next()) {
            result.add(getObjectSegmentMapper().toPrincipalOS(rs));
        }
        return result;
    }
}
