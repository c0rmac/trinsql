package com.trinitcore.sql;

/**
 * Created by cormacpjkinsella on 10/9/16.
 */
public class Map {
    public String key;
    public Object value;

    public Map(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String string() {
        return (String) this.value;
    }

}
