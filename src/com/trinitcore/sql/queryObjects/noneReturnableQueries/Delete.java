package com.trinitcore.sql.queryObjects.noneReturnableQueries;

import com.trinitcore.sql.Map;

/**
 * Created by cormacpjkinsella on 10/10/16.
 */
public class Delete extends NoneReturnableQuery {
    public Delete(String table, String whereColumn, Object whereValue) {
        super(table);
        this.query = "DELETE FROM "+table+" WHERE \""+whereColumn+"\" = ?";
        this.parameters.add(whereValue);
        query(false);
        close();
        System.out.println(this.query);
    }
}
