package com.trinitcore.sql;

import com.trinitcore.sql.queryObjects.returnableQueries.Select;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static java.lang.System.out;

/**
 * Created by cormacpjkinsella on 10/9/16.
 */
public class Row extends HashMap<String,Object> implements Comparable<Row> {

    public boolean containsAssociation = false;
    public String associationColumn;

    public JSONObject toJSONObject() {
        return Select.mapJSONObject(this);
    }

    public void iterateColumns(ColumnIterator e) {
        Iterator it = entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry pair = (java.util.Map.Entry) it.next();
            e.iterate((String) pair.getKey(),pair.getValue());
        }
    }

    @Override
    public int compareTo(Row o) {
        String a;
        String b;
        if (containsAssociation) {
            a = String.valueOf(
                    ((Row[]) get(associationColumn)).length
            );
        } else {
            a = "0";
        }

        if (o.containsAssociation) {
            b = String.valueOf(
                    ((Row[]) o.get(o.associationColumn)).length
            );
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
