package com.trinitcore.sqlv2.queryUtils.associationV2.table.handler

import com.trinitcore.sqlv2.commonUtils.Defaults
import com.trinitcore.sqlv2.commonUtils.QMap
import com.trinitcore.sqlv2.commonUtils.row.Row
import com.trinitcore.sqlv2.commonUtils.row.Rows
import com.trinitcore.sqlv2.queryObjects.Table
import com.trinitcore.sqlv2.queryObjects.builders.ModuleTableBuilder
import com.trinitcore.sqlv2.queryObjects.builders.TableBuilder
import com.trinitcore.sqlv2.queryUtils.associationV2.Associating
import com.trinitcore.sqlv2.queryUtils.associationV2.GenericAssociationHandler
import com.trinitcore.sqlv2.queryUtils.associationV2.table.TableAssociation
import com.trinitcore.sqlv2.queryUtils.builders.Query
import com.trinitcore.sqlv2.queryUtils.module.DataModule
import com.trinitcore.sqlv2.queryUtils.module.DataModules
import com.trinitcore.sqlv2.queryUtils.parameters.Where

class TableAssociationHandler(override val tableAssociation: TableAssociation, val parameters: Associating) : GenericAssociationHandler(tableAssociation) {

    val queryTableBuilder: TableBuilder = tableAssociation.moduleInitialisation?.let { ModuleTableBuilder(tableAssociation.tableName, moduleInitialisation = it) } ?: TableBuilder(tableAssociation.tableName)
    val queryTable: Table
    var queryRows: Rows? = null
    val notArray = tableAssociation.notArray

    init {
        tableAssociation.associations.forEach { queryTableBuilder.addAssociation(it) }
        this.queryTable = queryTableBuilder.build()
        queryTable.indexColumnKey = parameters.childColumnName
    }

    public val indexQueryParameters: Where = Where()
    public fun addQueryIndex(row: Row) {
        queryRows = null
        row[parameters.columnName]?.let {
            indexQueryParameters.orEqualValues(
                    QMap(parameters.childColumnName, it)
            )
        }
    }

    public fun removeQueryIndex(row: Row) {
        queryRows = null
        row[parameters.columnName]?.let {
            indexQueryParameters.removeOrEqualValue(
                    parameters.childColumnName, it
            )
        }
    }

    override fun match(matchingRow: Row): Any? {

        fun blankRows() : Rows {
            val table = queryTableBuilder.build()
            val rows = if (tableAssociation.moduleInitialisation != null) DataModules<DataModule>(Defaults.indexColumnKey, table, tableAssociation.moduleInitialisation!!)
                                else Rows(Defaults.indexColumnKey, table)
            rows.associations = table.associations
            return rows
        }

        var canQuery = true
        if (queryRows == null) {
            parameters.skipParentRowExcludesValues.forEach { t, u ->
                if (matchingRow[t] != u) {
                    canQuery = false
                    return@forEach
                }
            }

            parameters.skipParentRowHasValues.forEach { t, u ->
                if (matchingRow[t] == u) {
                    canQuery = false
                    return@forEach
                }
            }

            if (canQuery) this.queryRows = queryTable.find(where = Where().join(indexQueryParameters, Query.AND, true).join(parameters.generateWhereParameters(), Query.AND))
        }

        if (canQuery) {
            val child = this.queryRows!![matchingRow[parameters.columnName]]
            if (child is Rows) {
                if (notArray) return child.firstEntry().value
                return child
            } else if (child is Row) {
                // This usually happens when child indexColumnKey = ID for the queryTable
                if (notArray) return child

                val childRows = blankRows()
                childRows.put(child[Defaults.indexColumnKey]!!, child)
                return childRows
            }
        }
        return if (parameters.blankRowsIfMatchNotFound) blankRows() else null
    }
}