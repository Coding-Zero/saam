package com.codingzero.saam.app.server.base.mysql;

import com.codingzero.saam.infrastructure.database.ApplicationOS;
import com.codingzero.saam.infrastructure.database.ApplicationAccess;
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


public class ApplicationAccessImpl extends AbstractAccess implements ApplicationAccess {

    public static final String TABLE = "applications";

    public ApplicationAccessImpl(DataSource dataSource, ObjectSegmentMapper objectSegmentMapper) {
        super(dataSource, objectSegmentMapper);
    }

    @Override
    public boolean isDuplicateName(String name) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format(
                    "SELECT COUNT(*) FROM %s WHERE name = ? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setString(1, name);
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
    public String generateId() {
        return RandomKey.nextTimeBasedUUIDKey().toHexString();
    }

    @Override
    public void insert(ApplicationOS os) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                    TABLE,
                    "id, name, description, creation_time, password_policy, status",
                    "?, ?, ?, ?, ?, ?");
            stmt=conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getId()).getKey());
            stmt.setString(2, os.getName());
            stmt.setString(3, os.getDescription());
            stmt.setTimestamp(4, new Timestamp(os.getCreationTime().getTime()));
            stmt.setString(5, getObjectSegmentMapper().toJson(os.getPasswordPolicy()));
            stmt.setString(6, os.getStatus().name());
            stmt.executeUpdate();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void update(ApplicationOS os) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            String sql = String.format("UPDATE %s SET name=?, description=?, password_policy=?, status=? "
                            + " WHERE id=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, os.getName());
            stmt.setString(2, os.getDescription());
            stmt.setString(3, getObjectSegmentMapper().toJson(os.getPasswordPolicy()));
            stmt.setString(4, os.getStatus().name());
            stmt.setBytes(5, Key.fromHexString(os.getId()).getKey());
            stmt.executeUpdate();
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public void delete(ApplicationOS os) {
        Connection conn = getConnection();
        PreparedStatement stmt=null;
        try {
            String sql = String.format("DELETE FROM %s WHERE id = ? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareStatement(sql);
            stmt.setBytes(1, Key.fromHexString(os.getId()).getKey());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(conn);
        }
    }

    @Override
    public ApplicationOS selectById(String id) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = String.format("SELECT * FROM %s WHERE id=? LIMIT 1;",
                    TABLE);
            stmt = conn.prepareCall(sql);
            stmt.setBytes(1, Key.fromHexString(id).getKey());
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return getObjectSegmentMapper().toApplicationOS(rs);
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
    public PaginatedResult<List<ApplicationOS>> selectAll() {
        return new PaginatedResult<>(request -> ApplicationAccessImpl.this._selectAll(request));
    }

    private List<ApplicationOS> _selectAll(ResultFetchRequest request) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(String.format("SELECT * FROM %s ", TABLE));
            sql.append(MySQLHelper.buildSortingQuery(request.getSorting()));
            sql.append(" ");
            sql.append(MySQLHelper.buildPagingQuery((OffsetBasedResultPage) request.getPage()));
            stmt = conn.prepareCall(sql.toString());
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

    private List<ApplicationOS> toOSList(ResultSet rs, ResultPage pageOffset) throws SQLException, IOException {
        List<ApplicationOS> result = new ArrayList<>(pageOffset.getSize());
        while (rs.next()) {
            result.add(getObjectSegmentMapper().toApplicationOS(rs));
        }
        return result;
    }
}
