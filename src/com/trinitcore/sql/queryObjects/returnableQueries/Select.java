package com.trinitcore.sql.queryObjects.returnableQueries;

import com.trinitcore.sql.Association;
import com.trinitcore.sql.Map;
import com.trinitcore.sql.Row;
import com.trinitcore.sql.queryObjects.QueryObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by cormacpjkinsella on 10/9/16.
 */
public class Select extends QueryObject {
    public Row[] rows = null;
    public String initialQuery = null;
    public String whereQuery = "";
    public String orderQuery = "";
    public String limitQuery = "";

    List<Association> associationList = new ArrayList<>();

    public Select (String table, String... columns) {
        super(table, columns);
        this.initialQuery = "SELECT ";
        int count = 1;
        for (String column : columns) {
            this.initialQuery += column;

            if (!(count == columns.length)) {
                this.initialQuery += ", ";
            }
            count++;
        }
        this.initialQuery += " FROM " + table;
    }

    public Select (String table) {
        super(table);
        this.initialQuery = "SELECT ";
        this.initialQuery += "* FROM " + table;
    }

    public Select setAssociation(String parentColumn, String childColumn, Select table) {
        return createAssociation(parentColumn,childColumn,table,parentColumn);
    }

    public Select createAssociation(String parentColumn, String childColumn, Select childTable, String name) {
        associationList.add(new Association(parentColumn, this, childColumn, childTable, name));
        return this;
    }

    public void resetWhere() {
        this.whereQuery = "";
    }

    public Select where(String type, String equalityType, Map... expectedLocations) {
        String startOfString = " AND ";
        if (this.whereQuery.equals("")) startOfString = "WHERE";
        int count = 1;

        for (Map location : expectedLocations) {
            if (count == 1)
                this.whereQuery += " "+startOfString+" "+location.key+" "+equalityType+" ? ";
             else
                 this.whereQuery += " "+type+" "+location.key+" = ? ";
            parameters.add(location.value);
            count++;
        }
        return this;
    }

    public Select where(String column, Object value) {
        whereOr(new Map(column,value));
        return this;
    }

    public Select whereOr(Map... expectedLocations) {
        where("OR","=", expectedLocations);
        return this;
    }

    public Select whereAnd(Map... expectedLocations) {
        where("AND","=", expectedLocations);
        return this;
    }

    public Select whereLike(String column, Object value) {
        where("","LIKE", new Map(column,value));
        return this;
    }

    public Select order(Map order) {
        this.orderQuery = "";
        this.orderQuery += " ORDER BY "+order.key+" "+order.string();
        return this;
    }

    public Select order(String column, String method) {
        order(new Map(column,method));
        return this;
    }

    public Select limit(int value) {
        this.limitQuery = "";
        this.limitQuery += " LIMIT "+String.valueOf(value);
        return this;
    }

    public void reset() {
        this.rows = null;
        this.resultSet = null;
    }

    public static JSONObject mapJSONObject(Row row) {
        JSONObject map = new JSONObject();
        Iterator it = row.entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry pair = (java.util.Map.Entry)it.next();
            String key = (String) pair.getKey(); Object value = pair.getValue();
            if (value instanceof Row) {
                Row row1 = (Row) value;
                map.put(key,mapJSONObject(row1));
            }else if (value instanceof Row[]) {
                Row[] row1 = (Row[]) value;
                map.put(key,mapJSONArray(row1));
            }else map.put(key,value);
            // it.remove(); // avoids a ConcurrentModificationException
        }
        return map;
    }

    public static JSONArray mapJSONArray(Row[] rows) {
        JSONArray array = new JSONArray();
        for (Row row: rows) {
            array.add(mapJSONObject(row));
        }
        return array;
    }

    public JSONArray toJSONArray() {
        return mapJSONArray(getRows());
    }

    public Row[] getRows() {
        if (this.rows != null) return this.rows;
        try {
            if (this.resultSet == null) {
                this.query = this.initialQuery + this.whereQuery + this.orderQuery + this.limitQuery;
                query(true);
                System.out.println(query);
            }
            ResultSetMetaData rsmd = this.resultSet.getMetaData();
            List<Row> rows = new ArrayList<>();
            while (resultSet.next()) {
                Row map = new Row();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    String name = rsmd.getColumnName(i);
                    final Object result = this.resultSet.getObject(name);
                    map.put(name, result);
                }
                rows.add(map);
            }
            Row[] total = new Row[rows.size()];
            int count = 0;
            for (Row row : rows) {
                total[count] = row;
                count++;
            }
            close();
            this.rows = total;
            processAssociations();
            return total;
        } catch (SQLException exception) {
            return null;
        }
    }

    public void processAssociations() {
        if (this.associationList.size() != 0) {
            this.associationList.forEach(Association::process);
        }
    }

    public int rowCount() {
        return getRows().length;
    }

    public Row getRow() {
        return getRows()[0];
    }

    public Row getLastrow() {
        return getRows()[rowCount()-1];
    }

    public Object get() {
        Iterator it = getRow().entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry pair = (java.util.Map.Entry)it.next();
            return pair.getValue();
        }
        return null;
    }

    public Object[] getColumn() {
        Object[] column = new Object[rowCount()];
        int position = 0;
        for (Row row : getRows()) {
            Iterator it = row.entrySet().iterator();
            column[position] = it.next();
            position++;
        }
        return column;
    }

    public String getString(String column) {
        return String.valueOf(get(column));
    }

    public int getInt(String column) {
        return (int) get(column);
    }

    public double getDouble(String column) {
        return (double) get(column);
    }

    public Object get(String column) {
        return getRow().get(column);
    }

    public boolean hasResults() {
        return rowCount() != 0;
    }


}
