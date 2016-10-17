package com.trinitcore.sql.queryObjects.noneReturnableQueries;

import com.trinitcore.sql.Map;
import com.trinitcore.sql.Row;
import com.trinitcore.sql.queryObjects.QueryObject;

/**
 * Created by cormacpjkinsella on 10/10/16.
 */
public class Update extends NoneReturnableQuery {
    public Update(String table, String whereColumn, Object value, Map... values) {
        super(table, values);
        this.query = "UPDATE "+table+" ";
        int count = 1;
        for (Map column : values) {
            this.query += "SET "+column.key+" = ? ";
            this.parameters.add(column.value);
            if (count == values.length) break;
            this.query += ", ";
            count++;
        }
        this.query += "WHERE "+whereColumn+" = ?";
        this.parameters.add(value);
        System.out.println(this.query);
        query(false);
        close();
    }

    public Row getUpdatedRow() {
        return super.getLastRow();
    }
}
