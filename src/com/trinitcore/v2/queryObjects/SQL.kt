package com.trinitcore.v2.queryObjects

import com.trinitcore.v2.commonUtils.Defaults
import com.trinitcore.v2.commonUtils.then
import com.trinitcore.v2.queryUtils.connection.ConnectionManager
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * Created by Cormac on 18/08/2017.
 */

object SQL {

    public lateinit var sharedConnection: ConnectionManager

    public fun returnable(query: String, parameters: Array<Any>, returnColumnKey: Boolean = false): ResultSet? {
        val updateParams = arrayOf(Defaults.indexColumnKey)
        val statement = getStatement(query = query, parameters = parameters,
                resultSetParameters = returnColumnKey then updateParams ?: emptyArray()
        )
        if (returnColumnKey) {
            statement?.executeUpdate()
            return statement?.generatedKeys
        }
        return statement?.executeQuery()
    }

    public fun noneReturnable(query: String, parameters: Array<Any> = emptyArray()): Boolean {
        return !(getStatement(query, parameters)?.execute() ?: true)
    }

    private fun getStatement(query: String, parameters: Array<Any>, resultSetParameters: Array<String> = emptyArray()): PreparedStatement? {
        val statement = sharedConnection.currentConnection?.prepareStatement(query, resultSetParameters) ?: throw RuntimeException("No open connection found.")

        var count = 0
        for (p in parameters) statement.setObject(++count, p)

        return statement
    }

    public inline fun session(stream: () -> Unit): Any? {
        var shouldCloseConnection = true
        try {
            shouldCloseConnection = sharedConnection.open()
            return stream()
        } finally {
            if (shouldCloseConnection) {
                sharedConnection.close()
            }
        }
    }
}