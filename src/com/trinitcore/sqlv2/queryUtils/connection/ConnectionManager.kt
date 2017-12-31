package com.trinitcore.sqlv2.queryUtils.connection

import org.postgresql.util.PSQLException
import java.sql.Connection

/**
 * Created by Cormac on 18/08/2017.
 */
abstract class ConnectionManager {

    public abstract fun getNewConnection(): Connection
    public abstract fun getDatabaseName(): String
    public var currentConnection: Connection? = null

    private var currentCloseConnectionTimeoutThread:Thread? = null

    private fun openWithoutCondition() {
        this.currentConnection = getNewConnection()
    }

    @Synchronized
    public open fun open(): Boolean {
            currentCloseConnectionTimeoutThread?.interrupt()
            if (currentCloseConnectionTimeoutThread == null) currentCloseConnectionTimeoutThread = generateCloseConnectionThread()

            if (currentConnection == null || currentConnection?.isClosed == true) {
                this.currentConnection = getNewConnection()
                return true
            }
            return false
    }

    private fun generateCloseConnectionThread(): Thread {
        println("Generating close connection thread.")
        val thread = Thread {
            fun doSleep() {
                try {
                    println("Sleeping")
                    Thread.sleep(1000 * 60 * 5)
                } catch (e: InterruptedException) {
                    println("Close connection timeout has been reset")
                    doSleep()
                }
            }

            doSleep()

            close()
            println("The connection did close")

        }
        thread.start()
        return thread
    }

    public fun close() {
        currentConnection?.close()
        currentConnection = null
    }
}