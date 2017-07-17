package com.trinitcore.sql;

import com.trinitcore.sql.queryObjects.noneReturnableQueries.Update;
import com.trinitcore.sql.queryObjects.returnableQueries.Select;
import org.json.simple.JSONObject;

import java.util.*;

import static java.lang.System.out;

/**
 * Created by cormacpjkinsella on 10/9/16.
 */
public class Row extends HashMap<String,Object> implements Comparable<Row> {

    public boolean containsAssociation = false;
    public String associationColumn;
    public int associationRowCount;
    public boolean associationCounter;
    public boolean associationMatchingDataBoolean;

    public Select parentSelect;

    public Row(Select parentSelect) {
        this.parentSelect = parentSelect;
    }

    public JSONObject toJSONObject() {
        return Select.mapJSONObject(this);
    }

    public boolean hasAssociatingValuesWhere(String associationColumn, String column, Object value) {
        try {
            Object associationObject = get(associationColumn);
            if (associationObject instanceof Row[]) {
                Row[] associationRows = (Row[]) associationObject;
                if (associationRows.length == 0) return false;
                else {
                    for (Row row : associationRows) {
                        if (row.get(column).equals(value)) {
                            return true;
                        }
                    }
                }
            } else if (associationObject instanceof Row) {
                Row row = (Row) associationObject;
                if (row.get(column).equals(value)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void iterateColumns(ColumnIterator e) {
        Iterator it = entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry pair = (java.util.Map.Entry) it.next();
            e.iterate((String) pair.getKey(),pair.getValue());
        }
    }

    public Date getDate(String column) {
        return new Date((Long) get(column));
    }

    public void update(Map... parameters) {
        new Update(parentSelect.table, "ID", get("ID"), parameters);
    }

    @Override
    public int compareTo(Row o) {
        String a;
        String b;
        if (containsAssociation) {
            if (associationCounter || associationMatchingDataBoolean) {
                a = String.valueOf(get(associationColumn));
            } else {
                a = String.valueOf(
                        ((Row[]) get(associationColumn)).length
                );
            }
        } else {
            a = "0";
        }

        if (o.containsAssociation) {
            if (associationCounter || associationMatchingDataBoolean) {
                b = String.valueOf(o.get(o.associationColumn));
            } else {
                b = String.valueOf(
                        ((Row[]) o.get(o.associationColumn)).length
                );
            }
        } else {
            b = "0";
        }
        out.println("Length a = "+a+"; Length b = "+b);
        return a.compareTo(b);
    }

    public interface ColumnIterator {
        void iterate(String key, Object value);
    }

}
