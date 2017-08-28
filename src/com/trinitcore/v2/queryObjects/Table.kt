package com.trinitcore.v2.queryObjects

import com.trinitcore.v2.commonUtils.Defaults
import com.trinitcore.v2.commonUtils.QMap
import com.trinitcore.v2.commonUtils.row.Row
import com.trinitcore.v2.commonUtils.row.Rows
import com.trinitcore.v2.queryUtils.builders.Association
import com.trinitcore.v2.queryUtils.builders.Associations
import com.trinitcore.v2.queryUtils.builders.Query
import com.trinitcore.v2.queryUtils.builders.Query.DELETE
import com.trinitcore.v2.queryUtils.builders.Query.INSERT
import com.trinitcore.v2.queryUtils.builders.Query.SELECT
import com.trinitcore.v2.queryUtils.builders.Query.UPDATE
import com.trinitcore.v2.queryUtils.parameters.GenericAssociationsManager
import com.trinitcore.v2.queryUtils.parameters.Where
import com.trinitcore.v2.queryUtils.parameters.columns.Column
import java.sql.ResultSet

/**
 * Created by Cormac on 17/08/2017.
 */

class Table : GenericAssociationsManager {

    private var tableName: String
    private var tableColumns: Array<out String>

    constructor(named: String) {
        this.tableName = named
        this.tableColumns = emptyArray()
    }

    constructor(named: String, vararg columns: Column<out Any>) {
        this.tableName = named
        // this.tableColumns = columns.map { it.name }.toTypedArray()
        SQL.session {
            SQL.noneReturnable(Query.CREATE(named, columns))
        }
        tableColumns = emptyArray()
    }

    // Vars to be passed to Rows
    public var indexColumnKey = Defaults.indexColumnKey
    public var associations = Associations()

    public override fun addAssociation(association: Association): Table {
        this.associations.put(association.parameters.columnName, association)
        return this
    }

    private fun getCreatedRow(resultSet: ResultSet): Rows? {
        val where = Where()

        var valueChecked = false
        while (resultSet.next()) {
            valueChecked = true
            val ID = resultSet.getInt(Defaults.indexColumnKey)
            where.orEqualValues(
                    QMap(Defaults.indexColumnKey, ID)
            )
        }
        return if (valueChecked) {
            Table(tableName).find(where)
        } else {
            null
        }
    }

    public fun updateValues(where: Where, values: Array<out QMap>): Rows? {
        return SQL.session {
            val sqlString = UPDATE(tableName, values.map { it.key }.toTypedArray()) + where.toString()
            return SQL.returnable(sqlString, values.map { it.value }.plus(elements = where.getQueryParameters()).toTypedArray(), returnColumnKey = true)?.let { resultSet ->
                getCreatedRow(resultSet)
            }
        } as Rows
    }

    public fun updateRow(where: Where, vararg values: QMap): Row? {
        return updateValues(where, values)?.indexAsRow(0)
    }

    public fun update(where: Where, vararg values: QMap): Rows? {
        return updateValues(where, values)
    }

    public fun deleteRow(where: Where): Row? {
        return delete(where)?.indexAsRow(0)
    }

    public fun delete(where: Where): Rows? {
        return SQL.session {
            val sqlString = DELETE(tableName) + where.toString()
            val rowsToDelete = find(where)
            if (rowsToDelete.size != 0) {
                if (SQL.noneReturnable(sqlString, where.getQueryParameters().toTypedArray())) {
                    return rowsToDelete
                }
            }
            return null
        } as Rows
    }

    public fun insertValues(values: Array<out QMap>): Row? {
        return SQL.session {
            val sqlString = INSERT(tableName, values.map { it.key }.toTypedArray())
            return SQL.returnable(sqlString, values.map { it.value }.toTypedArray(), returnColumnKey = true)?.let { resultSet ->
                getCreatedRow(resultSet)?.indexAsRow(0)
            }
        } as Row
    }

    public fun multiInsert(vararg values: Array<QMap>): Rows? {
        return multiValueInsert(values = values)
    }

    fun multiValueInsert(values: Array<out Array<QMap>>): Rows? {
        return SQL.session {
            var sqlString = INSERT(tableName, values[0].map { it.key }.toTypedArray(), values.size)
            var sqlParameters = mutableListOf<Any>()
            for (value in values) {
                value.map { sqlParameters.add(it.value) }
            }
            return SQL.returnable(sqlString, sqlParameters.toTypedArray(), returnColumnKey = true)?.let { resultSet ->
                getCreatedRow(resultSet)
            }
        } as Rows?
    }

    public fun insert(vararg values: QMap): Row? {
        return insertValues(values = values)
    }

    public fun findRow(where: Where): Row? {
        return find(where).firstEntry().value as Row
    }

    public fun exists(where: Where) : Boolean {
        return SQL.session {
            return SQL.returnable(SELECT(this.tableName, this.tableColumns) + where.toString(), where.getQueryParameters().toTypedArray())?.next() ?: false
        } as Boolean
    }

    public fun find(where: Where = Where()): Rows {
        val sqlString = SELECT(this.tableName, this.tableColumns) + where.toString()

        val rows = Rows(indexColumnKey, this)
        rows.associations = this.associations

        SQL.session {
            val results = SQL.returnable(sqlString, where.getQueryParameters().toTypedArray())
            val systemTime1 = System.currentTimeMillis()
            results?.let { resultSet ->
                println(results.statement.toString())

                val metaData = resultSet.metaData
                while (resultSet.next()) {
                    val row = Row(this)
                    var i = 1
                    while (i <= metaData.columnCount) {
                        val name = metaData.getColumnName(i++)
                        val value = resultSet.getObject(name)
                        row[name] = value
                    }
                    rows[row[indexColumnKey]!!] = row
                }
                val systemTime2 = System.currentTimeMillis()
                println("Query took: " + (systemTime2 - systemTime1) + " milliseconds")
            }
            rows.addAllAssociations()
        }

        return rows
    }

}