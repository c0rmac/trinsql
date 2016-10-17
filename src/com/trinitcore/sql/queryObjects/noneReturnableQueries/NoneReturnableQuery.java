package com.trinitcore.sql.queryObjects.noneReturnableQueries;

import com.trinitcore.sql.Map;
import com.trinitcore.sql.Row;
import com.trinitcore.sql.queryObjects.QueryObject;
import com.trinitcore.sql.queryObjects.returnableQueries.Select;

/**
 * Created by cormacpjkinsella on 10/10/16.
 */
public class NoneReturnableQuery extends QueryObject {
    public String table;
    public Map[] values;

    public NoneReturnableQuery(String table, Map... values) {
        super(table);
        this.table = table;
        this.values = values;
    }


    Row getLastRow() {
        Select lastRow = new Select(this.table).order("ID","DESC").limit(1);
        return lastRow.getRow();
    }
}
