package com.trinitcore.sql.queryObjects;

import com.trinitcore.sql.SQL;

/**
 * Created by cormacpjkinsella on 10/9/16.
 */
public class QueryObject extends SQL{
    public final String table;
    public String[] columns = null;

    public QueryObject (String table, String... columns) {
        this.table = table;
        this.columns = columns;
    }

    public QueryObject (String table) {
        this.table = table;
    }
}
