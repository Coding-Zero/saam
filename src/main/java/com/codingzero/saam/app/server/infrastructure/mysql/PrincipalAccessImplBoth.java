package com.codingzero.saam.app.server.infrastructure.mysql;

import com.codingzero.saam.app.server.infrastructure.mysql.commons.AbstractAccess;
import com.codingzero.saam.app.server.infrastructure.mysql.commons.MySQLQueryBuilder;
import com.codingzero.saam.common.PrincipalId;
import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.data.PrincipalAccess;
import com.codingzero.saam.infrastructure.data.PrincipalOS;
import com.codingzero.utilities.key.Key;
import com.codingzero.utilities.key.RandomKey;
import com.codingzero.utilities.pagination.OffsetBasedResultPage;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.ResultFetchRequest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class PrincipalAccessImplBoth extends AbstractAccess implements PrincipalAccess {

//    public static final String TABLE = "principals_v1";
    public static final String TABLE = "saam.principals";

    private PrincipalAccessImpl helper;

    public PrincipalAccessImplBoth(PrincipalAccessImpl helper) {
        super(helper.getDataSource(), helper.getObjectSegmentMapper());
        this.helper = helper;
    }

    @Override
    public String generateId(String applicationId, PrincipalType type) {
        return RandomKey.nextTimeBasedUUIDKey().toHexString();
    }

    private boolean isVersion2(PrincipalId id) {
        Connection conn = getConnection();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT COUNT(*) FROM %s WHERE "
                            + "application_id=? AND id=? LIMIT 1;",
                    PrincipalAccessImpl.TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(id.getApplicationId()).getKey());
            stmt.setBytes(2, Key.fromHexString(id.getId()).getKey());
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

    private boolean isVersion1(PrincipalId id) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT COUNT(*) FROM %s WHERE "
                            + "application_id=? AND id=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(id.getApplicationId()).getKey());
            stmt.setBytes(2, Key.fromHexString(id.getId()).getKey());
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
    public void insert(PrincipalOS os) {
        if (!isVersion1(os.getId())) {
            insertV1(os);
        }
        helper.insert(os);
    }

    private void insertV1(PrincipalOS os) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                    TABLE,
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
        helper.delete(os);
        deleteV1(os);
    }

    private void deleteV1(PrincipalOS os) {
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
        helper.deleteByApplicationIdAndType(id, type);
        deleteByApplicationIdAndTypeV1(id, type);
    }

    private void deleteByApplicationIdAndTypeV1(String id, PrincipalType type) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s"
                            + " WHERE application_id=? AND type=? LIMIT 1000;",
                    TABLE);
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
        helper.deleteByApplicationId(id);
        deleteByApplicationIdV1(id);
    }

    private void deleteByApplicationIdV1(String id) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s"
                            + " WHERE application_id=? LIMIT 1000;",
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
    public PrincipalOS selectById(String applicationId, String id) {
        PrincipalOS os = helper.selectById(applicationId, id);
        if (null != os) {
            return os;
        }
        os = selectByIdV1(applicationId, id);
        if (null != os) {
            helper.insert(os);
        }
        return os;
    }

    private PrincipalOS selectByIdV1(String applicationId, String id) {
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

    private List<PrincipalOS> _selectByApplicationIdAndType(ResultFetchRequest request) {
        if (hasMigratedForSelectByApplicationIdAndType(request)) {
            List<PrincipalOS> osList = _selectByApplicationIdAndTypeV1(request);
            for (PrincipalOS os: osList) {
                if (!isVersion2(os.getId())) {
                    helper.insert(os);
                }
            }
        }
        return helper._selectByApplicationIdAndType(request);
    }

    private List<PrincipalOS> _selectByApplicationIdAndTypeV1(ResultFetchRequest request) {
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

    public boolean hasMigratedForSelectByApplicationIdAndType(ResultFetchRequest request) {
        String applicationId = (String) request.getArguments()[0];
        PrincipalType type = (PrincipalType) request.getArguments()[1];
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT count(*) FROM %s v1 \n" +
                            "\tLEFT JOIN %s v2\n" +
                            "\t\tON v1.application_id = v2.application_id \n" +
                            "\t\t\tAND v1.id = v2.id\n" +
                            "\tWHERE v1.application_id=? AND v1.type=? \n" +
                            "            AND v2.type IS NULL ",
                    TABLE,
                    PrincipalAccessImpl.TABLE);
            StringBuilder sqlBuilder = new StringBuilder(sql);
            sqlBuilder.append(MySQLQueryBuilder.buildSortingQuery(request.getSorting(), "v1"));
            sqlBuilder.append(" LIMIT 1; ");
            sql = sqlBuilder.toString();
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

    private List<PrincipalOS> _selectByApplicationId(ResultFetchRequest request) {
        if (hasMigratedForSelectByApplicationId(request)) {
            List<PrincipalOS> osList = _selectByApplicationIdV1(request);
            for (PrincipalOS os: osList) {
                if (!isVersion2(os.getId())) {
                    helper.insert(os);
                }
            }
        }
        return helper._selectByApplicationId(request);
    }

    private List<PrincipalOS> _selectByApplicationIdV1(ResultFetchRequest request) {
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

    public boolean hasMigratedForSelectByApplicationId(ResultFetchRequest request) {
        String applicationId = (String) request.getArguments()[0];
        Connection conn = getConnection();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT count(*) FROM %s v1 \n" +
                            "\tLEFT JOIN %s v2\n" +
                            "\t\tON v1.application_id = v2.application_id \n" +
                            "\t\t\tAND v1.id = v2.id\n" +
                            "\tWHERE v1.application_id= ? \n" +
                            "            AND v2.type IS NULL ",
                    TABLE,
                    PrincipalAccessImpl.TABLE);
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

    private List<PrincipalOS> toOSList(ResultSet rs, int size) throws SQLException, IOException {
        List<PrincipalOS> result = new ArrayList<>(size);
        while (rs.next()) {
            result.add(getObjectSegmentMapper().toPrincipalOS(rs));
        }
        return result;
    }
}
