package com.trinitcore.sqlv2.queryUtils.builders

import com.trinitcore.sqlv2.commonUtils.QMap
import com.trinitcore.sqlv2.commonUtils.then
import com.trinitcore.sqlv2.queryObjects.SQL
import com.trinitcore.sqlv2.queryUtils.parameters.Where
import com.trinitcore.sqlv2.queryUtils.parameters.columns.Column

/**
 * Created by Cormac on 17/08/2017.
 */
public object Query {
    private fun multiply(line: String, count: Int, separation: String = ""): String {
        var string = ""
        for (i in 1..count) {
            string += line; if (i != count) string += separation
        }
        return string
    }

    public val EQUALS = " = "
    public val LIKE = " LIKE "
    public val WHERE = " WHERE "
    public val AND = " AND "
    public val OR = " OR "

    public val NOTNULL = " NOT NULL "
    public val DEFAULT = " DEFAULT "

    public fun WHERE(column: String, equalizer: String, value: String = "?"): String = " \"$column\" $equalizer $value "
    public fun RETURNING(tableName: String): String = " RETURNING \"ID\"; "
    // fun RETURNING(tableName: String): String = " SELECT currval(pg_get_serial_sequence('$tableName', '"+Defaults.indexColumnKey+"')); "

    public fun INSERT(table: String, columns: Array<out String>, rowsToInsert: Int = 1): String = "INSERT INTO $table (" + columns.joinToString(separator = ",") { "\""+it+"\"" } + ") VALUES" +
            multiply(" (" + columns.joinToString(separator = ",") { "?" } + ") ", rowsToInsert, ",") +
            "; "

    public fun UPDATE(table: String, columns: Array<out String>): String = "UPDATE $table SET " + columns.joinToString(separator = ",") { "\""+it+"\"" + " = ?" } + " "
    public fun DELETE(table: String): String = "DELETE FROM $table "

    public fun DROP(table: String): String = "DROP TABLE $table"

    public fun TABLEEXIST(table: String, doesExist: () -> String, doesNotExist: () -> String) : String {
        return " IF EXISTS("+
                 SELECT("INFORMATION_SCHEMA.TABLES", arrayOf("table_name")) + Where().andEqualValues(QMap("table_schema", SQL.sharedConnection.getDatabaseName())).andLikeValues(QMap("table_name", table)).toString()+")" +
                " THEN " + doesExist() +
                " ELSE " + doesNotExist() +
                " END IF; "
    }
    public fun CREATE(table: String, columns: Array<out Column<out Any>>): String = " CREATE TABLE IF NOT EXISTS public.$table (\"ID\" SERIAL PRIMARY KEY," + columns.joinToString(separator = ",") { it.toString() } + "); "

    public fun SELECT(table: String): String = " SELECT * FROM $table "
    public fun SELECT(table: String, columns: Array<out String>): String = columns.isEmpty() then SELECT(table = table) ?: " SELECT " + columns.joinToString(separator = ",") + " FROM $table "
}
