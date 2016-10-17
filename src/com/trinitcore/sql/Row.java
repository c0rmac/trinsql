package com.trinitcore.sql;

import com.sun.tools.corba.se.idl.InterfaceGen;
import com.trinitcore.sql.queryObjects.returnableQueries.Select;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by cormacpjkinsella on 10/9/16.
 */
public class Row extends HashMap<String,Object> {

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

    public interface ColumnIterator {
        void iterate(String key, Object value);
    }

}
