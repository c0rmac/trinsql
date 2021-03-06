package com.trinitcore.sql.queryObjects.returnableQueries;

import com.trinitcore.sql.Association;
import com.trinitcore.sql.Map;
import com.trinitcore.sql.Row;
import com.trinitcore.sql.SQL;
import com.trinitcore.sql.queryObjects.QueryObject;
import com.trinitcore.sql.queryObjects.Where;
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
public class Select extends QueryObject implements Association.Listener{
    public Row[] rows = null;
    public String initialQuery = null;

    public String whereQuery = "";
    public List<Object> whereParameters = new ArrayList<>();

    public String orderQuery = "";
    public List<Object> orderParameters = new ArrayList<>();

    public String limitQuery = "";
    public List<Object> limitParameters = new ArrayList<>();

    public List<Association> associationList = new ArrayList<>();
    private boolean reverseArray;
    private boolean resetUponWhereChange = false;
    private Association.Listener masterTableListener = null;
    private boolean resetUponQuery = false;


    public Select (String table, String... columns) {
        super(table, columns);

        this.initialQuery = "SELECT ";
        int count = 1;
        for (String column : columns) {
            this.initialQuery += "\"" + column + "\"";

            if (!(count == columns.length)) {
                this.initialQuery += ", ";
            }
            count++;
        }
        this.initialQuery += " FROM " + table;
    }

    public Select (String table, Map... columnsKeyAndType) {
        super(table);
            String createTableQuery = "CREATE TABLE IF NOT EXISTS public."+table+" (\n" +
                    "  \"ID\" SERIAL PRIMARY KEY NOT NULL,\n";
            int i = 0;
            for (Map column : columnsKeyAndType) {
                createTableQuery += "  \""+column.key+"\" "+column.value;
                if (i+1 != columnsKeyAndType.length) {
                    createTableQuery += ",\n";
                }
                i++;
            }
            createTableQuery += ");\n";
            System.out.println("Create table query: "+createTableQuery);
            SQL createTable = new SQL(createTableQuery, new ArrayList<>());
            createTable.query(false).close();

        this.initialQuery = "SELECT ";
        this.initialQuery += "* FROM " + table;
    }

    public Select(String table) {
        super(table);
        this.initialQuery = "SELECT ";
        this.initialQuery += "* FROM " + table;
    }

    public Select resetUponWhereChange() {
        resetUponWhereChange = true;
        return this;
    }

