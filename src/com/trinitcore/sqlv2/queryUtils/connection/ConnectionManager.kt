package com.trinitcore.sqlv2.queryUtils.connection

import java.sql.Connection

/**
 * Created by Cormac on 18/08/2017.
 */
abstract class ConnectionManager {

    public abstract fun getNewConnection(): Connection
    public abstract fun getDatabaseName(): String
    public var currentConnection: Connection? = null

    public fun open(): Boolean {
        // Returns true if no connection was opened.
        if (currentConnection == null) {
            this.currentConnection = getNewConnection()
            return true
        }
        return false
    }

    public fun close() {
        currentConnection?.close()
        currentConnection = null
    }
}