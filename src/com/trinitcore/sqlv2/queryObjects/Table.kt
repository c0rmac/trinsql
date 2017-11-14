package com.trinitcore.sqlv2.queryObjects

import com.trinitcore.sqlv2.commonUtils.AssociatingQMap
import com.trinitcore.sqlv2.commonUtils.Defaults
import com.trinitcore.sqlv2.commonUtils.MultiAssociatingQMap
import com.trinitcore.sqlv2.commonUtils.QMap
import com.trinitcore.sqlv2.commonUtils.row.Row
import com.trinitcore.sqlv2.commonUtils.row.Rows
import com.trinitcore.sqlv2.queryUtils.associations.Association
import com.trinitcore.sqlv2.queryUtils.associations.Associations
import com.trinitcore.sqlv2.queryUtils.associations.GenericAssociation
import com.trinitcore.sqlv2.queryUtils.builders.Query
import com.trinitcore.sqlv2.queryUtils.builders.Query.DELETE
import com.trinitcore.sqlv2.queryUtils.builders.Query.INSERT
import com.trinitcore.sqlv2.queryUtils.builders.Query.SELECT
import com.trinitcore.sqlv2.queryUtils.builders.Query.UPDATE
import com.trinitcore.sqlv2.queryUtils.associations.GenericAssociationsManager
import com.trinitcore.sqlv2.queryUtils.builders.AssociationBuilder
import com.trinitcore.sqlv2.queryUtils.builders.Query.SELECT_COUNT
import com.trinitcore.sqlv2.queryUtils.parameters.Where
import com.trinitcore.sqlv2.queryUtils.parameters.columns.Column
import java.sql.ResultSet

/**
 * Created by Cormac on 17/08/2017.
 */

class Table : GenericAssociationsManager {

    public val tableName: String
    private var tableColumns: Array<out String>

    private var permanentTransactionParameters: Array<out QMap> = emptyArray()
    private var permanentQueryParameters: Where? = null
    private var permanentQueryParametersSeparator: String? = null

    constructor(named: String) {
        this.tableName = named
        this.tableColumns = emptyArray()
    }

    constructor(named: String, vararg columns: Column<out Any>) {
        this.tableName = named
        // this.tableColumns = columns.map { it.name }.toTypedArray()
        SQL.session({
            SQL.noneReturnable(Query.CREATE(named, columns))
        })
        tableColumns = emptyArray()
    }

    // Vars to be passed to Rows
    public var indexColumnKey = Defaults.indexColumnKey
    public var associations = Associations()

    public override fun addAssociation(association: GenericAssociation): Table {
        this.associations.put(association.getColumnTitle(), association)
        return this
    }

    override fun addAssociation(association: AssociationBuilder): Table {
        val build = association.build()
        this.associations.put(build.getColumnTitle(),build)
        return this
    }

    public fun permanentTransactionParameters(vararg qMap: QMap) : Table {
        this.permanentTransactionParameters = qMap
        return this
    }

    public fun permanentQueryParameters(where: Where, separator: String = Query.AND) : Table {
        this.permanentQueryParameters = where
        this.permanentQueryParametersSeparator = separator
        return this
    }

    private fun combineQMapValues(values1: Array<out QMap>, values2: Array<out QMap>) : Array<out QMap> {
        return (values1 as Array<Any>).plus(elements = values2 as Array<Any>) as Array<out QMap>
    }

    private fun sortTransactionQMapValues(parentValues: Array<out QMap>,topValues: MutableList<QMap>, subValues: MutableList<AssociatingQMap>, subMultiValues: MutableList<MultiAssociatingQMap>) {
        for (value in combineQMapValues(parentValues, permanentTransactionParameters)) {
            when (value) {
                is AssociatingQMap -> subValues.add(value)
                is MultiAssociatingQMap -> subMultiValues.add(value)
                else -> topValues.add(value)
            }
        }
    }

    private fun getCreatedRows(resultSet: ResultSet): Rows? {
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
        return SQL.session({
            val sqlString = UPDATE(tableName, values.map { it.key }.toTypedArray()) + where.toString()
            return SQL.returnable(sqlString, values.map { it.value }.plus(elements = where.getQueryParameters()).toTypedArray(), returnColumnKey = true)?.let { resultSet ->
                getCreatedRows(resultSet)
            }
        }) as Rows
    }

    public fun updateRow(where: Where, vararg values: QMap): Row? {
        return updateValues(where, values)?.indexAsRow(0)
    }

    public fun update(where: Where, vararg values: QMap): Rows? {
        return updateValues(where, values)
    }

    public fun updateByID(ID: Int, vararg values: QMap): Row? {
        return updateValues(Where().andEqualValues(
                QMap(indexColumnKey, ID)
        ), values)?.indexAsRow(0)
    }

    public fun deleteRow(where: Where): Row? {
        return delete(where)?.indexAsRow(0)
    }

    public fun delete(where: Where): Rows? {
        return SQL.session({
            val sqlString = DELETE(tableName) + where.toString()
            val rowsToDelete = find(where,false)
            if (rowsToDelete.size != 0) {
                if (SQL.noneReturnable(sqlString, where.getQueryParameters().toTypedArray())) {
                    return rowsToDelete
                }
            }
            return null
        }) as Rows
    }

