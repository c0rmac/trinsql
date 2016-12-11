package com.trinitcore.sql;

import java.sql.Connection;

/**
 * Created by cormacpjkinsella on 10/11/16.
 */
public abstract class Configuration {
    public abstract Connection getConnection();

    public Configuration() {

    }
}
