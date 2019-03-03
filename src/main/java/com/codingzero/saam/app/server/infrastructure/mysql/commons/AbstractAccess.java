package com.codingzero.saam.app.server.infrastructure.mysql.commons;

import com.codingzero.saam.app.server.infrastructure.mysql.ObjectSegmentMapper;
import com.codingzero.utilities.transaction.jdbc.JDBCTransactionalService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractAccess extends JDBCTransactionalService {

    private ObjectSegmentMapper objectSegmentMapper;

    public AbstractAccess(DataSource dataSource, ObjectSegmentMapper objectSegmentMapper) {
        super(dataSource);
        this.objectSegmentMapper = objectSegmentMapper;
    }

    public ObjectSegmentMapper getObjectSegmentMapper() {
        return objectSegmentMapper;
    }

}
