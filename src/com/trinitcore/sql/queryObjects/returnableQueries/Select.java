package com.trinitcore.sql.queryObjects.returnableQueries;

import com.trinitcore.sql.Association;
import com.trinitcore.sql.Map;
import com.trinitcore.sql.Row;
import com.trinitcore.sql.queryObjects.QueryObject;
import org.apache.commons.lang.ArrayUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by cormacpjkinsella on 10/9/16.
 */
public class Select extends QueryObject {
    public Row[] rows = null;
    public String initialQuery = null;
    public String whereQuery = "";
    public String orderQuery = "";
    public String limitQuery = "";

    public List<Association> associationList = new ArrayList<>();
    private boolean reverseArray;

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

    public Select(String table) {
        super(table);
        this.initialQuery = "SELECT ";
        this.initialQuery += "* FROM " + table;
    }

    public Select setAssociation(String parentColumn, String childColumn, Select table) {
        return createAssociation(parentColumn,childColumn,table,parentColumn);
    }

    public Association getAssociation(String column) {
        for (Association association : associationList) {
            if (association.name.equals(column)) {
                return association;
            }
        }
        return null;
    }

    public Select createAssociation(String parentColumn, String childColumn, Select childTable, String name, boolean forceArray, boolean rearrangeAssociationsByChildTableCount) {
        associationList.add(new Association(parentColumn, this, childColumn, childTable, name,forceArray,rearrangeAssociationsByChildTableCount,false,false,false));
        return this;
    }

    public Select createAssociation(String parentColumn, String childColumn, Select childTable, String name, boolean forceArray, boolean rearrangeAssociationsByChildTableCount, boolean reverseArray) {
        associationList.add(new Association(parentColumn, this, childColumn, childTable, name,forceArray,rearrangeAssociationsByChildTableCount,reverseArray,false,false));
        return this;
    }

    public Select createAssociationCounter(String parentColumn, String childColumn, Select childTable, String counterName) {
        associationList.add(new Association(parentColumn, this, childColumn, childTable, counterName,false,false,false,true,false));
        return this;
    }

    public Select createAssociationCounter(String parentColumn, String childColumn, Select childTable, String counterName, boolean rearrangeAssociationsByChildTableCount, boolean reverseArray) {
        associationList.add(new Association(parentColumn, this, childColumn, childTable, counterName,false,rearrangeAssociationsByChildTableCount,reverseArray,true,false));
        return this;
    }

    public Select createAssociationMatchingDataBoolean(String parentColumn, String childColumn, Select childTable, String name) {
        associationList.add(new Association(parentColumn, this, childColumn, childTable, name,false,false,false,false,true));
        return this;
    }

    public Select createAssociationMatchingDataBoolean(String parentColumn, String childColumn, Select childTable, String name, boolean rearrangeAssociationsByChildTableCount, boolean reverseArray) {
        associationList.add(new Association(parentColumn, this, childColumn, childTable, name,false,rearrangeAssociationsByChildTableCount,reverseArray,false,true));
        return this;
    }

    public Select createAssociation(String parentColumn, String childColumn, Select childTable, String name) {
        System.out.println("made an assoc");
        associationList.add(new Association(parentColumn, this, childColumn, childTable, name,false, false,false,false,false));
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


    public Row getWhere(String column, Object value) {
        try {
            for (Row row : getRows()) {
                if (row.get(column).equals(value)) {
                    return row;
                }
            }
        } catch (NullPointerException e) {

        }
        return null;
    }

    public Row[] getRowsWhere(String column, Object value) {
            List<Row> rows = new ArrayList<Row>(Arrays.asList(getRows()));
            List<Row> newRowsList = rows.stream().filter(row -> row.get(column).equals(value)).collect(Collectors.toList());
            // System.out.println("New row size: " + newRowsList.size()+" VS. Old row size: "+rows.size());
            Row[] newRows = new Row[newRowsList.size()];

            int position = 0;
            for (Row row : newRowsList) {
                newRows[position] = row;
                position += 1;
            }
            return newRows;
    }

    public boolean hasRowsWhere(String column, Object value) {
        try {
            Row[] rows = getRowsWhere(column, value);
            // System.out.println("SQL ROWS LENGTH: " + rows.length);
            if (rows.length != 0) return true;
            else return false;
        } catch (Exception e) {
            return false;
        }
    }

    public String getStringWhere(String requestedColumn, String column, Object value) {
        return (String) getWhere(column, value).get(requestedColumn);
    }

    public Integer getIntegerWhere(String requestedColumn,String column, Object value) {
        return (Integer) getWhere(column, value).get(requestedColumn);
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

    public Select reverseArray(boolean reverse) {
        this.reverseArray = reverse;
        return this;
    }

    public void reset(boolean parameters){
        this.rows = null;
        this.resultSet = null;
        if (parameters){ this.parameters = new ArrayList<>();
        this.whereQuery = "";
        }
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
            if (reverseArray) {
                ArrayUtils.reverse(this.rows);
            }
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
