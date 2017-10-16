package com.trinitcore.sqlv2.queryUtils.connection

import com.trinitcore.sqlv2.commonUtils.then
import java.sql.Connection
import java.sql.DriverManager

/**
 * Created by Cormac on 28/08/2017.
 */

public open class PostgresConnectionManager(public val hostURL: String = "localhost", public val dbName: String, public val username: String, public val password: String, public val requireSSL:Boolean = false) : ConnectionManager() {
    override fun getDatabaseName(): String {
        return dbName
    }

    override open fun open(): Boolean {
        return super.open()
    }

    override fun getNewConnection(): Connection {
        val e = "org.postgresql.Driver"
        Class.forName(e)

        return DriverManager.getConnection("jdbc:postgresql://$hostURL:5432/$dbName" + (requireSSL then "?sslmode=require" ?: ""), username, password)
    }
}