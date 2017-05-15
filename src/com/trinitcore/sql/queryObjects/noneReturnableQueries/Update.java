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
        genericConstructor(values);
        this.query += "WHERE \""+whereColumn+"\" = ?";
        this.parameters.add(value);
        System.out.println(this.query);
        query(false);
        close();
    }

    public Update(String table, String seperator, Map[] whereColumnsValues, Map... values) {
        super(table,values);
        genericConstructor(values);
        stringifyWhere(whereColumnsValues,seperator);
        System.out.println(this.query);
        query(false);
        close();
    }

    public void genericConstructor(Map[] values) {
        this.query = "UPDATE "+table+" ";
        int count = 1;
        for (Map column : values) {
            if (count == 1) {
                this.query += "SET \"" + column.key + "\" = ? ";
            }else {
                this.query += " \""+column.key+"\" = ? ";
            }
            this.parameters.add(column.getValue());
            if (count == values.length) break;
            this.query += ", ";
            count++;
        }
    }

    public void stringifyWhere(Map[] whereColumnsValues, String seperator) {
        String where = "WHERE ";
        int count = 1;
        for (Map map:whereColumnsValues) {
            where += "\""+map.key+"\" = ?";
            this.parameters.add(map.value);
            if (count == whereColumnsValues.length) break;
            where += " "+seperator+" ";
            count++;
        }
        this.query += where;
    }

    public Row getUpdatedRow() {
        return super.getLastRow();
    }
}
