package com.trinitcore.v2.queryUtils.connection

import java.sql.Connection
import java.sql.DriverManager

/**
 * Created by Cormac on 28/08/2017.
 */

class PostgresConnectionManager(public val dbName: String, public val username: String, public val password: String) : ConnectionManager() {
    override fun getDatabaseName(): String {
        return dbName
    }

    override fun getNewConnection(): Connection {
        val e = "org.postgresql.Driver"
        Class.forName(e)
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/$dbName", username, password)
    }
}