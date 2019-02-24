package com.codingzero.saam.app.server.base.mysql;

import com.codingzero.utilities.pagination.OffsetBasedResultPage;
import com.codingzero.utilities.pagination.ResultSorting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public final class MySQLQueryHelper {

    private static final Map<String, String> sortingMap = new HashMap<>();
    {
        sortingMap.put("creationTime", "creation_time");
    }

    public static String buildPagingQuery(OffsetBasedResultPage pageOffset) {
        if (null == pageOffset) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("LIMIT ");
        builder.append(pageOffset.getStart() - 1);
        builder.append(", ");
        builder.append(pageOffset.getSize());
        return builder.toString();
    }

    public static String buildSortingQuery(ResultSorting sorting) {
        if (null == sorting) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        String field = sortingMap.get(sorting.getField());
        if (null == field) {
            throw new IllegalArgumentException("No such sorting field found, " + sorting.getField());
        }
        builder.append("ORDER BY");
        builder.append(" creation_time");
        if (null != sorting.getOrder()) {
            builder.append(sorting.getOrder().name());
        }
        return builder.toString();
    }

}
