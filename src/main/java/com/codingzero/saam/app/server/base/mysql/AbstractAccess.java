package com.codingzero.saam.app.server.base.mysql;

import com.codingzero.utilities.transaction.jdbc.JDBCTransactionalService;

import javax.sql.DataSource;

public class AbstractAccess extends JDBCTransactionalService {

    private ObjectSegmentMapper objectSegmentMapper;

    public AbstractAccess(DataSource dataSource, ObjectSegmentMapper objectSegmentMapper) {
        super(dataSource);
        this.objectSegmentMapper = objectSegmentMapper;
    }

    public ObjectSegmentMapper getObjectSegmentMapper() {
        return objectSegmentMapper;
    }

}
