package com.trinitcore.sql.queryObjects.noneReturnableQueries;

import com.trinitcore.sql.Map;
import com.trinitcore.sql.Row;

/**
 * Created by cormacpjkinsella on 10/9/16.
 */
public class Insert extends NoneReturnableQuery {

    public Insert(String table, Map... values) {
        super(table);
        this.query = "INSERT INTO "+table+" (";
        int count = 1;
        for (Map column : values) {
            this.query += column.key;
            if (count == values.length) break;
            this.query += ", ";
            count++;
        }
        this.query += ") VALUES (";
        int count2 = 1;
        for (Map column : values) {
            this.query += "?";
            this.parameters.add(column.value);
            if (count2 == values.length) break;
            this.query += ", ";
            count2++;
        }
        this.query += ")";

        System.out.println(this.query);
        query(false);
        close();
    }

    public Row getInsertedRow() {
        return super.getLastRow();
    }
}
