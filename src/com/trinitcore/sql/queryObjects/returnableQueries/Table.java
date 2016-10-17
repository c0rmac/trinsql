package com.trinitcore.sql.queryObjects.returnableQueries;

import com.trinitcore.sql.Map;
import com.trinitcore.sql.Row;
import com.trinitcore.sql.queryObjects.noneReturnableQueries.Delete;
import com.trinitcore.sql.queryObjects.noneReturnableQueries.Insert;
import com.trinitcore.sql.queryObjects.noneReturnableQueries.Update;

import java.io.IOException;

/**
 * Created by cormacpjkinsella on 10/10/16.
 */
public class Table extends Select {
    public Table(String table, String... columns) {
        super(table, columns);
    }

    public Table(String table) {
        super(table);
    }

    public Table insert(Map... maps) {
        new Insert(this.table, maps);
        reset();
        return this;
    }

    public Table update(String whereColumn, Object value, Map... maps) throws IOException {
        if (!checkValidRow(whereColumn, value)) throw new IOException("No column has value '"+value+"'.");
        new Update(this.table,whereColumn,value,maps);
        reset();
        return this;
    }

    public Table delete(String whereColumn, Object value) throws IOException {
        if (!checkValidRow(whereColumn, value)) throw new IOException("No column has value '"+value+"'.");
        new Delete(this.table,whereColumn,value);
        reset();
        return this;
    }

    public boolean checkValidRow(String column, Object value) {
        for (Row row : getRows()) {
            if (row.get(column).equals(value)) {
                System.out.println("true");
                return true;
            }
        }
        return false;
    }

    @Override
    public Table createAssociation(String parentColumn, String childColumn, Select table, String name) {
        super.createAssociation(parentColumn,childColumn,table,name);
        return this;
    }

    @Override
    public Table setAssociation(String parentColumn, String childColumn, Select table) {
        super.setAssociation(parentColumn,childColumn,table);
        return this;
    }

    @Override
    public Table where(String type, String equalityType, Map... expectedLocations) {
        super.where(type,equalityType,expectedLocations);
        return this;
    }
    public Select whereLike(String column, Object value) {
        super.whereLike(column,value);
        return this;
    }
    @Override
    public Table where(String column, Object value) {
        super.where(column, value);
        return this;
    }

    @Override
    public Table whereOr(Map... expectedLocations) {
        super.whereOr(expectedLocations);
        return this;
    }

    @Override
    public Table whereAnd(Map... expectedLocations) {
        super.whereAnd(expectedLocations);
        return this;
    }

    @Override
    public Table order(Map order) {
        super.order(order);
        return this;
    }

    @Override
    public Table order(String column, String method) {
        super.order(column,method);
        return this;
    }

    @Override
    public Table limit(int value) {
        super.limit(value);
        return this;
    }
}