    private fun dealWithSubValueInsertTransactions(subValues: MutableList<AssociatingQMap>, subMultiValues: MutableList<MultiAssociatingQMap>, createdRow: Row) {
        for (subValue in subValues) {
            val association = associations[subValue.key]
            if (association is Association) {
                val associationParameters = association.parameters
                association.queryTable.insertValues(
                        subValue.getValueAsQMapArray().toList()
                                .plus(element = QMap(associationParameters.childColumnName, createdRow[associationParameters.columnName]!!))
                                .toTypedArray())
            }
        }

        for (subMultiValue in subMultiValues) {
            val association = associations[subMultiValue.key]
            if (association is Association) {
                val associationParameters = association?.parameters!!

                val preparedSubValues = subMultiValue.getValueAsQMapArrays().map {
                    it.toList()
                            .plus(element = QMap(associationParameters.childColumnName, createdRow[associationParameters.columnName]!!))
                            .toTypedArray()
                }
                association.queryTable.multiValueInsert(preparedSubValues.toTypedArray())
            }
        }
    }

    public fun insertValues(values: Array<out QMap>): Row? {
        return SQL.session {
            val topValues = mutableListOf<QMap>()
            val subValues = mutableListOf<AssociatingQMap>()
            val subMultiValues = mutableListOf<MultiAssociatingQMap>()

            sortTransactionQMapValues(values, topValues, subValues, subMultiValues)

            if (topValues.count() == 1 && topValues[0].key == Defaults.indexColumnKey) {
                return findRowByID(topValues[0].value)?.let {
                    dealWithSubValueInsertTransactions(subValues, subMultiValues, it)
                    it
                }
            } else if (topValues.count() != 0) {
                val sqlString = INSERT(tableName, topValues.map { it.key }.toTypedArray())
                return SQL.returnable(sqlString, topValues.map { it.value }.toTypedArray(), returnColumnKey = true)?.let { resultSet ->
                    val createdRow = getCreatedRows(resultSet)?.indexAsRow(0)
                    dealWithSubValueInsertTransactions(subValues, subMultiValues, createdRow!!)
                    return createdRow
                }
            } else {
                throw RuntimeException("No top insert parameters given.")
            }
        } as Row
    }

    public fun multiInsert(vararg values: Array<QMap>): Rows? {
        return multiValueInsert(values = values)
    }

    fun multiValueInsert(values: Array<out Array<QMap>>): Rows? {
        return SQL.session({
            /*

            val allSubValues = mutableListOf<MutableList<AssociatingQMap>>()
            val allMultiSubValues = mutableListOf<MutableList<MultiAssociatingQMap>>()
            for (value in values) {
                val subValues = mutableListOf<AssociatingQMap>()
                val subMultiValues = mutableListOf<MultiAssociatingQMap>()
                sortTransactionQMapValues(combineQMapValues(value, permanentTransactionParameters), topValues, subValues, subMultiValues)

                allSubValues.add(subValues)
                allMultiSubValues.add(subMultiValues)
            }

             */

            val topValues = mutableListOf<QMap>()
            val subValues = mutableListOf<AssociatingQMap>()
            val subMultiValues = mutableListOf<MultiAssociatingQMap>()

            for (value in values) {
                sortTransactionQMapValues(combineQMapValues(value, permanentTransactionParameters), topValues, subValues, subMultiValues)
            }
            val topParameterNames = values[0].filter { !(it is AssociatingQMap || it is MultiAssociatingQMap) }

            var sqlString = INSERT(tableName, topParameterNames.map { it.key }.toTypedArray(), values.size)

            return SQL.returnable(sqlString, topValues.map { it.value }.toTypedArray(), returnColumnKey = true)?.let { resultSet ->
                val rows = getCreatedRows(resultSet)
                for (row in rows!!.rowValues()) {
                    dealWithSubValueInsertTransactions(subValues, subMultiValues, row)
                }

                return rows
            }
        }) as Rows?
    }

    public fun insert(vararg values: QMap): Row? {
        return insertValues(values = values)
    }

    public fun findRow(): Row? {
        return findRow(Where())
    }

    public fun findRow(where: Where): Row? {
        return find(where).firstEntry()?.value as? Row?
    }

    public fun exists(where: Where) : Boolean {
        return SQL.session({
            return SQL.returnable(SELECT(this.tableName, this.tableColumns) + where.toString(), where.getQueryParameters().toTypedArray())?.next() ?: false
        }) as Boolean
    }

    public fun findRowByID(key: Any): Row? {
        return findRow(Where().andEqualValues(QMap(indexColumnKey, key)))
    }

    public fun findByID(key: Any): Rows {
        return find(Where().andEqualValues(QMap(indexColumnKey, key)))
    }

    public fun find(): Rows {
        return find(where = Where())
    }

    public fun find(where: Where = Where()): Rows {
        return find(where, true)
    }

    public fun getCount(where: Where = Where()): Int {
        permanentQueryParameters?.let {
            where.join(it, permanentQueryParametersSeparator!!)
        }

        val sqlString = SELECT_COUNT(this.tableName) + where.toString()
        return SQL.session {
            val results = SQL.returnable(sqlString, where.getQueryParameters().toTypedArray())
            results?.let { resultSet ->
                println(results.statement.toString())

                while (resultSet.next()) {
                    return resultSet.getInt(1)
                }
            }
        } as Int
    }

    public fun find(where: Where = Where(), associations: Boolean): Rows {
        permanentQueryParameters?.let {
            where.join(it, permanentQueryParametersSeparator!!)
        }

        val sqlString = SELECT(this.tableName, this.tableColumns) + where.toString()

        val rows = Rows(indexColumnKey, this)
        rows.associations = this.associations

        SQL.session({
            val results = SQL.returnable(sqlString, where.getQueryParameters().toTypedArray())
            val systemTime1 = System.currentTimeMillis()
            results?.let { resultSet ->
                println(results.statement.toString())

                val metaData = resultSet.metaData
                while (resultSet.next()) {
                    val row = Row(this, rows)
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
            if (associations) rows.addAllAssociations()
        })

        return rows
    }

}