    public Select resetUponQuery() {
        resetUponQuery = true;
        return this;
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

    public List<Object> compileParameters() {
        List<Object> parameters = new ArrayList<>();
        parameters.addAll(whereParameters);
        parameters.addAll(orderParameters);
        parameters.addAll(limitParameters);
        return parameters;
    }

    public Select createAssociation(String parentColumn, String childColumn, Select childTable, String name, boolean forceArray, boolean rearrangeAssociationsByChildTableCount) {
        associationList.add(new Association(this,parentColumn, this, childColumn, childTable, name,forceArray,rearrangeAssociationsByChildTableCount,false,false,false));
        return this;
    }

    public Select createAssociation(String parentColumn, String childColumn, Select childTable, String name, boolean forceArray, boolean rearrangeAssociationsByChildTableCount, boolean reverseArray) {
        associationList.add(new Association(this,parentColumn, this, childColumn, childTable, name,forceArray,rearrangeAssociationsByChildTableCount,reverseArray,false,false));
        return this;
    }

    public Select createAssociationCounter(String parentColumn, String childColumn, Select childTable, String counterName) {
        associationList.add(new Association(this,parentColumn, this, childColumn, childTable, counterName,false,false,false,true,false));
        return this;
    }

    public Select createAssociationCounter(String parentColumn, String childColumn, Select childTable, String counterName, boolean rearrangeAssociationsByChildTableCount, boolean reverseArray) {
        associationList.add(new Association(this,parentColumn, this, childColumn, childTable, counterName,false,rearrangeAssociationsByChildTableCount,reverseArray,true,false));
        return this;
    }

    public Select createAssociationMatchingDataBoolean(String parentColumn, String childColumn, Select childTable, String name) {
        associationList.add(new Association(this,parentColumn, this, childColumn, childTable, name,false,false,false,false,true));
        return this;
    }

    public Select createAssociationMatchingDataBoolean(String parentColumn, String childColumn, Select childTable, String name, boolean rearrangeAssociationsByChildTableCount, boolean reverseArray) {
        associationList.add(new Association(this,parentColumn, this, childColumn, childTable, name,false,rearrangeAssociationsByChildTableCount,reverseArray,false,true));
        return this;
    }

    public Select createAssociation(String parentColumn, String childColumn, Select childTable, String name) {
        System.out.println("made an assoc");
        associationList.add(new Association(this,parentColumn, this, childColumn, childTable, name,false, false,false,false,false));
        return this;
    }

    public void resetWhere() {
        System.out.println("Resetting where in table " + table + " " + this.whereQuery);
        // Wrapping it in this to prevent the GenericAssociation Listener delegate from calling associatingTableDidChange
        if (!this.whereQuery.equals("")) {
            this.whereQuery = "";
            this.whereParameters.clear();
            reset(false);
        }
    }

    public void resetOrder() {
        if (!this.orderQuery.equals("")) {
            this.orderQuery = "";
            this.orderParameters.clear();
            reset(false);
        }
    }

    public void resetLimit() {
        System.out.println("Resetting limit in table " + table + " " + this.limitQuery);
        // Wrapping it in this to prevent the GenericAssociation Listener delegate from calling associatingTableDidChange
        if (!this.limitQuery.equals("")) {
            this.limitQuery = "";
            this.limitParameters.clear();
            reset(false);
        }
    }

    public Select where(String type, String equalityType, Map... expectedLocations) {
        if (resetUponWhereChange) {
            reset(true);
        }
        String startOfString = " AND ";
        if (this.whereQuery.equals("")) startOfString = "WHERE";
        int count = 1;

        for (Map location : expectedLocations) {
            if (count == 1)
                this.whereQuery += " "+startOfString+" \""+location.key+"\" "+equalityType+" ? ";
             else
                 this.whereQuery += " "+type+" \""+location.key+"\" "+equalityType+" ? ";
            whereParameters.add(location.value);
            count++;
        }
        return this;
    }

    public Select where(Where content) {
        if (this.whereQuery.equals("")) this.whereQuery += content.getFullWhereQuery();
        else this.whereQuery += " AND " + content.query;

        this.whereParameters.addAll(content.whereParameters);
        return this;
    }

    public Row lastRow(String identifyByRow) {
        try {
            order(identifyByRow,"DESC");
            limit(1);
            reset(false);
            return getRow();
        } finally {
            resetLimit();
            resetOrder();
        }
    }

    public Row lastRowInTable(String identifyByRow) {
        reset(true);
        return lastRow(identifyByRow);
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

    public Row[] getRowsWhere(Map... parameters) {
        Row[] rows = getRows();
        try {
            List<Row> newRowsList = new ArrayList<>();
            for (Row row : rows) {
                boolean allParametersSatisfied = false;
                for (Map parameter : parameters) {
                    allParametersSatisfied = row.get(parameter.key) != null && row.get(parameter.key).equals(parameter.value);
                }
                if (allParametersSatisfied) newRowsList.add(row);
            }

            // System.out.println("New row size: " + newRowsList.size()+" VS. Old row size: "+rows.size());
            return newRowsList.toArray(new Row[0]);
        } catch (NullPointerException e) {
            return new Row[0];
        }
    }

    public Row[] getRowsWhere(String column, Object value) {
        return getRowsWhere(new Map(column, value));
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

    public Select whereLikeAnd(Map... expectedLocations) {
        where("AND", "LIKE", expectedLocations);
        return this;
    }

    public Select whereLikeOr(Map... expectedLocations) {
        where("OR", "LIKE", expectedLocations);
        return this;
    }

    // Where not
    public Select whereNot(String column, Object value) {
        whereNotOr(new Map(column,value));
        return this;
    }

    public Select whereNotOr(Map... expectedLocations) {
        where("OR","<>", expectedLocations);
        return this;
    }

    public Select whereNotAnd(Map... expectedLocations) {
        where("AND","<>", expectedLocations);
        return this;
    }

    // Where greater
    public Select whereGreaterAnd(Map... expectedLocations) {
        where("AND",">", expectedLocations);
        return this;
    }

    public Select whereGreaterOr(Map... expectedLocations) {
        where("OR",">", expectedLocations);
        return this;
    }

    public Select whereGreater(String column, Object value) {
        whereGreaterOr(new Map(column,value));
        return this;
    }

    // Where lesser
    public Select whereLesserAnd(Map... expectedLocations) {
        where("AND","<", expectedLocations);
        return this;
    }

    public Select whereLesserOr(Map... expectedLocations) {
        where("OR","<", expectedLocations);
        return this;
    }

    public Select whereLesser(String column, Object value) {
        whereLesserOr(new Map(column,value));
        return this;
    }

    public Select order(Map order) {
        this.orderQuery = "";
        this.orderQuery += " ORDER BY \""+order.key+"\" "+order.string();
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

    public void reset(boolean parameters) {
        reset(parameters, false);
    }

    public void reset(boolean parameters, boolean notifyDelegate){
        this.rows = null;
        this.resultSet = null;
        if (parameters){
            this.whereQuery = "";
            this.whereParameters = new ArrayList<>();
        }

        if (masterTableListener != null && notifyDelegate) {
            masterTableListener.associatingTableDidChange();
        }
    }

    public static Map[] utilMapToSQLMap(java.util.Map<String,String[]> parameters, String... excluded) {
        List<Map> sqlParameters = new ArrayList<>();
        for (String key : parameters.keySet()) {
            boolean continueMapping = true;
            for (String exclude : excluded) {
                if (key.equals(exclude)) {
                    continueMapping = false;
                    break;
                }
            }
            if (!continueMapping) continue;
            String value = parameters.get(key)[0];
            sqlParameters.add(new Map(key,value));
        }
        return sqlParameters.toArray(new Map[0]);
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

    @Override
    public SQL query(boolean returnable) {
        return super.query(returnable);
    }

    public Row[] getRows() {
        if (this.rows != null) return this.rows;
        try {
            this.parameters = compileParameters();
            if (this.resultSet == null) {
                this.query = this.initialQuery + this.whereQuery + this.orderQuery + this.limitQuery;
                query(true);
                System.out.println(query);
            }
            ResultSetMetaData rsmd = this.resultSet.getMetaData();
            List<Row> rows = new ArrayList<>();
            while (resultSet.next()) {
                Row map = new Row(this);
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
            if (resetUponQuery) {
                reset(true);
            }
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
            column[position] = ((java.util.Map.Entry) it.next()).getValue();
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

    public long getLong(String column) {return (long) get(column); }

    public double getDouble(String column) {
        return (double) get(column);
    }

    public Object get(String column) {
        return getRow().get(column);
    }

    public boolean hasResults() {
        return rowCount() != 0;
    }

    public int additionOfRows(String column) {
        int totalAddition = 0;
        for (Row appointment : getRows()) {
            totalAddition = totalAddition + (int) appointment.get(column);
        }
        return totalAddition;
    }

    @Override
    public void associatingTableDidChange() {
        System.out.println(table + " :: One of my association changed. I better reiterate... or reset");
        // processAssociations();
        reset(false);
    }

    public Association.Listener getMasterTableListener() {
        return masterTableListener;
    }

    public void setMasterTableListener(Association.Listener masterTableListener) {
        this.masterTableListener = masterTableListener;
    }
}
