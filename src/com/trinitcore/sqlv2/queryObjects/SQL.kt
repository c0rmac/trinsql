package com.trinitcore.sqlv2.queryObjects

import com.trinitcore.sqlv2.commonUtils.Defaults
import com.trinitcore.sqlv2.commonUtils.NullValue
import com.trinitcore.sqlv2.commonUtils.then
import com.trinitcore.sqlv2.queryUtils.connection.ConnectionManager
import org.postgresql.util.PSQLException
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * Created by Cormac on 18/08/2017.
 */

object SQL {

    public lateinit var sharedConnection: ConnectionManager

    public fun returnable(query: String, parameters: Array<Any>, returnColumnKey: Boolean = false): ResultSet? {
            val updateParams = arrayOf(Defaults.indexColumnKey)
            var statement: PreparedStatement? = null
            statement = getStatement(query = query, parameters = parameters,
                    resultSetParameters = returnColumnKey then updateParams ?: emptyArray()
            )

            //try {
                if (returnColumnKey) {
                    statement.executeUpdate()
                    return statement.generatedKeys
                }
                return statement.executeQuery()
            /*} catch (e: PSQLException) {
                println("Reopening connection")
                return SQL.session {
                    return returnable(query, parameters, returnColumnKey)
                } as ResultSet?
            }*/

    }

    public fun noneReturnable(query: String, parameters: Array<Any> = emptyArray()): Boolean {
            return !(getStatement(query, parameters).execute() ?: true)
    }

    private fun getStatement(query: String, parameters: Array<Any>, resultSetParameters: Array<String> = emptyArray()): PreparedStatement {
        val finalparameters = parameters.map { if (it is NullValue) null else it }

        /*
        if (sharedConnection.currentConnection == null || sharedConnection.currentConnection?.isClosed == true) {
            return SQL.session {
                return getStatement(query, parameters, resultSetParameters)
            } as PreparedStatement
        }
        */
        val statement = sharedConnection.currentConnection!!.prepareStatement(query, resultSetParameters)

        var count = 0
        for (p in finalparameters) statement.setObject(++count, p)

        return statement
    }

    public inline fun session(stream: () -> Unit): Any? {
        var shouldCloseConnection = true
        shouldCloseConnection = sharedConnection.open()

        val s = stream()
        // if (shouldCloseConnection) {
            // sharedConnection.close()
        // }
        return s
    }
}