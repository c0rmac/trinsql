package com.trinitcore.sql;

import java.sql.Connection;

/**
 * Created by cormacpjkinsella on 10/11/16.
 */
public class Configuration {
    public Connection connection = null;

    public Configuration(boolean alive) {

    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
