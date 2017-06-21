package com.trinitcore.sql.queryObjects.returnableQueries;

import com.trinitcore.sql.Association;
import com.trinitcore.sql.Map;
import com.trinitcore.sql.Row;
import com.trinitcore.sql.queryObjects.noneReturnableQueries.Delete;
import com.trinitcore.sql.queryObjects.noneReturnableQueries.Insert;
import com.trinitcore.sql.queryObjects.noneReturnableQueries.Update;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public Table(String table, Map... columnsKeyAndType) {
        super(table, columnsKeyAndType);
    }

    @Override
    public Table resetUponWhereChange() {
        super.resetUponWhereChange();
        return this;
    }

    public Table insert(Map... maps) {
        new Insert(this.table, maps);
        reset(false, true);
        return this;
    }

    public Table insert(Row row) {
        List<Map> maps = new ArrayList<>();
        row.iterateColumns((key, value) -> {
            if (!key.equals("ID") && !(value instanceof Row)) {
                maps.add(new Map(key, value));
            }
        });
        return insert(maps.toArray(new Map[0]));
    }

    public Table update(String whereColumn, Object value, Map... maps){
       // if (!checkValidRow(whereColumn, value)) return this;
        new Update(this.table,whereColumn,value,maps);
        reset(false, true);
        return this;
    }

    public Table update(String seperator, Map[] whereColumnsValues, Map... maps) {
        new Update(table,seperator,whereColumnsValues,maps);
        reset(false, true);
        return this;
    }

    public Table delete(String whereColumn, Object value){
        // if (!checkValidRow(whereColumn, value))return this;
        new Delete(this.table,whereColumn,value);
        reset(false, true);
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

    public Table reverseArray(boolean reverse) {
        super.reverseArray(reverse);
        return this;
    }

    @Override
    public Table createAssociation(String parentColumn, String childColumn, Select table, String name) {
        super.createAssociation(parentColumn,childColumn,table,name);
        return this;
    }

    @Override
    public Table createAssociationCounter(String parentColumn, String childColumn, Select childTable, String counterName) {
        super.createAssociationCounter(parentColumn,childColumn,childTable,counterName);
        return this;
    }
    @Override
    public Table createAssociationCounter(String parentColumn, String childColumn, Select childTable, String counterName, boolean rearrangeAssociationsByChildTableCount, boolean reverseArray) {
        super.createAssociationCounter(parentColumn, childColumn,childTable,counterName,rearrangeAssociationsByChildTableCount,reverseArray);
        return this;
    }
    @Override
    public Table createAssociation(String parentColumn, String childColumn, Select childTable, String name, boolean forceArray, boolean rearrangeAssociationsByChildTableCount) {
        super.createAssociation(parentColumn,childColumn,childTable,name,forceArray,rearrangeAssociationsByChildTableCount);
        return this;
    }

    @Override
    public Table createAssociation(String parentColumn, String childColumn, Select childTable, String name, boolean forceArray, boolean rearrangeAssociationsByChildTableCount, boolean reverseArray) {
        super.createAssociation(parentColumn,childColumn,childTable,name,forceArray,rearrangeAssociationsByChildTableCount,reverseArray);
        return this;
    }

    @Override
    public Table createAssociationMatchingDataBoolean(String parentColumn, String childColumn, Select childTable, String name) {
        super.createAssociationMatchingDataBoolean(parentColumn,childColumn,childTable,name);
        return this;
    }

    @Override
    public Table createAssociationMatchingDataBoolean(String parentColumn, String childColumn, Select childTable, String name, boolean rearrangeAssociationsByChildTableCount, boolean reverseArray) {
        super.createAssociationMatchingDataBoolean(parentColumn,childColumn,childTable,name,rearrangeAssociationsByChildTableCount,reverseArray);
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
    public Table whereNot(String column, Object value) {
        super.whereNot(column, value);
        return this;
    }

    // Where greater
    @Override
    public Select whereGreaterAnd(Map... expectedLocations) {
        super.whereGreaterAnd(expectedLocations);
        return this;
    }

    @Override
    public Select whereGreaterOr(Map... expectedLocations) {
        super.whereGreaterOr(expectedLocations);
        return this;
    }

    @Override
    public Select whereGreater(String column, Object value) {
        super.whereGreater(column, value);
        return this;
    }

    // Where lesser
    @Override
    public Select whereLesserAnd(Map... expectedLocations) {
        super.whereLesserAnd(expectedLocations);
        return this;
    }

    @Override
    public Select whereLesserOr(Map... expectedLocations) {
        super.whereLesserOr(expectedLocations);
        return this;
    }

    @Override
    public Select whereLesser(String column, Object value) {
        super.whereLesser(column,value);
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
