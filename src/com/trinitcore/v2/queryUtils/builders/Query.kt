package com.trinitcore.v2.queryUtils.builders

import com.trinitcore.v2.commonUtils.QMap
import com.trinitcore.v2.commonUtils.then
import com.trinitcore.v2.queryObjects.SQL
import com.trinitcore.v2.queryUtils.parameters.Where
import com.trinitcore.v2.queryUtils.parameters.columns.Column

/**
 * Created by Cormac on 17/08/2017.
 */
object Query {
    private fun multiply(line: String, count: Int, separation: String = ""): String {
        var string = ""
        for (i in 1..count) {
            string += line; if (i != count) string += separation
        }
        return string
    }

    val EQUALS = " = "
    val LIKE = " LIKE "
    val WHERE = " WHERE "
    val AND = " AND "
    val OR = " OR "

    val NOTNULL = " NOT NULL "
    val DEFAULT = " DEFAULT "

    fun WHERE(column: String, equalizer: String, value: String = "?"): String = " \"$column\" $equalizer $value "
    fun RETURNING(tableName: String): String = " RETURNING \"ID\"; "
    // fun RETURNING(tableName: String): String = " SELECT currval(pg_get_serial_sequence('$tableName', '"+Defaults.indexColumnKey+"')); "

    fun INSERT(table: String, columns: Array<out String>, rowsToInsert: Int = 1): String = "INSERT INTO $table (" + columns.joinToString(separator = ",") + ") VALUES" +
            multiply(" (" + columns.joinToString(separator = ",") { "?" } + ") ", rowsToInsert, ",") +
            "; "

    fun UPDATE(table: String, columns: Array<out String>): String = "UPDATE $table SET " + columns.joinToString(separator = ",") { it + " = ?" } + "; "
    fun DELETE(table: String): String = "DELETE FROM $table; "

    fun DROP(table: String): String = "DROP TABLE $table"

    fun TABLEEXIST(table: String, doesExist: () -> String, doesNotExist: () -> String) : String {
        return " IF EXISTS("+
                 SELECT("INFORMATION_SCHEMA.TABLES", arrayOf("table_name")) + Where().andEqualValues(QMap("table_schema", SQL.sharedConnection.getDatabaseName())).andLikeValues(QMap("table_name", table)).toString()+")" +
                " THEN " + doesExist() +
                " ELSE " + doesNotExist() +
                " END IF; "
    }
    fun CREATE(table: String, columns: Array<out Column<out Any>>): String = " CREATE TABLE IF NOT EXISTS $table (" + columns.joinToString(separator = ",") { it.toString() } + "); "

    fun SELECT(table: String): String = " SELECT * FROM $table "
    fun SELECT(table: String, columns: Array<out String>): String = columns.isEmpty() then SELECT(table = table) ?: " SELECT " + columns.joinToString(separator = ",") + " FROM $table "
}
