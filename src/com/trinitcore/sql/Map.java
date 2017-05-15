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

    public Object getValue() {
        if (String.valueOf(this.value).matches("[0-9]+")) {
            if (String.valueOf(this.value).length() >= 9) {
                return Long.valueOf(String.valueOf(this.value));
            } else {
                return Integer.valueOf(String.valueOf(this.value));
            }
        }
        return this.value;
    }

    public String string() {
        return (String) this.value;
    }

}
