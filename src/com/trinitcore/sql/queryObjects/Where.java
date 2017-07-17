package com.trinitcore.sql.queryObjects;

import com.trinitcore.sql.Map;
import com.trinitcore.sql.queryObjects.returnableQueries.Select;

import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cormac on 29/06/2017.
 */
public class Where {
    public String prefix = " WHERE ";
    public String query = "";
    public List<Object> whereParameters = new ArrayList<>();

    public Where where(String type, String equalityType, Map... expectedLocations) {
        String startOfString = (query.equals("")) ? "" : " AND ";
        int count = 1;

        for (Map location : expectedLocations) {
            if (count == 1)
                this.query += " "+startOfString+" \""+location.key+"\" "+equalityType+" ? ";
            else
                this.query += " "+type+" \""+location.key+"\" "+equalityType+" ? ";
            whereParameters.add(location.value);
            count++;
        }
        return this;
    }

    public String getFullWhereQuery() {
        return prefix + query;
    }

    public Where wrap(String type, Where content) {
        where(type, "");
        query += " (";
        query += content.query;
        whereParameters.addAll(content.whereParameters);
        query += ") ";
        return this;
    }

    public Where and(Map... expectedLocations) {
        where("AND", "=", expectedLocations);
        return this;
    }

    public Where andLike(Map... expectedLocations) {
        where("AND", "LIKE", expectedLocations);
        return this;
    }

    public Where andWrap(Where content) {
        wrap("AND", content);
        return this;
    }

    public Where or(Map... expectedLocations) {
        where("OR", "=", expectedLocations);
        return this;
    }

    public Where orLike(Map... expectedLocations) {
        where("OR", "LIKE", expectedLocations);
        return this;
    }

    public Where orWrap(Where content) {
        wrap("OR", content);
        return this;
    }

}
